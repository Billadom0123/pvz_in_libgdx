package com.example.plantsvszombies.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.example.plantsvszombies.Resource.Almanac.AlmanacPlant;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.models.Plants.BasePlant;
import com.example.plantsvszombies.screens.BaseGameScreen;

import java.util.Objects;

public class AlmanacPlantCard extends PlantCard {

    protected AlmanacPlant almanacPlant;

    public AlmanacPlantCard(AlmanacPlant plant, Skin skin) {
        super(null, skin, BasePlant.create(plant.getPlantClass(), null, 0, 0));
        this.almanacPlant = plant;
    }

    @Override public void drawShadow(Batch batch, float parentAlpha) {}

    public AlmanacPlant getAlmanacPlant() {
        return almanacPlant;
    }
}
