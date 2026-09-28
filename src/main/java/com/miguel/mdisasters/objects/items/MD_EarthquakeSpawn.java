package com.miguel.mdisasters.objects.items;

import com.miguel.mdisasters.init.entities.MD_Entities;
import com.miguel.mdisasters.objects.entities.MD_Earthquake;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class MD_EarthquakeSpawn extends Item {
    public MD_EarthquakeSpawn(Properties properties) {

        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        // Solo crear el tornado en el servidor
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

            MD_Earthquake MD_Earthquake = MD_Entities.EARTHQUAKE.get().create(level);

            if (MD_Earthquake != null) {

                // Aparece en la posición del jugador
                MD_Earthquake.moveTo(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        player.getYRot(),
                        0.0F
                );

                // Añadir la entidad al mundo
                level.addFreshEntity(MD_Earthquake);
            }
        }

        return InteractionResultHolder.sidedSuccess(
                stack,
                level.isClientSide()
        );
    }
}
