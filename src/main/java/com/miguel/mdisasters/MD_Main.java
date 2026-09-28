package com.miguel.mdisasters;

import com.miguel.mdisasters.config.MD_Config;
import com.miguel.mdisasters.init.worldgen.biome.MD_TerraBlender;
import com.miguel.mdisasters.init.worldgen.biome.surface.MD_Surface;
import com.miguel.mdisasters.init.blocks.MD_Blocks;
import com.miguel.mdisasters.init.worldgen.MD_WorldGenProvider;
import com.miguel.mdisasters.init.entities.MD_Entities;
import com.miguel.mdisasters.init.items.MD_Items;
import com.miguel.mdisasters.init.sounds.MD_Sounds;
import com.miguel.mdisasters.init.structure.MD_Structures;
import com.miguel.mdisasters.init.worldgen.MD_Features;
import com.miguel.mdisasters.render.MD_EmptyRenderer;
import com.miguel.mdisasters.render.MD_MeteorRenderer;
import com.miguel.mdisasters.render.MD_TornadoRenderer;
import com.miguel.mdisasters.tabs.MD_Tab;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import terrablender.api.SurfaceRuleManager;

import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MD_Main.MODID)
public class MD_Main
{
    public static final String MODID = "mdisasters";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MD_Main(FMLJavaModLoadingContext event)
    {
        IEventBus modEventBus = event.getModEventBus();

        MD_Tab.register(modEventBus);
        MD_Items.register(modEventBus);
        MD_Blocks.register(modEventBus);
        MD_Entities.register(modEventBus);
        MD_Sounds.register(modEventBus);
        MD_Structures.register(modEventBus);
        MD_Features.register(modEventBus);
        MD_TerraBlender.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MD_Config.SPEC);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }



    private void commonSetup(final FMLCommonSetupEvent event)
    {
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MODID, MD_Surface.makeRules());
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS)
        {
            event.accept(MD_Blocks.VOLCANO_BLOCK);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO FROM SERVER STARTING");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class DataGenerators {
        @SubscribeEvent
        public static void gatherData(GatherDataEvent event) {
            DataGenerator generator = event.getGenerator();
            PackOutput packOutput = generator.getPackOutput();
            CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

            generator.addProvider(event.includeServer(), new MD_WorldGenProvider(packOutput, lookupProvider));
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            // Reemplaza TornadoRenderer::new por tu clase de renderizado o un NoopRenderer provisional
            event.registerEntityRenderer(MD_Entities.TORNADO.get(), MD_TornadoRenderer::new);
            event.registerEntityRenderer(MD_Entities.METEOR.get(), MD_MeteorRenderer::new);
            event.registerEntityRenderer(MD_Entities.TSUNAMI.get(), MD_EmptyRenderer::new);
            event.registerEntityRenderer(MD_Entities.TSUNAMI_LAVA.get(), MD_EmptyRenderer::new);
            event.registerEntityRenderer(MD_Entities.VOLCANO.get(), MD_EmptyRenderer::new);
            event.registerEntityRenderer(MD_Entities.FLOOD.get(), MD_EmptyRenderer::new);
            event.registerEntityRenderer(MD_Entities.EARTHQUAKE.get(), MD_EmptyRenderer::new);
        }

    }


}