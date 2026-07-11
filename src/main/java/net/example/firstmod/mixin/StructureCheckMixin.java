package net.example.firstmod.mixin;

import net.example.firstmod.ExampleMod;
import net.example.firstmod.config.ProgressionSettings;
import net.example.firstmod.StructureGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(StructureCheck.class)
public abstract class StructureCheckMixin {

    @Shadow
    private RegistryAccess registryAccess;

    @Shadow
    private StructureTemplateManager structureTemplateManager;

    @Shadow
    private ChunkGenerator chunkGenerator;

    @Shadow
    private RandomState randomState;

    @Shadow
    private LevelHeightAccessor heightAccessor;

    @Shadow
    private BiomeSource biomeSource;

    @Shadow
    private long seed;

    @Inject(method = "checkStart", at = @At("RETURN"), cancellable = true)
    private void modifyCheckStart(
            ChunkPos pos,
            Structure structure,
            StructurePlacement placement,
            boolean requireUnreferenced,
            CallbackInfoReturnable<StructureCheckResult> cir
    ) {
        if (!ProgressionSettings.isEnabled()) return;

        Registry<Structure> reg = this.registryAccess.lookupOrThrow(Registries.STRUCTURE);
        Identifier id = reg.getKey(structure);
        if (id == null) return;

        StructureGroup group = StructureGroup.fromStructureId(id);
        double dist = Math.sqrt(
            (double)pos.getMiddleBlockX() * pos.getMiddleBlockX() +
            (double)pos.getMiddleBlockZ() * pos.getMiddleBlockZ()
        );
        float mult = ProgressionSettings.getEffectiveMultiplier(group, dist);

        if (mult <= 1.0f) return;

        StructureCheckResult original = cir.getReturnValue();
        if (original == StructureCheckResult.START_NOT_PRESENT) {
            float extraChance = 1.0f - 1.0f / mult;
            if (ThreadLocalRandom.current().nextFloat() < extraChance) {
                if (structure.findValidGenerationPoint(
                        new Structure.GenerationContext(
                                this.registryAccess,
                                this.chunkGenerator,
                                this.biomeSource,
                                this.randomState,
                                this.structureTemplateManager,
                                this.seed,
                                pos,
                                this.heightAccessor,
                                structure.biomes()::contains
                        )
                ).isPresent()) {
                    cir.setReturnValue(StructureCheckResult.CHUNK_LOAD_NEEDED);
                    ExampleMod.LOGGER.debug("FORCE {} at {} (mult={})", structure, pos, mult);
                }
            }
        }
    }
}
