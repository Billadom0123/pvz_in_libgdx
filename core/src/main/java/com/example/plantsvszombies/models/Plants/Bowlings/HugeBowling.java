package com.example.plantsvszombies.models.Plants.Bowlings;

import com.badlogic.gdx.math.Rectangle;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.SoundManager;
import com.example.plantsvszombies.models.DamageType;
import com.example.plantsvszombies.models.Zombies.BaseZombie;
import com.example.plantsvszombies.screens.BaseGameScreen;

public class HugeBowling extends BaseBowling {

    public HugeBowling(BaseGameScreen screen, float x, float y) {
        super(screen,
            AssetTexture.HUGE_WALLNUT.get(AssetMan.getInstance()),
            AssetAnimation.HUGE_WALLNUT.get(AssetMan.getInstance()),
            x, y);
        this.name = "Huge Wallnut";
        this.bounds = new Rectangle(x + texture.getWidth() * .4f, y + texture.getHeight() * .2f,
            texture.getWidth() * .2f, texture.getHeight() * .2f);
    }

    @Override
    public void changeDirection() {
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        directionY = 0; // 保持水平移动
        directionX = 1; // 保持向右移动
    }

    @Override
    public void hit(BaseZombie zombie) {
        SoundManager.getInstance().playSound(AssetSound.BOWLING_IMPACT);
        zombie.takeDamage(damageToZombie, DamageType.BOWLING);
    }
}
