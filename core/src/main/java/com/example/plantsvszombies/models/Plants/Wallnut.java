package com.example.plantsvszombies.models.Plants;

import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.screens.BaseGameScreen;

public class Wallnut extends BasePlant {

    public Wallnut() {
        super();
        this.name = "WallNut";
        this.cost = 50;
        this.cooldown = 30.0f; // 坚果墙的冷却时间较长
        this.init_cd = 20f;
        this.maxHealth = 4000;
        this.health = maxHealth;
    }

    public Wallnut(Texture texture, AnimationClip animation, float x, float y) {
        this(null, texture, animation, x, y);
    }

    public Wallnut(BaseGameScreen screen, Texture texture, AnimationClip animation, float x, float y) {
        super(screen, texture, animation, x, y);
        this.name = "WallNut";
        this.cost = 50;
        this.cooldown = 30.0f; // 坚果墙的冷却时间较长
        this.init_cd = 20f;
        this.maxHealth = 4000;
        this.health = maxHealth;
    }

    public Wallnut(BaseGameScreen screen, float x, float y) {
        this(screen,
            AssetTexture.NORMAL_WALLNUT_STILL.get(AssetMan.getInstance()),
            AssetAnimation.WALL_NUT.get(AssetMan.getInstance()),
            x, y);
        this.shadow = AssetTexture.PLANT_SHADOW.get(AssetMan.getInstance());
    }
    /**
     * 坚果墙是静态的，它自己没有任何行为，
     * 所以它的update方法是空的。它只是被动地承受伤害。
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        AssetMan assetMan = AssetMan.getInstance();
        if (health <= maxHealth * 0.33f) {
            animation = AssetAnimation.WALL_NUT_CRACKED2.get(assetMan);
        } else if (health <= maxHealth * 0.66f) {
            animation = AssetAnimation.WALL_NUT_CRACKED1.get(assetMan);
        }
    }
}
