package com.miguel.mdisasters.objects.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MD_VolcanoBlock extends Block {

    public MD_VolcanoBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        // En 1.20.1 comprobamos inmunidad al fuego mediante fireImmune()
        if (!entity.fireImmune() && entity instanceof LivingEntity) {
            entity.hurt(level.damageSources().hotFloor(), 1.0F);
        }
        super.stepOn(level, pos, state, entity);
    }
}