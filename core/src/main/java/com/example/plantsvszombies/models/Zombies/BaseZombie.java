package com.example.plantsvszombies.models.Zombies;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.example.plantsvszombies.AssetImpl.AnimationClip;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.SoundManager;
import com.example.plantsvszombies.models.DamageType;
import com.example.plantsvszombies.models.Plants.BasePlant;
import com.example.plantsvszombies.models.Texture.Falling;
import com.example.plantsvszombies.screens.BaseGameScreen;

import java.util.Arrays;

public abstract class BaseZombie {
    protected Vector2 position;
    protected float speed = MathUtils.random(15f, 20f); // 僵尸有自己的移动速度
    protected int health;
    protected Rectangle bounds;

    protected float stateTime = MathUtils.random(0f, 2f);
    protected boolean isHit = false;
    protected float hitStateTime = 0f;

    protected int MAX_HEALTH = 270;
    protected int DAMAGED_POINT = 180;
    protected int CRITICAL_POINT = 90;

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

    private boolean beExploded = false;
    private boolean isDying = false;     // 是否在播放死亡动画
    private float dieStateTime = 0f;     // 死亡动画计时
    private boolean removable = false;   // 是否可以从游戏中移除
    private boolean inCriticalState = false;

    private boolean isMovingToDoor = false;
    private float moveToDoorDuration;
    private float moveToDoorTimer;
    private float doorY;

    // 防具相关，包括一类（头盔）和二类（门）
    protected boolean hasHelmet = false;
    protected boolean hasShield = false;
    protected int helmetHealth = 0;
    protected int shieldHealth = 0;

    // 减速相关
    protected float snowTimer = 0f;


    protected Rectangle centerBounds;
    protected BaseGameScreen screen;

    // 材质动画相关
    protected Texture texture;
    protected Texture shadow;
    protected AnimationClip animation;
    protected AnimationClip normalAnimation;
    protected AnimationClip dieAnimation;
    protected AnimationClip explodeAnimation;
    protected AnimationClip attackAnimation;
    protected Texture headTexture;
    protected AnimationClip lostHeadAnimation;
    protected AnimationClip lostHeadAttackAnimation;

    public BaseZombie(BaseGameScreen screen, float x, float y) {
        this.screen = screen;
        this.position = new Vector2(x, y);
        this.health = 270;
    }

    public BaseZombie(Texture texture, AnimationClip animation, float x, float y) {
        this(null, texture, animation, x, y);
    }

    public BaseZombie(BaseGameScreen screen, Texture texture, AnimationClip animation, float x, float y) {
        this.screen = screen;
        this.texture = texture;
        this.animation = animation;
        this.position = new Vector2(x, y);
        this.health = 270;
        this.bounds = new Rectangle(x, y, animation.getWidth(), animation.getHeight());
        this.stateTime = 0f;
        this.centerBounds = new Rectangle(x + animation.getWidth() * 0.6f, y + animation.getHeight() * 0.2f,
            animation.getWidth() * 0.1f, animation.getHeight() * 0.3f);
    }

    public static BaseZombie create(Class<? extends BaseZombie> zombieClass, BaseGameScreen screen, float x, float y) {
        try {
            return zombieClass.getConstructor(BaseGameScreen.class, float.class, float.class)
                .newInstance(screen, x, y);
        } catch (Exception e) {
            Gdx.app.error("BaseZombie", "Failed to create zombie instance", e);
            return null;
        }
    }

    /**
     * 僵尸的更新逻辑
     * @param delta 时间差
     */
    public void update(float delta) {

        if (snowTimer > 0f) {
            snowTimer -= delta;
            delta /= 2f;
            if (snowTimer < 0f) snowTimer = 0f;
        }

        if (isDying) {
            dieStateTime += delta;
            // 死亡动画播完，允许移除
            if (animation.isAnimationFinished(dieStateTime)) {
                removable = true;
            }
            return;
        }

        if (isMovingToDoor) {
            moveToDoorTimer += delta;
            float progress = moveToDoorTimer / moveToDoorDuration;
            float targetY = doorY;
            float startY = position.y;
            position.y = MathUtils.lerp(startY, targetY, progress);
            bounds.y = position.y;
        }

        stateTime += delta;
        if (isHit) {
            hitStateTime += delta;
        }
        if (isAttacking && targetPlant != null) {
            // 如果正在攻击，就不移动，而是累加攻击计时器
            animation = attackAnimation;
            attackTimer += delta;
            eatSoundTimer += delta;
            if (attackTimer >= ATTACK_INTERVAL) {
                attackTimer = 0; // 重置计时器
                targetPlant.takeDamage(ATTACK_DAMAGE); // 对目标植物造成伤害
            }
            if (eatSoundTimer >= EAT_SOUND_INTERVAL) {
                eatSoundTimer = 0f;
                // 播放吃植物的声音
                SoundManager.getInstance().playRandomSound(Arrays.asList(AssetSound.CHOMP, AssetSound.CHOMP_SOFT));
            }
            if (targetPlant.getHealth() <= 0) {
                SoundManager.getInstance().playSound(AssetSound.GULP);
            }
        } else {
            // 如果没有在攻击，就从右向左移动
            animation = normalAnimation;
            position.x -= speed * delta;
            bounds.x -= speed * delta;
            centerBounds.x -= speed * delta;
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
        if (inCriticalState && !isDying) {
            health -= (int) (60 * delta);
        }
        if (health <= 0 && !isDying) {
            startDying();
        }
        if (health <= 0 && beExploded) {

        }
        if (health < CRITICAL_POINT && !inCriticalState) {
            inCriticalState = true;
            Falling falling = new Falling(
                headTexture, position.x + animation.getWidth() * .5f, position.y + animation.getHeight() * .5f,
                new Vector2(MathUtils.random(-30f, 30f), MathUtils.random(100f, 200f)),
                MathUtils.random(-90f, 90f), position.y);
            screen.addFalling(falling);
            SoundManager.getInstance().playSound(AssetSound.LIMBS_POP);
        }
    }

    /**
     * 静态僵尸的更新逻辑，用于图鉴和准备页面展示
     * @param delta 时间差
     */
    public void stillUpdate(float delta) {
        stateTime += delta;
    }

    public void moveToDoor(float duration, float doorY) {
        centerBounds = new Rectangle(-100, -100, 0, 0); // 移出中心碰撞框，避免继续与植物碰撞
        isMovingToDoor = true;
        moveToDoorDuration = duration;
        moveToDoorTimer = 0f;
        this.doorY = doorY;
    }


    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void drawAnimation(SpriteBatch batch) {
        if (isDying) {
            if (beExploded) {
                animation = explodeAnimation;
            } else {
                animation = dieAnimation;
            }
            batch.draw(animation.getFrame(dieStateTime), position.x, position.y);
            return;
        }
        if (inCriticalState) {
            if (isAttacking) { animation = lostHeadAttackAnimation; }
            else { animation = lostHeadAnimation; }
        }
        batch.draw(animation.getFrame(stateTime), position.x, position.y);
        if (snowTimer > 0f) {
            batch.setColor(0f, 60/255f, 1f, 1f);
            batch.draw(animation.getFrame(stateTime), position.x, position.y);
            batch.setColor(1f, 1f, 1f, 1f);
        }
        if (isHit && showHitEffect) {
            // 根据受击时间做一个从强到弱的衰减（0..1）
            float percent = hitStateTime / HIT_EFFECT_DURATION;
            float intensity = Math.min((float) (4 * (percent - Math.pow(percent, 2))), 1f);
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

    public void drawShadow(SpriteBatch batch) {
        if (isDying) return;
        batch.draw(shadow, centerBounds.x + (centerBounds.width - shadow.getWidth()) / 2f,
            position.y - shadow.getHeight() / 4f);
    }



    public void takeDamage(int damage, DamageType type) {
        // 如果已经可以移除，忽略伤害
        if (removable) return;
        this.isHit = true;
        // 先处理防具
        if (hasShield && shieldHealth > 0 && !type.isIgnoreShield()) {
            damageToDoor(damage, type);
        } else if (hasHelmet && helmetHealth > 0 && !type.isIgnoreHelmet()) {
            damageToHelmet(damage, type);
        } else {
            // 直接对僵尸本体造成伤害
            this.health -= damage;
            if (type == DamageType.EXPLOSIVE && health <= 0) {
                beExploded = true;
                startDying();
            }
        }
    }

    public void takeDamage(int damage) {
        takeDamage(damage, DamageType.NORMAL);
    }

    protected void damageToHelmet(int damage, DamageType type) {
        helmetHealth -= damage;
        if (helmetHealth <= 0) {
            hasHelmet = false;
            if (type == DamageType.BOWLING) return; // 保龄球伤害溢出
            health += helmetHealth; // 溢出伤害传递给僵尸本体
        }
    }

    protected void damageToDoor(int damage, DamageType type) {
        shieldHealth -= damage;
        if (shieldHealth <= 0) {
            hasShield = false;
            if (type == DamageType.BOWLING) return; // 保龄球伤害溢出
            if (type.isIgnoreHelmet()) {
                health += shieldHealth; // 溢出伤害传递给僵尸本体
            } else {
                if (hasHelmet && helmetHealth > 0) {
                    damageToHelmet(-shieldHealth, type); // 溢出伤害传递给头盔
                } else {
                    health += shieldHealth; // 溢出伤害传递给僵尸本体
                }
            }
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

    public void startDying() {
        isDying = true;
        dieStateTime = 0f;
        isAttacking = false;
        targetPlant = null;
        speed = 0f;
        centerBounds = new Rectangle(-100, -100, 0, 0); // 移出中心碰撞框，避免继续与植物碰撞
        SoundManager.getInstance().playRandomSound(Arrays.asList(AssetSound.ZOMBIE_FALLING_1, AssetSound.ZOMBIE_FALLING_2));
    }

    public boolean isRemovable() {
        return removable;
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

    public float getY() {
        return position.y;
    }

    public void stand() {
        speed = 0f;
    }

    public void setAnimation(AnimationClip animation) {
        this.animation = animation;
    }

    public boolean hasHelmet() {
        return hasHelmet;
    }

    public boolean hasShield() {
        return hasShield;
    }

    public Texture getTexture() { return texture; }

    public AnimationClip getAnimation() { return animation; }

    public void setPosition(float x, float y) {
        this.centerBounds.x += x - position.x;
        this.bounds.x += x - position.x;
        this.position.x = x;
        this.centerBounds.y += y - position.y;
        this.bounds.y += y - position.y;
        this.position.y = y;
    }

    // java
    public void setPositionByCenter(float centerX, float centerY) {
        if (centerBounds == null || bounds == null || position == null) return;
        float currentCenterX = centerBounds.x + centerBounds.width * 0.5f;
        float currentCenterY = centerBounds.y + centerBounds.height * 0.5f;
        float dx = centerX - currentCenterX;
        float dy = centerY - currentCenterY;
        position.x += dx;
        position.y += dy;
        bounds.x += dx;
        bounds.y += dy;
        centerBounds.x += dx;
        centerBounds.y += dy;
    }

    public void applySnowEffect(float duration) {
        this.snowTimer = duration;
    }
}
