package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.config.MD_Config;
import com.miguel.mdisasters.init.sounds.MD_Sounds;
import com.miguel.mdisasters.objects.sounds.MD_TornadoSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class MD_Tornado extends Entity {

    // Borra las variables soundTicks y SOUND_LENGTH_TICKS anteriores.
    private boolean soundStarted = false;

    private static final double MAX_HEIGHT = 15.0;

    private double directionX = 0;
    private double directionZ = 1;

    private double targetDirectionX = 0;
    private double targetDirectionZ = 1;


    public MD_Tornado(
            EntityType<? extends MD_Tornado> type,
            Level level
    ) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;


        if (!level.isClientSide()) {

            double angle =
                    level.random.nextDouble() * Math.PI * 2;

            directionX = -Math.sin(angle);
            directionZ = Math.cos(angle);

            targetDirectionX = directionX;
            targetDirectionZ = directionZ;
        }


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

    private void playTornadoSound() {
        // Solo iniciamos el sonido una vez; el objeto se encargará de actualizar su posición e ir en bucle
        if (!soundStarted) {
            net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                    new MD_TornadoSoundInstance(this)
            );
            soundStarted = true;
        }
    }

    @Override
    public void tick() {

        super.tick();



        if (this.level().isClientSide()) {

            double radius = 2.5;
            int height = 15;

            for (int i = 0; i < height; i++) {

                double currentRadius =
                        radius * (i / (double) height);

                double angle =
                        (this.tickCount * 0.2)
                                + (i * 0.5);

                double xOffset =
                        Math.cos(angle) * currentRadius;

                double zOffset =
                        Math.sin(angle) * currentRadius;

                this.level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE,

                        this.getX() + xOffset,
                        this.getY() + i,
                        this.getZ() + zOffset,

                        0,
                        0.1,
                        0
                );
            }

            playTornadoSound();
            return;
        }


        // ==========================================
        // SERVIDOR
        // ==========================================

        // Duración
        if (this.tickCount >= MD_Config.TORNADO_DURATION.get()) {

            this.discard();

            return;
        }


        // ==========================================
        // CAMBIAR DIRECCIÓN ALEATORIAMENTE
        // ==========================================

        if (this.tickCount % 20 == 0) {

            double angle =
                    this.level().random.nextDouble()
                            * Math.PI * 2;

            targetDirectionX =
                    Math.cos(angle);

            targetDirectionZ =
                    Math.sin(angle);
        }


        // ==========================================
        // SUAVIZAR EL CAMBIO DE DIRECCIÓN
        // ==========================================

        double turnSpeed = 0.02;

        directionX +=
                (targetDirectionX - directionX)
                        * turnSpeed;

        directionZ +=
                (targetDirectionZ - directionZ)
                        * turnSpeed;


        // Normalizar dirección

        double length =
                Math.sqrt(
                        directionX * directionX
                                + directionZ * directionZ
                );

        if (length > 0) {

            directionX /= length;
            directionZ /= length;
        }


        // ==========================================
        // MOVER TORNADO
        // ==========================================

        this.setDeltaMovement(
                directionX * MD_Config.TORNADO_SPEED.get(),
                0,
                directionZ * MD_Config.TORNADO_SPEED.get()
        );

        this.move(
                MoverType.SELF,
                this.getDeltaMovement()
        );

        int groundY = this.level().getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                this.getBlockX(),
                this.getBlockZ()
        );
        this.setPos(this.getX(), groundY, this.getZ());


        // ==========================================
        // BUSCAR ENTIDADES
        // ==========================================

        AABB area =
                this.getBoundingBox()
                        .inflate(
                                MD_Config.TORNADO_RADIUS.get(),
                                MAX_HEIGHT,
                                MD_Config.TORNADO_RADIUS.get()
                        );

        List<LivingEntity> entities =
                this.level().getEntitiesOfClass(
                        LivingEntity.class,
                        area
                );


        for (LivingEntity entity : entities) {

            double dx =
                    this.getX()
                            - entity.getX();

            double dz =
                    this.getZ()
                            - entity.getZ();


            double distance =
                    Math.sqrt(
                            dx * dx
                                    + dz * dz
                    );


            if (distance > MD_Config.TORNADO_RADIUS.get()) {
                continue;
            }


            // Evitar división por cero
            if (distance < 0.1) {
                distance = 0.1;
            }


            // ======================================
            // ATRACCIÓN HACIA EL CENTRO
            // ======================================

            double pullStrength = 0.12;

            double pullX =
                    (dx / distance)
                            * pullStrength;

            double pullZ =
                    (dz / distance)
                            * pullStrength;


            // ======================================
            // GIRO DEL TORNADO
            // ======================================

            double swirlStrength = 0.25;

            double swirlX =
                    (-dz / distance)
                            * swirlStrength;

            double swirlZ =
                    (dx / distance)
                            * swirlStrength;


            // ======================================
            // LEVANTAR ENTIDADES
            // ======================================

            double liftStrength = 0.12;


            entity.setDeltaMovement(

                    entity.getDeltaMovement().x
                            + pullX
                            + swirlX,

                    Math.min(
                            entity.getDeltaMovement().y
                                    + liftStrength,
                            0.8
                    ),

                    entity.getDeltaMovement().z
                            + pullZ
                            + swirlZ
            );


            entity.hurtMarked = true;
        }
    }


    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }


    @Override
    public boolean fireImmune() {
        return true;
    }


}