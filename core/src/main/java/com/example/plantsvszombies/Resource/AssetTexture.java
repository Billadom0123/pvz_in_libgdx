package com.example.plantsvszombies.Resource;

import com.badlogic.gdx.graphics.Texture;
import com.example.plantsvszombies.managers.AssetMan;

public enum AssetTexture {
    // 加载页相关
    TITLE_SCREEN("graphics/Screen/titlescreen.jpg"),
    MAIN_SCREEN("graphics/Screen/MainMenu.png"),
    ADVENTURE_BUTTON("graphics/Screen/SelectorScreen_Adventure_button.png"),
    START_ADVENTURE_BUTTON("graphics/Screen/SelectorScreen_StartAdventure_Button1.png"),
    START_ADVENTURE_HIGHLIGHT("graphics/Screen/SelectorScreen_StartAdventure_Highlight.png"),
    MINI_GAME_BUTTON("graphics/Screen/SelectorScreen_Survival_button.png"), // 这，这对吗……
    MINI_GAME_HIGHLIGHT("graphics/Screen/SelectorScreen_Survival_highlight.png"),
    PUZZLE_BUTTON("graphics/Screen/SelectorScreen_Challenges_button.png"), // 一觉醒来，剩下三个按钮全部崩坏，唯独冒险不变……
    PUZZLE_HIGHLIGHT("graphics/Screen/SelectorScreen_Challenges_highlight.png"),
    CHALLENGE_BUTTON("graphics/Screen/SelectorScreen_Vasebreaker_button.png"),
    CHALLENGE_HIGHLIGHT("graphics/Screen/SelectorScreen_vasebreaker_highlight.png"),
    HELP_BUTTON("graphics/Screen/SelectorScreen_Help1.png"),
    HELP_HIGHLIGHT("graphics/Screen/SelectorScreen_Help2.png"),
    OPTIONS_BUTTON("graphics/Screen/SelectorScreen_Options1.png"),
    OPTIONS_HIGHLIGHT("graphics/Screen/SelectorScreen_Options2.png"),
    QUIT_BUTTON("graphics/Screen/SelectorScreen_Quit1.png"),
    QUIT_HIGHLIGHT("graphics/Screen/SelectorScreen_Quit2.png"),
    ALMANAC("graphics/Screen/SelectorScreen_Almanac.png"),
    ALMANAC_HIGHLIGHT("graphics/Screen/SelectorScreen_AlmanacHighlight.png"),

    // UI 相关，包括对话框和按钮
    DIALOG_HEADER("graphics/Skin/dialog_header.png"),
    DIALOG_TOP_LEFT("graphics/Skin/dialog_topleft.png"),
    DIALOG_TOP_MIDDLE("graphics/Skin/dialog_topmiddle.png"),
    DIALOG_TOP_RIGHT("graphics/Skin/dialog_topright.png"),
    DIALOG_CENTER_LEFT("graphics/Skin/dialog_centerleft.png"),
    DIALOG_CENTER_MIDDLE("graphics/Skin/dialog_centermiddle.png"),
    DIALOG_CENTER_RIGHT("graphics/Skin/dialog_centerright.png"),
    DIALOG_BOTTOM_LEFT("graphics/Skin/dialog_bottomleft.png"),
    DIALOG_BOTTOM_MIDDLE("graphics/Skin/dialog_bottommiddle.png"),
    DIALOG_BOTTOM_RIGHT("graphics/Skin/dialog_bottomright.png"),
    DIALOG_BIG_BOTTOM_LEFT("graphics/Skin/dialog_bigbottomleft.png"),
    DIALOG_BIG_BOTTOM_MIDDLE("graphics/Skin/dialog_bigbottommiddle.png"),
    DIALOG_BIG_BOTTOM_RIGHT("graphics/Skin/dialog_bigbottomright.png"),

    BUTTON_LEFT("graphics/Skin/button_left.png"),
    BUTTON_MIDDLE("graphics/Skin/button_middle.png"),
    BUTTON_RIGHT("graphics/Skin/button_right.png"),
    BUTTON_DOWN_LEFT("graphics/Skin/button_down_left.png"),
    BUTTON_DOWN_MIDDLE("graphics/Skin/button_down_middle.png"),
    BUTTON_DOWN_RIGHT("graphics/Skin/button_down_right.png"),

    // 图鉴相关
    ALMANAC_INDEX_BACKGROUND("graphics/Screen/Almanac_IndexBack.jpg"),
    ALMANAC_CLOSE_BUTTON("graphics/Screen/Almanac_CloseButton.png"),
    ALMANAC_CLOSE_BUTTON_HIGHLIGHT("graphics/Screen/Almanac_CloseButtonHighlight.png"),
    ALMANAC_PLANT_BACK("graphics/Screen/Almanac_PlantBack.jpg"),
    ALMANAC_PLANT_CARD("graphics/Screen/Almanac_PlantCard.png"),
    ALMANAC_GROUND_DAY("graphics/Screen/Almanac_GroundDay.jpg"),
    SEED_CHOOSER_BUTTON("graphics/Screen/SeedChooser_Button.png"),
    SEED_CHOOSER_BUTTON_GLOW("graphics/Screen/SeedChooser_Button_Glow.png"),
    SEED_PACKET_FLASH("graphics/Screen/SeedPacketFlash.png"),
    ALMANAC_INDEX_BUTTON("graphics/Screen/Almanac_IndexButton.png"),
    ALMANAC_INDEX_BUTTON_HIGHLIGHT("graphics/Screen/Almanac_IndexButtonHighlight.png"),
    ALMANAC_ZOMBIE_BACK("graphics/Screen/Almanac_ZombieBack.jpg"),
    ALMANAC_ZOMBIE_CARD("graphics/Screen/Almanac_ZombieCard.png"),
    ALMANAC_ZOMBIE_WINDOW_BORDER("graphics/Screen/Almanac_ZombieWindow2.png"),
    ALMANAC_ZOMBIE_WINDOW_BG("graphics/Screen/Almanac_ZombieWindow.png"),

    // 菜单相关
    MENU_BACKGROUND("graphics/Skin/options_menuback.png"),
    MENU_BACK_TO_GAME_BUTTON("graphics/Skin/options_backtogamebutton0.png"),




    MAP_0("graphics/Map/map0.jpg"),

    PEASHOOTER_STILL("graphics/Plants/Peashooter/Still.png"),
    PEASHOOTER("graphics/Plants/Peashooter/Peashooter.png"),
    PEA_SPLAT("graphics/Bullets/PeaNormalExplode/pea_splats.png"),
    PEA_NORMAL("graphics/Bullets/PeaNormal/PeaNormal_0.png"),
    PEA_SHADOW("graphics/Screen/Pea_Shadow.png"),

    SUNFLOWER_STILL("graphics/Plants/SunFlower/Still.png"),
    SUNFLOWER("graphics/Plants/SunFlower/SunFlower.png"),
    SUNFLOWER_BRIGHT("graphics/Plants/SunFlower/SunFlowerBright.png"),

    NORMAL_WALLNUT_STILL("graphics/Plants/WallNut/StillNormal.png"),
    NORMAL_WALLNUT("graphics/Plants/WallNut/WallNut.png"),
    NORMAL_WALLNUT_CRACKED_1("graphics/Plants/WallNut/Wallnut_cracked1.png"),
    NORMAL_WALLNUT_CRACKED_2("graphics/Plants/WallNut/Wallnut_cracked2.png"),
    BOOM_WALLNUT("graphics/Plants/WallNut/boom.png"),
    HUGE_WALLNUT("graphics/Plants/WallNut/huge.png"),

    POTATO_MINE_STILL("graphics/Plants/PotatoMine/Still.png"),
    POTATO_MINE_ARM_UP("graphics/Plants/PotatoMine/ArmUp.png"),
    POTATO_MINE_ARMED_UP("graphics/Plants/PotatoMine/ArmedUp.png"),
    POTATO_MINE_ARMED_UP_NO_LIGHT("graphics/Plants/PotatoMine/ArmedUpNoLight.png"),
    POTATO_MINE_EXPLOSION("graphics/Plants/PotatoMine/Explosion.png"),
    POTATO_MINE_EXPLOSION_WORD("graphics/Plants/PotatoMine/ExplosionSpudow.png"),

    CHERRY_BOMB_STILL("graphics/Plants/CherryBomb/Still.png"),
    CHERRY_BOMB("graphics/Plants/CherryBomb/CherryBomb.png"),
    CHERRY_BOMB_BOOM("graphics/Plants/CherryBomb/Boom.png"),

    PLANT_SHADOW("graphics/Screen/plantshadow2.png"),

    SNOW_PEASHOOTER_STILL("graphics/Plants/SnowPea/Still.png"),
    SNOW_PEASHOOTER("graphics/Plants/SnowPea/SnowPea.png"),
    SNOW_PEA_NORMAL("graphics/Bullets/SnowPea/ProjectileSnowPea.png"),
    SNOW_PEA_SPLAT("graphics/Bullets/SnowPea/SnowPea_splats.png"),

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

    BUCKETHEAD_ZOMBIE_STILL("graphics/Zombies/BucketheadZombie/Still.png"),
    BUCKETHEAD_ZOMBIE("graphics/Zombies/BucketheadZombie/BucketheadZombie.png"),
    BUCKETHEAD_ZOMBIE_ATTACK("graphics/Zombies/BucketheadZombie/BucketheadZombieAttack.png"),
    BUCKET("graphics/Zombies/BucketheadZombie/Zombie_bucket3.png"),

    ZOMBIE_SHADOW("graphics/Screen/plantshadow.png"),

    SEED_BANK("graphics/Screen/SeedBank.png"),
    SUN_STILL("graphics/Screen/SunStill.png"),
    SUN("graphics/Screen/Sun.png"),
    LAWNMOWER("graphics/Screen/car.png"),
    ZOMBIES_ATE_YOUR_BRAINS("graphics/Screen/ZombiesAteYourBrains.png"),
    SEED_PACKET_LARGER("graphics/Screen/SeedPacket_Larger.png"),
    SEED_PACKET_6F("graphics/Screen/SeedPacket_6f.png"),
    SEED_PACKET_5F("graphics/Screen/SeedPacket_5f.png"),
    SHOVEL_BANK("graphics/Screen/ShovelBank.png"),
    SHOVEL("graphics/Screen/Shovel.png"),

    WALLNUT_BOWLING_STRIPE("graphics/Screen/Wallnut_bowlingstripe.png"),
    CONVEYOR_BELT("graphics/Screen/ConveyorBelt.png"), // 传送带的履带
    CONVEYOR_BELT_BACKDROP("graphics/Screen/ConveyorBelt_backdrop.png"); // 传送带的背景

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
