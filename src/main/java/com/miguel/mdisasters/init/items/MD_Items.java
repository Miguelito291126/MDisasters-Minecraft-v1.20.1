package com.miguel.mdisasters.init.items;

import com.miguel.mdisasters.MD_Main;
import com.miguel.mdisasters.objects.items.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class MD_Items {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MD_Main.MODID);

    public static final RegistryObject<Item> VOLCANO_SPAWN = RegisterItem(
            "volcano_spawn",
            () -> new MD_VolcanoSpawn(new Item.Properties())
    );

    public static final RegistryObject<Item> TORNADO_SPAWN = RegisterItem(
            "tornado_spawn",
            () -> new MD_TornadoSpawn(new Item.Properties())
    );

    public static final RegistryObject<Item> TSUNAMI_SPAWN = RegisterItem(
            "tsunami_spawn",
            () -> new MD_TsunamiSpawn(new Item.Properties())
    );

    public static final RegistryObject<Item> METEOR_SPAWN = RegisterItem(
            "meteors_spawn",
            () -> new MD_MeteorSpawn(new Item.Properties())
    );

    public static final RegistryObject<Item> EARTHQUAKE_SPAWN = RegisterItem(
            "earthquake_spawn",
            () -> new MD_EarthquakeSpawn(new Item.Properties())
    );

    public static final RegistryObject<Item> FLOOD_SPAWN = RegisterItem(
            "flood_spawn",
            () -> new MD_FloodSpawn(new Item.Properties())
    );

    public static final RegistryObject<Item> TSUNAMI_LAVA_SPAWN = RegisterItem(
            "tsunami_lava_spawn",
            () -> new MD_LavaTsunamiSpawn(new Item.Properties())
    );

    public static <T extends Item> RegistryObject<T> RegisterItem(
            String name,
            Supplier<T> item
    ) {
        return ITEMS.register(name, item);
    }

    public static void register(final IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
