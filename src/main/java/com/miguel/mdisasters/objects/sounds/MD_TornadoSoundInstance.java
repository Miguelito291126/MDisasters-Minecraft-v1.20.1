package com.miguel.mdisasters.objects.sounds;

import com.miguel.mdisasters.objects.entities.MD_Tornado;
import com.miguel.mdisasters.init.sounds.MD_Sounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class MD_TornadoSoundInstance extends AbstractTickableSoundInstance {
    private final MD_Tornado tornado;

    public MD_TornadoSoundInstance(MD_Tornado tornado) {
        // Enlaza el registro de tu sonido, la categoría (puedes cambiar HOSTILE por WEATHER) y un random seed
        super(MD_Sounds.TORNADO_SOUND.get(), SoundSource.WEATHER, SoundInstance.createUnseededRandom());
        this.tornado = tornado;
        this.looping = true; // Forzamos a que el motor de audio haga un bucle nativo si termina el archivo
        this.delay = 0;
        this.volume = 1.0F;
        this.pitch = 1.0F;

        // Posicionamiento inicial del sonido
        this.x = (float) tornado.getX();
        this.y = (float) tornado.getY();
        this.z = (float) tornado.getZ();
    }

    @Override
    public void tick() {
        // Si el tornado desaparece o es eliminado por el servidor, detenemos el sonido inmediatamente
        if (this.tornado.isRemoved()) {
            this.stop();
            return;
        }

        // ¡Aquí está la magia! El sonido actualiza su posición 3D siguiendo a la entidad en movimiento
        this.x = (float) this.tornado.getX();
        this.y = (float) this.tornado.getY();
        this.z = (float) this.tornado.getZ();
    }
}
