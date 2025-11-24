package com.example.plantsvszombies.models.Zombies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.example.plantsvszombies.AssetImpl.AnimationClip;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.SoundManager;
import com.example.plantsvszombies.models.DamageType;
import com.example.plantsvszombies.models.Texture.Falling;
import com.example.plantsvszombies.screens.BaseGameScreen;

import java.util.Arrays;

public class BucketHeadZombie extends NormalZombie {

    private static final int BUCKET_HEAD_HEALTH = 1100;
    private int bucketHeadHealth = BUCKET_HEAD_HEALTH;

    public BucketHeadZombie(Texture texture, AnimationClip animation, float x, float y) {
        super(texture, animation, x, y);
    }

    public BucketHeadZombie(BaseGameScreen gameScreen, float x, float y) {
        super(gameScreen, x, y);
        AssetMan assetMan = AssetMan.getInstance();
        this.normalAnimation = AssetAnimation.BUCKETHEAD_ZOMBIE.get(assetMan);
        this.animation = normalAnimation;
        this.centerBounds = new Rectangle(x + animation.getWidth() * 0.55f, y + animation.getHeight() * 0.2f,
            animation.getWidth() * 0.1f, animation.getHeight() * 0.3f);
        this.texture = AssetTexture.BUCKETHEAD_ZOMBIE_STILL.get(assetMan);
        this.attackAnimation = AssetAnimation.BUCKETHEAD_ZOMBIE_ATTACK.get(assetMan);
        this.hasHelmet = true;
        this.helmetHealth = BUCKET_HEAD_HEALTH;
    }

    @Override
    protected void damageToHelmet(int damage, DamageType damageType) {
        AssetMan assetMan = AssetMan.getInstance();
        if (!damageType.isIgnoreHelmet() && hasHelmet && damageType.isHaveHitSound()) {
            SoundManager.getInstance().playRandomSound(Arrays.asList(AssetSound.SPLAT_BUCKETHEAD_1, AssetSound.SPLAT_BUCKETHEAD_2));
        }
        super.damageToHelmet(damage, damageType);
        if (!hasHelmet) {
            // Buckethead is destroyed, switch to normal zombie texture and animation
            this.texture = AssetTexture.NORMAL_ZOMBIE_STILL.get(assetMan);
            this.normalAnimation = AssetAnimation.NORMAL_ZOMBIE.get(assetMan);
            this.animation = normalAnimation;
            this.position = new Vector2(position.x - animation.getWidth() * .05f, position.y);
            this.centerBounds = new Rectangle(position.x + animation.getWidth() * 0.6f, position.y + animation.getHeight() * 0.2f,
                animation.getWidth() * 0.1f, animation.getHeight() * 0.3f);
            this.attackAnimation = AssetAnimation.NORMAL_ZOMBIE_ATTACK.get(assetMan);
            Falling falling = new Falling(
                AssetTexture.BUCKET.get(assetMan), position.x + animation.getWidth() * .5f, position.y + animation.getHeight() * .5f,
                new Vector2(MathUtils.random(-30f, 30f), MathUtils.random(100f, 200f)),
                MathUtils.random(-90f, 90f), position.y);
            screen.addFalling(falling);
        }
    }

    public boolean isBucketGone() { return bucketHeadHealth <= 0; }
}
