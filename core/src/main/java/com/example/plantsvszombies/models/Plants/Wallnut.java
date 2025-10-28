package com.example.plantsvszombies.models.Plants;

import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.screens.GameScreen;

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

    public Wallnut(GameScreen screen, Texture texture, AnimationClip animation, float x, float y) {
        super(screen, texture, animation, x, y);
        this.name = "WallNut";
        this.cost = 50;
        this.cooldown = 30.0f; // 坚果墙的冷却时间较长
        this.init_cd = 20f;
        this.maxHealth = 4000;
        this.health = maxHealth;
    }

    public Wallnut(GameScreen screen, float x, float y) {
        this(screen,
            AssetMan.getInstance().getTexture("graphics/Plants/WallNut/StillNormal.png"),
            AssetMan.getInstance().getAnimation("WallNut"),
            x, y);
    }
    /**
     * 坚果墙是静态的，它自己没有任何行为，
     * 所以它的update方法是空的。它只是被动地承受伤害。
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        if (health <= maxHealth * 0.33f) {
            animation = AssetMan.getInstance().getAnimation("WallNut_Cracked2");
        } else if (health <= maxHealth * 0.66f) {
            animation = AssetMan.getInstance().getAnimation("WallNut_Cracked1");
        }
    }
}
