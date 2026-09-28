package com.miguel.mdisasters.tabs;

import com.miguel.mdisasters.MD_Main;
import com.miguel.mdisasters.init.blocks.MD_Blocks;
import com.miguel.mdisasters.init.items.MD_Items;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MD_Tab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MD_Main.MODID);

    public static final RegistryObject<CreativeModeTab> MDISASTERS_TAB = CREATIVE_MODE_TAB.register("mdisasters_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(MD_Blocks.VOLCANO_BLOCK.get()))
                    .title(Component.translatable("creativetab.mdisasters_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(MD_Blocks.VOLCANO_BLOCK.get());
                        pOutput.accept(MD_Items.METEOR_SPAWN.get());
                        pOutput.accept(MD_Items.VOLCANO_SPAWN.get());
                        pOutput.accept(MD_Items.TSUNAMI_SPAWN.get());
                        pOutput.accept(MD_Items.TORNADO_SPAWN.get());
                        pOutput.accept(MD_Items.EARTHQUAKE_SPAWN.get());
                        pOutput.accept(MD_Items.FLOOD_SPAWN.get());
                        pOutput.accept(MD_Items.TSUNAMI_LAVA_SPAWN.get());
                    })
                    .build()
    );

    public static void register(final IEventBus modBus) {
        CREATIVE_MODE_TAB.register(modBus);
    }
}
