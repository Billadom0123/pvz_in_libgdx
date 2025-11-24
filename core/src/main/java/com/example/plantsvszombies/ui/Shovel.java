package com.example.plantsvszombies.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.SoundManager;

public class Shovel extends Group {

    private AssetMan assetMan;
    private TextureActor shovelBankTexture;
    private TextureActor shovelTexture;

    private boolean isSelected = false;

    public Shovel() {
        this.assetMan = AssetMan.getInstance();
        this.shovelBankTexture = new TextureActor(AssetTexture.SHOVEL_BANK.get(assetMan));
        this.shovelTexture = new TextureActor(AssetTexture.SHOVEL.get(assetMan));

        addActor(shovelBankTexture);
        addActor(shovelTexture);

        layout();
    }

    public void select() {
        SoundManager.getInstance().playSound(AssetSound.SHOVEL_SELECT);
        shovelTexture.setVisible(false);
        isSelected = true;
    }

    public void deselect() {
        SoundManager.getInstance().playSound(AssetSound.TAP2);
        shovelTexture.setVisible(true);
        isSelected = false;
    }

    public boolean isSelected() { return isSelected; }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void layout() {
        setSize(shovelBankTexture.getWidth(), shovelBankTexture.getHeight());

        shovelBankTexture.setTouchable(Touchable.disabled);
        shovelBankTexture.setPosition(0, 0);
        shovelTexture.setTouchable(Touchable.disabled);


        this.shovelTexture.setPosition(
                (getWidth() - shovelTexture.getWidth()) / 2,
                (getHeight() - shovelTexture.getHeight()) / 2
        );
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public Texture getShovelTexture() {
        return shovelTexture.getTexture();
    }
}
