package com.example.plantsvszombies.models.Zombies;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.screens.BaseGameScreen;

public class NormalZombie extends BaseZombie {
    public NormalZombie(Texture texture, AnimationClip animation, float x, float y) {
        super(texture, animation, x, y);
    }

    public NormalZombie(BaseGameScreen gameScreen, float x, float y) {
        super(gameScreen, x, y);
        AssetMan assetMan = AssetMan.getInstance();
        this.normalAnimation = AssetAnimation.NORMAL_ZOMBIE.get(assetMan);
        this.animation = normalAnimation;
        this.texture = AssetTexture.NORMAL_ZOMBIE_STILL.get(assetMan);
        this.shadow = AssetTexture.ZOMBIE_SHADOW.get(assetMan);
        this.dieAnimation = AssetAnimation.NORMAL_ZOMBIE_DIE.get(assetMan);
        this.explodeAnimation = AssetAnimation.NORMAL_ZOMBIE_BOOM_DIE.get(assetMan);
        this.attackAnimation = AssetAnimation.NORMAL_ZOMBIE_ATTACK.get(assetMan);
        this.headTexture = AssetTexture.NORMAL_ZOMBIE_HEAD.get(assetMan);
        this.lostHeadAnimation = AssetAnimation.NORMAL_ZOMBIE_LOST_HEAD.get(assetMan);
        this.lostHeadAttackAnimation = AssetAnimation.NORMAL_ZOMBIE_LOST_HEAD_ATTACK.get(assetMan);
        this.bounds = new Rectangle(x, y, animation.getWidth(), animation.getHeight());
        this.centerBounds = new Rectangle(x + animation.getWidth() * 0.6f, y + animation.getHeight() * 0.2f,
            animation.getWidth() * 0.1f, animation.getHeight() * 0.3f);
    }
}
