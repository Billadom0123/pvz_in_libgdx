package com.example.plantsvszombies.models.Zombies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.models.DamageType;
import com.example.plantsvszombies.models.Texture.Falling;
import com.example.plantsvszombies.screens.GameScreen;

public class ConeHeadZombie extends NormalZombie {

    private static final int CONE_HEAD_HEALTH = 370;
    private int coneHeadHealth = CONE_HEAD_HEALTH;

    public ConeHeadZombie(Texture texture, AnimationClip animation, float x, float y) {
        super(texture, animation, x, y);
    }

    public ConeHeadZombie(GameScreen gameScreen, float x, float y) {
        super(gameScreen, x, y);
        AssetMan assetMan = AssetMan.getInstance();
        this.normalAnimation = AssetAnimation.CONEHEAD_ZOMBIE.get(assetMan);
        this.animation = normalAnimation;
        this.centerBounds = new Rectangle(x + animation.getWidth() * 0.7f, y + animation.getHeight() * 0.2f,
            animation.getWidth() * 0.1f, animation.getHeight() * 0.3f);
        this.texture = AssetTexture.CONEHEAD_ZOMBIE_STILL.get(assetMan);
        this.attackAnimation = AssetAnimation.CONEHEAD_ZOMBIE_ATTACK.get(assetMan);
    }

    @Override
    public void takeDamage(int damage, DamageType damageType) {
        AssetMan assetMan = AssetMan.getInstance();
        if (coneHeadHealth > 0 && damageType != DamageType.EXPLOSIVE) {
            coneHeadHealth -= damage;
            if (coneHeadHealth <= 0) {
                // Cone head is destroyed, switch to normal zombie texture and animation
                this.texture = AssetTexture.NORMAL_ZOMBIE_STILL.get(assetMan);
                this.normalAnimation = AssetAnimation.NORMAL_ZOMBIE.get(assetMan);
                this.animation = normalAnimation;
                this.position = new Vector2(position.x + animation.getWidth() * 0.1f, position.y);
                this.centerBounds = new Rectangle(position.x + animation.getWidth() * 0.6f, position.y + animation.getHeight() * 0.2f,
                    animation.getWidth() * 0.1f, animation.getHeight() * 0.3f);
                this.attackAnimation = AssetAnimation.NORMAL_ZOMBIE_ATTACK.get(assetMan);
                Falling falling = new Falling(
                    AssetTexture.CONE.get(assetMan), position.x + animation.getWidth() * .5f, position.y + animation.getHeight() * .5f,
                    new Vector2(MathUtils.random(-30f, 30f), MathUtils.random(100f, 200f)),
                    MathUtils.random(-90f, 90f), position.y);
                screen.addFalling(falling);
                super.takeDamage(-coneHeadHealth, damageType);
            } else {
                super.takeDamage(0, damageType);
            }
        } else {
            super.takeDamage(damage, damageType);
        }
    }

    @Override
    public void drawAnimation(SpriteBatch batch) {
        super.drawAnimation(batch);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
