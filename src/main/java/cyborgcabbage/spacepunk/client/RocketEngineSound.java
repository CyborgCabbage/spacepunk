package cyborgcabbage.spacepunk.client;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class RocketEngineSound extends MovingSoundInstance {
    private final RocketEntity rocket;

    public RocketEngineSound(RocketEntity _rocket) {
        super(Spacepunk.ROCKET_LAUNCH_SOUND_EVENT, SoundCategory.NEUTRAL, SoundInstance.createRandom());
        rocket = _rocket;
        x = (float)rocket.getX();
        y = (float)rocket.getY();
        z = (float)rocket.getZ();
        volume = rocket.engineOn() ? 1 : 0;
        pitch = 1;
        repeat = true;
        repeatDelay = 0;
    }

    @Override
    public boolean canPlay() {
        return !rocket.isSilent();
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public void tick() {
        if (rocket.isRemoved()) {
            setDone();
            return;
        }
        if(rocket.engineOn()){
            volume += 0.03;
        }else{
            volume -= 0.06;
        }
        volume = MathHelper.clamp(volume, 0, 1);
        x = (float)rocket.getX();
        y = (float)rocket.getY();
        z = (float)rocket.getZ();
    }
}
