package com.example.plantsvszombies.models.Plants;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetParticle;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.models.DamageType;
import com.example.plantsvszombies.screens.GameScreen;

import java.util.Timer;
import java.util.TimerTask;

public class PotatoMine extends BasePlant {

    private enum State {
        IDLE, ARMING, ARMED, EXPLODED
    }

    private static final float ARMING_TIME = 15f; // 土豆地雷的激活时间
    private State state;
    private float armingTimer = 0f;
    private int EXPLOSION_DAMAGE = 1800;
    private float explodeTimer = 0f;
    private Texture explodeText;

    public PotatoMine() {
        super();
        this.name = "PotatoMine";
        this.cost = 25;
        this.cooldown = 30f;
        this.init_cd = 30f;
        this.state = State.IDLE;
    }

    public PotatoMine(GameScreen gameScreen, Texture texture, AnimationClip animation, float x, float y) {
        super(gameScreen, texture, animation, x, y);
        this.name = "PotatoMine";
        this.cost = 25;
        this.cooldown = 30f;
        this.init_cd = 30f;
        this.state = State.IDLE;
    }

    public PotatoMine(GameScreen gameScreen, float x, float y) {
        this(gameScreen,
            AssetTexture.POTATO_MINE_ARMED_UP_NO_LIGHT.get(AssetMan.getInstance()),
            AssetAnimation.POTATO_MINE.get(AssetMan.getInstance()),
            x, y);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        AssetMan assetMan = AssetMan.getInstance();
        if (state == State.IDLE && stateTime >= ARMING_TIME) {
            state = State.ARMING;
            armingTimer = 0f;
            animation = AssetAnimation.POTATO_MINE_ARM_UP.get(assetMan);
            AssetMan.getInstance().playSound(AssetSound.POTATO_MINE_RISE, 1f);
            ParticleEffect dig_up = AssetParticle.POTATO_MINE_RISE.get(assetMan);
            dig_up.setPosition(position.x + animation.getWidth() / 2f, position.y);
            dig_up.scaleEffect(.6f);
            dig_up.start();
            screen.addParticle(dig_up);
        }
        if (state == State.ARMING) {
            armingTimer += delta;
            if (animation.isAnimationFinished(armingTimer)) {
                state = State.ARMED;
                animation = AssetAnimation.POTATO_MINE_ARMED.get(assetMan);
            }
        }
        if (state == State.EXPLODED) {
            explodeTimer += delta;
            if (animation.isAnimationFinished(explodeTimer)) {
                alive = false; // 爆炸动画结束后，移除土豆地雷
            }
        }
    }

    @Override
    public void drawAnimation(SpriteBatch batch) {
        if (state == State.ARMING) {
            batch.draw(animation.getFrame(armingTimer), 0, 0);
            super.drawAnimation(batch);
        } else if (state == State.EXPLODED) {
            batch.draw(animation.getFrame(explodeTimer), position.x, position.y);
            batch.draw(explodeText,
                position.x + (animation.getWidth() - explodeText.getWidth()) / 2 + (MathUtils.random() - 0.5f) * 4,
                position.y + (animation.getHeight() - explodeText.getHeight()) / 2 + 35 + (MathUtils.random() - 0.5f) * 2);
        } else {
            super.drawAnimation(batch);
        }
    }

    /**
     * 土豆爆炸！
     */
    public void explode() {
        AssetMan assetMan = AssetMan.getInstance();
        Rectangle explosionArea = new Rectangle(bounds.x - 20, bounds.y, bounds.width + 40, bounds.height);
        AnimationClip explosion = AssetAnimation.POTATO_MINE_EXPLOSION.get(assetMan);
        position = new Vector2( position.x - (explosion.getWidth() - bounds.width) / 2,
            position.y - (explosion.getHeight() - bounds.height) / 2);
        animation = explosion;
        ParticleEffect explosionEffect = AssetParticle.POTATO_MINE_EXPLODE.get(assetMan);
        explosionEffect.setPosition(position.x + animation.getWidth() / 2f, position.y + animation.getHeight() / 2f);
        explosionEffect.scaleEffect(0.35f);
        explosionEffect.start();
        screen.addParticle(explosionEffect);
        assetMan.playSound(AssetSound.POTATO_MINE_EXPLODE, 0.6f);
        bounds = new Rectangle(-100, -100, 0, 0); // 移出碰撞框，避免继续与僵尸碰撞
        screen.triggerCameraShake(0.25f, 6f);
        screen.doAoeDamage(explosionArea, EXPLOSION_DAMAGE, DamageType.NORMAL);
        explodeText = AssetTexture.POTATO_MINE_EXPLOSION_WORD.get(assetMan);
        state = State.EXPLODED;
    }

    @Override
    public void takeDamage(int damage) {
        if (state == State.ARMED) {
            explode();
        } else {
            super.takeDamage(damage);
        }
    }
}
