package com.miguel.mdisasters.init.blocks;

import com.miguel.mdisasters.MD_Main;
import com.miguel.mdisasters.init.items.MD_Items;
import com.miguel.mdisasters.objects.blocks.MD_VolcanoBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class MD_Blocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(
                    ForgeRegistries.BLOCKS,
                    MD_Main.MODID
            );

    public static final RegistryObject<Block> VOLCANO_BLOCK =
            registerBlock(
                    "volcano_block",
                    () -> new MD_VolcanoBlock(
                            BlockBehaviour.Properties.copy(Blocks.STONE)
                    )
            );

    public static <T extends Block> RegistryObject<T> registerBlock(
            String name,
            Supplier<T> block
    ) {
        // 1. Primero registramos el bloque y guardamos el RegistryObject
        RegistryObject<T> registeredBlock = BLOCKS.register(name, block);

        // 2. Luego registramos el item usando el RegistryObject, NO el Supplier
        registerBlockItem(name, registeredBlock);

        return registeredBlock;
    }

    // Cambiamos el parámetro de Supplier<T> a RegistryObject<T>
    public static <T extends Block> RegistryObject<Item> registerBlockItem(
            String name,
            RegistryObject<T> block
    ) {
        return MD_Items.ITEMS.register(
                name,
                () -> new BlockItem(
                        block.get(), // Ahora .get() funcionará correctamente durante el registro
                        new Item.Properties()
                )
        );
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}