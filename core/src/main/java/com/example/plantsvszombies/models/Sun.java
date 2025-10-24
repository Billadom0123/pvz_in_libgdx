package com.example.plantsvszombies.models;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Sun {
    public enum SunState {
        FALLING, ON_GROUND, COLLECTED
    }

    private Vector2 position;
    private final Texture texture;
    private final Rectangle bounds;
    private SunState state;

    private final float fallSpeed = 100f;
    private final float groundY;
    public boolean active = true;

    public Sun(Texture texture, float startX, float startY, float groundY) {
        this.texture = texture;
        this.position = new Vector2(startX, startY);
        this.bounds = new Rectangle(startX, startY, texture.getWidth(), texture.getHeight());
        this.groundY = groundY;
        this.state = SunState.FALLING;
    }

    public void update(float delta) {
        if (state == SunState.FALLING) {
            position.y -= fallSpeed * delta;
            if (position.y <= groundY) {
                position.y = groundY;
                state = SunState.ON_GROUND;
            }
            bounds.setPosition(position);
        }
    }

    public void collect() {
        state = SunState.COLLECTED;
        active = false; // 标记为待移除
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
