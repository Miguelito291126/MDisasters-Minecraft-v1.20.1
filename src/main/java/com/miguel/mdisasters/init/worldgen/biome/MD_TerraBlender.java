package com.miguel.mdisasters.init.worldgen.biome;

import com.miguel.mdisasters.MD_Main;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;
import terrablender.core.TerraBlender;

public final class MD_TerraBlender {
    public static void register() {
        // Verificar si TerraBlender está disponible antes de registrarse
        if (TerraBlender.CONFIG == null) {
            return; // TerraBlender no se ha inicializado, saltar registro
        }
        
        Regions.register(new MD_OverworldRegion(new ResourceLocation(MD_Main.MODID, "overworld"), 10));
    }
}
