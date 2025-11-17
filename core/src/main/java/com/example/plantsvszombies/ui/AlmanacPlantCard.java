package com.example.plantsvszombies.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.example.plantsvszombies.Resource.Almanac.AlmanacPlant;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.models.Plants.BasePlant;
import com.example.plantsvszombies.screens.BaseGameScreen;

import java.util.Objects;

public class AlmanacPlantCard extends PlantCard {

    protected AlmanacPlant almanacPlant;
    protected TextureActor highlight;

    public AlmanacPlantCard(AlmanacPlant plant, Skin skin) {
        super(null, skin, BasePlant.create(plant.getPlantClass(), null, 0, 0));
        this.almanacPlant = plant;
        this.highlight = new TextureActor(AssetTexture.SEED_PACKET_FLASH.get(AssetMan.getInstance()));
        this.highlight.setVisible(false);
        this.addActor(highlight);

        this.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                highlight.setVisible(true);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                highlight.setVisible(false);
            }
        });
    }

    @Override public void drawShadow(Batch batch, float parentAlpha) {}

    public AlmanacPlant getAlmanacPlant() {
        return almanacPlant;
    }
}
