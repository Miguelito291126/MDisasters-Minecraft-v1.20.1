package com.miguel.mdisasters.init.worldgen;

import com.miguel.mdisasters.MD_Main;
import com.miguel.mdisasters.objects.worldgen.MD_VolcanoFeature;
import com.miguel.mdisasters.objects.worldgen.MD_VolcanoSurfaceFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MD_Features {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, MD_Main.MODID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> VOLCANO =
            FEATURES.register("volcano", MD_VolcanoFeature::new);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> VOLCANO_SURFACE =
            FEATURES.register("volcano_surface", MD_VolcanoSurfaceFeature::new);

    public static void register(IEventBus modBus) {
        FEATURES.register(modBus);
    }
}
