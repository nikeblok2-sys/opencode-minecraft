package net.example.firstmod.component;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ElderGuardian;

import java.util.Map;

public class BossRewards {

    private static final Map<Class<?>, Integer> BOSS_SP = Map.of(
        EnderDragon.class, 100,
        WitherBoss.class, 60,
        ElderGuardian.class, 30
    );

    public static void onEntityDeath(LivingEntity entity, ServerPlayer killer) {
        if (entity == null || killer == null) return;
        Integer reward = null;
        for (var entry : BOSS_SP.entrySet()) {
            if (entry.getKey().isInstance(entity)) {
                reward = entry.getValue();
                break;
            }
        }
        if (reward == null || reward <= 0) return;
        ProgressionStore store = ProgressionStore.getOrCreate(killer.level().getServer());
        ProgressionData data = store.get(killer.getUUID());
        String bossId = entity.getType().builtInRegistryHolder().key().identifier().toString();
        if (data.killedBosses.contains(bossId)) return;
        data.killedBosses.add(bossId);
        data.addSp(reward);
        store.save();
        killer.sendSystemMessage(Component.translatable("firstmod.boss.reward", reward, bossId));
    }
}
