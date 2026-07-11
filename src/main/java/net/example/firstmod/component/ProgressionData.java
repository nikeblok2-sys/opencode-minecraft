package net.example.firstmod.component;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import java.util.HashSet;
import java.util.Set;

public class ProgressionData {

    public int[] statLevels = new int[StatFormulas.STAT_COUNT];
    public int totalEarnedSp;
    public int spentSp;
    public final Set<String> killedBosses = new HashSet<>();
    public final Set<Integer> distanceMilestones = new HashSet<>();

    public int availableSp() {
        return totalEarnedSp - spentSp;
    }

    public boolean tryUpgrade(int statIndex) {
        if (statIndex < 0 || statIndex >= StatFormulas.STAT_COUNT) return false;
        int cost = StatFormulas.cost(statLevels[statIndex]);
        if (availableSp() < cost) return false;
        spentSp += cost;
        statLevels[statIndex]++;
        return true;
    }

    public void addSp(int amount) {
        totalEarnedSp += amount;
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("StatLevels", statLevels);
        tag.putInt("TotalEarned", totalEarnedSp);
        tag.putInt("Spent", spentSp);
        ListTag bosses = new ListTag();
        for (String b : killedBosses) bosses.add(StringTag.valueOf(b));
        tag.put("Bosses", bosses);
        tag.putIntArray("Miles", distanceMilestones.stream().mapToInt(Integer::intValue).toArray());
        return tag;
    }

    public static ProgressionData fromNbt(CompoundTag tag) {
        ProgressionData data = new ProgressionData();
        int[] levels = tag.getIntArray("StatLevels").orElse(new int[StatFormulas.STAT_COUNT]);
        if (levels.length == StatFormulas.STAT_COUNT) {
            System.arraycopy(levels, 0, data.statLevels, 0, StatFormulas.STAT_COUNT);
        }
        data.totalEarnedSp = tag.getIntOr("TotalEarned", 0);
        data.spentSp = tag.getIntOr("Spent", 0);
        ListTag bosses = tag.getListOrEmpty("Bosses");
        for (int i = 0; i < bosses.size(); i++) data.killedBosses.add(bosses.getStringOr(i, ""));
        int[] miles = tag.getIntArray("Miles").orElse(new int[0]);
        for (int m : miles) data.distanceMilestones.add(m);
        return data;
    }
}
