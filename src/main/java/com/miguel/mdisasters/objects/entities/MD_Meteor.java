package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.config.MD_Config;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class MD_Meteor extends Entity {
    private static final int MAX_LIFETIME = 20 * 20;

    public MD_Meteor(EntityType<? extends MD_Meteor> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = false;
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

        if (level().isClientSide()) {
            spawnParticles();
            return;
        }

        setDeltaMovement(
                getDeltaMovement().x,
                getDeltaMovement().y - MD_Config.METEOR_SPEED.get() * 0.03D,
                getDeltaMovement().z
        );
        move(MoverType.SELF, getDeltaMovement());

        if (onGround() || horizontalCollision || tickCount >= MAX_LIFETIME) {
            explode();
        }
    }

    private void explode() {
        level().explode(
                this,
                getX(),
                getY(),
                getZ(),
                MD_Config.METEOR_EXPLOSION_POWER.get().floatValue(),
                Level.ExplosionInteraction.BLOCK
        );
        discard();
    }

    private void spawnParticles() {
        for (int i = 0; i < 6; i++) {
            level().addParticle(
                    ParticleTypes.FLAME,
                    getX() + (random.nextDouble() - 0.5) * 1.5,
                    getY() + random.nextDouble() * 1.5,
                    getZ() + (random.nextDouble() - 0.5) * 1.5,
                    0,
                    0.05,
                    0
            );
            level().addParticle(
                    ParticleTypes.LARGE_SMOKE,
                    getX(),
                    getY(),
                    getZ(),
                    0,
                    0.05,
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
