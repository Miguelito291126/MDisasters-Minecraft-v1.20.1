package com.miguel.mdisasters.init.sounds;

import com.miguel.mdisasters.MD_Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class MD_Sounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MD_Main.MODID);

    public static final RegistryObject<SoundEvent> EARTHQUAKE_SOUND =
            RegisterSounds("earthquake_sound");

    public static final RegistryObject<SoundEvent> TORNADO_SOUND =
            RegisterSounds("tornado_sound");



    private static RegistryObject<SoundEvent> RegisterSounds(
            String name
    ) {
        return SOUNDS.register(
                name,
                () -> SoundEvent.createVariableRangeEvent(
                        new ResourceLocation(MD_Main.MODID, name)
                )
        );
    }

    public static void register(final IEventBus modBus) {
        SOUNDS.register(modBus);
    }

}

