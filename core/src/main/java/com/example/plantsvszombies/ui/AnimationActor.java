package com.example.plantsvszombies.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.example.plantsvszombies.Animation.AnimationClip;

public class AnimationActor extends Actor {
    private AnimationClip animation;
    private TextureRegion region;
    private float stateTime = 0f;

    public AnimationActor(AnimationClip animation) {
        this.animation = animation;
        this.region = animation.getFrame(0f);
        setSize(region.getRegionWidth(), region.getRegionHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // 应用父级的颜色和透明度
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        // 绘制纹理
        batch.draw(region,
            getX(), getY(),
            getOriginX(), getOriginY(),
            getWidth(), getHeight(),
            getScaleX(), getScaleY(),
            getRotation()
        );
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        if (animation != null) {
            region = animation.getFrame(stateTime);
        }
    }

    public void setAnimation(AnimationClip animation) {
        this.animation = animation;
        this.region = animation.getFrame(stateTime);
        setSize(region.getRegionWidth(), region.getRegionHeight());
    }

    public AnimationClip getAnimation() {
        return animation;
    }

    public void resetAnimation() { this.stateTime = 0f; }
    public Texture getCurrentFrameTexture() { return region.getTexture(); }
    public void setStateTime(float time) { this.stateTime = time; }
}
