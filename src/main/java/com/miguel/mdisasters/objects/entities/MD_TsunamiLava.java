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

public class MD_TsunamiLava extends Entity {
    public MD_TsunamiLava(EntityType<? extends MD_TsunamiLava> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
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
        createLavaWave();

        AABB area = this.getBoundingBox().inflate(
                MD_Config.TSUNAMI_WIDTH.get() / 2.0,
                MD_Config.TSUNAMI_HEIGHT.get() / 2.0,
                MD_Config.TSUNAMI_WIDTH.get() / 2.0
        );
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity entity : entities) {
            entity.hurt(this.damageSources().lava(), 4.0F);
            entity.setDeltaMovement(directionX, 0.3, directionZ);
            entity.hurtMarked = true;
        }

        if (this.tickCount >= MD_Config.TSUNAMI_DURATION.get()) {
            this.discard();
        }
    }

    private void createLavaWave() {
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
                        level().setBlock(front, Blocks.LAVA.defaultBlockState(), 3);
                    }

                    int oldX = (int) Math.round(width * sideX - 3 * forwardX);
                    int oldZ = (int) Math.round(width * sideZ - 3 * forwardZ);
                    BlockPos behind = center.offset(oldX, y, oldZ);
                    if (level().getBlockState(behind).is(Blocks.LAVA)) {
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
        for (int i = 0; i < 20; i++) {
            double x = this.getX() + (this.random.nextDouble() - 0.5) * MD_Config.TSUNAMI_WIDTH.get();
            double y = this.getY() + this.random.nextDouble() * MD_Config.TSUNAMI_HEIGHT.get();
            double z = this.getZ() + (this.random.nextDouble() - 0.5) * MD_Config.TSUNAMI_WIDTH.get();
            this.level().addParticle(ParticleTypes.LAVA, x, y, z, 0, 0.05, 0);
            this.level().addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.05, 0);
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
