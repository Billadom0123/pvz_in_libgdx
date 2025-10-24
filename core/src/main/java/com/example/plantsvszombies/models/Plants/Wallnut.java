package com.example.plantsvszombies.models.Plants;

import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.managers.AssetMan;

public class Wallnut extends BasePlant {

    public Wallnut(Texture texture, AnimationClip animation, float x, float y) {
        super(texture, animation, x, y);
        this.maxHealth = 4000; // 坚果墙的生命值远高于其他植物
        this.health = maxHealth;
    }

    /**
     * 坚果墙是静态的，它自己没有任何行为，
     * 所以它的update方法是空的。它只是被动地承受伤害。
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        // Do nothing
        if (health <= maxHealth * 0.33f) {
            animation = AssetMan.getInstance().getAnimation("WallNut_Cracked2");
        } else if (health <= maxHealth * 0.66f) {
            animation = AssetMan.getInstance().getAnimation("WallNut_Cracked1");
        }
    }
}
