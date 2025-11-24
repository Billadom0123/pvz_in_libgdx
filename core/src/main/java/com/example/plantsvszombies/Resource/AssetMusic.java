package com.example.plantsvszombies.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.example.plantsvszombies.managers.AssetMan;

public enum AssetMusic {

    // 加载页相关
    MAINTHEME("graphics/audio/MainTheme.ogg"),

    GRASSWALK("graphics/audio/Grasswalk.ogg"),
    LOSE_MUSIC("graphics/audio/losemusic.ogg"),
    THE_KING_PAPERMAN("graphics/audio/The-King-Paperman.mp3"),
    MINI_GAME("graphics/audio/MiniGame.ogg"),
    HLSNW("graphics/audio/hlsnw.ogg"),
    CHOOSE_YOUR_SEEDS("graphics/audio/ChooseYourSeeds.ogg");


    private final String filePath;
    private boolean loaded;

    AssetMusic(String filePath) {
        this.filePath = filePath;
        this.loaded = false;
    }

    public void load(AssetMan assetMan) {
        if (loaded) { return; }
        Gdx.app.log("AssetMusic", "Load Music: " + name());
        assetMan.loadMusic(this.filePath);
        loaded = true;
    }

    public static void loadAll(AssetMan assetMan) {
        for (AssetMusic music : AssetMusic.values()) {
            music.load(assetMan);
        }
    }

    public Music get(AssetMan assetMan) {
        if (!loaded) { load(assetMan); }
        return assetMan.getMusic(this.filePath);
    }

    public Music loadAndGet(AssetMan assetMan) {
        load(assetMan);
        assetMan.getAssetManager().finishLoading();
        return assetMan.getMusic(this.filePath);
    }
}
