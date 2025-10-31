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

    POTATO_MINE(AssetTexture.POTATO_MINE_STILL, 1, 1000),
    POTATO_MINE_ARM_UP(AssetTexture.POTATO_MINE_ARM_UP, 30, 1500),
    POTATO_MINE_ARMED(AssetTexture.POTATO_MINE_ARMED_UP, 8, 1600),
    POTATO_MINE_EXPLOSION(AssetTexture.POTATO_MINE_EXPLOSION, 1, 1000),

    NORMAL_ZOMBIE(AssetTexture.NORMAL_ZOMBIE, 22, 2970),
    NORMAL_ZOMBIE_DIE(AssetTexture.NORMAL_ZOMBIE_DIE, 10, 2000),
    NORMAL_ZOMBIE_ATTACK(AssetTexture.NORMAL_ZOMBIE_ATTACK, 21, 2150),
    NORMAL_ZOMBIE_LOST_HEAD(AssetTexture.NORMAL_ZOMBIE_LOST_HEAD, 18, 3240),
    NORMAL_ZOMBIE_LOST_HEAD_ATTACK(AssetTexture.NORMAL_ZOMBIE_LOST_HEAD_ATTACK, 11, 1100),

    SUN(AssetTexture.SUN, 29, 2900);

    private final AssetTexture source;
    private final int frames;
    private final float duration;

    AssetAnimation(AssetTexture source, int frames, float duration) {
        this.source = source;
        this.frames = frames;
        this.duration = duration;
    }

    public void load(AssetMan assetMan) {
        assetMan.loadAnimation(this.name(), source.getFilePath(), frames, duration);
    }

    public static void loadAll(AssetMan assetMan) {
        for (AssetAnimation animation : AssetAnimation.values()) {
            animation.load(assetMan);
        }
    }

    public AnimationClip get(AssetMan assetMan) {
        return assetMan.getAnimation(this.name());
    }
}
