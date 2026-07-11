package net.example.firstmod;

import net.minecraft.resources.Identifier;

public enum StructureGroup {
    /** Данжи, які масштабуються: mineshaft, monster_room, stronghold, trial_chambers тощо */
    DUNGEON_SCALABLE,
    /** Данжи, які краще не чіпати: fortress, end_city, bastion_remnant */
    DUNGEON_FIXED,
    /** Мирні структури: села, піраміди, іглу, тощо */
    PEACEFUL;

    public static StructureGroup fromStructureId(Identifier id) {
        String path = id.getPath();

        return switch (path) {

            case "fortress", "end_city", "bastion_remnant" -> DUNGEON_FIXED;

            case "village_plains", "village_desert", "village_savanna",
                 "village_snowy", "village_taiga",
                 "desert_pyramid", "jungle_pyramid", "igloo",
                 "swamp_hut", "pillager_outpost",
                 "ruined_portal", "ruined_portal_desert",
                 "ruined_portal_jungle", "ruined_portal_mountain",
                 "ruined_portal_nether", "ruined_portal_ocean",
                 "ruined_portal_swamp",
                 "ancient_city",
                 "trail_ruins" -> PEACEFUL;

            default -> DUNGEON_SCALABLE;
        };
    }

    public static StructureGroup fromStructureId(String id) {
        return fromStructureId(Identifier.parse(id));
    }
}
