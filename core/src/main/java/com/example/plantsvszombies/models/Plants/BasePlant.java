package com.example.plantsvszombies.models.Plants;


import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.example.plantsvszombies.Animation.AnimationClip;

public abstract class BasePlant {
    // --- 属性 ---
    protected Vector2 position; // 位置 (使用Vector2更专业)
    protected Texture texture;    // 纹理 (贴图)
    protected int maxHealth;      // 最大生命值
    protected int health;         // 生命值
    protected Rectangle bounds;   // 碰撞矩形，用于检测是否被击中或被攻击
    public boolean alive = true;  // 存活状态
    protected AnimationClip animation;
    protected float stateTime;

    protected boolean isHit = false;
    protected float hitStateTime = 0f;
    private static final float HIT_DURATION = 0.1f;      // 受击动画持续时间

    /**
     * 构造函数：当一个新植物被创建时调用
     * @param texture 它的外观贴图
     * @param x       它在世界中的X坐标
     * @param y       它在世界中的Y坐标
     */
    public BasePlant(Texture texture, float x, float y) {
        this.texture = texture;
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        this.maxHealth = 100;
        this.health = maxHealth; // 默认生命值
        this.stateTime = 0f;
    }

    public BasePlant(Texture texture, AnimationClip animation, float x, float y) {
        this.texture = texture;
        this.animation = animation;
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        this.maxHealth = 100;
        this.health = maxHealth; // 默认生命值
        this.stateTime = 0f;
    }

    /**
     * 抽象方法：更新逻辑。
     * 每个植物的每帧行为都不同（比如豌豆要射击，向日葵要生产），
     * 所以我们声明为抽象方法，强制子类必须自己实现(override)这个方法。
     * @param delta 距离上一帧的时间差，用于实现平滑的、与帧率无关的动画和计时
     */
    public void update(float delta) {
        stateTime += delta;
        if (isHit) {
            hitStateTime += delta;
            if (hitStateTime >= HIT_DURATION) {
                isHit = false;
                hitStateTime = 0f;
            }
        }
    }

    /**
     * 通用方法：将自己绘制到屏幕上
     * @param batch LibGDX的绘图批处理对象
     */
    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void drawAnimation(SpriteBatch batch) {
        batch.draw(animation.getFrame(stateTime), position.x, position.y);
        if (isHit) {
            // 根据受击时间做一个从强到弱的衰减（0..1）
            float t = Math.min(1f, hitStateTime / HIT_DURATION); // 0 -> 1
            float intensity = 1f - t; // 1 -> 0（初始最强）
            if (intensity > 0f) {
                // 设置为白色并开启加法混合（GL_SRC_ALPHA, GL_ONE）
                batch.setColor(1f, 1f, 1f, intensity);
                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

                // 再绘制一次帧，产生叠加变白效果
                batch.draw(animation.getFrame(stateTime), position.x, position.y);

                // 恢复默认混合和颜色（避免影响后续渲染）
                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }
    }

    /**
     * 通用方法：受到伤害
     * @param damage 伤害数值
     */
    public void takeDamage(int damage) {
        this.isHit = true;
        this.health -= damage;
        if (this.health <= 0) {
            this.alive = false; // 生命值耗尽，标记为死亡
        }
    }

    // --- Getters --- 提供给外部获取内部信息的安全途径
    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return position;
    }
}
