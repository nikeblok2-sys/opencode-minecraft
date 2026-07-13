package net.example.firstmod.component;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import java.util.HashSet;
import java.util.Set;

public class ProgressionData {

    public int[] statLevels = new int[StatRegistry.count()];
    public int totalEarnedPp;
    public int spentPp;
    public final Set<String> killedBosses = new HashSet<>();
    public final Set<Integer> distanceMilestones = new HashSet<>();

    public int availablePp() {
        return totalEarnedPp - spentPp;
    }

    public boolean tryUpgrade(int statIndex) {
        if (statIndex < 0 || statIndex >= StatRegistry.count()) return false;
        int cost = StatFormulas.cost(statLevels[statIndex]);
        if (availablePp() < cost) return false;
        spentPp += cost;
        statLevels[statIndex]++;
        return true;
    }

    public void addPp(int amount) {
        totalEarnedPp += amount;
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("StatLevels", statLevels);
        tag.putInt("TotalEarned", totalEarnedPp);
        tag.putInt("Spent", spentPp);
        ListTag bosses = new ListTag();
        for (String b : killedBosses) bosses.add(StringTag.valueOf(b));
        tag.put("Bosses", bosses);
        tag.putIntArray("Miles", distanceMilestones.stream().mapToInt(Integer::intValue).toArray());
        return tag;
    }

    public static ProgressionData fromNbt(CompoundTag tag) {
        ProgressionData data = new ProgressionData();
        int[] levels = tag.getIntArray("StatLevels").orElse(new int[StatRegistry.count()]);
        if (levels.length == StatRegistry.count()) {
            System.arraycopy(levels, 0, data.statLevels, 0, StatRegistry.count());
        }
        data.totalEarnedPp = tag.getIntOr("TotalEarned", 0);
        data.spentPp = tag.getIntOr("Spent", 0);
        ListTag bosses = tag.getListOrEmpty("Bosses");
        for (int i = 0; i < bosses.size(); i++) data.killedBosses.add(bosses.getStringOr(i, ""));
        int[] miles = tag.getIntArray("Miles").orElse(new int[0]);
        for (int m : miles) data.distanceMilestones.add(m);
        return data;
    }
}
