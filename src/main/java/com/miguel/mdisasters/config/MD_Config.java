package com.miguel.mdisasters.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MD_Config {
    private MD_Config(ForgeConfigSpec.Builder builder) {
        // implementation omitted for shortness
    }

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue TORNADO_SPEED;
    public static final ForgeConfigSpec.DoubleValue TORNADO_RADIUS;
    public static final ForgeConfigSpec.IntValue TORNADO_DURATION;
    public static final ForgeConfigSpec.IntValue TORNADO_HEIGHT;
    public static final ForgeConfigSpec.IntValue TORNADO_WIDTH;

    public static final ForgeConfigSpec.DoubleValue TSUNAMI_SPEED;
    public static final ForgeConfigSpec.DoubleValue TSUNAMI_WIDTH;
    public static final ForgeConfigSpec.DoubleValue TSUNAMI_HEIGHT;
    public static final ForgeConfigSpec.IntValue TSUNAMI_DURATION;
    public static final ForgeConfigSpec.IntValue TSUNAMI_DEPTH;

    public static final ForgeConfigSpec.IntValue SPAWNER_CHANCE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SPAWNER;

    public static final ForgeConfigSpec.DoubleValue VOLCANO_SPEED;

    public static final ForgeConfigSpec.DoubleValue METEOR_SPEED;
    public static final ForgeConfigSpec.IntValue METEOR_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue METEOR_EXPLOSION_POWER;
    public static final ForgeConfigSpec.DoubleValue METEOR_HEIGHT;
    public static final ForgeConfigSpec.DoubleValue METEOR_WIDTH;

    public static final ForgeConfigSpec.DoubleValue EARTHQUAKE_MAGNITUDE;
    public static final ForgeConfigSpec.IntValue EARTHQUAKE_DURATION;
    public static final ForgeConfigSpec.IntValue EARTHQUAKE_MAX_RADIUS;

    public static final ForgeConfigSpec.DoubleValue FLOOD_SPEED;
    public static final ForgeConfigSpec.DoubleValue FLOOD_MAX_RADIUS;
    public static final ForgeConfigSpec.IntValue FLOOD_HEIGHT;
    public static final ForgeConfigSpec.BooleanValue FLOOD_INFINITE_EXPANSION;

    static {


        BUILDER.push("tornado");
        TORNADO_SPEED = BUILDER.comment("Velocidad de desplazamiento del tornado").defineInRange("speed", 0.15, 0.0, 10.0);
        TORNADO_RADIUS = BUILDER.comment("Radio de impacto del tornado").defineInRange("radius", 6.0, 1.0, 100.0);
        TORNADO_DURATION = BUILDER.comment("Duración en ticks (20 ticks = 1 seg)").defineInRange("duration", 600, 1, 72000);
        TORNADO_HEIGHT = BUILDER.comment("Altura del tornado").defineInRange("height", 30, 5, 150);
        TORNADO_WIDTH = BUILDER.comment("Ancho del tornado").defineInRange("width", 4, 1, 150);
        BUILDER.pop();

        BUILDER.push("tsunami");
        TSUNAMI_SPEED = BUILDER.comment("Velocidad del tsunami").defineInRange("speed", 0.35, 0.0, 10.0);
        TSUNAMI_WIDTH = BUILDER.comment("Ancho de la ola").defineInRange("width", 80.0, 5.0, 200.0);
        TSUNAMI_HEIGHT = BUILDER.comment("Altura de la ola").defineInRange("height", 10.0, 1.0, 50.0);
        TSUNAMI_DURATION = BUILDER.comment("Duración en ticks").defineInRange("duration", 600, 1, 72000);
        TSUNAMI_DEPTH = BUILDER.comment("Grosor de la ola").defineInRange("depth", 2, 1, 10);
        BUILDER.pop();

        BUILDER.push("volcano");
        VOLCANO_SPEED = BUILDER.comment("Velocidad de presión del volcán").defineInRange("speed", 0.05, 0.01, 20.0);
        BUILDER.pop();

        BUILDER.push("spawner");
        SPAWNER_CHANCE = BUILDER.comment("Probabilidad por jugador y tick; menor número significa más frecuente").defineInRange("chance", 12000, 200, 100000);
        ENABLE_SPAWNER = BUILDER.comment("Habilitar el spawner de desastres").define("enable_spawner", true);
        BUILDER.pop();

        BUILDER.push("meteor");
        METEOR_SPEED = BUILDER.comment("Velocidad de caída").defineInRange("speed", 1.0, 0.1, 200.0);
        METEOR_DISTANCE = BUILDER.comment("Distancia de aparición sobre el objetivo").defineInRange("distance", 80, 5, 500);
        METEOR_EXPLOSION_POWER = BUILDER.comment("Potencia de la explosión").defineInRange("explosion_power", 10.0, 0.0, 500.0);
        METEOR_HEIGHT = BUILDER.comment("Altura física del meteorito").defineInRange("height", 1.5, 0.1, 500.0);
        METEOR_WIDTH = BUILDER.comment("Anchura física del meteorito").defineInRange("width", 1.5, 0.1, 500.0);
        BUILDER.pop();

        BUILDER.push("earthquake");
        EARTHQUAKE_MAGNITUDE = BUILDER.comment("Magnitud del terremoto").defineInRange("magnitude", 7.0, 1.0, 10.0);
        EARTHQUAKE_DURATION = BUILDER.comment("Duración del terremoto").defineInRange("duration", 600, 20, 72000);
        EARTHQUAKE_MAX_RADIUS = BUILDER.comment("Radio máximo del terremoto").defineInRange("max_radius", 80, 1, 500);
        BUILDER.pop();

        BUILDER.push("flood");
        FLOOD_SPEED = BUILDER.comment("Velocidad de expansión").defineInRange("speed", 0.4, 0.01, 10.0);
        FLOOD_MAX_RADIUS = BUILDER.comment("Radio máximo de la inundación").defineInRange("max_radius", 100.0, 1.0, 1000.0);
        FLOOD_HEIGHT = BUILDER.comment("Altura máxima del agua").defineInRange("height", 4, 1, 50);
        FLOOD_INFINITE_EXPANSION = BUILDER.comment("Permite expansión indefinida").define("infinite_expansion", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}