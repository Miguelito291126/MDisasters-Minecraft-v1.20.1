package com.miguel.mdisasters.objects.items;

import com.miguel.mdisasters.init.entities.MD_Entities;
import com.miguel.mdisasters.objects.entities.MD_TsunamiLava;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class MD_LavaTsunamiSpawn extends Item {
    public MD_LavaTsunamiSpawn(Properties properties) {
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

            MD_TsunamiLava tsunami = MD_Entities.TSUNAMI_LAVA.get().create(level);

            if (tsunami != null) {

                // Aparece en la posición del jugador
                tsunami.moveTo(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        player.getYRot(),
                        0.0F
                );

                // Añadir la entidad al mundo
                level.addFreshEntity(tsunami);
            }
        }

        return InteractionResultHolder.sidedSuccess(
                stack,
                level.isClientSide()
        );
    }
}
