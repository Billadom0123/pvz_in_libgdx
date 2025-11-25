package com.example.plantsvszombies.models.Plants;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.example.plantsvszombies.AssetImpl.AnimationClip;
import com.example.plantsvszombies.Resource.AssetAnimation;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.SoundManager;
import com.example.plantsvszombies.models.Projectiles.Pea;
import com.example.plantsvszombies.screens.BaseGameScreen;

public class Peashooter extends BasePlant {

    private float shootTimer;
    private static final float SHOOT_INTERVAL = MathUtils.random(1.4f, 1.6f); // 平均每1.5秒发射一次

    public Peashooter(BaseGameScreen screen, Texture texture, AnimationClip animation, float x, float y) {
        super(texture, animation, x, y);
        this.name = "Peashooter";
        this.screen = screen;
        this.shootTimer = 0f;
        this.cost = 100;
        this.cooldown = 7.5f;
    }

    public Peashooter(BaseGameScreen screen, float x, float y) {
        this(screen,
            AssetTexture.PEASHOOTER_STILL.get(AssetMan.getInstance()),
            AssetAnimation.PEASHOOTER.get(AssetMan.getInstance()),
            x, y);
        this.shadow = AssetTexture.PLANT_SHADOW.get(AssetMan.getInstance());
    }

    /**
     * 实现自己的更新逻辑
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        shootTimer += delta;
        if (shootTimer >= SHOOT_INTERVAL && shouldFire()) {
            shootTimer = 0; // 重置计时器
            SoundManager.getInstance().playSound(AssetSound.PEA_SHOOT);
            fire();
        }
    }

    private boolean shouldFire() {
        return screen.anyZombieInHeight(position.y);
    }

    /**
     * 开火！
     */
    protected void fire() {
        // 计算子弹生成的位置（在豌豆射手的嘴部）
        float projectileX = position.x + bounds.width * 2 / 3;
        float projectileY = position.y + bounds.height / 2;
        // 请求GameScreen在指定位置生成一颗子弹
        screen.spawnProjectile(new Pea(projectileX, projectileY));
    }
}
