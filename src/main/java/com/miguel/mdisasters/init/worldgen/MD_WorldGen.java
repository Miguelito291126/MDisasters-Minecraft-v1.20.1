package com.miguel.mdisasters.init.worldgen;

import com.miguel.mdisasters.MD_Main;
import com.miguel.mdisasters.objects.worldgen.MD_VolcanoFeature;
import com.miguel.mdisasters.objects.worldgen.MD_VolcanoSurfaceFeature;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

import java.util.List;

import static com.miguel.mdisasters.init.worldgen.MD_Features.VOLCANO;
import static com.miguel.mdisasters.init.worldgen.MD_Features.VOLCANO_SURFACE;

public final class MD_WorldGen {
    public static final ResourceKey<ConfiguredFeature<?, ?>> VOLCANO_CONFIGURED =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(MD_Main.MODID, "volcano"));

    public static final ResourceKey<PlacedFeature> VOLCANO_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(MD_Main.MODID, "volcano"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> VOLCANO_SURFACE_CONFIGURED =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    new ResourceLocation(MD_Main.MODID, "volcano_surface"));

    public static final ResourceKey<PlacedFeature> VOLCANO_SURFACE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    new ResourceLocation(MD_Main.MODID, "volcano_surface"));

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> volcano_configured = configuredFeatures.getOrThrow(VOLCANO_CONFIGURED);
        Holder<ConfiguredFeature<?, ?>> volcanoSurface_configured = configuredFeatures.getOrThrow(VOLCANO_SURFACE_CONFIGURED);

        // Volcano en terreno sólido (no en agua) - altura por defecto para asegurar superficie sólida
        context.register(
                VOLCANO_PLACED,
                new PlacedFeature(
                        volcano_configured,
                        List.of(
                                RarityFilter.onAverageOnceEvery(32),
                                CountPlacement.of(1),
                                InSquarePlacement.spread(),
                                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG) // ✅ API correcta - altura por defecto (64 bloques)
                        )
                )
        );

        // Volcano surface en terreno sólido también
        context.register(
                VOLCANO_SURFACE_PLACED,
                new PlacedFeature(
                        volcanoSurface_configured,
                        List.of(
                                CountPlacement.of(1),
                                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG) // ✅ API correcta - altura por defecto (64 bloques)
                        )
                )
        );

    }

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(
                VOLCANO_CONFIGURED,
                new ConfiguredFeature(VOLCANO.get(), new NoneFeatureConfiguration())
        );


        context.register(
                VOLCANO_SURFACE_CONFIGURED,
                new ConfiguredFeature(VOLCANO_SURFACE.get(), new NoneFeatureConfiguration())
        );

    }
}
