package com.example.plantsvszombies.ui;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.example.plantsvszombies.Resource.AssetSkin;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.SoundManager;

public class PvzGraveButton extends PressableGroup {

    protected Texture defaultLeft;
    protected Texture defaultMiddle;
    protected Texture defaultRight;
    protected Texture downLeft;
    protected Texture downMiddle;
    protected Texture downRight;
    protected Texture currentLeft;
    protected Texture currentMiddle;
    protected Texture currentRight;

    protected String content;
    protected Skin pvzCustomUiSkin;


    // UI相关
    protected Label contentLabel;
    protected TextureActor leftActor;
    protected Array<TextureActor> middleActors = new Array<>();
    protected TextureActor rightActor;
    protected Sound clickSound;


    public PvzGraveButton(String content) {
        this.content = content;
        AssetMan assetMan = AssetMan.getInstance();
        this.defaultLeft = AssetTexture.BUTTON_LEFT.get(assetMan);
        this.defaultMiddle = AssetTexture.BUTTON_MIDDLE.get(assetMan);
        this.defaultRight = AssetTexture.BUTTON_RIGHT.get(assetMan);
        this.downLeft = AssetTexture.BUTTON_DOWN_LEFT.get(assetMan);
        this.downMiddle = AssetTexture.BUTTON_DOWN_MIDDLE.get(assetMan);
        this.downRight = AssetTexture.BUTTON_DOWN_RIGHT.get(assetMan);
        this.currentLeft = defaultLeft;
        this.currentMiddle = defaultMiddle;
        this.currentRight = defaultRight;
        this.clickSound = AssetSound.GRAVE_BUTTON.get(assetMan);
        this.pvzCustomUiSkin = AssetSkin.PVZ_CUSTOM_SKIN.get(assetMan);

        layout();

        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                contentLabel.setStyle(pvzCustomUiSkin.get("GreenBright18", Label.LabelStyle.class));
                refresh();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                contentLabel.setStyle(pvzCustomUiSkin.get("Green18", Label.LabelStyle.class));
                refresh();
            }


            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // 再次确认是在 hitArea 内（冗余校验）
                if (hit(x, y, true) != null) {
                    SoundManager.getInstance().playSound(clickSound);
                    currentLeft = downLeft;
                    currentMiddle = downMiddle;
                    currentRight = downRight;
                    contentLabel.setStyle(pvzCustomUiSkin.get("GreenBright18", Label.LabelStyle.class));
                    refresh();
                    return true;
                }
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                currentLeft = defaultLeft;
                currentMiddle = defaultMiddle;
                currentRight = defaultRight;
                contentLabel.setStyle(pvzCustomUiSkin.get("Green18", Label.LabelStyle.class));
                refresh();
            }
        });
    }

    public void layout() {
        contentLabel = new Label(content, pvzCustomUiSkin.get("Green18", Label.LabelStyle.class));
        contentLabel.setScale(2f);
        contentLabel.setAlignment(Align.center);
        contentLabel.pack();

        float contentWidth = contentLabel.getWidth();

        int middles = (int) Math.ceil((contentWidth - 30f) / currentMiddle.getWidth()); // 材质左右两侧有10f边界

        float width = 0f;

        leftActor = new TextureActor(currentLeft);
        leftActor.setPosition(width, 0f);
        this.addActor(leftActor);
        width += currentLeft.getWidth();

        for (int i = 0; i < middles; i++) {
            TextureActor middleActor = new TextureActor(currentMiddle);
            middleActor.setPosition(width, 0f);
            this.addActor(middleActor);
            middleActors.add(middleActor);
            width += currentMiddle.getWidth();
        }

        rightActor = new TextureActor(currentRight);
        rightActor.setPosition(width, 0f);
        this.addActor(rightActor);
        width += currentRight.getWidth();

        contentLabel.setPosition((width - contentLabel.getWidth()) / 2f, (currentLeft.getHeight() - contentLabel.getHeight()) / 2f + 2f); // 4f微调
        contentLabel.setTouchable(Touchable.disabled);
        this.addActor(contentLabel);

        this.setSize(width, currentLeft.getHeight());
    }

    public void refresh() {
        leftActor.setTexture(currentLeft);
        for (TextureActor middleActor : middleActors) {
            middleActor.setTexture(currentMiddle);
        }
        rightActor.setTexture(currentRight);
    }

    public void setContent(String content) {
        this.content = content;
        this.clearChildren();
        layout();
    }
}
