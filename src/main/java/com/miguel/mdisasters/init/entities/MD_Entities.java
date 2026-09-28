package com.miguel.mdisasters.init.entities;

import com.miguel.mdisasters.MD_Main;
import com.miguel.mdisasters.init.sounds.MD_Sounds;
import com.miguel.mdisasters.objects.entities.*;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class MD_Entities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(
                    ForgeRegistries.ENTITY_TYPES,
                    MD_Main.MODID
            );

    public static final RegistryObject<EntityType<MD_Tornado>> TORNADO =
            RegisterEntity(
                    "tornado",
                    () -> EntityType.Builder
                            .of(MD_Tornado::new, MobCategory.MISC)
                            .sized(3.0F, 10.0F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build("tornado")
            );

    public static final RegistryObject<EntityType<MD_Tsunami>> TSUNAMI =
            RegisterEntity(
                    "tsunami",
                    () -> EntityType.Builder
                            .of(MD_Tsunami::new, MobCategory.MISC)
                            .sized(3.0F, 10.0F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build("tsunami")
            );

    public static final RegistryObject<EntityType<MD_TsunamiLava>> TSUNAMI_LAVA =
            RegisterEntity(
                    "tsunami_lava",
                    () -> EntityType.Builder
                            .of(MD_TsunamiLava::new, MobCategory.MISC)
                            .sized(3.0F, 10.0F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build("tsunami_lava")
            );

    public static final RegistryObject<EntityType<MD_Volcano>> VOLCANO =
            RegisterEntity(
                    "volcano",
                    () -> EntityType.Builder
                            .of(MD_Volcano::new, MobCategory.MISC)
                            .sized(3.0F, 10.0F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build("volcano")
            );

    public static final RegistryObject<EntityType<MD_Earthquake>> EARTHQUAKE =
            RegisterEntity(
                    "earthquake",
                    () -> EntityType.Builder
                            .of(MD_Earthquake::new, MobCategory.MISC)
                            .sized(3.0F, 10.0F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build("earthquake")
            );

    public static final RegistryObject<EntityType<MD_Flood>> FLOOD =
            RegisterEntity(
                    "flood",
                    () -> EntityType.Builder
                            .of(MD_Flood::new, MobCategory.MISC)
                            .sized(3.0F, 10.0F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build("flood")
            );

    public static final RegistryObject<EntityType<MD_Meteor>> METEOR =
            RegisterEntity(
                    "meteor",
                    () -> EntityType.Builder
                            .of(MD_Meteor::new, MobCategory.MISC)
                            .sized(3.0F, 10.0F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build("meteor")
            );


    private static <T extends Entity> RegistryObject<EntityType<T>> RegisterEntity(
            String name,
            Supplier<EntityType<T>> entity
    ) {
        return ENTITIES.register(name, entity);
    }

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}