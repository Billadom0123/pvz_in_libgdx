package com.example.plantsvszombies.models.Zombies;


import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.models.Plants.BasePlant;

import java.util.List;

public abstract class BaseZombie {
    protected Vector2 position;
    protected Texture texture;
    protected float speed; // 僵尸有自己的移动速度
    protected int health;
    protected Rectangle bounds;
    public boolean alive = true;
    protected AnimationClip animation;
    protected float stateTime;
    protected boolean isHit = false;
    protected float hitStateTime = 0f;

    // 僵尸独有的状态
    protected boolean isAttacking = false; // 当前是否在攻击植物
    protected BasePlant targetPlant = null;      // 攻击的目标
    protected float attackTimer = 0;         // 攻击计时器
    private static final float ATTACK_INTERVAL = 0.04f; // 攻击间隔
    private static final int ATTACK_DAMAGE = 4;       // 攻击力
    private static final float HIT_EFFECT_DURATION = 0.1f;      // 受击动画持续时间
    private float hitDelayTimer = 0f;
    private boolean showHitEffect = true;
    private static final float HIT_EFFECT_INTERVAL = 0.4f;
    private static final float EAT_SOUND_INTERVAL = 0.5f; // 吃植物声音间隔
    private float eatSoundTimer = 0f; // 吃植物声音计时器


    protected Rectangle centerBounds;

    public BaseZombie(Texture texture, AnimationClip animation, float x, float y) {
        this.texture = texture;
        this.animation = animation;
        this.position = new Vector2(x, y);
        this.speed = MathUtils.random(15f, 20f); // 给每个僵尸一个随机的速度，让它们看起来更自然
        this.health = 270;
        this.bounds = new Rectangle(x, y, animation.getWidth(), animation.getHeight());
        stateTime = 0f;
        this.centerBounds = new Rectangle(x + animation.getWidth() * 0.4f, y + animation.getHeight() * 0.3f,
                animation.getWidth() * 0.2f, animation.getHeight() * 0.4f);
    }

    /**
     * 僵尸的更新逻辑
     * @param delta 时间差
     */
    public void update(float delta) {
        stateTime += delta;
        if (isHit) {
            hitStateTime += delta;
        }
        if (isAttacking && targetPlant != null) {
            // 如果正在攻击，就不移动，而是累加攻击计时器
            animation = AssetMan.getInstance().getAnimation("NormalZombieAttack");
            attackTimer += delta;
            eatSoundTimer += delta;
            if (attackTimer >= ATTACK_INTERVAL) {
                attackTimer = 0; // 重置计时器
                targetPlant.takeDamage(ATTACK_DAMAGE); // 对目标植物造成伤害
            }
            if (eatSoundTimer >= EAT_SOUND_INTERVAL) {
                eatSoundTimer = 0f;
                // 播放吃植物的声音
                AssetMan.getInstance().playRandomSound(List.of("graphics/audio/chomp.ogg", "graphics/audio/chompsoft.ogg"), 0.4f);
            }
        } else {
            // 如果没有在攻击，就从右向左移动
            animation = AssetMan.getInstance().getAnimation("NormalZombie");
            position.x -= speed * delta;
            bounds.setX(position.x); // 别忘了更新碰撞矩形的位置
            centerBounds.setX(position.x + animation.getWidth() * 0.4f);
        }
        if (hitStateTime >= HIT_EFFECT_DURATION) {
            isHit = false;
            hitStateTime = 0f;
        }
        if (!showHitEffect) {
            hitDelayTimer += delta;
            if (hitDelayTimer >= HIT_EFFECT_INTERVAL) {
                showHitEffect = true;
                hitDelayTimer = 0f;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void drawAnimation(SpriteBatch batch) {
        batch.draw(animation.getFrame(stateTime), position.x, position.y);
        if (isHit && showHitEffect) {
            // 根据受击时间做一个从强到弱的衰减（0..1）
            float percent = hitStateTime / HIT_EFFECT_DURATION;
            float intensity = Math.min((float) (4*(percent - Math.pow(percent, 2))), 1f);
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

    public void takeDamage(int damage) {
        this.isHit = true;
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

    public Rectangle getCenterBounds() {
        return centerBounds;
    }

    public float getX() {
        return position.x;
    }
}
