package com.example.plantsvszombies.models;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.example.plantsvszombies.Animation.AnimationClip;

public class Sun extends Actor {
    public enum SunState {
        FALLING, ON_GROUND, COLLECTED
    }

    private final Texture texture;
    private final Rectangle bounds;
    private SunState state;
    private AnimationClip animation;
    private float stateTime = 0f;

    private final float fallSpeed = 100f;
    private final float groundY;
    public boolean active = true;

    private float stillTime = 0f;

    public Sun(Texture texture, float startX, float startY, float groundY) {
        this.texture = texture;
        this.bounds = new Rectangle(startX, startY, texture.getWidth(), texture.getHeight());
        this.groundY = groundY;
        this.state = SunState.FALLING;
        setPosition(startX, startY); // 设置 Actor 的初始位置
        setScale(1f);
    }

    public Sun(Texture texture, AnimationClip animation, float startX, float startY, float groundY) {
        this.texture = texture;
        this.animation = animation;
        this.bounds = new Rectangle(startX, startY, texture.getWidth(), texture.getHeight());
        this.groundY = groundY;
        this.state = SunState.FALLING;
        setPosition(startX, startY); // 设置 Actor 的初始位置
        setScale(1f);
    }


    public void update(float delta) {
        stateTime += delta;
        if (state == SunState.FALLING) {
            float newY = getY() - fallSpeed * delta;
            if (newY <= groundY) {
                newY = groundY;
                state = SunState.ON_GROUND;
            }
            setPosition(getX(), newY); // 更新 Actor 的位置
            bounds.setPosition(getX(), getY()); // 同步边界位置
        } else if (state == SunState.ON_GROUND) {
            stillTime += delta;
            if (stillTime >= 10f) {
                addAction(Actions.sequence(Actions.fadeOut(0.5f),
                    Actions.run(() -> {
                    active = false; // 标记为待移除
                    remove();
                })));
            }
        }
    }

    public void collectTo(int targetX, int targetY) {
        state = SunState.COLLECTED;
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveTo(targetX, targetY, 0.5f),
                Actions.scaleTo(0.6f, 0.6f, 0.5f)),
            Actions.fadeOut(0.3f),
            Actions.run(() -> {
                active = false; // 标记为待移除
                remove();
            })
        ));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        update(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(animation.getFrame(stateTime),
            getX(), getY(),
            getOriginX(), getOriginY(),
            texture.getWidth(), texture.getHeight(),
            getScaleX(), getScaleY(), getRotation()); // 使用 Actor 的位置
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
