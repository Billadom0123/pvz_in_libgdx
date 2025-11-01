package com.example.plantsvszombies.Resource;

import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.managers.AssetMan;

public enum AssetTexture {
    // 加载页相关
    TITLE_SCREEN("graphics/Screen/titlescreen.jpg"),




    MAP_0("graphics/Map/map0.jpg"),

    PEASHOOTER_STILL("graphics/Plants/Peashooter/Still.png"),
    PEASHOOTER("graphics/Plants/Peashooter/Peashooter.png"),
    PEA_SPLAT("graphics/Bullets/PeaNormalExplode/pea_splats.png"),
    PEA_NORMAL("graphics/Bullets/PeaNormal/PeaNormal_0.png"),

    SUNFLOWER_STILL("graphics/Plants/SunFlower/Still.png"),
    SUNFLOWER("graphics/Plants/SunFlower/SunFlower.png"),
    SUNFLOWER_BRIGHT("graphics/Plants/SunFlower/SunFlowerBright.png"),

    NORMAL_WALLNUT_STILL("graphics/Plants/WallNut/StillNormal.png"),
    NORMAL_WALLNUT("graphics/Plants/WallNut/WallNut.png"),
    NORMAL_WALLNUT_CRACKED_1("graphics/Plants/WallNut/Wallnut_cracked1.png"),
    NORMAL_WALLNUT_CRACKED_2("graphics/Plants/WallNut/Wallnut_cracked2.png"),

    POTATO_MINE_STILL("graphics/Plants/PotatoMine/Still.png"),
    POTATO_MINE_ARM_UP("graphics/Plants/PotatoMine/ArmUp.png"),
    POTATO_MINE_ARMED_UP("graphics/Plants/PotatoMine/ArmedUp.png"),
    POTATO_MINE_ARMED_UP_NO_LIGHT("graphics/Plants/PotatoMine/ArmedUpNoLight.png"),
    POTATO_MINE_EXPLOSION("graphics/Plants/PotatoMine/Explosion.png"),
    POTATO_MINE_EXPLOSION_WORD("graphics/Plants/PotatoMine/ExplosionSpudow.png"),

    CHERRY_BOMB_STILL("graphics/Plants/CherryBomb/Still.png"),
    CHERRY_BOMB("graphics/Plants/CherryBomb/CherryBomb.png"),
    CHERRY_BOMB_BOOM("graphics/Plants/CherryBomb/Boom.png"),

    NORMAL_ZOMBIE_STILL("graphics/Zombies/Zombie/ZombieStill.png"),
    NORMAL_ZOMBIE("graphics/Zombies/Zombie/Zombie.png"),
    NORMAL_ZOMBIE_ATTACK("graphics/Zombies/Zombie/ZombieAttack.png"),
    NORMAL_ZOMBIE_HEAD("graphics/Zombies/Zombie/ZombieHead.png"),
    NORMAL_ZOMBIE_DIE("graphics/Zombies/Zombie/ZombieDie.png"),
    NORMAL_ZOMBIE_BOOM_DIE("graphics/Zombies/Zombie/BoomDie.png"),
    NORMAL_ZOMBIE_LOST_HEAD("graphics/Zombies/Zombie/ZombieLostHead.png"),
    NORMAL_ZOMBIE_LOST_HEAD_ATTACK("graphics/Zombies/Zombie/ZombieLostHeadAttack.png"),

    CONEHEAD_ZOMBIE_STILL("graphics/Zombies/ConeheadZombie/Still.png"),
    CONEHEAD_ZOMBIE("graphics/Zombies/ConeheadZombie/ConeheadZombie.png"),
    CONEHEAD_ZOMBIE_ATTACK("graphics/Zombies/ConeheadZombie/ConeheadZombieAttack.png"),
    CONE("graphics/Zombies/ConeheadZombie/Zombie_cone2.png"),


    SEED_BANK("graphics/Screen/SeedBank.png"),
    SUN_STILL("graphics/Screen/SunStill.png"),
    SUN("graphics/Screen/Sun.png"),
    LAWNMOWER("graphics/Screen/car.png"),
    ZOMBIES_ATE_YOUR_BRAINS("graphics/Screen/ZombiesAteYourBrains.png"),
    SEED_PACKET_LARGER("graphics/Screen/SeedPacket_Larger.png"),
    SEED_PACKET_6F("graphics/Screen/SeedPacket_6f.png"),
    SEED_PACKET_5F("graphics/Screen/SeedPacket_5f.png");

    private final String filePath;
    private boolean loaded;

    AssetTexture(String filePath) {
        this.filePath = filePath;
        this.loaded = false;
    }

    public void load(AssetMan assetMan) {
        if (loaded) { return; }
        assetMan.loadTexture(this.filePath);
        loaded = true;
    }

    public static void loadAll(AssetMan assetMan) {
        for (AssetTexture texture : AssetTexture.values()) {
            texture.load(assetMan);
        }
    }

    public Texture get(AssetMan assetMan) {
        if (!loaded) { load(assetMan); }
        return assetMan.getTexture(this.filePath);
    }

    public String getFilePath() {
        return filePath;
    }

    public Texture loadAndGet(AssetMan assetMan) {
        load(assetMan);
        assetMan.getAssetManager().finishLoading();
        return assetMan.getTexture(this.filePath);
    }
}
