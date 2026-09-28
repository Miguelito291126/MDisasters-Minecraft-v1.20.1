package com.miguel.mdisasters.objects.structure;

import com.miguel.mdisasters.init.blocks.MD_Blocks;
import com.miguel.mdisasters.init.entities.MD_Entities;
import com.miguel.mdisasters.objects.entities.MD_Volcano;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.phys.AABB;

public class MD_VolcanoStructure extends StructurePiece {
    public MD_VolcanoStructure(CompoundTag pos) {
        super(StructurePieceType.MINE_SHAFT_ROOM, 0, new BoundingBox(
                pos.getInt("x"),
                pos.getInt("y"),
                pos.getInt("z"),
                pos.getInt("x") + 5,
                pos.getInt("y") + 6,
                pos.getInt("z") + 5
        ));
        this.setOrientation(null);
    }
    public static void spawnVolcanoEntity(WorldGenLevel level, BlockPos structurePos) {
        BlockPos entityPos = findVolcanoTop(level, structurePos);
        AABB searchBox = new AABB(entityPos).inflate(2.0D);

        if (!level.getEntitiesOfClass(MD_Volcano.class, searchBox).isEmpty()) {
            return;
        }

        MD_Volcano volcano = MD_Entities.VOLCANO.get().create(level.getLevel());
        if (volcano == null) {
            return;
        }

        volcano.moveTo(
                entityPos.getX() + 0.5D,
                entityPos.getY(),
                entityPos.getZ() + 0.5D,
                0.0F,
                0.0F
        );
        level.addFreshEntity(volcano);
    }

    private static BlockPos findVolcanoTop(WorldGenLevel level, BlockPos base) {
        BlockPos.MutableBlockPos cursor = base.mutable();

        while (cursor.getY() < level.getMaxBuildHeight()
                && !level.getBlockState(cursor).isAir()) {
            cursor.move(0, 1, 0);
        }

        return cursor.immutable();
    }

    public static boolean generate(WorldGenLevel level, BlockPos center, RandomSource random) {
        int height = 16 + random.nextInt(9);
        int maxRadius = 6 + random.nextInt(3);

        for (int y = 0; y <= height; y++) {
            double progress = (double) y / height;
            double radiusFactor = Math.pow(1.0D - progress, 1.5D);
            double currentRadius = maxRadius * radiusFactor;
            double craterRadius = 2.0D + progress * 1.5D;

            for (int x = (int) -currentRadius - 2; x <= currentRadius + 2; x++) {
                for (int z = (int) -currentRadius - 2; z <= currentRadius + 2; z++) {
                    double distance = Math.sqrt(x * x + z * z);
                    double noise = (random.nextDouble() - 0.5D) * 1.5D;

                    if (distance + noise > currentRadius) {
                        continue;
                    }

                    BlockPos target = center.offset(x, y, z);
                    BlockState state = distance <= craterRadius && y > 2
                            ? Blocks.LAVA.defaultBlockState()
                            : getRandomVolcanicBlock(random);
                    level.setBlock(target, state, 3);
                }
            }
        }

        return true;
    }

    public static boolean generate(WorldGenLevel level, BlockPos center) {
        return generate(level, center, RandomSource.create());
    }

    private static BlockState getRandomVolcanicBlock(RandomSource random) {
        int chance = random.nextInt(100);
        if (chance < 50) {
            return MD_Blocks.VOLCANO_BLOCK.get().defaultBlockState();
        } else if (chance < 75) {
            return Blocks.OBSIDIAN.defaultBlockState();
        } else if (chance < 90) {
            return Blocks.MAGMA_BLOCK.defaultBlockState();
        }
        return Blocks.GRAVEL.defaultBlockState();
    }


    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext pContext, CompoundTag pTag) {
        pTag.putInt("x", this.boundingBox.minX());
        pTag.putInt("y", this.boundingBox.minY());
        pTag.putInt("z", this.boundingBox.minZ());
    }

    @Override
    public void postProcess(WorldGenLevel pLevel, StructureManager pStructureManager, ChunkGenerator pGenerator, RandomSource pRandom, BoundingBox pBox, ChunkPos pChunkPos, BlockPos pPos) {
        generate(pLevel, pPos, pRandom);
        spawnVolcanoEntity(pLevel, pPos);
    }


}
