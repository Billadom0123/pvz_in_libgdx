package com.example.plantsvszombies.models.Texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Falling {
    public enum State { ARC, PHYSICS, ON_GROUND }

    private final Texture texture;
    private final Vector2 position;
    private final Vector2 velocity;

    private float rotation;
    private float rotationSpeed;
    private boolean active = true;

    private final float groundY; // 触地高度（像素坐标系下的Y）
    private boolean touchedGround = false;
    private boolean bounced = false;

    // 地面停留与淡出
    private float stayTimer = 0f;
    private float groundStay = 1.0f;     // 落地停留时长
    private float fadeDuration = 0.4f;   // 淡出时长
    private float alpha = 1f;

    // 状态机
    private State state = State.PHYSICS;

    public Falling(Texture texture, float x, float y, Vector2 initialVelocity, float rotationSpeed, float groundY) {
        this.texture = texture;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(initialVelocity);
        this.rotation = 0f;
        this.rotationSpeed = rotationSpeed;
        this.groundY = groundY;
    }

    public void setGroundStay(float seconds) {
        groundStay = Math.max(0f, seconds);
    }

    public void setFadeDuration(float seconds) {
        fadeDuration = Math.max(0f, seconds);
    }

    public void update(float delta) {
        if (!active) return;

        switch (state) {
            case PHYSICS: {
                if (!touchedGround || !bounced) {
                    position.add(velocity.x * delta, velocity.y * delta);
                    velocity.y -= 500f * delta; // 重力
                    rotation += rotationSpeed * delta;

                    if (position.y <= groundY) {

                        if (!bounced) {
                            velocity.y = -velocity.y * 0.5f; // 反弹
                            velocity.x = velocity.x * 0.7f;  // 摩擦减速
                            bounced = true;
                            state = State.PHYSICS; // 继续物理状态
                        } else {
                            position.y = groundY;
                            rotationSpeed = 0f;
                            stayTimer = 0f;
                            touchedGround = true;
                            velocity.setZero();
                            state = State.ON_GROUND;
                        }

                    }
                }
                break;
            }
            case ON_GROUND: {
                stayTimer += delta;
                if (stayTimer >= groundStay) {
                    float fd = stayTimer - groundStay;
                    if (fadeDuration > 0f) {
                        alpha = 1f - Math.min(fd / fadeDuration, 1f);
                        if (fd >= fadeDuration) active = false;
                    } else {
                        active = false;
                    }
                }
                break;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if (!active) return;
        batch.setColor(1, 1, 1, alpha);

        float ox = texture.getWidth() / 2f;
        float oy = texture.getHeight() / 2f;
        batch.draw(
            texture,
            position.x, position.y,
            ox, oy,
            texture.getWidth(), texture.getHeight(),
            1f, 1f,
            rotation,
            0, 0,
            texture.getWidth(), texture.getHeight(),
            false, false
        );

        batch.setColor(1,1,1,1);
    }

    public boolean isActive() { return active; }
    public State getState() { return state; }
    public Vector2 getPosition() { return position; }
}
