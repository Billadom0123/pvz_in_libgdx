package com.example.plantsvszombies.models.Texture;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.plantsvszombies.Animation.AnimationClip;

public class AnimationFalling {
    private AnimationClip animation;
    private float stateTime;
    private float x;
    private float y;

    public AnimationFalling(AnimationClip animation, float x, float y) {
        this.animation = animation;
        this.stateTime = 0f;
        this.x = x;
        this.y = y;
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void drawAnimation(SpriteBatch batch) {
        if (!animation.isAnimationFinished(stateTime)) {
            batch.draw(animation.getFrame(stateTime), x, y);
        }
    }

    public void setAnimation(AnimationClip animation) {
        this.animation = animation;
    }
}
