package com.example.plantsvszombies.models.Plants;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.example.plantsvszombies.AssetImpl.AnimationClip;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.screens.BaseGameScreen;

public class Sunflower extends BasePlant {

    public enum State {
        NORMAL,
        GENERATING,
        FADING
    }
    private float sunProduceTimer;
    private static final float SUN_PRODUCE_INTERVAL = 25f; // 每25秒产生一次太阳
    private State state = State.NORMAL;
    private static final float WINDOW = 1f; // 生成太阳动画变亮及变暗的持续时间

    public Sunflower() {
        super();
        this.name = "SunFlower";
        this.cost = 50;
        this.cooldown = 7.5f;
    }

    public Sunflower(Texture texture, AnimationClip animation, float x, float y) {
        this(null, texture, animation, x, y);
    }

    public Sunflower(BaseGameScreen screen, Texture texture, AnimationClip animation, float x, float y) {
        super(screen, texture, animation, x, y);
        this.name = "SunFlower";
        this.sunProduceTimer = 20f; // 初始时让它快产生太阳
        this.cost = 50;
        this.cooldown = 7.5f;
    }

    public Sunflower(BaseGameScreen screen, float x, float y) {
        this(screen,
            AssetTexture.SUNFLOWER_STILL.get(AssetMan.getInstance()),
            AssetAnimation.SUNFLOWER.get(AssetMan.getInstance()),
            x, y);
        this.shadow = AssetTexture.PLANT_SHADOW.get(AssetMan.getInstance());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        sunProduceTimer += delta;
//        if (sunProduceTimer >= SUN_PRODUCE_INTERVAL) {
//            sunProduceTimer = 0;
//            produceSun();
//            state = State.FADING;
//        } else if (sunProduceTimer >= SUN_PRODUCE_INTERVAL - 2f) {
//            state = State.GENERATING;
//        }
        if (sunProduceTimer >= SUN_PRODUCE_INTERVAL) {
            sunProduceTimer -= SUN_PRODUCE_INTERVAL;
            produceSun();
        } else if (sunProduceTimer >= SUN_PRODUCE_INTERVAL - WINDOW) {
            state = State.GENERATING;
        } else if (sunProduceTimer >= WINDOW) {
            state = State.NORMAL;
        }
    }

    @Override
    public void drawAnimation(SpriteBatch batch) {
        super.drawAnimation(batch);
        // 可以根据状态绘制不同的动画效果
        if (state == State.GENERATING) {
            // 绘制生成太阳的动画效果
            // 这里可以添加具体的动画绘制代码
            float alpha = MathUtils.sin((sunProduceTimer - (SUN_PRODUCE_INTERVAL - WINDOW)) / WINDOW * MathUtils.PI / 2f);
            batch.setColor(1, 1, 1, alpha);
            batch.draw(AssetAnimation.SUNFLOWER_BRIGHT.get(AssetMan.getInstance()).getFrame(stateTime), position.x, position.y);
            batch.setColor(1,1,1,1);
        } else if (state == State.FADING) {
            // 绘制淡出效果
            float alpha = MathUtils.cos((sunProduceTimer) / WINDOW * MathUtils.PI / 2f);
            batch.setColor(1, 1, 1, alpha);
            batch.draw(AssetAnimation.SUNFLOWER_BRIGHT.get(AssetMan.getInstance()).getFrame(stateTime), position.x, position.y);
            batch.setColor(1, 1, 1, 1);
        }
    }



    private void produceSun() {
        screen.spawnGeneratedSun(25, position.x + bounds.width / 2, position.y + bounds.height / 2);
        state = State.FADING;
    }
}
