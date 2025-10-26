package com.example.plantsvszombies.models.Plants;

import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.managers.GameManager;
import com.example.plantsvszombies.screens.GameScreen;

public class Sunflower extends BasePlant {

    public enum State {
        NORMAL,
        GENERATING
    }
    private float sunProduceTimer;
    private static final float SUN_PRODUCE_INTERVAL = 10.0f; // 每10秒产生一次太阳

    public Sunflower() {
        super();
        this.name = "Sunflower";
        this.cost = 50;
        this.cooldown = 7.5f;
    }

    public Sunflower(Texture texture, float x, float y) {
        super(texture, x, y);
        this.sunProduceTimer = 0f;
    }

    public Sunflower(Texture texture, AnimationClip animation, float x, float y) {
        super(texture, animation, x, y);
        this.sunProduceTimer = 0f;
    }

    public Sunflower(GameScreen screen, Texture texture, AnimationClip animation, float x, float y) {
        super(screen, texture, animation, x, y);
        this.sunProduceTimer = 0f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        sunProduceTimer += delta;
        if (sunProduceTimer >= SUN_PRODUCE_INTERVAL) {
            sunProduceTimer = 0;
            produceSun();
        }
    }

    private void produceSun() {
        screen.spawnGeneratedSun(25, position.x + bounds.width / 2, position.y + bounds.height / 2);
    }
}
