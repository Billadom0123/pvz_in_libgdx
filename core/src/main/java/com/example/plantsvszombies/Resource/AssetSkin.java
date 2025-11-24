package com.example.plantsvszombies.Resource;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.example.plantsvszombies.managers.AssetMan;

public enum AssetSkin {
    PVZ_CUSTOM_SKIN("graphics/Skin/pvz_custom_skin.json"),
    DEFAULT_SKIN("graphics/Skin/uiskin.json");

    private String filePath;
    private boolean loaded = false;

    AssetSkin(String filePath) {
        this.filePath = filePath;
    }

    public void load(AssetMan assetMan) {
        if (loaded) return;
        assetMan.loadSkin(filePath);
        loaded = true;
    }

    public static void loadAll(AssetMan assetMan) {
        for (AssetSkin skin : AssetSkin.values()) {
            skin.load(assetMan);
        }
    }

    public Skin get(AssetMan assetMan) {
        if (!loaded) load(assetMan);
        return assetMan.getSkin(filePath);
    }
}
