package com.example.plantsvszombies.models.Plants;

import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.AssetImpl.AnimationClip;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.models.Projectiles.SnowPea;
import com.example.plantsvszombies.screens.BaseGameScreen;

public class SnowPeashooter extends Peashooter {

    public SnowPeashooter(BaseGameScreen screen, Texture texture, AnimationClip animation, float x, float y) {
        super(screen, texture, animation, x, y);
        this.name = "Snow Pea";
        this.cost = 175;
    }

    public SnowPeashooter(BaseGameScreen gameScreen, float x, float y) {
        super(gameScreen, x, y);
        this.name = "Snow Pea";
        this.cost = 175;
        this.texture = AssetTexture.SNOW_PEASHOOTER_STILL.get(AssetMan.getInstance());
        this.animation = AssetAnimation.SNOW_PEASHOOTER.get(AssetMan.getInstance());
    }

    @Override
    protected void fire() {
        float projectileX = position.x + bounds.width * 2 / 3;
        float projectileY = position.y + bounds.height / 2;
        screen.spawnProjectile(new SnowPea(projectileX, projectileY));
    }
}
