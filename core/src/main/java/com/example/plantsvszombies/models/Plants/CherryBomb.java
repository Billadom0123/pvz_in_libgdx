package com.example.plantsvszombies.models.Plants;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.models.DamageType;
import com.example.plantsvszombies.screens.BaseGameScreen;

public class CherryBomb extends BasePlant {

    private static final float EXPLOSION_RADIUS = 125f; // 爆炸半径
    private float explosionTimer = 0f;
    private boolean exploded = false;

    public CherryBomb() {
        super();
        this.name = "CherryBomb";
        this.cost = 150;
        this.cooldown = 50f;
    }

    public CherryBomb(BaseGameScreen screen, Texture texture, AnimationClip animation, float x, float y) {
        super(screen, texture, animation, x - 20, y); // 调整X坐标以居中
        this.name = "CherryBomb";
        this.cost = 150;
        this.cooldown = 50f;
        this.init_cd = 35f;
        this.bounds = new Rectangle(position.x + 20, position.y, texture.getWidth() - 40, texture.getHeight());
    }

    public CherryBomb(BaseGameScreen screen, float x, float y) {
        this(screen,
            AssetTexture.CHERRY_BOMB_STILL.get(AssetMan.getInstance()),
            AssetAnimation.CHERRY_BOMB.get(AssetMan.getInstance()),
            x, y);
        this.shadow = AssetTexture.PLANT_SHADOW.get(AssetMan.getInstance());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (stateTime == 0f && !exploded) {
            AssetMan.getInstance().playSound(AssetSound.CHERRY_BOMB_READY, .6f);
        }
        stateTime += delta;
        if (animation.isAnimationFinished(stateTime) && !exploded) {
            explode();
        }
        if (exploded) {
            explosionTimer += delta;
        }
        if (exploded && animation.isAnimationFinished(explosionTimer)) {
            this.alive = false; // 爆炸动画播放完毕后移除
        }
    }

    @Override
    public void drawAnimation(SpriteBatch batch) {
        if (exploded) {
            batch.draw(animation.getFrame(explosionTimer), position.x, position.y);
        } else {
            super.drawAnimation(batch);
        }
    }

    private void explode() {
        explosionTimer = 0f; // 重置计时器以播放爆炸动画
        exploded = true;
        AssetMan assetMan = AssetMan.getInstance();
        float centerX = position.x + bounds.width / 2;
        float centerY = position.y + bounds.height / 2;
        Rectangle explosionArea = new Rectangle(
            centerX - EXPLOSION_RADIUS,
            centerY - EXPLOSION_RADIUS,
            EXPLOSION_RADIUS * 2,
            EXPLOSION_RADIUS * 2
        );
        screen.doAoeDamage(explosionArea, 1800, DamageType.EXPLOSIVE);
        assetMan.playSound(AssetSound.CHERRY_BOMB_EXPLODE, .6f);
        AnimationClip explosion = AssetAnimation.CHERRY_BOMB_BOOM.get(assetMan);
        position = new Vector2(position.x - (explosion.getWidth() - animation.getWidth()) / 2,
            position.y - (explosion.getHeight() - animation.getHeight()) / 2);
        animation = explosion;
        bounds = new Rectangle(-100, -100, 0, 0); // 移出碰撞框，避免继续与僵尸碰撞
    }
}
