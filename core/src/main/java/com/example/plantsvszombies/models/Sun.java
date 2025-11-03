package com.example.plantsvszombies.models;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.example.plantsvszombies.Animation.AnimationClip;

public class Sun extends Actor {
    public enum SunState {
        GENERATED, FALLING, ON_GROUND, COLLECTED
    }

    private int value;
    private final Texture texture;
    private final Rectangle bounds;
    private SunState state;
    private AnimationClip animation;
    private float stateTime = 0f;

    private final float fallSpeed = 100f;
    private final float groundY;
    public boolean active = true;

    private float stillTime = 0f;

    private final Vector2 arcStart = new Vector2();
    private final Vector2 arcCtrl = new Vector2();
    private final Vector2 arcEnd = new Vector2();
    private float arcTimer = 0f;
    private float arcDuration = 0.6f; // 出场抛物线时长，可按需要调整

    public Sun(Texture texture, float startX, float startY, float groundY) {
        this(25, texture, null, startX, startY, groundY);
    }

    public Sun(Texture texture, AnimationClip animation, float startX, float startY, float groundY) {
        this(25, texture, animation, startX, startY, groundY);
    }

    public Sun(int value, Texture texture, AnimationClip animation, float startX, float startY, float groundY) {
        this.value = value;
        this.texture = texture;
        this.animation = animation;
        this.bounds = new Rectangle(startX, startY, texture.getWidth(), texture.getHeight());
        this.groundY = groundY;
        this.state = SunState.FALLING;
        setPosition(startX - bounds.width / 2f, startY - bounds.height / 2f); // 设置 Actor 的初始位置
        setScale(1f);
    }

    public void startArcTo(float endX, float endY, float height) {
        arcStart.set(getX(), getY());
        arcEnd.set(endX, endY);
        // 控制点在中点正上方 height 像素，形成“拱形”
        arcCtrl.set((arcStart.x + arcEnd.x) * 0.5f, Math.max(arcStart.y, arcEnd.y) + height);
        arcTimer = 0f;
        // 抛物线阶段
        state = SunState.GENERATED;
    }

    public static Sun PlantGeneratedSun(int value, Texture texture, AnimationClip animation, float startX, float startY, float groundY) {
        Sun s = new Sun(value, texture, animation, startX, startY, groundY);
        float endX = startX - s.getBounds().width / 2f + MathUtils.random(-60f, 60f);
        float endY = groundY;
        float height = MathUtils.random(70f, 110f);
        s.startArcTo(endX, endY, height);
        // 可按需要调整时长
        s.arcDuration = MathUtils.random(0.5f, 0.75f);
        return s;
    }


    public void update(float delta) {
        stateTime += delta;

        if (state == SunState.GENERATED) {
            arcTimer += delta;
            float t = Math.min(arcTimer / arcDuration, 1f);
            // 二次贝塞尔：B(t) = (1-t)^2*P0 + 2(1-t)t*C + t^2*P1
            float omt = 1f - t;
            float x = omt * omt * arcStart.x + 2f * omt * t * arcCtrl.x + t * t * arcEnd.x;
            float y = omt * omt * arcStart.y + 2f * omt * t * arcCtrl.y + t * t * arcEnd.y;

            setPosition(x, y);
            bounds.setPosition(x, y);

            if (t >= 1f) {
                // 抵达地面后转入停留
                state = SunState.ON_GROUND;
                stillTime = 0f;
            }
            return;
        }

        if (state == SunState.FALLING) {
            float newY = getY() - fallSpeed * delta;
            if (newY <= groundY) {
                newY = groundY;
                state = SunState.ON_GROUND;
            }
            setPosition(getX(), newY); // 更新 Actor 的位置
            bounds.setPosition(getX(), getY()); // 同步边界位置
            return;
        }
        if (state == SunState.ON_GROUND) {
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
            Actions.parallel(
                Actions.fadeOut(0.3f),
                Actions.alpha(0f, 0.3f)),
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
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(animation.getFrame(stateTime),
            getX(), getY(),
            getOriginX(), getOriginY(),
            texture.getWidth(), texture.getHeight(),
            getScaleX(), getScaleY(), getRotation()); // 使用 Actor 的位置
        batch.setColor(color.r, color.g, color.b, color.a);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getValue() { return value; }

    public SunState getState() { return state; }
}
