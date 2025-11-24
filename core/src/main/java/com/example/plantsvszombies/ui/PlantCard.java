package com.example.plantsvszombies.ui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.example.plantsvszombies.Resource.AssetParticle;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.GameManager;
import com.example.plantsvszombies.models.Plants.BasePlant;
import com.example.plantsvszombies.screens.BaseGameScreen;

public class PlantCard extends Group {

    private final AssetMan assetMan;
    protected final BaseGameScreen screen;
    protected final TextureActor cardTexture; // 卡片图片
    protected final TextureActor plantTexture; // 植物图片
    private final Vector2 position; // 矫正位置
    protected final Label costLabel;   // 显示种植消耗的标签
    protected final String plantType;     // 卡片代表的植物类型 (字符串标识)
    private final int cost;            // 种植所需太阳
    private final float cooldown;
    private float cooldownTimer;
    private final Class<? extends BasePlant> plantClass;

    private boolean isSelected = false; // 是否被选中
    private boolean isSelectable = false; // 是否可以被选中

    // 使用 Skin 的 white 区域创建可复用的黑色半透明遮罩
    private final Drawable overlay; // alpha 0.5


    public PlantCard(BaseGameScreen screen, Skin skin, BasePlant plant) {
        this.screen = screen;
        this.assetMan = AssetMan.getInstance();
        this.cardTexture = new TextureActor(AssetTexture.SEED_PACKET_5F.get(assetMan));
        this.plantTexture = new TextureActor(plant.getTexture());
        this.position = plant.getPosition();
        this.plantType = plant.getName();
        this.cost = plant.getCost();
        this.cooldown = plant.getCooldown();
        this.cooldownTimer = plant.getCooldown() - plant.getInitCd();
        this.plantClass = plant.getClass();
        this.costLabel = new Label(String.valueOf(cost), skin);

        // 复用 skin 的 white 作为遮罩底图
        this.overlay = skin.newDrawable("white", new Color(0f, 0f, 0f, 0.6f));

        addActor(this.cardTexture);
        addActor(this.plantTexture);
        addActor(this.costLabel);

        layout();
    }

    protected void layout() {
        setSize(cardTexture.getWidth(), cardTexture.getHeight());

        this.cardTexture.setTouchable(Touchable.disabled);
        this.plantTexture.setTouchable(Touchable.disabled);
        this.costLabel.setTouchable(Touchable.disabled);

        cardTexture.setPosition(0, 0);
        plantTexture.setScale(.5f);
        plantTexture.setPosition((getWidth() - plantTexture.getWidth() * plantTexture.getScaleX()) / 2,
            (getHeight() - plantTexture.getHeight() * plantTexture.getScaleY()) / 2 + 5);
        costLabel.setWidth(20);
        costLabel.setColor(Color.BLACK);
        costLabel.setFontScale(.75f);
        costLabel.setAlignment(Align.center);
        costLabel.setPosition(10, -2);
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

    public boolean isSelectable() {
        return isSelectable;
    }

    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
    }

    public int getCost() {
        return cost;
    }

    /**
     * 重写draw方法，定义绘制逻辑
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        drawShadow(batch, parentAlpha);
    }

    public void drawShadow(Batch batch, float parentAlpha) {
        // 如果冷却还未好，则在卡片上覆盖一层半透明黑色，使其“变灰”
        if (isOnCooldown()) {
            float cooldownRatio = cooldownTimer / cooldown;
            float overlayHeight = getHeight() * (1 - cooldownRatio);
            if (!isSunEnough()) {
                // 同时缺阳光，再加一层遮罩加深颜色
                overlay.draw(batch, getX(), getY(), getWidth(), getHeight());
            }
            overlay.draw(batch, getX(), getY() + getHeight() - overlayHeight, getWidth(), overlayHeight);
            isSelectable = false;
        } else if (!isSunEnough()) {
            // 冷却好但太阳不足：整张卡加一层遮罩
            overlay.draw(batch, getX(), getY(), getWidth(), getHeight());
            isSelectable = false;
        } else {
            // 可选状态切换为 true 时触发一次闪光粒子
            if (!isSelectable) {
                ParticleEffect particleEffect = AssetParticle.SEED_PACKET_FLASH.get(assetMan);
                particleEffect.setPosition(getX() + getWidth() / 2f, getY() + getHeight() / 2f);
                particleEffect.start();
                setSelectable(true);
            }
        }
        if (isSelected) {
            overlay.draw(batch, getX(), getY(), getWidth(), getHeight());
        }
    }

    public boolean isOnCooldown() {
        return cooldownTimer < cooldown;
    }

    public boolean isSunEnough() {
        return screen.isSunAvailable(cost);
    }

    public Class<? extends BasePlant> getPlantClass() { return plantClass; }

    public boolean plantInstanceOf(Class<?> clazz) {
        return plantClass.equals(clazz);
    }

    public void resetCooldown() {
        this.cooldownTimer = 0f;
    }

    public void fullRecharge() { this.cooldownTimer = this.cooldown; }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (GameManager.getInstance().isCardCooldownOn()) {
            cooldownTimer += delta;
        } else {
            cooldownTimer = cooldown;
        }
    }

    public Texture getPlantTexture() {
        return plantTexture.getTexture();
    }

    public Vector2 getPlantPosition() {
        return position;
    }

    public String getPlantType() {
        return plantType;
    }
}
