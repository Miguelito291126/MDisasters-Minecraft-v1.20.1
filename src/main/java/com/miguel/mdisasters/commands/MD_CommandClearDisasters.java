package com.miguel.mdisasters.commands;

import com.miguel.mdisasters.MD_Main;
import com.miguel.mdisasters.objects.entities.MD_Earthquake;
import com.miguel.mdisasters.objects.entities.MD_Flood;
import com.miguel.mdisasters.objects.entities.MD_Meteor;
import com.miguel.mdisasters.objects.entities.MD_Tornado;
import com.miguel.mdisasters.objects.entities.MD_Tsunami;
import com.miguel.mdisasters.objects.entities.MD_TsunamiLava;
import com.miguel.mdisasters.objects.entities.MD_Volcano;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MD_Main.MODID)
public final class MD_CommandClearDisasters {
    private MD_CommandClearDisasters() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("mdisasters")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("clear")
                                .executes(context -> clear(context.getSource().getServer().getAllLevels(),
                                        context.getSource())))
        );
    }

    private static int clear(Iterable<ServerLevel> levels, net.minecraft.commands.CommandSourceStack source) {
        int count = 0;
        for (ServerLevel level : levels) {
            for (Entity entity : level.getAllEntities()) {
                if (isDisaster(entity)) {
                    entity.discard();
                    count++;
                }
            }
        }

        int removed = count;
        source.sendSuccess(() -> Component.literal(
                removed + " disasters have been eliminated from the world."
        ), true);
        return count;
    }

    private static boolean isDisaster(Entity entity) {
        return entity instanceof MD_Tornado
                || entity instanceof MD_Tsunami
                || entity instanceof MD_TsunamiLava
                || entity instanceof MD_Flood
                || entity instanceof MD_Meteor
                || entity instanceof MD_Earthquake;
    }
}
