package com.example.plantsvszombies.ui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.example.plantsvszombies.managers.GameManager;
import com.example.plantsvszombies.models.Plants.BasePlant;

public class PlantCard extends Actor {
    private final Texture cardTexture; // 卡片图片
    private final Texture plantTexture; // 植物图片
    private final Label costLabel;   // 显示种植消耗的标签
    public final String plantType;     // 卡片代表的植物类型 (字符串标识)
    private final int cost;            // 种植所需太阳
    private final float cooldown;
    private float cooldownTimer = 0f;
    private Class<?> plantClass;

    private boolean isSelected = false; // 是否被选中



    public PlantCard(Texture cardTexture, BasePlant plant) {
        this.cardTexture = cardTexture;
        this.plantType = plant.getName();
        this.cost = plant.getCost();
        this.cooldown = plant.getCooldown();
        this.cooldownTimer = plant.getCooldown() - plant.getInitCd();
        this.plantClass = plant.getClass();
        this.plantTexture = plant.getTexture();
        this.costLabel = new Label(String.valueOf(cost), new Label.LabelStyle());

        // 设置Actor的尺寸，这对于点击检测很重要
        setWidth(cardTexture.getWidth());
        setHeight(cardTexture.getHeight());
    }

    public PlantCard(Texture cardTexture, Skin skin, BasePlant plant) {
        this.cardTexture = cardTexture;
        this.plantType = plant.getName();
        this.cost = plant.getCost();
        this.cooldown = plant.getCooldown();
        this.cooldownTimer = plant.getCooldown() - plant.getInitCd();
        this.plantClass = plant.getClass();
        this.plantTexture = plant.getTexture();
        this.costLabel = new Label(String.valueOf(cost), skin);

        // 设置Actor的尺寸，这对于点击检测很重要
        setWidth(cardTexture.getWidth());
        setHeight(cardTexture.getHeight());
    }

    // --- 公共方法 ---
    public void select() {
        this.isSelected = true;
    }

    public void deselect() {
        this.isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public int getCost() {
        return cost;
    }

    /**
     * 重写draw方法，定义自己的绘制逻辑
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        // 继承父Actor的颜色和透明度
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        // 绘制卡片纹理
        batch.draw(cardTexture, getX(), getY(), getWidth(), getHeight());

        // 如果当前太阳数量不足以支付cost，则在卡片上覆盖一层半透明黑色，使其“变灰”
        if (GameManager.getInstance().getSun() < cost) {
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(cardTexture, getX(), getY(), getWidth(), getHeight());
            batch.setColor(color); // 恢复batch的颜色，以免影响后续绘制
        }

        if (isOnCooldown()) {
            float cooldownRatio = cooldownTimer / cooldown;
            float overlayHeight = getHeight() * (1 - cooldownRatio);
            if (GameManager.getInstance().getSun() < cost) {
                // 如果同时缺钱，则先绘制使其变得更灰
                batch.setColor(0, 0, 0, 0.7f);
            } else {
                batch.setColor(0, 0, 0, 0.5f);
            }
            batch.draw(cardTexture, getX(), getY() + getHeight() - overlayHeight, getWidth(), overlayHeight);
            batch.setColor(color); // 恢复batch的颜色，以免影响后续绘制
        }

        if (isSelected) {
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(cardTexture, getX(), getY(), getWidth(), getHeight());
            batch.setColor(color); // 恢复batch的颜色，以免影响后续绘制
        }
    }

    public boolean isOnCooldown() {
        return cooldownTimer < cooldown;
    }

    public boolean plantInstanceOf(Class<?> clazz) {
        return plantClass.equals(clazz);
    }

    public void resetCooldown() {
        this.cooldownTimer = 0f;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        cooldownTimer += delta;
    }
}
