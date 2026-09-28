package com.miguel.mdisasters.objects.items;

import com.miguel.mdisasters.objects.structure.MD_VolcanoStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class MD_VolcanoSpawn extends Item {
    public MD_VolcanoSpawn(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        HitResult hit = player.pick(
                100.0D,
                0.0F,
                false
        );

        // Comprobar que está mirando un bloque
        if (hit.getType() == HitResult.Type.BLOCK) {

            BlockHitResult blockHit =
                    (BlockHitResult) hit;

            BlockPos pos =
                    blockHit.getBlockPos();

            // Las estructuras solo se colocan en el servidor.
            if (!level.isClientSide()) {
                BlockPos origin = pos.above();
                MD_VolcanoStructure.generate((WorldGenLevel) level, origin);
                MD_VolcanoStructure.spawnVolcanoEntity((WorldGenLevel) level, origin);
            }
        }

        return InteractionResultHolder.sidedSuccess(
                stack,
                level.isClientSide()
        );
    }
}
