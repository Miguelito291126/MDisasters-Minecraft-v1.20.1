package com.miguel.mdisasters.objects.worldgen;

import com.miguel.mdisasters.MD_Main;
import com.miguel.mdisasters.init.entities.MD_Entities;
import com.miguel.mdisasters.objects.entities.MD_Volcano;
import com.miguel.mdisasters.objects.structure.MD_VolcanoStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class MD_VolcanoFeature extends Feature<NoneFeatureConfiguration> {
    public MD_VolcanoFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        int centerX = (origin.getX() >> 4 << 4) + 8;
        int centerZ = (origin.getZ() >> 4 << 4) + 8;
        int surfaceY = level.getHeight(
                net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG,
                centerX,
                centerZ
        );

        BlockPos center = new BlockPos(centerX, surfaceY, centerZ);

        boolean volcanoGenerated = MD_VolcanoStructure.generate(level, center, context.random());
        if (volcanoGenerated) {
            MD_VolcanoStructure.spawnVolcanoEntity(level, center);
            MD_Main.LOGGER.info("Volcano generated at: " + center);
        }
        return volcanoGenerated;
    }
}