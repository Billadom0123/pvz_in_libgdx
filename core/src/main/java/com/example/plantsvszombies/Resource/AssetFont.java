package com.example.plantsvszombies.Resource;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.example.plantsvszombies.managers.AssetMan;

public enum AssetFont {

    DWARVEN_STONECRAFT_SIZE12_DEFAULT("graphics/Skin/DwarvenStoneCraftCyr.otf", new FreeTypeFontGenerator.FreeTypeFontParameter() {{
        size = 12;
    }}),

    DWARVEN_STONECRAFT_SIZE20_DEFAULT("graphics/Skin/DwarvenStoneCraftCyr.otf", new FreeTypeFontGenerator.FreeTypeFontParameter() {{
        size = 20;
    }}),

    DWARVEN_STONECRAFT_SIZE24_DEFAULT("graphics/Skin/DwarvenStoneCraftCyr.otf", new FreeTypeFontGenerator.FreeTypeFontParameter() {{
        size = 24;
    }}),

    DWARVEN_STONECRAFT_SIZE30_DEFAULT("graphics/Skin/DwarvenStoneCraftCyr.otf", new FreeTypeFontGenerator.FreeTypeFontParameter() {{
        size = 30;
    }}),

    DWARVEN_STONECRAFT_SIZE48_DEFAULT("graphics/Skin/DwarvenStoneCraftCyr.otf", new FreeTypeFontGenerator.FreeTypeFontParameter() {{
        size = 48;
    }}),

    BRIANNE_S_HAND_SIZE20_DEFAULT("graphics/Skin/Brianne-s-hand-1.ttf", new FreeTypeFontGenerator.FreeTypeFontParameter() {{
        size = 20;
    }}),

    BRIANNE_S_HAND_SIZE14_DEFAULT("graphics/Skin/Brianne-s-hand-1.ttf", new FreeTypeFontGenerator.FreeTypeFontParameter() {{
        size = 14;
    }});




    private final String filePath;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private boolean loaded = false;

    AssetFont(String filePath, FreeTypeFontGenerator.FreeTypeFontParameter parameter) {
        this.filePath = filePath;
        this.parameter = parameter;
    }

    public void load(AssetMan assetMan) {
        if (loaded) { return; }
        assetMan.loadFont(this.name(), this.filePath, this.parameter);
        loaded = true;
    }

    public static void loadAll(AssetMan assetMan) {
        for (AssetFont font : AssetFont.values()) {
            font.load(assetMan);
        }
    }

    public BitmapFont get(AssetMan assetMan) {
        if (!loaded) { load(assetMan); }
        return assetMan.getFont(this.name());
    }

    public BitmapFont loadAndGet(AssetMan assetMan) {
        load(assetMan);
        assetMan.getAssetManager().finishLoading();
        return assetMan.getFont(this.name());
    }

}
