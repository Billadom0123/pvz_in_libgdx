package com.example.plantsvszombies.Animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationClip {
    private Animation<TextureRegion> animation;
    private float duration;
    private int frames;

    public AnimationClip(String filePath, int frames, float duration) {
        Texture texture = new Texture(filePath);
        TextureRegion[] animationFrames = TextureRegion.split(texture, texture.getWidth() / frames, texture.getHeight())[0];
        this.animation = new Animation<>(duration / frames, animationFrames);
        this.duration = duration;
        this.frames = frames;
    }

    public AnimationClip(Texture texture, int frames, float duration) {
        TextureRegion[] animationFrames = TextureRegion.split(texture, texture.getWidth() / frames, texture.getHeight())[0];
        this.animation = new Animation<>(duration / frames, animationFrames);
        this.duration = duration;
        this.frames = frames;
    }

    public AnimationClip(TextureRegion[] frames, float duration) {
        this.animation = new Animation<>(duration / frames.length, frames);
        this.duration = duration;
        this.frames = frames.length;
    }

    public AnimationClip(Animation<TextureRegion> animation, int frames, float duration) {
        this.animation = animation;
        this.duration = duration;
        this.frames = frames;
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

    public TextureRegion getFrame(float stateTime) {
        return animation.getKeyFrame(stateTime, true);
    }

    public float getWidth() {
        return animation.getKeyFrame(0).getRegionWidth();
    }

    public float getHeight() {
        return animation.getKeyFrame(0).getRegionHeight();
    }
}
