package com.example.plantsvszombies.Resource;

import com.badlogic.gdx.audio.Music;
import com.example.plantsvszombies.managers.AssetMan;

public enum AssetMusic {

    GRASSWALK("graphics/audio/Grasswalk.ogg"),
    LOSE_MUSIC("graphics/audio/losemusic.ogg"),
    THE_KING_PAPERMAN("graphics/audio/The-King-Paperman.mp3");


    private final String filePath;

    AssetMusic(String filePath) {
        this.filePath = filePath;
    }

    public void load(AssetMan assetMan) {
        assetMan.loadMusic(this.filePath);
    }

    public static void loadAll(AssetMan assetMan) {
        for (AssetMusic music : AssetMusic.values()) {
            music.load(assetMan);
        }
    }

    public Music get(AssetMan assetMan) {
        return assetMan.getMusic(this.filePath);
    }
}
