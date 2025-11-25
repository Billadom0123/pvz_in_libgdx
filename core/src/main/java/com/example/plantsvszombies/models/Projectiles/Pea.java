package com.example.plantsvszombies.models.Projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.SoundManager;
import com.example.plantsvszombies.models.Zombies.BaseZombie;

public class Pea extends BaseProjectile {

    public Pea(float x, float y) {
        this(AssetTexture.PEA_NORMAL.get(AssetMan.getInstance()), x, y);
    }

    public Pea(Texture texture, float x, float y) {
        super(texture, 20, x, y);
        this.speed = MathUtils.random(345f, 355f);
        setHitAnimation(AssetAnimation.PEA_SPLAT.get(AssetMan.getInstance()));
        this.shadow = AssetTexture.PEA_SHADOW.get(AssetMan.getInstance());
    }

    @Override
    public void drawShadow(SpriteBatch batch) {
        if (shadow != null && !hit) {
            batch.draw(shadow,
                bounds.x + (bounds.width - shadow.getWidth()) / 2f,
                position.y - 35f);
        }
    }

    @Override
    public void damage(BaseZombie zombie) {
        if (hit) return;
        super.damage(zombie);
        SoundManager.getInstance().playSound(AssetSound.PEA_SPLAT);
    }
}
