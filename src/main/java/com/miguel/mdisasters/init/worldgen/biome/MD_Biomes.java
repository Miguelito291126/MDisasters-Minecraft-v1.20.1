package com.miguel.mdisasters.init.worldgen.biome;

import com.miguel.mdisasters.MD_Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import com.miguel.mdisasters.init.worldgen.MD_WorldGen;

public final class MD_Biomes {

    public static final ResourceKey<Biome> VOLCANO_BIOME = ResourceKey.create(Registries.BIOME,
        new ResourceLocation(MD_Main.MODID, "volcano_biome"));

    public static void boostrap(BootstapContext<Biome> context) {
        context.register(VOLCANO_BIOME, VolcanoBiome(context));
    }

    public static Biome VolcanoBiome(BootstapContext<Biome> context) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        spawnBuilder.addSpawn(MobCategory.MISC, new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 5, 4, 4));

        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(
            context.lookup(Registries.PLACED_FEATURE),
            context.lookup(Registries.CONFIGURED_CARVER)
        );

        BiomeDefaultFeatures.addDefaultCrystalFormations(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(biomeBuilder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);

        biomeBuilder.addFeature(
            GenerationStep.Decoration.SURFACE_STRUCTURES,
            context.lookup(Registries.PLACED_FEATURE)
            .getOrThrow(MD_WorldGen.VOLCANO_PLACED)
        );
        biomeBuilder.addFeature(
            GenerationStep.Decoration.SURFACE_STRUCTURES,
            context.lookup(Registries.PLACED_FEATURE)
            .getOrThrow(MD_WorldGen.VOLCANO_SURFACE_PLACED)
        );

        return new Biome.BiomeBuilder()
            .hasPrecipitation(false)  // ✅ NO LLOVE - no genera en agua
            .downfall(0.0f)           // ✅ SIN LLUVIA
            .temperature(2.0f)         // MUY CALIENTE
            .generationSettings(biomeBuilder.build())
            .mobSpawnSettings(spawnBuilder.build())
            .specialEffects(new BiomeSpecialEffects.Builder()
                .waterColor(0xFF9D00)  // ✅ NARANJA BRILLANTE - color del agua volcánica
                .waterFogColor(0x8B2626)  // ✅ ROJO OSCURO - para el agua
                .skyColor(0x787878)      // GRIS - cielo ceniciento
                .grassColorOverride(0x5C4033)  // MARRÓN - tierra volcánica
                .foliageColorOverride(0x2F1B11) // ROJO OSCURO - vegetación nether
                .fogColor(0x787878)      // GRIS
                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                .ambientLoopSound(SoundEvents.AMBIENT_NETHER_WASTES_LOOP)  // ✅ SONIDO NETHER
                .build()
            )
            .build();
    }
}
