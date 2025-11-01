package com.example.plantsvszombies.Resource;

import com.badlogic.gdx.audio.Sound;
import com.example.plantsvszombies.managers.AssetMan;

public enum AssetSound {

    SEED_LIFT("graphics/audio/seedlift.ogg"), // 从顶部选植物的音效
    TAP_CANCEL("graphics/audio/tap2.ogg"), // 取消选择植物的音效
    BUZZER("graphics/audio/buzzer.ogg"), // 无法选择植物的音效
    COLLECT_SUN("graphics/audio/points.ogg"), // 收集阳光的音效
    PLANT_1("graphics/audio/plant1.ogg"), // 种植植物的音效1
    PLANT_2("graphics/audio/plant2.ogg"), // 种植植物的音效2
    CHOMP("graphics/audio/chomp.ogg"), // 僵尸吃植物的音效1
    CHOMP_SOFT("graphics/audio/chompsoft.ogg"), // 僵尸吃植物的音效2
    GULP("graphics/audio/gulp.ogg"), // 僵尸吃掉植物的音效
    LIMBS_POP("graphics/audio/limbs_pop.ogg"), // 僵尸掉头的音效
    ZOMBIE_FALLING_1("graphics/audio/zombie_falling_1.ogg"), // 僵尸倒地的音效1
    ZOMBIE_FALLING_2("graphics/audio/zombie_falling_2.ogg"), // 僵尸倒地的音效2
    FIRST_ZOMBIE("graphics/audio/awooga.ogg"), // 第一只僵尸出现的音效
    LAWNMOWER_START("graphics/audio/lawnmower.ogg"), // 小推车的音效
    PEA_SHOOT("graphics/audio/throw.ogg"), // 射豌豆的音效
    PEA_SPLAT("graphics/audio/splat2.ogg"), // 豌豆攻击普通僵尸的音效
    PEA_SPLAT_CONEHEAD("graphics/audio/plastichit.ogg"), // 豌豆攻击路障僵尸的音效
    POTATO_MINE_RISE("graphics/audio/dirt_rise.ogg"), // 土豆地雷出土的音效
    POTATO_MINE_EXPLODE("graphics/audio/potato_mine.ogg"), // 土豆地雷爆炸的音效
    DAVE_SCREAM("graphics/audio/scream.ogg"), // 失败时戴夫的尖叫声
    CHERRY_BOMB_READY("graphics/audio/reverse_explosion.ogg"), // 樱桃炸弹准备爆炸的音效
    CHERRY_BOMB_EXPLODE("graphics/audio/cherrybomb.ogg"); // 樱桃炸弹爆炸的音效

    private final String filePath;
    private boolean loaded;

    AssetSound(String filePath) {
        this.filePath = filePath;
        this.loaded = false;
    }

    public void load(AssetMan assetMan) {
        if (loaded) { return; }
        assetMan.loadSound(this.filePath);
        loaded = true;
    }

    public static void loadAll(AssetMan assetMan) {
        for (AssetSound sound : AssetSound.values()) {
            sound.load(assetMan);
        }
    }

    public Sound get(AssetMan assetMan) {
        if (!loaded) { load(assetMan); }
        return assetMan.getSound(this.filePath);
    }

    public Sound loadAndGet(AssetMan assetMan) {
        load(assetMan);
        assetMan.getAssetManager().finishLoading();
        return assetMan.getSound(this.filePath);
    }
}
