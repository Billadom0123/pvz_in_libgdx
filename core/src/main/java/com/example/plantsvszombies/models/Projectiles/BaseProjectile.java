package com.example.plantsvszombies.models.Projectiles;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.example.plantsvszombies.screens.GameScreen;

public class BaseProjectile {
    private Vector2 position;
    private Texture texture;
    private float speed;
    private int damage;
    private Rectangle bounds;
    public boolean active = true; // 标记是否存活，用于后续移除

    public BaseProjectile(Texture texture, int damage, float x, float y) {
        this.texture = texture;
        this.damage = damage;
        this.position = new Vector2(x, y);
        this.speed = 350f;
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public void update(float delta) {
        // 匀速向右移动
        position.x += speed * delta;
        bounds.setX(position.x);

        // 如果飞出屏幕右侧，则标记为待移除
        if (position.x > GameScreen.WORLD_WIDTH) {
            active = false;
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getDamage() {
        return damage;
    }
}
