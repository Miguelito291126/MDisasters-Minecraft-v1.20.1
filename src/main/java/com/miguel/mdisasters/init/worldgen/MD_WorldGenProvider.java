package com.miguel.mdisasters.init.worldgen;

import com.miguel.mdisasters.MD_Main;
import com.miguel.mdisasters.init.worldgen.biome.MD_Biomes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class MD_WorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, MD_WorldGen::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, MD_WorldGen::bootstrapPlaced)
            .add(Registries.BIOME, MD_Biomes::boostrap);


    public MD_WorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(MD_Main.MODID));
    }
}