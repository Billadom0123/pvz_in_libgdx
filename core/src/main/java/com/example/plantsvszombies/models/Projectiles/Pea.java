package com.example.plantsvszombies.models.Projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.models.Zombies.BaseZombie;
import com.example.plantsvszombies.models.Zombies.ConeHeadZombie;
import com.example.plantsvszombies.models.Zombies.NormalZombie;

public class Pea extends BaseProjectile {
    public Pea(Texture texture, float x, float y) {
        super(texture, 20, x, y);
        setHitAnimation(AssetAnimation.PEA_SPLAT.get(AssetMan.getInstance()));
    }

    @Override
    public void damage(BaseZombie zombie) {
        if (!hit && zombie instanceof ConeHeadZombie) {
            AssetMan.getInstance().playSound(AssetSound.PEA_SPLAT_CONEHEAD, .5f);
            AssetMan.getInstance().playSound(AssetSound.PEA_SPLAT, 0.5f);
        } else if (!hit && zombie instanceof NormalZombie) {
            AssetMan.getInstance().playSound(AssetSound.PEA_SPLAT, 0.5f);
        }
        super.damage(zombie);
    }
}
