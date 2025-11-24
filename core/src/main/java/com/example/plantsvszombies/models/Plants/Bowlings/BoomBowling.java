package com.example.plantsvszombies.models.Plants.Bowlings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.example.plantsvszombies.AssetImpl.AnimationClip;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.SoundManager;
import com.example.plantsvszombies.models.DamageType;
import com.example.plantsvszombies.models.Zombies.BaseZombie;
import com.example.plantsvszombies.screens.BaseGameScreen;

public class BoomBowling extends BaseBowling {

    private static final float EXPLOSION_RADIUS = 125f; // 爆炸半径
    private float explosionTimer = 0f;
    private boolean exploded = false;

    public BoomBowling(BaseGameScreen screen, float x, float y) {
        super(screen,
            AssetTexture.BOOM_WALLNUT.get(AssetMan.getInstance()),
            AssetAnimation.BOOM_WALLNUT.get(AssetMan.getInstance()),
            x, y);
        this.name = "Boom Wallnut";
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (exploded) {
            explosionTimer += delta;
        }
        if (exploded && animation.isAnimationFinished(explosionTimer)) {
            dispose();
        }
    }

    @Override
    public void drawAnimation(SpriteBatch batch) {
        if (!exploded) {
            super.drawAnimation(batch);
        } else {
            batch.draw(animation.getFrame(explosionTimer), position.x, position.y);
        }
    }

    @Override
    public void hit(BaseZombie zombie) {
        // 一旦碰撞到僵尸就爆炸，先停止移动
        speedX = 0;
        speedY = 0;
        explode();
    }

    private void explode() {
        explosionTimer = 0f; // 重置计时器以播放爆炸动画
        exploded = true;
        AssetMan assetMan = AssetMan.getInstance();
        float centerX = bounds.x + bounds.width / 2;
        float centerY = bounds.y + bounds.height / 2;
        Rectangle explosionArea = new Rectangle(
            centerX - EXPLOSION_RADIUS,
            centerY - EXPLOSION_RADIUS,
            EXPLOSION_RADIUS * 2,
            EXPLOSION_RADIUS * 2
        );
        screen.doAoeDamage(explosionArea, 1800, DamageType.EXPLOSIVE);
        SoundManager.getInstance().playSound(AssetSound.CHERRY_BOMB_EXPLODE);
        AnimationClip explosion = AssetAnimation.CHERRY_BOMB_BOOM.get(assetMan);
        position = new Vector2(position.x - (explosion.getWidth() - animation.getWidth()) / 2,
            position.y - (explosion.getHeight() - animation.getHeight()) / 2);
        animation = explosion;
        bounds = new Rectangle(-100, -100, 0, 0); // 移出碰撞框，避免继续与僵尸碰撞
    }
}
