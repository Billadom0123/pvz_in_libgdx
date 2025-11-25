// java
package com.example.plantsvszombies.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.example.plantsvszombies.Resource.AssetFont;
import com.example.plantsvszombies.Resource.AssetSkin;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;

public class PvzDialogBox extends Group {

    // 9 宫格边框块（复用做边缘；中间块用循环平铺新建）
    private final TextureActor header;
    private final TextureActor topLeft;
    private final TextureActor topMiddle;   // 仅用于量尺寸
    private final TextureActor topRight;
    private final TextureActor centerLeft;
    private final TextureActor centerMiddle; // 仅用于量尺寸
    private final TextureActor centerRight;
    private final TextureActor bottomLeft;
    private final TextureActor bottomMiddle; // 仅用于量尺寸
    private final TextureActor bottomRight;

    private float dragOffsetX;
    private float dragOffsetY;

    private Label titleLabel;
    private Label contentLabel;
    private PvzGraveButton[] graveButtons;

    // 布局参数
    private float contentMaxWidth = 250f; // 内容文字最大宽度（固定宽以触发换行）
    private static final float GAP_X = 16f;   // 内容区左右内边距
    private static final float PAD_Y = 20f;   // 内容区上下内边距
    private static final float GAP_Y = 30f;    // 标题与内容之间的垂直间距
    private static final float HEADER_OVERLAP = 20f; // header 覆盖到框内的像素

    public PvzDialogBox(String title, String content, PvzGraveButton[] buttons) {
        AssetMan assetMan = AssetMan.getInstance();
        this.header = new TextureActor(AssetTexture.DIALOG_HEADER.get(assetMan));
        this.topLeft = new TextureActor(AssetTexture.DIALOG_TOP_LEFT.get(assetMan));
        this.topMiddle = new TextureActor(AssetTexture.DIALOG_TOP_MIDDLE.get(assetMan));
        this.topRight = new TextureActor(AssetTexture.DIALOG_TOP_RIGHT.get(assetMan));
        this.centerLeft = new TextureActor(AssetTexture.DIALOG_CENTER_LEFT.get(assetMan));
        this.centerMiddle = new TextureActor(AssetTexture.DIALOG_CENTER_MIDDLE.get(assetMan));
        this.centerRight = new TextureActor(AssetTexture.DIALOG_CENTER_RIGHT.get(assetMan));
        this.bottomLeft = new TextureActor(AssetTexture.DIALOG_BOTTOM_LEFT.get(assetMan));
        this.bottomMiddle = new TextureActor(AssetTexture.DIALOG_BOTTOM_MIDDLE.get(assetMan));
        this.bottomRight = new TextureActor(AssetTexture.DIALOG_BOTTOM_RIGHT.get(assetMan));
        this.graveButtons = buttons;

        layout(title, content);

        // 拖拽
        this.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dragOffsetX = x;
                dragOffsetY = y;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                float newX = getX() + (x - dragOffsetX);
                float newY = getY() + (y - dragOffsetY);
                clampToStage(newX, newY);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // 最终再保证不超出舞台范围
                clampToStage(getX(), getY());
            }
        });
    }

    public void layout(String title, String content) {
        clearChildren();

        Skin uiSkin = AssetSkin.PVZ_CUSTOM_SKIN.get(AssetMan.getInstance());

        titleLabel = new Label(title, uiSkin.get("DialogTitle", Label.LabelStyle.class));
        titleLabel.setAlignment(Align.center);

        contentLabel = new Label(content, uiSkin.get("DialogContent", Label.LabelStyle.class));
        contentLabel.setWrap(true);
        contentLabel.setWidth(contentMaxWidth); // 固定宽度触发换行
        contentLabel.setAlignment(Align.center);
//        contentLabel.invalidateHierarchy();
        String contentText = content == null ? "" : content;
        GlyphLayout gl = new GlyphLayout();
        gl.setText(contentLabel.getStyle().font, contentText);
        float naturalContentW = Math.max(1f, gl.width); // 自然宽度（单行、不限宽）
        float targetContentW = Math.min(naturalContentW, contentMaxWidth); // 仅超出才夹到上限

        contentLabel.setWidth(targetContentW);
        contentLabel.invalidateHierarchy(); // 让 getPrefHeight 按当前宽度重算

        float titlePrefW = titleLabel.getPrefWidth();
        float titlePrefH = titleLabel.getPrefHeight();

        // 2) 计算内容区所需尺寸（由 Label 决定）
        float desiredInnerW = Math.max(targetContentW, titlePrefW) - 150f;
        float desiredInnerH = contentLabel.getHeight() + GAP_Y;

        // 3) 计算需要的中间列/行数（按中间块尺寸取整）
        float tileW = centerMiddle.getWidth();
        float tileH = centerMiddle.getHeight();
        int cols = Math.max(0, (int)Math.ceil(desiredInnerW / tileW));
        int rows = Math.max(0, (int)Math.ceil(desiredInnerH / tileH));

        float innerW = cols * tileW;
        float innerH = rows * tileH;

        float leftW = topLeft.getWidth();
        float rightW = topRight.getWidth();
        float topH = topLeft.getHeight();
        float bottomH = bottomLeft.getHeight();

        float totalW = leftW + innerW + rightW;
        contentLabel.setWidth(totalW - 70f);
        float baseH = bottomH + innerH + topH;
        float headerY = baseH - HEADER_OVERLAP;
        float totalH = headerY + header.getHeight();

        // 底行
        float x0 = 0f, y0 = 0f;
        bottomLeft.setPosition(x0, y0);
        addActor(bottomLeft);
        for (int c = 0; c < cols; c++) {
            TextureActor mid = new TextureActor(AssetTexture.DIALOG_BOTTOM_MIDDLE.get(AssetMan.getInstance()));
            mid.setPosition(leftW + c * tileW, y0);
            addActor(mid);
        }
        bottomRight.setPosition(leftW + innerW, y0);
        addActor(bottomRight);

        // 中间行
        for (int r = 0; r < rows; r++) {
            float y = bottomH + r * tileH;
            TextureActor l = new TextureActor(AssetTexture.DIALOG_CENTER_LEFT.get(AssetMan.getInstance()));
            l.setPosition(x0, y);
            addActor(l);
            for (int c = 0; c < cols; c++) {
                TextureActor mid = new TextureActor(AssetTexture.DIALOG_CENTER_MIDDLE.get(AssetMan.getInstance()));
                mid.setPosition(leftW + c * tileW, y);
                addActor(mid);
            }
            TextureActor rr = new TextureActor(AssetTexture.DIALOG_CENTER_RIGHT.get(AssetMan.getInstance()));
            rr.setPosition(leftW + innerW, y);
            addActor(rr);
        }

        // 顶行
        float topY = bottomH + innerH;
        topLeft.setPosition(x0, topY);
        addActor(topLeft);
        for (int c = 0; c < cols; c++) {
            TextureActor mid = new TextureActor(AssetTexture.DIALOG_TOP_MIDDLE.get(AssetMan.getInstance()));
            mid.setPosition(leftW + c * tileW, topY);
            addActor(mid);
        }
        topRight.setPosition(leftW + innerW, topY);
        addActor(topRight);

        header.setPosition((totalW - header.getWidth()) / 2f, headerY);
        addActor(header);

        // 6) 放置 Label（在内容区内居中/对齐）
        float contentAreaX = leftW;
        float contentAreaW = innerW;

        float titleY = headerY - PAD_Y - titleLabel.getHeight(); // 标题在 header 下方
        float contentY = titleY - GAP_Y - contentLabel.getHeight();

        // 标题横向占满内容区，文本居中
        titleLabel.setSize(contentAreaW, titlePrefH);
        titleLabel.setPosition(contentAreaX, titleY);
        titleLabel.setAlignment(Align.center);
        addActor(titleLabel);

        // 内容按固定宽度放置，水平居中
        float contentX = contentAreaX + (contentAreaW - contentLabel.getWidth()) / 2f;
        contentLabel.setPosition(contentX, contentY);
        addActor(contentLabel);

        // 放置按钮组（如有）
        if (graveButtons != null && graveButtons.length > 0) {
            float buttonsTotalW = 0f;
            for (PvzGraveButton btn : graveButtons) {
                buttonsTotalW += btn.getWidth();
            }
            float currX = (totalW - buttonsTotalW - (graveButtons.length - 1) * GAP_X) / 2f;
            for (PvzGraveButton btn : graveButtons) {
                btn.setPosition(currX, (bottomH - btn.getHeight()) / 2f);
                addActor(btn);
                currX += btn.getWidth() + GAP_X;
            }
        }

        // 7) 最终大小
        setSize(totalW, totalH);
    }
//
//    @Override
//    public void draw(Batch batch, float parentAlpha) {
//        super.draw(batch, parentAlpha);
//        batch.end();
//        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.rect(titleLabel.getX(), titleLabel.getY(), titleLabel.getWidth(), titleLabel.getHeight());
//        shapeRenderer.setColor(Color.GREEN);
//        shapeRenderer.rect(contentLabel.getX(), contentLabel.getY(), contentLabel.getWidth(), contentLabel.getHeight());
//        shapeRenderer.end();
//        batch.begin();
//    }

    // 动态修改内容最大宽度并重排
    public void setContentMaxWidth(float maxWidth) {
        this.contentMaxWidth = Math.max(1f, maxWidth);
        // 复用当前文本重排
        String title = titleLabel != null ? titleLabel.getText().toString() : "";
        String content = contentLabel != null ? contentLabel.getText().toString() : "";
        layout(title, content);
    }

    // 修改内容文本并重排
    public void setContentText(String text) {
        String title = titleLabel != null ? titleLabel.getText().toString() : "";
        layout(title, text);
    }

    // 修改标题并重排
    public void setTitleText(String text) {
        String content = contentLabel != null ? contentLabel.getText().toString() : "";
        layout(text, content);
    }

    private void clampToStage(float x, float y) {
        if (getStage() == null) {
            // 未加入舞台时直接设置位置
            setPosition(x, y);
            return;
        }
        float stageW = getStage().getViewport().getWorldWidth();
        float stageH = getStage().getViewport().getWorldHeight();
        float maxX = Math.max(0f, stageW - getWidth());
        float maxY = Math.max(0f, stageH - getHeight());
        float clampedX = Math.max(0f, Math.min(x, maxX));
        float clampedY = Math.max(0f, Math.min(y, maxY));
        setPosition(clampedX, clampedY);
    }
}
