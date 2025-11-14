package com.example.plantsvszombies.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Rectangle;
import com.example.plantsvszombies.models.DamageType;
import com.example.plantsvszombies.models.Projectiles.BaseProjectile;
import com.example.plantsvszombies.models.Sun;
import com.example.plantsvszombies.models.Texture.Falling;

public interface BaseGameScreen extends Screen, InputProcessor {

    // 由植物/事件触发，生成投射物到游戏世界
    void spawnProjectile(BaseProjectile projectile);

    // 范围伤害（例如 CherryBomb）
    void doAoeDamage(Rectangle area, int damage, DamageType type);

    // 检查当前是否有足够的太阳（UI / 卡片使用）
    boolean isSunAvailable(int cost);

    // 触发相机抖动（例如发生爆炸时）
    void triggerCameraShake(float duration, float magnitude);

    // 在随机一行或事件处产生下落物（例如太阳）
    void addFalling(Falling falling);

    // 将粒子效果加入屏幕的粒子列表（PlantCard 等触发）
    void addParticle(ParticleEffect particle);

    // 外部（例如植物）直接生成阳光到场景中
    void spawnGeneratedSun(int value, float x, float y);

    // 查询指定高度是否有僵尸（AI 判定用）
    boolean anyZombieInHeight(float height);

    // Crazy Mode Activate!
    void beginCrazyMode();

    // 收集阳光
    void collectSun(Sun sun);

    int getRow(float y);

    int getCol(float x);

    void addGameDialog(String message, float duration);
}
