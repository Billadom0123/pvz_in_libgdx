package com.example.plantsvszombies.models.Zombies;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.example.plantsvszombies.models.Plants.BasePlant;

public abstract class BaseZombie {
    protected Vector2 position;
    protected Texture texture;
    protected float speed; // 僵尸有自己的移动速度
    protected int health;
    protected Rectangle bounds;
    public boolean alive = true;

    // 僵尸独有的状态
    protected boolean isAttacking = false; // 当前是否在攻击植物
    protected BasePlant targetPlant = null;      // 攻击的目标
    protected float attackTimer = 0;         // 攻击计时器
    private static final float ATTACK_INTERVAL = 1.5f; // 攻击间隔
    private static final int ATTACK_DAMAGE = 10;       // 攻击力

    public BaseZombie(Texture texture, float x, float y) {
        this.texture = texture;
        this.position = new Vector2(x, y);
        this.speed = MathUtils.random(20f, 35f); // 给每个僵尸一个随机的速度，让它们看起来更自然
        this.health = 100;
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    /**
     * 僵尸的更新逻辑
     * @param delta 时间差
     */
    public void update(float delta) {
        if (isAttacking && targetPlant != null) {
            // 如果正在攻击，就不移动，而是累加攻击计时器
            attackTimer += delta;
            if (attackTimer >= ATTACK_INTERVAL) {
                attackTimer = 0; // 重置计时器
                targetPlant.takeDamage(ATTACK_DAMAGE); // 对目标植物造成伤害
            }
        } else {
            // 如果没有在攻击，就从右向左移动
            position.x -= speed * delta;
            bounds.setX(position.x); // 别忘了更新碰撞矩形的位置
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            alive = false;
        }
    }

    /**
     * 由外部逻辑（碰撞检测）调用，让僵尸进入攻击状态
     * @param plant 攻击目标
     */
    public void startAttacking(BasePlant plant) {
        isAttacking = true;
        targetPlant = plant;
    }

    /**
     * 当目标植物死亡或僵尸继续前进时，停止攻击
     */
    public void stopAttacking() {
        isAttacking = false;
        targetPlant = null;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getX() {
        return position.x;
    }
}
