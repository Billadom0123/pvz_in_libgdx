package com.example.plantsvszombies.Resource;

import com.example.plantsvszombies.Animation.AnimationClip;
import com.example.plantsvszombies.managers.AssetMan;

public enum AssetAnimation {

    PEASHOOTER(AssetTexture.PEASHOOTER, 13, 1170),
    PEA_SPLAT(AssetTexture.PEA_SPLAT, 4, 200),

    SUN_FLOWER(AssetTexture.SUNFLOWER, 18, 1980),
    SUN_FLOWER_BRIGHT(AssetTexture.SUNFLOWER_BRIGHT, 18, 1980),

    WALL_NUT(AssetTexture.NORMAL_WALLNUT, 16, 1920),
    WALL_NUT_CRACKED1(AssetTexture.NORMAL_WALLNUT_CRACKED_1, 11, 1100),
    WALL_NUT_CRACKED2(AssetTexture.NORMAL_WALLNUT_CRACKED_2, 15, 1500),
    BOOM_WALLNUT(AssetTexture.BOOM_WALLNUT, 1, 1000),
    HUGE_WALLNUT(AssetTexture.HUGE_WALLNUT, 1, 1000),

    POTATO_MINE(AssetTexture.POTATO_MINE_STILL, 1, 1000),
    POTATO_MINE_ARM_UP(AssetTexture.POTATO_MINE_ARM_UP, 30, 1500),
    POTATO_MINE_ARMED(AssetTexture.POTATO_MINE_ARMED_UP, 8, 1600),
    POTATO_MINE_EXPLOSION(AssetTexture.POTATO_MINE_EXPLOSION, 1, 1000),

    CHERRY_BOMB(AssetTexture.CHERRY_BOMB, 7, 980),
    CHERRY_BOMB_BOOM(AssetTexture.CHERRY_BOMB_BOOM, 13, 1120),

    NORMAL_ZOMBIE(AssetTexture.NORMAL_ZOMBIE, 22, 2970),
    NORMAL_ZOMBIE_DIE(AssetTexture.NORMAL_ZOMBIE_DIE, 10, 2000),
    NORMAL_ZOMBIE_BOOM_DIE(AssetTexture.NORMAL_ZOMBIE_BOOM_DIE, 20, 2500),
    NORMAL_ZOMBIE_ATTACK(AssetTexture.NORMAL_ZOMBIE_ATTACK, 21, 2150),
    NORMAL_ZOMBIE_LOST_HEAD(AssetTexture.NORMAL_ZOMBIE_LOST_HEAD, 18, 3240),
    NORMAL_ZOMBIE_LOST_HEAD_ATTACK(AssetTexture.NORMAL_ZOMBIE_LOST_HEAD_ATTACK, 11, 1100),

    CONEHEAD_ZOMBIE(AssetTexture.CONEHEAD_ZOMBIE, 21, 3690),
    CONEHEAD_ZOMBIE_ATTACK(AssetTexture.CONEHEAD_ZOMBIE_ATTACK, 11, 990),

    BUCKETHEAD_ZOMBIE(AssetTexture.BUCKETHEAD_ZOMBIE, 15, 2700),
    BUCKETHEAD_ZOMBIE_ATTACK(AssetTexture.BUCKETHEAD_ZOMBIE_ATTACK, 11, 990),

    SUN(AssetTexture.SUN, 29, 2900),

    CONVEYOR_BELT(AssetTexture.CONVEYOR_BELT, 6, 200);

    private final AssetTexture source;
    private final int frames;
    private final float duration;
    private boolean loaded;

    AssetAnimation(AssetTexture source, int frames, float duration) {
        this.source = source;
        this.frames = frames;
        this.duration = duration;
        this.loaded = false;
    }

    public void load(AssetMan assetMan) {
        if (loaded) return;
        source.load(assetMan);
        assetMan.loadAnimation(this.name(), source.getFilePath(), frames, duration);
        loaded = true;
    }

    public static void loadAll(AssetMan assetMan) {
        for (AssetAnimation animation : AssetAnimation.values()) {
            animation.load(assetMan);
        }
    }

    public AnimationClip get(AssetMan assetMan) {
        if (!loaded) { load(assetMan); }
        return assetMan.getAnimation(this.name());
    }

    public AnimationClip loadAndGet(AssetMan assetMan) {
        load(assetMan);
        assetMan.getAssetManager().finishLoading();
        return assetMan.getAnimation(this.name());
    }
}
