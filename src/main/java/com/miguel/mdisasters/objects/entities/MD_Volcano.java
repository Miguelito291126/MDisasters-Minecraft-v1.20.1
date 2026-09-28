package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.config.MD_Config;
import com.miguel.mdisasters.init.blocks.MD_Blocks;
import com.miguel.mdisasters.init.entities.MD_Entities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class MD_Volcano extends Entity {
    private static final EntityDataAccessor<Boolean> IS_ERUPTING =
            SynchedEntityData.defineId(MD_Volcano.class, EntityDataSerializers.BOOLEAN);
    private static final int LAVA_RADIUS = 12;

    private double speed = MD_Config.VOLCANO_SPEED.get();
    private double pressure;
    private boolean pressureLeak;
    private int eruptionPhase;
    private int phaseTimer;

    public MD_Volcano(EntityType<? extends MD_Volcano> type, Level level) {
        super(type, level);
        this.noPhysics = false;
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(IS_ERUPTING, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        pressure = tag.getDouble("Pressure");
        speed = tag.contains("Speed") ? tag.getDouble("Speed") : 0.4;
        pressureLeak = tag.getBoolean("PressureLeak");
        setErupting(tag.getBoolean("Erupting"));
        eruptionPhase = tag.getInt("EruptionPhase");
        phaseTimer = tag.getInt("PhaseTimer");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putDouble("Pressure", pressure);
        tag.putDouble("Speed", speed);
        tag.putBoolean("PressureLeak", pressureLeak);
        tag.putBoolean("Erupting", isErupting());
        tag.putInt("EruptionPhase", eruptionPhase);
        tag.putInt("PhaseTimer", phaseTimer);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            spawnParticles();
            return;
        }

        pressureIncrement();
        if (pressure >= 100 && !isErupting()) {
            setErupting(true);
            eruptionPhase = 1;
            phaseTimer = 0;
        }

        if (isErupting()) {
            processEruption();
        }

        damageNearbyEntities();
    }

    private void pressureIncrement() {
        if (!isErupting() && !pressureLeak) {
            pressure += speed;
        } else if (!isErupting() && pressureLeak) {
            pressure = Math.max(0, pressure - speed);
            if (pressure == 0) {
                pressureLeak = false;
            }
        }
    }

    private void processEruption() {
        phaseTimer++;
        switch (eruptionPhase) {
            case 1 -> {
                spawnEarthquake();
                eruptionPhase = 2;
                phaseTimer = 0;
            }
            case 2 -> {
                if (phaseTimer >= 40) {
                    spawnLavaLumps(2);
                    eruptionPhase = 3;
                    phaseTimer = 0;
                }
            }
            case 3 -> {
                if (phaseTimer >= 30) {
                    spawnLavaLumps(4);
                    eruptionPhase = 4;
                    phaseTimer = 0;
                }
            }
            case 4 -> {
                if (phaseTimer >= 20) {
                    setErupting(false);
                    pressureLeak = true;
                    pressure = 99;
                    eruptionPhase = 0;
                    phaseTimer = 0;
                }
            }
            default -> {
            }
        }
    }

    private void spawnEarthquake() {
        MD_Earthquake earthquake = MD_Entities.EARTHQUAKE.get().create(level());
        if (earthquake != null) {
            earthquake.moveTo(getX(), getY(), getZ(), 0, 0);
            level().addFreshEntity(earthquake);
        }
    }

    private void spawnLavaLumps(int count) {
        level().playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE,
                net.minecraft.sounds.SoundSource.BLOCKS, 1.5F, 0.6F);
        for (int i = 0; i < count; i++) {
            FallingBlockEntity lump = FallingBlockEntity.fall(
                    level(),
                    blockPosition().above(),
                    Blocks.MAGMA_BLOCK.defaultBlockState()
            );
            lump.setPos(getX() + 0.5, getY() + 1.2, getZ() + 0.5);
            lump.setDeltaMovement(
                    (random.nextDouble() - 0.5) * 0.6,
                    0.8 + random.nextDouble() * 0.5,
                    (random.nextDouble() - 0.5) * 0.6
            );
            level().addFreshEntity(lump);
        }
    }

    private void spawnParticles() {
        boolean erupting = isErupting();
        int count = erupting ? 12 : 4;
        for (int i = 0; i < count; i++) {
            double x = getX() + (random.nextDouble() - 0.5D) * 2.0D;
            double y = getY() + random.nextDouble() * 0.8D;
            double z = getZ() + (random.nextDouble() - 0.5D) * 2.0D;

            level().addParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    x,
                    y,
                    z,
                    (random.nextDouble() - 0.5D) * 0.2D,
                    0.1D + random.nextDouble() * 0.3D,
                    (random.nextDouble() - 0.5D) * 0.2D
            );

            if (erupting) {
                level().addParticle(
                        ParticleTypes.FLAME,
                        x,
                        y,
                        z,
                        (random.nextDouble() - 0.5D) * 0.25D,
                        0.15D + random.nextDouble() * 0.35D,
                        (random.nextDouble() - 0.5D) * 0.25D
                );
                level().addParticle(
                        ParticleTypes.LAVA,
                        getX() + (random.nextDouble() - 0.5D) * 0.5D,
                        getY() + 0.2D,
                        getZ() + (random.nextDouble() - 0.5D) * 0.5D,
                        0.0D,
                        0.5D,
                        0.0D
                );
            }
        }
    }

    private void damageNearbyEntities() {
        AABB area = getBoundingBox().inflate(LAVA_RADIUS, 8, LAVA_RADIUS);
        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity entity : entities) {
            BlockState blockState = level().getBlockState(entity.getOnPos());
            if (!blockState.is(Blocks.LAVA)) {
                entity.hurt(damageSources().lava(), 2.0F);
                entity.setSecondsOnFire(3);
            }
        }
    }

    private boolean isErupting() {
        return entityData.get(IS_ERUPTING);
    }

    private void setErupting(boolean erupting) {
        entityData.set(IS_ERUPTING, erupting);
    }

    public void createVolcano() {
        BlockPos center = blockPosition();
        BlockState volcanoBlock = MD_Blocks.VOLCANO_BLOCK.get().defaultBlockState();
        for (int y = 0; y <= 8; y++) {
            int radius = Math.max(2, 12 - y * 12 / 8);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= radius * radius
                            && level().getBlockState(center.offset(x, y, z)).isAir()) {
                        level().setBlock(center.offset(x, y, z), volcanoBlock, 3);
                    }
                }
            }
        }
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (x * x + z * z <= 9) {
                    level().setBlock(center.offset(x, 9, z), Blocks.LAVA.defaultBlockState(), 3);
                }
            }
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
