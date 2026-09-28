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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class MD_Flood extends Entity {
    private static final int MAX_LIFETIME = 20 * 60;

    private double currentRadius = 1.0;

    public MD_Flood(EntityType<? extends MD_Flood> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.currentRadius = tag.getDouble("CurrentRadius");
        if (this.currentRadius <= 0) {
            this.currentRadius = 1.0;
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putDouble("CurrentRadius", this.currentRadius);
    }

    @Override
    public void tick() {
        super.tick();
        this.currentRadius += MD_Config.FLOOD_SPEED.get();

        if (!this.level().isClientSide()) {
            if (!MD_Config.FLOOD_INFINITE_EXPANSION.get()
                    && this.currentRadius >= MD_Config.FLOOD_MAX_RADIUS.get()) {
                this.discard();
                return;
            }

            updateFloodRing();
            pushEntities();

            if (this.tickCount >= MAX_LIFETIME) {
                this.discard();
            }
        } else {
            spawnParticles();
        }
    }

    private void updateFloodRing() {
        BlockPos center = this.blockPosition();
        int radius = (int) Math.ceil(this.currentRadius);
        double innerRadiusSquared = Math.max(0, this.currentRadius - 1.5);
        innerRadiusSquared *= innerRadiusSquared;
        double outerRadiusSquared = this.currentRadius * this.currentRadius;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distanceSquared = x * x + z * z;
                if (distanceSquared < innerRadiusSquared || distanceSquared > outerRadiusSquared) {
                    continue;
                }

                BlockPos column = center.offset(x, 0, z);
                BlockPos ground = findGround(column);
                if (ground == null) {
                    continue;
                }

                for (int y = 1; y <= MD_Config.FLOOD_HEIGHT.get(); y++) {
                    BlockPos target = ground.above(y);
                    if (canReplace(level().getBlockState(target))) {
                        level().setBlock(target, Blocks.WATER.defaultBlockState(), 3);
                    }
                }

                BlockPos below = column.below();
                while (below.getY() > level().getMinBuildHeight()
                        && canReplace(level().getBlockState(below))) {
                    level().setBlock(below, Blocks.WATER.defaultBlockState(), 3);
                    below = below.below();
                }
            }
        }
    }

    private BlockPos findGround(BlockPos start) {
        BlockPos.MutableBlockPos cursor = start.mutable();
        for (int i = 0; i < 32 && cursor.getY() > level().getMinBuildHeight(); i++) {
            if (!level().getBlockState(cursor).isAir()) {
                return cursor.immutable();
            }
            cursor.move(0, -1, 0);
        }
        return null;
    }

    private boolean canReplace(BlockState state) {
        return state.isAir() || state.canBeReplaced();
    }

    private void pushEntities() {
        AABB area = this.getBoundingBox().inflate(
                this.currentRadius,
                MD_Config.FLOOD_HEIGHT.get(),
                this.currentRadius
        );
        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity entity : entities) {
            double dx = entity.getX() - getX();
            double dz = entity.getZ() - getZ();
            double distance = Math.max(0.001, Math.sqrt(dx * dx + dz * dz));
            entity.setDeltaMovement(
                    dx / distance * MD_Config.FLOOD_SPEED.get() * 1.2,
                    Math.max(entity.getDeltaMovement().y, 0.05),
                    dz / distance * MD_Config.FLOOD_SPEED.get() * 1.2
            );
            entity.hurtMarked = true;
        }
    }

    private void spawnParticles() {
        for (int i = 0; i < 10; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double x = getX() + Math.cos(angle) * currentRadius;
            double z = getZ() + Math.sin(angle) * currentRadius;
            level().addParticle(ParticleTypes.SPLASH, x, getY() + 0.1, z, Math.cos(angle) * 0.1, 0.05, Math.sin(angle) * 0.1);
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
