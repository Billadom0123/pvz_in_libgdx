package com.example.plantsvszombies.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;

public class GameDialog {

    private final Table dialogTable;

    public GameDialog(Skin skin, String message, float cameraX, float cameraY, float cameraWidth, float cameraHeight, float duration) {
        // 创建半透明背景
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f); // 黑色，50%透明
        pixmap.fill();
        Texture backgroundTexture = new Texture(pixmap);
        pixmap.dispose();

        // 创建文字标签
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        Label messageLabel = new Label(message, labelStyle);
        messageLabel.setFontScale(1.5f);
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true); // 设置自动换行

        // 创建Table并布局
        dialogTable = new Table();
        dialogTable.setBackground(new TextureRegionDrawable(backgroundTexture));
        dialogTable.setPosition(cameraX, cameraY);
        dialogTable.add(messageLabel).width(cameraWidth); // 设置宽度为相机宽度的80%
        dialogTable.pack();

        // 设置Table居中到相机视野范围
        dialogTable.setPosition(cameraX - dialogTable.getWidth() / 2, cameraY - cameraHeight * 2 / 5);

        // 设置冷却时间，自动移除对话框
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                dialogTable.remove();
            }
        }, duration);
    }

    public Table getDialogTable() {
        return dialogTable;
    }
}
