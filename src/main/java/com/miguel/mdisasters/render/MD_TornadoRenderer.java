package com.miguel.mdisasters.render;

import com.miguel.mdisasters.MD_Main;
import com.miguel.mdisasters.config.MD_Config;
import com.miguel.mdisasters.objects.entities.MD_Tornado;
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

public class MD_TornadoRenderer extends EntityRenderer<MD_Tornado> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MD_Main.MODID, "textures/particle/tornado.png");

    public MD_TornadoRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(
            MD_Tornado entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        poseStack.pushPose();

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        float time = entity.tickCount + partialTick;
        int layers = Math.max(10, MD_Config.TORNADO_HEIGHT.get() / 2);

        for (int i = 0; i < layers; i++) {
            float progress = (float) i / layers;
            float height = progress * MD_Config.TORNADO_HEIGHT.get();
            float radius = 0.8F + (float) Math.pow(progress, 1.5D)
                    * (MD_Config.TORNADO_WIDTH.get() / 2.0F - 0.8F);
            float offsetX = (float) Math.sin((entity.tickCount + i) * 0.2D) * 0.3F;
            float offsetZ = (float) Math.cos((entity.tickCount + i) * 0.2D) * 0.3F;
            float alpha = 0.4F - progress * 0.15F;

            poseStack.pushPose();
            poseStack.translate(offsetX, height, offsetZ);
            poseStack.mulPose(Axis.YP.rotationDegrees(time * 15.0F + i * 20.0F));
            renderLayer(poseStack, consumer, packedLight, radius, alpha);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private void renderLayer(
            PoseStack poseStack,
            VertexConsumer consumer,
            int packedLight,
            float radius,
            float alpha
    ) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();
        int color = Math.max(0, Math.min(255, (int) (alpha * 255.0F)));

        vertex(consumer, matrix, normal, packedLight, -radius, 0.0F, -radius, 0.0F, 0.0F, color);
        vertex(consumer, matrix, normal, packedLight, radius, 0.0F, -radius, 1.0F, 0.0F, color);
        vertex(consumer, matrix, normal, packedLight, radius, 0.0F, radius, 1.0F, 1.0F, color);
        vertex(consumer, matrix, normal, packedLight, -radius, 0.0F, radius, 0.0F, 1.0F, color);
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
            float v,
            int alpha
    ) {
        consumer.vertex(matrix, x, y, z)
                .color(204, 204, 204, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(MD_Tornado entity) {
        return TEXTURE;
    }
}
