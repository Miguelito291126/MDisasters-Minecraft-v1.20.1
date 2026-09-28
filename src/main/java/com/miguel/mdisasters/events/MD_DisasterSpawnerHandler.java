package com.miguel.mdisasters.events;

import com.miguel.mdisasters.MD_Main;
import com.miguel.mdisasters.config.MD_Config;
import com.miguel.mdisasters.init.entities.MD_Entities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MD_Main.MODID)
public final class MD_DisasterSpawnerHandler {
    private MD_DisasterSpawnerHandler() {
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !MD_Config.ENABLE_SPAWNER.get()) {
            return;
        }

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (!(player.level() instanceof ServerLevel level)
                    || level.dimension() != Level.OVERWORLD
                    || level.random.nextInt(MD_Config.SPAWNER_CHANCE.get()) != 0) {
                continue;
            }

            spawnRandomDisaster(level, player);
        }
    }

    private static void spawnRandomDisaster(ServerLevel level, ServerPlayer player) {
        double angle = level.random.nextDouble() * Math.PI * 2.0D;
        double distance = 30.0D + level.random.nextInt(31);
        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;
        int blockX = (int) Math.floor(x);
        int blockZ = (int) Math.floor(z);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockX, blockZ);
        Entity disaster;
        MutableComponent message;
        ChatFormatting color;

        switch (level.random.nextInt(6)) {
            case 0 -> {
                disaster = create(level, MD_Entities.METEOR.get());
                y += MD_Config.METEOR_DISTANCE.get();
                message = Component.literal("A meteorite is falling near your location!");
                color = ChatFormatting.RED;
            }
            case 1 -> {
                disaster = create(level, MD_Entities.TORNADO.get());
                message = Component.literal("A tornado has formed nearby!");
                color = ChatFormatting.DARK_GRAY;
            }
            case 2 -> {
                disaster = create(level, MD_Entities.TSUNAMI.get());
                message = Component.literal("A tsunami is approaching!");
                color = ChatFormatting.BLUE;
            }
            case 3 -> {
                disaster = create(level, MD_Entities.FLOOD.get());
                message = Component.literal("A flood is approaching!");
                color = ChatFormatting.DARK_BLUE;
            }
            case 4 -> {
                disaster = create(level, MD_Entities.EARTHQUAKE.get());
                message = Component.literal("An earthquake has appeared!");
                color = ChatFormatting.DARK_RED;
            }
            default -> {
                disaster = create(level, MD_Entities.TSUNAMI_LAVA.get());
                message = Component.literal("A lava tsunami is approaching!");
                color = ChatFormatting.GOLD;
            }
        }

        if (disaster == null) {
            return;
        }

        disaster.moveTo(x, y, z, player.getYRot(), 0.0F);
        level.addFreshEntity(disaster);
        player.sendSystemMessage(message.withStyle(color));
    }

    private static <T extends Entity> T create(ServerLevel level, net.minecraft.world.entity.EntityType<T> type) {
        return type.create(level);
    }
}
