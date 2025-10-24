package com.example.plantsvszombies.Animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationClip {
    private Animation<TextureRegion> animation;
    private float duration;
    private int frames;
    private float stateTime;

    public AnimationClip(String filePath, int frames, float duration) {
        Texture texture = new Texture(filePath);
        TextureRegion[] animationFrames = TextureRegion.split(texture, texture.getWidth() / frames, texture.getHeight())[0];
        this.animation = new Animation<>(duration / frames, animationFrames);
        this.duration = duration;
        this.frames = frames;
        this.stateTime = 0f;
    }

    public AnimationClip(Texture texture, int frames, float duration) {
        TextureRegion[] animationFrames = TextureRegion.split(texture, texture.getWidth() / frames, texture.getHeight())[0];
        this.animation = new Animation<>(duration / frames, animationFrames);
        this.duration = duration;
        this.frames = frames;
        this.stateTime = 0f;
    }

    public AnimationClip(TextureRegion[] frames, float duration) {
        this.animation = new Animation<>(duration / frames.length, frames);
        this.duration = duration;
        this.frames = frames.length;
        this.stateTime = 0f;
    }

    public AnimationClip(Animation<TextureRegion> animation, int frames, float duration) {
        this.animation = animation;
        this.duration = duration;
        this.frames = frames;
        this.stateTime = 0f;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public double getDuration() {
        return duration;
    }

    public int getFrames() {
        return frames;
    }

    public TextureRegion getFrame() {
        return animation.getKeyFrame(stateTime, true);
    }

    /**
     * 设置动画的状态时间
     * @param stateTime 状态时间
     */
    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public void update(float delta) {
        stateTime += delta;
    }
}
