package com.example.plantsvszombies.models.Plants;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.screens.GameScreen;

public class Sunflower extends BasePlant {

    public enum State {
        NORMAL,
        GENERATING
    }
    private float sunProduceTimer;
    private static final float SUN_PRODUCE_INTERVAL = 25f; // 每25秒产生一次太阳
    private State state = State.NORMAL;

    public Sunflower() {
        super();
        this.name = "SunFlower";
        this.cost = 50;
        this.cooldown = 7.5f;
    }

    public Sunflower(Texture texture, float x, float y) {
        this(texture, null, x, y);
    }

    public Sunflower(Texture texture, AnimationClip animation, float x, float y) {
        this(null, texture, animation, x, y);
    }

    public Sunflower(GameScreen screen, Texture texture, AnimationClip animation, float x, float y) {
        super(screen, texture, animation, x, y);
        this.name = "SunFlower";
        this.sunProduceTimer = 0f;
        this.cost = 50;
        this.cooldown = 7.5f;
    }

    public Sunflower(GameScreen screen, float x, float y) {
        this(screen,
            AssetTexture.SUNFLOWER_STILL.get(AssetMan.getInstance()),
            AssetAnimation.SUN_FLOWER.get(AssetMan.getInstance()),
            x, y);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        sunProduceTimer += delta;
        if (sunProduceTimer >= SUN_PRODUCE_INTERVAL) {
            sunProduceTimer = 0;
            produceSun();
            state = State.NORMAL;
        } else if (sunProduceTimer >= SUN_PRODUCE_INTERVAL - 2f) {
            // 在产生太阳前2秒切换到生成状态
            // 可以在这里添加生成动画的逻辑
            state = State.GENERATING;
        }
    }

    @Override
    public void drawAnimation(SpriteBatch batch) {
        super.drawAnimation(batch);
        // 可以根据状态绘制不同的动画效果
        if (state == State.GENERATING) {
            // 绘制生成太阳的动画效果
            // 这里可以添加具体的动画绘制代码
            float alpha = (float) (Math.sin((sunProduceTimer - (SUN_PRODUCE_INTERVAL - 2f)) / 4 * Math.PI));
            batch.setColor(1, 1, 1, alpha * .8f);
            batch.draw(AssetAnimation.SUN_FLOWER_BRIGHT.get(AssetMan.getInstance()).getFrame(stateTime), position.x, position.y);
            batch.setColor(1,1,1,1);
        }
    }



    private void produceSun() {
        screen.spawnGeneratedSun(25, position.x + bounds.width / 2, position.y + bounds.height / 2);
    }
}
