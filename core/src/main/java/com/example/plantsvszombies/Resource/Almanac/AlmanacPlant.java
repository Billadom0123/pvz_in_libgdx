package com.example.plantsvszombies.Resource.Almanac;

import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.models.Plants.*;

public enum AlmanacPlant {

    PEASHOOTER(Peashooter.class, "Almanac/Plant/Peashooter.json", AssetTexture.ALMANAC_GROUND_DAY),
    SUNFLOWER(Sunflower.class, "Almanac/Plant/Sunflower.json", AssetTexture.ALMANAC_GROUND_DAY),
    CHERRY_BOMB(CherryBomb.class, "Almanac/Plant/CherryBomb.json", AssetTexture.ALMANAC_GROUND_DAY),
    WALL_NUT(Wallnut.class, "Almanac/Plant/Wallnut.json", AssetTexture.ALMANAC_GROUND_DAY),
    POTATO_MINE(PotatoMine.class, "Almanac/Plant/PotatoMine.json", AssetTexture.ALMANAC_GROUND_DAY),
    SNOW_PEA(SnowPeashooter.class, "Almanac/Plant/SnowPea.json", AssetTexture.ALMANAC_GROUND_DAY);


    private final Class<? extends BasePlant> plantClass;
    private final String jsonFilePath;
    private final AssetTexture background;

    AlmanacPlant(Class<? extends BasePlant> plantClass, String jsonFilePath, AssetTexture background) {
        this.plantClass = plantClass;
        this.jsonFilePath = jsonFilePath;
        this.background = background;
    }

    public Class<? extends BasePlant> getPlantClass() {
        return plantClass;
    }

    public String getJsonFilePath() {
        return jsonFilePath;
    }

    public AssetTexture getBackground() {
        return background;
    }
}
