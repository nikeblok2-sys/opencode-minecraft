package net.example.firstmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class ProgressionPayloads {

    public static final Identifier SYNC_ID = Identifier.fromNamespaceAndPath("firstmod", "progression_sync");
    public static final Identifier UPGRADE_ID = Identifier.fromNamespaceAndPath("firstmod", "stat_upgrade");
    public static final Identifier SELL_ITEMS_ID = Identifier.fromNamespaceAndPath("firstmod", "sell_items");
    public static final Identifier REQUEST_STATS_ID = Identifier.fromNamespaceAndPath("firstmod", "request_stats");
    public static final Identifier REQUEST_SELL_ID = Identifier.fromNamespaceAndPath("firstmod", "request_sell");

    public record SyncPayload(int[] statLevels, int availableSp, int totalEarned, int spent)
            implements CustomPacketPayload {

        public static final StreamCodec<FriendlyByteBuf, SyncPayload> CODEC = new StreamCodec<>() {
            @Override
            public SyncPayload decode(FriendlyByteBuf buf) {
                int[] levels = buf.readVarIntArray();
                int sp = buf.readVarInt();
                int earned = buf.readVarInt();
                int spent = buf.readVarInt();
                return new SyncPayload(levels, sp, earned, spent);
            }

            @Override
            public void encode(FriendlyByteBuf buf, SyncPayload p) {
                buf.writeVarIntArray(p.statLevels);
                buf.writeVarInt(p.availableSp);
                buf.writeVarInt(p.totalEarned);
                buf.writeVarInt(p.spent);
            }
        };

        public static final CustomPacketPayload.Type<SyncPayload> TYPE = new CustomPacketPayload.Type<>(SYNC_ID);

        @Override
        public CustomPacketPayload.Type<SyncPayload> type() {
            return TYPE;
        }
    }

    public record StatUpgradePayload(int statIndex, int count) implements CustomPacketPayload {

        public static final StreamCodec<FriendlyByteBuf, StatUpgradePayload> CODEC = new StreamCodec<>() {
            @Override
            public StatUpgradePayload decode(FriendlyByteBuf buf) {
                return new StatUpgradePayload(buf.readVarInt(), buf.readVarInt());
            }

            @Override
            public void encode(FriendlyByteBuf buf, StatUpgradePayload p) {
                buf.writeVarInt(p.statIndex);
                buf.writeVarInt(p.count);
            }
        };

        public static final CustomPacketPayload.Type<StatUpgradePayload> TYPE = new CustomPacketPayload.Type<>(UPGRADE_ID);

        @Override
        public CustomPacketPayload.Type<StatUpgradePayload> type() {
            return TYPE;
        }
    }

    public record SellItemsPayload(int[] slots, int[] counts) implements CustomPacketPayload {

        public static final StreamCodec<FriendlyByteBuf, SellItemsPayload> CODEC = new StreamCodec<>() {
            @Override
            public SellItemsPayload decode(FriendlyByteBuf buf) {
                int len = buf.readVarInt();
                int[] slots = new int[len];
                int[] counts = new int[len];
                for (int i = 0; i < len; i++) {
                    slots[i] = buf.readVarInt();
                    counts[i] = buf.readVarInt();
                }
                return new SellItemsPayload(slots, counts);
            }

            @Override
            public void encode(FriendlyByteBuf buf, SellItemsPayload p) {
                buf.writeVarInt(p.slots.length);
                for (int i = 0; i < p.slots.length; i++) {
                    buf.writeVarInt(p.slots[i]);
                    buf.writeVarInt(p.counts[i]);
                }
            }
        };

        public static final CustomPacketPayload.Type<SellItemsPayload> TYPE = new CustomPacketPayload.Type<>(SELL_ITEMS_ID);

        @Override
        public CustomPacketPayload.Type<SellItemsPayload> type() {
            return TYPE;
        }
    }

    public record RequestStatsPayload() implements CustomPacketPayload {
        public static final StreamCodec<FriendlyByteBuf, RequestStatsPayload> CODEC =
            StreamCodec.unit(new RequestStatsPayload());
        public static final CustomPacketPayload.Type<RequestStatsPayload> TYPE = new CustomPacketPayload.Type<>(REQUEST_STATS_ID);

        @Override
        public CustomPacketPayload.Type<RequestStatsPayload> type() {
            return TYPE;
        }
    }

    public record RequestSellPayload() implements CustomPacketPayload {
        public static final StreamCodec<FriendlyByteBuf, RequestSellPayload> CODEC =
            StreamCodec.unit(new RequestSellPayload());
        public static final CustomPacketPayload.Type<RequestSellPayload> TYPE = new CustomPacketPayload.Type<>(REQUEST_SELL_ID);

        @Override
        public CustomPacketPayload.Type<RequestSellPayload> type() {
            return TYPE;
        }
    }
}
