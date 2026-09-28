package com.miguel.mdisasters.objects.sounds;

import com.miguel.mdisasters.init.sounds.MD_Sounds;
import com.miguel.mdisasters.objects.entities.MD_Earthquake;
import com.miguel.mdisasters.objects.entities.MD_Tornado;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class MD_EarthquakeSoundInstance extends AbstractTickableSoundInstance {
    private final MD_Earthquake earthquake;

    public MD_EarthquakeSoundInstance(MD_Earthquake earthquake) {
        // Enlaza el registro de tu sonido, la categoría (puedes cambiar HOSTILE por WEATHER) y un random seed
        super(MD_Sounds.EARTHQUAKE_SOUND.get(), SoundSource.WEATHER, SoundInstance.createUnseededRandom());
        this.earthquake = earthquake;
        this.looping = true; // Forzamos a que el motor de audio haga un bucle nativo si termina el archivo
        this.delay = 0;
        this.volume = 1.0F;
        this.pitch = 1.0F;

        // Posicionamiento inicial del sonido
        this.x = (float) earthquake.getX();
        this.y = (float) earthquake.getY();
        this.z = (float) earthquake.getZ();
    }

    @Override
    public void tick() {
        // Si el terremoto desaparece o es eliminado por el servidor, detenemos el sonido inmediatamente
        if (this.earthquake.isRemoved()) {
            this.stop();
            return;
        }

        // ¡Aquí está la magia! El sonido actualiza su posición 3D siguiendo a la entidad en movimiento
        this.x = (float) this.earthquake.getX();
        this.y = (float) this.earthquake.getY();
        this.z = (float) this.earthquake.getZ();
    }
}
