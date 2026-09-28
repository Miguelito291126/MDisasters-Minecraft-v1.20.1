package com.miguel.mdisasters.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class MD_EmptyRenderer extends EntityRenderer<Entity> {

    public MD_EmptyRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(
            Entity entity,
            float entityYaw,
            float partialTick,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            net.minecraft.client.renderer.MultiBufferSource buffer,
            int packedLight
    ) {
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return null;
    }
}