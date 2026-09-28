package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.config.MD_Config;
import com.miguel.mdisasters.init.sounds.MD_Sounds;
import com.miguel.mdisasters.objects.sounds.MD_EarthquakeSoundInstance;
import com.miguel.mdisasters.objects.sounds.MD_TornadoSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvent;
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

public class MD_Earthquake extends Entity {
    private boolean soundStarted = false;


    private static final double RADIUS_GROWTH = 0.5;

    private double currentRadius = 1.0;

    public MD_Earthquake(EntityType<? extends MD_Earthquake> type, Level level) {
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

    private void playEarthquakeSound() {
        // Solo iniciamos el sonido una vez; el objeto se encargará de actualizar su posición e ir en bucle
        if (!soundStarted) {
            net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                    new MD_EarthquakeSoundInstance(this)
            );
            soundStarted = true;
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putDouble("CurrentRadius", this.currentRadius);
    }

    @Override
    public void tick() {
        super.tick();

        currentRadius = Math.min(MD_Config.EARTHQUAKE_MAX_RADIUS.get(), currentRadius + RADIUS_GROWTH);

        if (level().isClientSide()) {
            spawnParticles();
            playEarthquakeSound();
            return;
        }

        generateFissures();
        shakeEntities();

        if (tickCount >= MD_Config.EARTHQUAKE_DURATION.get()) {
            discard();
        }
    }

    private void generateFissures() {
        double magnitude = MD_Config.EARTHQUAKE_MAGNITUDE.get();
        int lines = Math.max(3, (int) Math.round(magnitude * 1.2));
        BlockPos center = blockPosition();

        for (int line = 0; line < lines; line++) {
            double angle = Math.PI * 2.0 / lines * line + random.nextDouble() * 0.15;
            double directionX = Math.cos(angle);
            double directionZ = Math.sin(angle);

            for (double distance = 1; distance <= currentRadius; distance++) {
                int x = (int) Math.round(distance * directionX);
                int z = (int) Math.round(distance * directionZ);
                BlockPos ground = findGround(center.offset(x, 0, z));
                if (ground == null) {
                    continue;
                }

                int depth = Math.max(3, (int) Math.round(magnitude * 1.5) + random.nextInt(4));
                for (int y = 0; y < depth; y++) {
                    BlockPos target = ground.below(y);
                    BlockState state = level().getBlockState(target);
                    if (state.is(Blocks.BEDROCK) || state.isAir()) {
                        continue;
                    }

                    if (y == depth - 1 && random.nextDouble() < 0.12) {
                        level().setBlock(target, Blocks.LAVA.defaultBlockState(), 3);
                    } else {
                        level().destroyBlock(target, false);
                    }
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

    private void shakeEntities() {
        AABB area = getBoundingBox().inflate(currentRadius, 10, currentRadius);
        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, area);
        double force = MD_Config.EARTHQUAKE_MAGNITUDE.get() / 10.0 * 0.6;

        for (LivingEntity entity : entities) {
            if (!entity.onGround()) {
                continue;
            }
            entity.setDeltaMovement(
                    entity.getDeltaMovement().x + (random.nextDouble() - 0.5) * force,
                    random.nextInt(4) == 0 ? 0.15 + force * 0.3 : entity.getDeltaMovement().y,
                    entity.getDeltaMovement().z + (random.nextDouble() - 0.5) * force
            );
            entity.hurtMarked = true;
        }
    }

    private void spawnParticles() {
        for (int i = 0; i < 15; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = random.nextDouble() * currentRadius;
            level().addParticle(
                    ParticleTypes.CLOUD,
                    getX() + Math.cos(angle) * distance,
                    getY() + 0.1,
                    getZ() + Math.sin(angle) * distance,
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
