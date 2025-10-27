package com.example.plantsvszombies.models.Projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.models.Zombies.BaseZombie;
import com.example.plantsvszombies.models.Zombies.NormalZombie;

public class Pea extends BaseProjectile {
    public Pea(Texture texture, float x, float y) {
        super(texture, 20, x, y);
        setHitAnimation(AssetMan.getInstance().getAnimation("PeaSplat"));
    }

    @Override
    public void damage(BaseZombie zombie) {
        if (!hit && zombie instanceof NormalZombie) {
            AssetMan.getInstance().playSound("graphics/audio/splat2.ogg", 0.5f);
        }
        super.damage(zombie);
    }
}
