package com.miguel.mdisasters.objects.worldgen;

import com.miguel.mdisasters.init.blocks.MD_Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class MD_VolcanoSurfaceFeature extends Feature<NoneFeatureConfiguration> {
    public MD_VolcanoSurfaceFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        int chunkOriginX = origin.getX() >> 4 << 4;
        int chunkOriginZ = origin.getZ() >> 4 << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkOriginX + x;
                int worldZ = chunkOriginZ + z;
                int surfaceY = level.getHeight(
                        Heightmap.Types.WORLD_SURFACE_WG,
                        worldX,
                        worldZ
                ) - 1;

                BlockPos surface = new BlockPos(worldX, surfaceY, worldZ);
                BlockState surfaceState = level.getBlockState(surface);
                
                // Verificar que no sea agua ni lava
                if (surfaceState.isAir() || surfaceState.is(Blocks.WATER) || surfaceState.is(Blocks.LAVA)) {
                    continue;
                }
                
                // Verificar altura máxima para evitar generar en el océano
                int maxBuildHeight = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, worldX, worldZ);
                if (surfaceY >= maxBuildHeight) {
                    // Generar solo si hay agua cerca (mínimo 16 bloques de distancia)
                    boolean isWaterNearby = false;
                    int checkRadius = 8; // 16 bloques de radio en cada dirección
                    
                    for (int dy = -checkRadius; dy <= checkRadius; dy++) {
                        for (int dx = -checkRadius; dx <= checkRadius; dx++) {
                            for (int dz = -checkRadius; dz <= checkRadius; dz++) {
                                int distSq = dx * dx + dy * dy + dz * dz;
                                if (distSq > 100) continue; // Más de 16 bloques
                                
                                BlockPos checkPos = new BlockPos(worldX + dx, surfaceY + dy, worldZ + dz);
                                BlockState blockState = level.getBlockState(checkPos);
                                if (blockState.is(Blocks.WATER)) {
                                    isWaterNearby = true;
                                    break;
                                }
                            }
                        }
                    }
                    
                    // Solo generar si NO hay agua cerca y estamos en terreno alto
                    if (!isWaterNearby && surfaceY >= maxBuildHeight - 5) {
                        level.setBlock(
                                surface,
                                MD_Blocks.VOLCANO_BLOCK.get().defaultBlockState(),
                                3
                        );

                        for (int depth = 1; depth <= 3; depth++) {
                            level.setBlock(
                                    surface.below(depth),
                                    Blocks.MAGMA_BLOCK.defaultBlockState(),
                                    3
                            );
                        }
                    }
                } else if (surfaceY >= maxBuildHeight - 20) {
                    // Generar en terreno alto pero no extremo
                    level.setBlock(
                            surface,
                            MD_Blocks.VOLCANO_BLOCK.get().defaultBlockState(),
                            3
                    );

                    for (int depth = 1; depth <= 3; depth++) {
                        level.setBlock(
                                surface.below(depth),
                                Blocks.MAGMA_BLOCK.defaultBlockState(),
                                3
                        );
                    }
                }
            }
        }

        int lavaPools = 2 + random.nextInt(2);
        for (int i = 0; i < lavaPools; i++) {
            int poolX = origin.getX() + random.nextInt(16);
            int poolZ = origin.getZ() + random.nextInt(16);
            int surfaceY = level.getHeight(
                    Heightmap.Types.WORLD_SURFACE_WG,
                    poolX,
                    poolZ
            ) - 1;
            generateLavaPool(level, new BlockPos(poolX, surfaceY, poolZ), random);
        }

        return true;
    }

    private void generateLavaPool(WorldGenLevel level, BlockPos surface, RandomSource random) {
        int radius = 3 + random.nextInt(3);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius || random.nextInt(4) == 0) {
                    continue;
                }

                int targetX = surface.getX() + x;
                int targetZ = surface.getZ() + z;
                if ((targetX >> 4) != (surface.getX() >> 4)
                        || (targetZ >> 4) != (surface.getZ() >> 4)) {
                    continue;
                }

                BlockPos target = new BlockPos(targetX, surface.getY() - 1, targetZ);
                if (!level.getBlockState(target).isAir()) {
                    level.setBlock(target, Blocks.LAVA.defaultBlockState(), 3);
                    level.setBlock(target.above(), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }
}
