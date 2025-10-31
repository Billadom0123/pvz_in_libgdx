package com.example.plantsvszombies.ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.example.plantsvszombies.Resource.AssetParticle;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.GameManager;
import com.example.plantsvszombies.models.Plants.BasePlant;
import com.example.plantsvszombies.screens.GameScreen;

public class PlantCard extends Group {

    private AssetMan assetMan;
    private GameScreen screen;
    private final TextureActor cardTexture; // 卡片图片
    private final TextureActor plantTexture; // 植物图片
    private final Label costLabel;   // 显示种植消耗的标签
    public final String plantType;     // 卡片代表的植物类型 (字符串标识)
    private final int cost;            // 种植所需太阳
    private final boolean haveCooldown = false;
    private final float cooldown;
    private float cooldownTimer = 0f;
    private Class<?> plantClass;

    private boolean isSelected = false; // 是否被选中
    private boolean isSelectable = false; // 是否可以被选中



    public PlantCard(GameScreen screen, Skin skin, BasePlant plant) {
        this.assetMan = AssetMan.getInstance();
        this.screen = screen;
        this.cardTexture = new TextureActor(AssetTexture.SEED_PACKET_5F.get(assetMan));
        this.plantTexture = new TextureActor(plant.getTexture());
        this.plantType = plant.getName();
        this.cost = plant.getCost();
        this.cooldown = plant.getCooldown();
        this.cooldownTimer = plant.getCooldown() - plant.getInitCd();
        this.plantClass = plant.getClass();
        this.costLabel = new Label(String.valueOf(cost), skin);


        addActor(this.cardTexture);
        addActor(this.plantTexture);
        addActor(this.costLabel);

        layout();
    }

    private void layout() {
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
     * 重写draw方法，定义自己的绘制逻辑
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        // 继承父Actor的颜色和透明度

        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);


        // 如果冷却还未好，则在卡片上覆盖一层半透明黑色，使其“变灰”
        if (isOnCooldown()) {
            float cooldownRatio = cooldownTimer / cooldown;
            float overlayHeight = getHeight() * (1 - cooldownRatio);
            if (!isSunEnough()) {
                // 如果同时缺Sun，则先绘制使其变得更灰
                shapeRenderer.setColor(0, 0, 0, 0.7f);
            } else {
                shapeRenderer.setColor(0, 0, 0, 0.5f);
            }
            shapeRenderer.rect(getX(), getY() + getHeight() - overlayHeight, getWidth(), overlayHeight);
            isSelectable = false;
        } else if (!isSunEnough()) {
            shapeRenderer.setColor(0, 0, 0, 0.5f);
            shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
            isSelectable = false;
        } else {
            if (!isSelectable) {
                ParticleEffect particleEffect = AssetParticle.SEED_PACKET_FLASH.get(assetMan);
                particleEffect.setPosition(getX() + getWidth() / 2f, getY() + getHeight() / 2f);
                particleEffect.start();
                screen.addParticle(particleEffect);
                setSelectable(true);
            }
        }
        if (isSelected) {
            shapeRenderer.setColor(0, 0, 0, 0.5f);
            shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        }
        shapeRenderer.end();
        batch.begin();
    }

    public boolean isOnCooldown() {
        return cooldownTimer < cooldown;
    }

    public boolean isSunEnough() {
        return screen.isSunAvailable(cost);
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
        if (GameManager.getInstance().isCardCooldownOn()) {
            cooldownTimer += delta;
        } else {
            cooldownTimer = cooldown;
        }
    }

    public Texture getPlantTexture() {
        return plantTexture.getTexture();
    }
}
