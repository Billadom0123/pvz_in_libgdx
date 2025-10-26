package com.example.plantsvszombies.models.Plants;


import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.models.Projectiles.BaseProjectile;
import com.example.plantsvszombies.models.Projectiles.Pea;
import com.example.plantsvszombies.screens.GameScreen;

public class Peashooter extends BasePlant {
    // 需要一个对GameScreen的引用，这样它才能调用GameScreen的方法来在世界中生成一个子弹实体
    private float shootTimer;
    private static final float SHOOT_INTERVAL = 1.5f; // 每2秒发射一次

    public Peashooter() {
        super();
        this.name = "Peashooter";
        this.cost = 100;
        this.cooldown = 7.5f;
    }

    public Peashooter(GameScreen screen, Texture texture, AnimationClip animation, float x, float y) {
        super(texture, animation, x, y);
        this.name = "Peashooter";
        this.screen = screen;
        this.shootTimer = 0f;
    }

    /**
     * 实现自己的更新逻辑
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        shootTimer += delta;
        if (shootTimer >= SHOOT_INTERVAL) {
            shootTimer = 0; // 重置计时器
            AssetMan.getInstance().playSound("graphics/audio/throw.ogg", 0.5f);
            fire();
        }
    }

    /**
     * 开火！
     */
    private void fire() {
        // 计算子弹生成的位置（在豌豆射手的嘴部）
        float projectileX = position.x + bounds.width;
        float projectileY = position.y + bounds.height / 2;
        // 请求GameScreen在指定位置生成一颗子弹
        screen.spawnProjectile(new Pea(screen.getAssetManager().get("graphics/Bullets/PeaNormal/PeaNormal_0.png", Texture.class), projectileX, projectileY));
    }
}
