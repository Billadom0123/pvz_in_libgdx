package com.example.plantsvszombies.models.Projectiles;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.example.plantsvszombies.AssetImpl.AnimationClip;
import com.example.plantsvszombies.models.Zombies.BaseZombie;
import com.example.plantsvszombies.screens.GameScreen;

public abstract class BaseProjectile {
    protected Vector2 position;
    protected Texture texture;
    protected Texture shadow;
    protected float speed;
    protected int damage;
    protected Rectangle bounds;
    public boolean active = true; // 标记是否存活，用于后续移除
    protected boolean hit = false;
    private boolean hasHitAnimation = false;
    private float hitTimer = 0f;
    protected AnimationClip hitAnimation; // 命中动画，可选
    protected ParticleEffect particleEffect;

    public BaseProjectile(Texture texture, int damage, float x, float y) {
        this.texture = texture;
        this.damage = damage;
        this.position = new Vector2(x, y);
        this.speed = 350f;
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public BaseProjectile(Texture texture, AnimationClip hitAnimation, int damage, float x, float y) {
        this.texture = texture;
        this.damage = damage;
        this.position = new Vector2(x, y);
        this.speed = 350f;
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        this.hitAnimation = hitAnimation;
        this.hasHitAnimation = true;
    }

    public void update(float delta) {
        if (hasHitAnimation && hit) {
            hitTimer += delta;
            if (hitAnimation.isAnimationFinished(hitTimer)) finish();
        } else {
            // 匀速向右移动
            position.x += speed * delta;
            bounds.setX(position.x);
            if (particleEffect != null) updateParticleEffect();
            // 如果飞出屏幕右侧，则标记为待移除
            if (position.x > GameScreen.WORLD_WIDTH) finish();
        }
    }

    protected void updateParticleEffect() {
        particleEffect.setPosition(position.x + texture.getWidth() / 2f,
            position.y + texture.getHeight() / 2f);
    }

    private void finish() {
        active = false;
        if (particleEffect != null) particleEffect.allowCompletion();
    }

    public void damage(BaseZombie zombie) {
        if (!hit) {
            zombie.takeDamage(damage);
            hit = true;
        }
    }

    public void draw(SpriteBatch batch) {
        if (hasHitAnimation && hit) {
            batch.draw(hitAnimation.getFrame(hitTimer), position.x, position.y);
        } else {
            batch.draw(texture, position.x, position.y);
        }
    }

    public void drawShadow(SpriteBatch batch) {
        if (shadow != null && !hit) {
            batch.draw(shadow,
                bounds.x + (bounds.width - shadow.getWidth()) / 2f,
                position.y - shadow.getHeight() / 4f);
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getDamage() {
        return damage;
    }

    public void setHitAnimation(AnimationClip hitAnimation) {
        this.hitAnimation = hitAnimation;
        this.hasHitAnimation = true;
    }

    public ParticleEffect getParticleEffect() {
        return particleEffect;
    }
}
