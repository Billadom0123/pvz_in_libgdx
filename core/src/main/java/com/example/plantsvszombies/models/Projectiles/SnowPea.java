package com.example.plantsvszombies.models.Projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetParticle;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.models.Zombies.BaseZombie;

public class SnowPea extends Pea {

    private final float slowEffectTime = 10f;

    public SnowPea(float x, float y) {
        this(AssetTexture.SNOW_PEA_NORMAL.get(AssetMan.getInstance()), x, y);
    }

    public SnowPea(Texture texture, float x, float y) {
        super(texture, x, y);
        this.speed = 300f; // Snow Pea speed
        setHitAnimation(AssetAnimation.SNOW_PEA_SPLAT.get(AssetMan.getInstance()));
        this.particleEffect = AssetParticle.SNOW_PEA.get(AssetMan.getInstance());
        particleEffect.scaleEffect(.6f);
    }

    @Override
    protected void updateParticleEffect() {
        particleEffect.setPosition(bounds.x, bounds.y + bounds.height / 2f);
    }

    @Override
    public void damage(BaseZombie zombie) {
        super.damage(zombie);
        zombie.applySnowEffect(slowEffectTime);
    }
}
