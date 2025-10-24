package com.example.plantsvszombies.models.Plants;

import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.managers.GameManager;

public class Sunflower extends BasePlant {
    private float sunProduceTimer;
    private static final float SUN_PRODUCE_INTERVAL = 10.0f; // 每10秒产生一次太阳

    public Sunflower(Texture texture, float x, float y) {
        super(texture, x, y);
        this.sunProduceTimer = 0f;
    }

    public Sunflower(Texture texture, AnimationClip animation, float x, float y) {
        super(texture, animation, x, y);
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
        // 直接调用GameManager的单例来增加太阳数量
        GameManager.getInstance().addSun(25);
        // 打印日志，方便调试
        System.out.println("Sunflower produced 25 sun. Total: " + GameManager.getInstance().getSun());
    }
}
