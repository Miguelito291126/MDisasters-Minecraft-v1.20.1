package com.miguel.mdisasters.objects.items;

import com.miguel.mdisasters.init.entities.MD_Entities;
import com.miguel.mdisasters.objects.entities.MD_Meteor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class MD_MeteorSpawn extends Item {

    public MD_MeteorSpawn(Properties properties) {
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

            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockPos pos = blockHit.getBlockPos();

            MD_Meteor mdMeteor = MD_Entities.METEOR.get().create(level);

            if (mdMeteor != null) {

                // Aparecer delante del jugador
                double distance = 5.0D;

                double yaw = Math.toRadians(player.getYRot());

                double x = pos.getX() - Math.sin(yaw) * distance;
                double y = pos.getY() + 20.0D;
                double z = pos.getZ() + Math.cos(yaw) * distance;

                mdMeteor.moveTo(
                        x,
                        y,
                        z,
                        player.getYRot(),
                        0.0F
                );

                // Añadir el meteorito al mundo
                level.addFreshEntity(mdMeteor);
            }
        }

        return InteractionResultHolder.sidedSuccess(
                stack,
                level.isClientSide()
        );
    }
}