package com.miguel.mdisasters.objects.items;

import com.miguel.mdisasters.init.entities.MD_Entities;
import com.miguel.mdisasters.objects.entities.MD_Tsunami;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class MD_TsunamiSpawn extends Item {

    public MD_TsunamiSpawn(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {

        ItemStack stack = player.getItemInHand(hand);

        // Obtener lo que está mirando el jugador
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

            if (!level.isClientSide()) {

                MD_Tsunami mdTsunami =
                        MD_Entities.TSUNAMI
                                .get()
                                .create(level);

                if (mdTsunami != null) {

                    // Aparecer en el bloque seleccionado
                    mdTsunami.moveTo(
                            pos.getX() + 0.5,
                            pos.getY() + 1.0,
                            pos.getZ() + 0.5,
                            player.getYRot(),
                            0.0F
                    );

                    level.addFreshEntity(mdTsunami);
                }
            }
        }

        return InteractionResultHolder.sidedSuccess(
                stack,
                level.isClientSide()
        );
    }
}