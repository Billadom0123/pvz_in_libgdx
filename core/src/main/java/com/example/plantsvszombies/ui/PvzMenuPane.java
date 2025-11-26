package com.example.plantsvszombies.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.example.plantsvszombies.PvzGame;
import com.example.plantsvszombies.Resource.AssetFont;
import com.example.plantsvszombies.Resource.AssetSkin;
import com.example.plantsvszombies.Resource.AssetSound;
import com.example.plantsvszombies.Resource.AssetTexture;
import com.example.plantsvszombies.managers.AssetMan;
import com.example.plantsvszombies.managers.GameManager;
import com.example.plantsvszombies.managers.SoundManager;
import com.example.plantsvszombies.screens.AlmanacScreen;
import com.example.plantsvszombies.screens.BaseGameScreen;
import com.example.plantsvszombies.screens.HomeScreen;

public class PvzMenuPane extends Group {
    public enum State {
        IN_GAME, HOME_PAGE
    }

    protected final PvzGame game;
    protected final Screen screen;
    protected final State state;
    private final TextureActor background;
    private final AssetMan assetMan;
    private final SoundManager soundManager;
    private final GameManager gameManager;

    private float dragOffsetX;
    private float dragOffsetY;


    public PvzMenuPane(PvzGame game,BaseGameScreen gameScreen) {
        this.game = game;
        this.assetMan = AssetMan.getInstance();
        this.soundManager = SoundManager.getInstance();
        this.gameManager = GameManager.getInstance();
        this.screen = gameScreen;
        this.background = new TextureActor(AssetTexture.MENU_BACKGROUND.get(assetMan));
        this.state = State.IN_GAME;
        layout();
        enableDrag();
    }

    public PvzMenuPane(PvzGame game, HomeScreen homeScreen) {
        this.game = game;
        this.assetMan = AssetMan.getInstance();
        this.soundManager = SoundManager.getInstance();
        this.gameManager = GameManager.getInstance();
        this.screen = homeScreen;
        this.background = new TextureActor(AssetTexture.MENU_BACKGROUND.get(assetMan));
        this.state = State.HOME_PAGE;
        layout();
        enableDrag();
    }

    private void layout() {
        clearChildren();
        background.setPosition(0f, 0f);
        setSize(background.getWidth(), background.getHeight());
        addActor(background);

        Skin skin = AssetSkin.PVZ_CUSTOM_SKIN.get(AssetMan.getInstance());

        Label musicVolumeLabel = new Label("Music", skin);
        musicVolumeLabel.setPosition(125, 345);
        addActor(musicVolumeLabel);
        Slider musicVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        musicVolumeSlider.setPosition(200, 340);
        musicVolumeSlider.setValue(soundManager.getMusicVolume());
        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.setMusicVolume(musicVolumeSlider.getValue());
            }
        });
        addActor(musicVolumeSlider);

        Label sfxVolumeLabel = new Label("Sound FX", skin);
        sfxVolumeLabel.setPosition(90, 315);
        addActor(sfxVolumeLabel);
        Slider sfxVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        sfxVolumeSlider.setValue(soundManager.getSfxVolume());
        sfxVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.setSfxVolume(sfxVolumeSlider.getValue());
                if (!sfxVolumeSlider.isDragging()) {
                    soundManager.playSound(AssetSound.BUTTON_CLICK);
                }
            }
        });
        sfxVolumeSlider.setPosition(200, 310);
        addActor(sfxVolumeSlider);

        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            CheckBox fullscreenCheckbox = new CheckBox("Full Screen", skin);
            fullscreenCheckbox.setPosition(100, 275);
            fullscreenCheckbox.setChecked(Gdx.graphics.isFullscreen());
            fullscreenCheckbox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    soundManager.playSound(AssetSound.BUTTON_CLICK);
                    boolean isChecked = fullscreenCheckbox.isChecked();
                    if (isChecked != Gdx.graphics.isFullscreen()) {
                        if (isChecked) {
                            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                        } else {
                            Gdx.graphics.setWindowedMode(800, 600);
                        }
                    }
                }
            });
            addActor(fullscreenCheckbox);
        }

        if (state == State.HOME_PAGE) {
            CheckBox crazyModeCheckbox = new CheckBox("Crazy Mode", skin);
            crazyModeCheckbox.setPosition(100, 240);
            crazyModeCheckbox.setChecked(gameManager.isCrazyMode());
            crazyModeCheckbox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    soundManager.playSound(AssetSound.BUTTON_CLICK);
                    boolean isChecked = crazyModeCheckbox.isChecked();
                    gameManager.setCrazyMode(isChecked);
                }
            });
            addActor(crazyModeCheckbox);
        }

        if (state == State.IN_GAME) {
            PvzGraveButton almanacButton = new PvzGraveButton("View Almanac");
            almanacButton.setPosition(background.getX() + (background.getWidth() - almanacButton.getWidth()) / 2f, 220);
            almanacButton.setClickListener(() -> game.setScreen(new AlmanacScreen(game, screen)));
            addActor(almanacButton);

            PvzGraveButton restartButton = new PvzGraveButton("Restart Level");
            restartButton.setPosition(background.getX() + (background.getWidth() - restartButton.getWidth()) / 2f, 175);
            restartButton.setClickListener(() -> {
                PvzGraveButton restart = new PvzGraveButton("RESTART");
                PvzGraveButton cancel = new PvzGraveButton("CANCEL");
                PvzDialogBox dialogBox = new PvzDialogBox("Restart Level?",
                    "Do you want to try this level again from the beginning?",
                    new PvzGraveButton[]{restart, cancel});

                restart.setClickListener(() -> {
                    ((BaseGameScreen)screen).restart();
                });

                cancel.setClickListener(() -> ((BaseGameScreen)screen).hideModal());

                ((BaseGameScreen)screen).showModal(dialogBox, 0f);
            });
            addActor(restartButton);

            PvzGraveButton mainMenuButton = new PvzGraveButton("  Main Menu  ");
            mainMenuButton.setPosition(background.getX() + (background.getWidth() - mainMenuButton.getWidth()) / 2f, 130);
            mainMenuButton.setClickListener(() -> {
                PvzGraveButton leave = new PvzGraveButton("  LEAVE  ");
                PvzGraveButton cancel = new PvzGraveButton(" CANCEL ");
                PvzDialogBox dialogBox = new PvzDialogBox("Leave Game?",
                    "Do you want to return to the main menu? \n \n Your game will not be saved.",
                    new PvzGraveButton[]{leave, cancel});

                leave.setClickListener(() -> {
                    gameManager.reset();
                    game.setScreen(new HomeScreen(game));
                });

                cancel.setClickListener(() -> ((BaseGameScreen)screen).hideModal());

                ((BaseGameScreen)screen).showModal(dialogBox, 0f);
            });
            addActor(mainMenuButton);
        }

        PressableGroup closeButton = new PressableGroup();
        closeButton.setTouchable(Touchable.enabled);

        TextureActor closeTexture = new TextureActor(AssetTexture.MENU_BACK_TO_GAME_BUTTON.get(AssetMan.getInstance()));
        closeButton.setSize(closeTexture.getWidth(), closeTexture.getHeight());
        closeButton.addActor(closeTexture);

        Label closeLabel = new Label("", skin.get("Green36", Label.LabelStyle.class));
        if (state == State.IN_GAME) {
            closeLabel.setText("Back to Game");
        } else if (state == State.HOME_PAGE) {
            closeLabel.setText("OK");
        }
        closeLabel.pack();
        closeLabel.setPosition(
                (closeTexture.getWidth() - closeLabel.getWidth()) / 2f,
                (closeTexture.getHeight() - closeLabel.getHeight()) / 2f + 5f
        );
        closeButton.addActor(closeLabel);

        closeButton.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                closeLabel.setStyle(skin.get("GreenBright36", Label.LabelStyle.class));
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                closeLabel.setStyle(skin.get("Green36", Label.LabelStyle.class));
            }
        });
        closeButton.setClickListener(() -> {
            if (state == State.HOME_PAGE) {
                soundManager.playSound(AssetSound.BUTTON_CLICK);
                ((HomeScreen)screen).hideModal();
            } else if (state == State.IN_GAME) {
                screen.resume();
            }
        });
        closeButton.setPosition(30, 18);
        addActor(closeButton);
    }

    private void enableDrag() { // 支持 X + Y
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Actor target = event.getTarget();
                if (target != background) return false; // 仅背景触发拖拽
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
                clampToStage(getX(), getY());
            }
        });
    }

    private void clampToStage(float x, float y) {
        if (getStage() == null) {
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
