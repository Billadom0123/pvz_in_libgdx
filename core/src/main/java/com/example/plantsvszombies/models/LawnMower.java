package com.example.plantsvszombies.models;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.screens.GameScreen;

public class LawnMower {
    public enum LawnMowerState {
        READY, ACTIVATED
    }

    private Vector2 position;
    private final Texture texture;
    private final Rectangle bounds;
    private LawnMowerState state;
    private final float speed = 400f;
    public boolean active = true;

    public LawnMower(Texture texture, float x, float y) {
        this.texture = texture;
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        this.state = LawnMowerState.READY;
    }

    public void update(float delta) {
        if (state == LawnMowerState.ACTIVATED) {
            position.x += speed * delta;
            bounds.setX(position.x);

            if (position.x > GameScreen.WORLD_WIDTH) {
                active = false; // 移出屏幕后失效
            }
        }
    }

    public void activate() {
        if (state == LawnMowerState.READY) {
            state = LawnMowerState.ACTIVATED;
            AssetMan.getInstance().playSound("graphics/audio/lawnmower.ogg", 0.6f);
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public LawnMowerState getState() {
        return state;
    }
}
