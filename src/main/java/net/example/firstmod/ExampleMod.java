package net.example.firstmod;

import net.example.firstmod.command.ProgressionCommand;
import net.example.firstmod.profile.ProgressionProfileCommand;
import net.example.firstmod.component.MilestoneManager;
import net.example.firstmod.component.ProgressionData;
import net.example.firstmod.component.ProgressionStore;
import net.example.firstmod.component.StatEffectHelper;
import net.example.firstmod.component.StatRegistry;
import net.example.firstmod.config.ModConfigManager;
import net.example.firstmod.config.ProgressionConfig;
import net.example.firstmod.data.PlayerDataManager;
import net.example.firstmod.network.ProgressionPayloads;
import net.example.firstmod.shop.ItemValuation;
import net.example.firstmod.profile.ProgressionProfileManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
	public static final String MOD_ID = "firstmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ModConfigManager getConfigManager() {
		return ModConfigManager.get();
	}

	public static PlayerDataManager getPlayerDataManager() {
		return PlayerDataManager.get();
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ProgressionConfig.load();

		registerPayloads();
		registerCommands();
		registerEvents();
	}

	private void registerPayloads() {
		PayloadTypeRegistry.clientboundPlay().register(ProgressionPayloads.SyncPayload.TYPE, ProgressionPayloads.SyncPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ProgressionPayloads.StatUpgradePayload.TYPE, ProgressionPayloads.StatUpgradePayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ProgressionPayloads.SellItemsPayload.TYPE, ProgressionPayloads.SellItemsPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ProgressionPayloads.RequestStatsPayload.TYPE, ProgressionPayloads.RequestStatsPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ProgressionPayloads.RequestSellPayload.TYPE, ProgressionPayloads.RequestSellPayload.CODEC);
	}

	private void registerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ProgressionCommand.register(dispatcher);
			ProgressionProfileCommand.register(dispatcher);
		});
	}

	private void registerEvents() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			syncPlayerData(handler.player);
			StatEffectHelper.applyAttributes(handler.player);
		});

		ServerPlayNetworking.registerGlobalReceiver(ProgressionPayloads.StatUpgradePayload.TYPE, (payload, context) -> {
			handleStatUpgrade(context.player(), payload.statIndex(), payload.count());
		});

		ServerPlayNetworking.registerGlobalReceiver(ProgressionPayloads.SellItemsPayload.TYPE, (payload, context) -> {
			handleSellItems(context.player(), payload.slots(), payload.counts());
		});

		ServerPlayNetworking.registerGlobalReceiver(ProgressionPayloads.RequestStatsPayload.TYPE, (payload, context) -> {
			syncPlayerData(context.player());
		});

		ServerPlayNetworking.registerGlobalReceiver(ProgressionPayloads.RequestSellPayload.TYPE, (payload, context) -> {
			syncPlayerData(context.player());
		});

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			getConfigManager().init(server);
			getPlayerDataManager().init(server);
			ProgressionProfileManager.init(server);
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			ProgressionStore store = ProgressionStore.getOrCreate(server);
			store.save();
			getPlayerDataManager().saveAll();
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			MilestoneManager.onServerTick(server);
		});
	}

	private void syncPlayerData(ServerPlayer player) {
		ProgressionStore store = ProgressionStore.getOrCreate(player.level().getServer());
		ProgressionData data = store.get(player.getUUID());
		ProgressionPayloads.SyncPayload sync = new ProgressionPayloads.SyncPayload(
			data.statLevels, data.availablePp(), data.totalEarnedPp, data.spentPp);
		ServerPlayNetworking.send(player, sync);
	}

	private void handleStatUpgrade(ServerPlayer player, int statIndex, int count) {
		ProgressionStore store = ProgressionStore.getOrCreate(player.level().getServer());
		ProgressionData data = store.get(player.getUUID());
		int upgraded = 0;
		for (int i = 0; i < count; i++) {
			if (data.tryUpgrade(statIndex)) upgraded++;
			else break;
		}
		if (upgraded > 0) {
			store.save();
			syncPlayerData(player);
			StatEffectHelper.applyAttributes(player);
			String key = StatRegistry.get(statIndex).key();
			player.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
				"firstmod.stat.upgraded", key, data.statLevels[statIndex]));
			if (upgraded > 1) {
				player.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
					"firstmod.stat.upgraded_extra", upgraded));
			}
		} else {
			player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("firstmod.stat.cant_afford"));
		}
	}

	private void handleSellItems(ServerPlayer player, int[] slots, int[] counts) {
		double total = 0;
		var inv = player.getInventory();
		for (int i = 0; i < slots.length; i++) {
			int slot = slots[i];
			int count = counts[i];
			if (slot < 0 || slot >= inv.getContainerSize()) continue;
			ItemStack stack = inv.getItem(slot);
			if (stack.isEmpty() || stack.getCount() < count) continue;
			ItemStack toSell = stack.split(count);
			total += ItemValuation.getValue(toSell);
		}
		if (total <= 0) return;
        int ppAmount = Math.max(1, (int) Math.round(total));
		ProgressionStore store = ProgressionStore.getOrCreate(player.level().getServer());
		ProgressionData data = store.get(player.getUUID());
		data.addPp(ppAmount);
		store.save();
		syncPlayerData(player);
		player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("firstmod.shop.sold", ppAmount));
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
