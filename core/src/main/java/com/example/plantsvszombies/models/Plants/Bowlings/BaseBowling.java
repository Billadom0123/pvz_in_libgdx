package com.example.plantsvszombies.models.Plants.Bowlings;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.example.plantsvszombies.AssetImpl.AnimationClip;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.SoundManager;
import com.example.plantsvszombies.models.DamageType;
import com.example.plantsvszombies.models.Plants.BasePlant;
import com.example.plantsvszombies.models.Zombies.BaseZombie;
import com.example.plantsvszombies.screens.BaseGameScreen;

public class BaseBowling extends BasePlant {
    protected float speedX = 200f; // 保龄球的移动速度
    protected float speedY = 200f; // 垂直速度
    protected int damageToZombie = 1800;
    protected int damageToHelmet = 900;
    protected int damageToShield = 900;
    protected int directionX = 1; // 移动方向，1表示向右，-1表示向左
    protected int directionY = 0; // 移动方向，1表示向上，-1表示向下，0表示正常向右
    protected int currentRow;
    protected boolean hasHitInRow = false;
    public Long rollingSoundId;
    protected float rotationSpeed = 0f;
    protected boolean bounceInX = false;

    public BaseBowling(BaseGameScreen screen, Texture texture, AnimationClip animation, float x, float y) {
        super(screen, texture, animation, x, y);
        this.bounds = new Rectangle(x + texture.getWidth() * .4f, y + texture.getHeight() * .4f,
            texture.getWidth() * .2f, texture.getHeight() * .2f);
        this.cooldown = 0f; // 保龄球没有冷却时间
        this.cost = 0;
        this.health = 4000;
        this.currentRow = screen.getRow(bounds.y + bounds.height / 2f);
        this.rotationSpeed = -speedX * 1.5f;
        this.shadow = AssetTexture.PLANT_SHADOW.get(AssetMan.getInstance());
    }

    public void beginRolling() {
        rollingSoundId = SoundManager.getInstance().loopSound(AssetSound.BOWLING_ROLLING);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        // 保龄球沿X轴移动
        position.x += speedX * delta * directionX;
        bounds.x += speedX * delta * directionX;
        position.y += speedY * delta * directionY;
        bounds.y += speedY * delta * directionY;
        int row = screen.getRow(bounds.y + bounds.height / 2f);
        if (row != currentRow) {
            hasHitInRow = false;
            currentRow = row;
        }
        if (screen.getRow(bounds.y) < 0) {
            directionY = 1; // 到达底部，向上移动
            hasHitInRow = false;
        } else if (screen.getRow(bounds.y + bounds.height) > 4) {
            directionY = -1; // 到达顶部，向下移动
            hasHitInRow = false;
        }
        if (bounceInX) {
            if (screen.getCol(bounds.x) < 0) directionX = 1;
            if (screen.getCol(bounds.x + bounds.width) > 8) directionX = -1;
        }
    }

    @Override
    public void drawAnimation(SpriteBatch batch) {
        // 保龄球不需要高亮效果，但是需要旋转
        batch.setColor(1, 1, 1, 1); // 确保颜色为白色
        batch.draw(animation.getFrame(stateTime), position.x, position.y,
                   texture.getWidth() / 2f, texture.getHeight() / 2f,
                   texture.getWidth(), texture.getHeight(),
                   1f, 1f,
                   stateTime * rotationSpeed); // 根据速度调整旋转角度
    }

    public void hit(BaseZombie zombie) {
        if (!hasHitInRow) {
            SoundManager.getInstance().playSound(AssetSound.BOWLING_IMPACT);
            if (zombie.hasShield()) {
                zombie.takeDamage(damageToShield, DamageType.BOWLING);
            } else if (zombie.hasHelmet()) {
                zombie.takeDamage(damageToHelmet, DamageType.BOWLING);
            } else {
                zombie.takeDamage(damageToZombie, DamageType.BOWLING);
            }
            hasHitInRow = true;
            changeDirection();
        }
    }

    public void changeDirection() {
        if (directionY == 0) {
            if (currentRow == 0) {
                directionY = 1; // 底行只能向上
            } else if (currentRow == 4) {
                directionY = -1; // 顶行只能向下
            } else {
                if (MathUtils.random() < 0.5f) {
                    directionY = 1; // 向上
                } else {
                    directionY = -1; // 向下
                }
            }
        } else if (directionY == 1) {
            directionY = -1; // 向下
        } else if (directionY == -1) {
            directionY = 1; // 向上
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        SoundManager.getInstance().stopSoundInstance(AssetSound.BOWLING_ROLLING, rollingSoundId);
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public boolean getBounceInX() { return bounceInX; }

    public void setBounceInX(boolean bounceInX) { this.bounceInX = bounceInX; }
}
