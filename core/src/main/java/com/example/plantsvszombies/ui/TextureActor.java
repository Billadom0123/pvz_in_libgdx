package com.example.plantsvszombies.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class TextureActor extends Actor {
    private Texture texture;
    private TextureRegion region;

    public TextureActor(Texture texture) {
        this.texture = texture;
        this.region = new TextureRegion(texture);
        setSize(texture.getWidth(), texture.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // 应用父级的颜色和透明度
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        // 绘制纹理
        batch.draw(region,
            getX(), getY(),
            getOriginX(), getOriginY(),
            getWidth(), getHeight(),
            getScaleX(), getScaleY(),
            getRotation()
        );
    }

    public void setTexture(Texture newTexture) {
        this.texture = newTexture;
        this.region = new TextureRegion(newTexture);
        setSize(newTexture.getWidth(), newTexture.getHeight());
    }

    public Texture getTexture() {
        return texture;
    }
}
