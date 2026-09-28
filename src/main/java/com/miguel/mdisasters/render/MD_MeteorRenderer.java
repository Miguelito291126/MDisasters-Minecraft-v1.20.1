package com.miguel.mdisasters.render;

import com.miguel.mdisasters.MD_Main;
import com.miguel.mdisasters.config.MD_Config;
import com.miguel.mdisasters.objects.entities.MD_Meteor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MD_MeteorRenderer extends EntityRenderer<MD_Meteor> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MD_Main.MODID, "textures/block/volcano_block.png");

    public MD_MeteorRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(
            MD_Meteor entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        poseStack.pushPose();
        float scaleX = MD_Config.METEOR_WIDTH.get().floatValue();
        float scaleY = MD_Config.METEOR_HEIGHT.get().floatValue();
        poseStack.scale(scaleX, scaleY, scaleX);
        poseStack.mulPose(Axis.XP.rotationDegrees((entity.tickCount + partialTick) * 10.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTick) * 10.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees((entity.tickCount + partialTick) * 5.0F));

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        renderCube(poseStack, consumer, packedLight);

        poseStack.popPose();
    }

    private void renderCube(PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        quad(pose, consumer, matrix, normal, packedLight,
                -0.5F, -0.5F, 0.5F,
                0.5F, -0.5F, 0.5F,
                0.5F, 0.5F, 0.5F,
                -0.5F, 0.5F, 0.5F);
        quad(pose, consumer, matrix, normal, packedLight,
                0.5F, -0.5F, -0.5F,
                -0.5F, -0.5F, -0.5F,
                -0.5F, 0.5F, -0.5F,
                0.5F, 0.5F, -0.5F);
        quad(pose, consumer, matrix, normal, packedLight,
                -0.5F, 0.5F, -0.5F, 0.5F, 0.5F, -0.5F, 0.5F, 0.5F, 0.5F, -0.5F, 0.5F, 0.5F);
        quad(pose, consumer, matrix, normal, packedLight,
                -0.5F, -0.5F, 0.5F, -0.5F, -0.5F, -0.5F, 0.5F, -0.5F, -0.5F, 0.5F, 0.5F, -0.5F);
        quad(pose, consumer, matrix, normal, packedLight,
                0.5F, -0.5F, 0.5F, 0.5F, -0.5F, -0.5F, 0.5F, 0.5F, -0.5F, 0.5F, 0.5F, 0.5F);
        quad(pose, consumer, matrix, normal, packedLight,
                -0.5F, -0.5F, -0.5F, -0.5F, -0.5F, 0.5F, -0.5F, 0.5F, 0.5F, -0.5F, 0.5F, -0.5F);
    }

    private void quad(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            Matrix4f matrix,
            Matrix3f normal,
            int packedLight,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4
    ) {
        vertex(consumer, matrix, normal, packedLight, x1, y1, z1, 0.0F, 1.0F);
        vertex(consumer, matrix, normal, packedLight, x2, y2, z2, 1.0F, 1.0F);
        vertex(consumer, matrix, normal, packedLight, x3, y3, z3, 1.0F, 0.0F);
        vertex(consumer, matrix, normal, packedLight, x4, y4, z4, 0.0F, 0.0F);
    }

    private void vertex(
            VertexConsumer consumer,
            Matrix4f matrix,
            Matrix3f normal,
            int packedLight,
            float x,
            float y,
            float z,
            float u,
            float v
    ) {
        consumer.vertex(matrix, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(MD_Meteor entity) {
        return TEXTURE;
    }
}
