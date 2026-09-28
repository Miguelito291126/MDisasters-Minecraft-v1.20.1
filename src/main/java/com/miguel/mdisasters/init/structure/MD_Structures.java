package com.miguel.mdisasters.init.structure;

import com.miguel.mdisasters.MD_Main;
import com.miguel.mdisasters.objects.structure.MD_VolcanoStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class MD_Structures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE = DeferredRegister.create(
            Registries.STRUCTURE_TYPE,
            MD_Main.MODID
    );

    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(
            Registries.STRUCTURE_PIECE,
            MD_Main.MODID
    );

    public static final RegistryObject<StructureType<?>> VOLCANO_STRUCTURE = RegisterStructure(
            "volcano_structure",
            () -> () -> JigsawStructure.CODEC
    );

    // Registramos el tipo de PIEZA para tu volcán procedural
    public static final RegistryObject<StructurePieceType> VOLCANO_PIECE =
            RegisterStructurePiece("volcano_piece", () -> (context, tag) -> new MD_VolcanoStructure(tag));

    public static final ResourceKey<Structure> VOLCANO_KEY =
            ResourceKey.create(
                    Registries.STRUCTURE,
                    new ResourceLocation(MD_Main.MODID, "volcano_structure")
            );

    public static <T extends Structure> RegistryObject<StructureType<?>> RegisterStructure(
            String name,
            Supplier<StructureType<T>> structure
    )
    {
       return STRUCTURE_TYPE.register(name, structure);
    }

    public static RegistryObject<StructurePieceType> RegisterStructurePiece(
            String name,
            Supplier<StructurePieceType> structure
    )
    {
        return STRUCTURE_PIECES.register(name, structure);
    }

    public static void register(IEventBus eventBus) {
        STRUCTURE_TYPE.register(eventBus);
        STRUCTURE_PIECES.register(eventBus);
    }

}