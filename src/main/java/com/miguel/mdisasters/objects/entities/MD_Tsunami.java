package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.config.MD_Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class MD_Tsunami extends Entity {
    public MD_Tsunami(EntityType<? extends MD_Tsunami> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.setBoundingBox(new AABB(
                -MD_Config.TSUNAMI_WIDTH.get() / 2.0,
                0,
                -MD_Config.TSUNAMI_WIDTH.get() / 2.0,
                MD_Config.TSUNAMI_WIDTH.get() / 2.0,
                MD_Config.TSUNAMI_HEIGHT.get(),
                MD_Config.TSUNAMI_WIDTH.get() / 2.0
        ));
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            spawnParticles();
            return;
        }

        double radians = Math.toRadians(this.getYRot());
        double directionX = -Math.sin(radians);
        double directionZ = Math.cos(radians);

        this.setDeltaMovement(
                directionX * MD_Config.TSUNAMI_SPEED.get(),
                0,
                directionZ * MD_Config.TSUNAMI_SPEED.get()
        );
        this.move(MoverType.SELF, this.getDeltaMovement());
        createWaterWave();

        AABB area = this.getBoundingBox().inflate(
                MD_Config.TSUNAMI_WIDTH.get() / 2.0,
                MD_Config.TSUNAMI_HEIGHT.get() / 2.0,
                MD_Config.TSUNAMI_WIDTH.get() / 2.0
        );
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity entity : entities) {
            entity.setDeltaMovement(
                    directionX * 1.2,
                    Math.max(entity.getDeltaMovement().y, 0.35),
                    directionZ * 1.2
            );
            entity.hurtMarked = true;
        }

        if (this.tickCount >= MD_Config.TSUNAMI_DURATION.get()) {
            this.discard();
        }
    }

    private void createWaterWave() {
        BlockPos center = this.blockPosition();
        double radians = Math.toRadians(this.getYRot());
        double sideX = Math.cos(radians);
        double sideZ = Math.sin(radians);
        double forwardX = -Math.sin(radians);
        double forwardZ = Math.cos(radians);
        int halfWidth = (int) Math.ceil(MD_Config.TSUNAMI_WIDTH.get() / 2.0);
        int height = (int) Math.ceil(MD_Config.TSUNAMI_HEIGHT.get());

        for (int width = -halfWidth; width <= halfWidth; width++) {
            for (int depth = 0; depth < MD_Config.TSUNAMI_DEPTH.get(); depth++) {
                for (int y = 0; y < height; y++) {
                    int offsetX = (int) Math.round(width * sideX + depth * forwardX);
                    int offsetZ = (int) Math.round(width * sideZ + depth * forwardZ);
                    BlockPos front = center.offset(offsetX, y, offsetZ);

                    if (canReplace(level().getBlockState(front))) {
                        level().setBlock(front, Blocks.WATER.defaultBlockState(), 3);
                    }

                    int oldX = (int) Math.round(width * sideX - 3 * forwardX);
                    int oldZ = (int) Math.round(width * sideZ - 3 * forwardZ);
                    BlockPos behind = center.offset(oldX, y, oldZ);
                    if (level().getBlockState(behind).is(Blocks.WATER)) {
                        level().setBlock(behind, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private boolean canReplace(BlockState state) {
        return state.isAir() || state.canBeReplaced();
    }

    private void spawnParticles() {
        for (int i = 0; i < 15; i++) {
            this.level().addParticle(
                    ParticleTypes.SPLASH,
                    this.getX() + (this.random.nextDouble() - 0.5) * MD_Config.TSUNAMI_WIDTH.get(),
                    this.getY() + this.random.nextDouble() * MD_Config.TSUNAMI_HEIGHT.get(),
                    this.getZ() + (this.random.nextDouble() - 0.5) * MD_Config.TSUNAMI_WIDTH.get(),
                    0,
                    0.1,
                    0
            );
        }
    }
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
