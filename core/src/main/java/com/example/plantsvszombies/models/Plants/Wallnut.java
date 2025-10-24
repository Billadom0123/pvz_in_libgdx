package com.example.plantsvszombies.models.Plants;

import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.Animation.AnimationClip;

public class Wallnut extends BasePlant {

    public Wallnut(Texture texture, AnimationClip animation, float x, float y) {
        super(texture, animation, x, y);
        this.health = 500; // 坚果墙的生命值远高于其他植物
    }

    /**
     * 坚果墙是静态的，它自己没有任何行为，
     * 所以它的update方法是空的。它只是被动地承受伤害。
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        // Do nothing
    }
}
