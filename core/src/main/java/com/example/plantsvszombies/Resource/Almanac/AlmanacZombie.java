package com.example.plantsvszombies.Resource.Almanac;

import com.example.plantsvszombies.models.Zombies.BaseZombie;
import com.example.plantsvszombies.models.Zombies.BucketHeadZombie;
import com.example.plantsvszombies.models.Zombies.ConeHeadZombie;
import com.example.plantsvszombies.models.Zombies.NormalZombie;

public enum AlmanacZombie {

    NORMAL_ZOMBIE(NormalZombie.class, "Almanac/Zombie/NormalZombie.json", .65f, .6f, .6f),
    CONEHEAD_ZOMBIE(ConeHeadZombie.class, "Almanac/Zombie/ConeheadZombie.json", .65f, .7f, .65f),
    BUCKETHEAD_ZOMBIE(BucketHeadZombie.class, "Almanac/Zombie/BucketheadZombie.json", .65f, .5f, .65f);

    private final Class<? extends BaseZombie> zombieClass;
    private final String jsonFilePath;
    private final float scale;
    private final float xCenterPercent;
    private final float yCenterPercent;

    AlmanacZombie(Class<? extends BaseZombie> zombieClass, String jsonFilePath, float scale, float xCenterPercent, float yCenterPercent) {
        this.zombieClass = zombieClass;
        this.jsonFilePath = jsonFilePath;
        this.scale = scale;
        this.xCenterPercent = xCenterPercent;
        this.yCenterPercent = yCenterPercent;
    }

    public Class<? extends BaseZombie> getZombieClass() {
        return zombieClass;
    }

    public String getJsonFilePath() {
        return jsonFilePath;
    }

    public float getScale() {
        return scale;
    }

    public float getXCenterPercent() {
        return xCenterPercent;
    }

    public float getYCenterPercent() {
        return yCenterPercent;
    }
}
