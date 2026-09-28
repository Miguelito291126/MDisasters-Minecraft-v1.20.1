package com.miguel.mdisasters.init.worldgen.biome.surface;

import com.miguel.mdisasters.init.worldgen.biome.MD_Biomes;
import com.miguel.mdisasters.init.blocks.MD_Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public final class MD_Surface {

    private static final SurfaceRules.RuleSource MAGMA_BLOCK = makeStateRule(Blocks.MAGMA_BLOCK);
    private static final SurfaceRules.RuleSource VOLCANO_BLOCK = makeStateRule(MD_Blocks.VOLCANO_BLOCK.get());

    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.RuleSource volcanoBiomeSurface = SurfaceRules.ifTrue(
                SurfaceRules.isBiome(MD_Biomes.VOLCANO_BIOME),
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(
                                SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                                VOLCANO_BLOCK
                        ),
                        SurfaceRules.ifTrue(
                                SurfaceRules.stoneDepthCheck(3, false, CaveSurface.FLOOR),
                                MAGMA_BLOCK
                        )
                )
        );

        return volcanoBiomeSurface;
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
