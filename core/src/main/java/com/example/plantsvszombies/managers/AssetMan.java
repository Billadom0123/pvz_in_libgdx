package com.example.plantsvszombies.managers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.example.plantsvszombies.Animation.AnimationClip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetMan {
    private static AssetMan instance;
    private final AssetManager assetManager;
    private final Map<String, AnimationClip> animations;
    private final Map<String, ParticleEffect> particleEffects = new HashMap<>();


    private AssetMan() {
        assetManager = new AssetManager();
        animations = new HashMap<>();
    }

    public static AssetMan getInstance() {
        if (instance == null) {
            instance = new AssetMan();
        }
        return instance;
    }

    /**
     * 将所有需要的资源加入加载队列
     */
    public void loadAssets() {
        loadTextureAssets();
        loadSoundAssets();
        loadMusicAssets();
        loadParticleEffects();
        assetManager.finishLoading();
        loadAnimationAssets();
    }

    private void loadTextureAssets() {
        assetManager.load("graphics/Map/map0.jpg", Texture.class);
        assetManager.load("graphics/Plants/Peashooter/Still.png", Texture.class);
        assetManager.load("graphics/Plants/Peashooter/Peashooter.png", Texture.class);
        assetManager.load("graphics/Bullets/PeaNormalExplode/pea_splats.png", Texture.class);
        assetManager.load("graphics/Plants/SunFlower/Still.png", Texture.class);
        assetManager.load("graphics/Plants/SunFlower/SunFlower.png", Texture.class);
        assetManager.load("graphics/Plants/SunFlower/SunFlowerBright.png", Texture.class);
        assetManager.load("graphics/Plants/WallNut/StillNormal.png", Texture.class);
        assetManager.load("graphics/Plants/WallNut/WallNut.png", Texture.class);
        assetManager.load("graphics/Plants/WallNut/Wallnut_cracked1.png", Texture.class);
        assetManager.load("graphics/Plants/WallNut/Wallnut_cracked2.png", Texture.class);
        assetManager.load("graphics/Plants/PotatoMine/Still.png", Texture.class);
        assetManager.load("graphics/Plants/PotatoMine/ArmUp.png", Texture.class);
        assetManager.load("graphics/Plants/PotatoMine/ArmedUp.png", Texture.class);
        assetManager.load("graphics/Plants/PotatoMine/ArmedUpNoLight.png", Texture.class);
        assetManager.load("graphics/Plants/PotatoMine/Explosion.png", Texture.class);
        assetManager.load("graphics/Plants/PotatoMine/ExplosionSpudow.png", Texture.class);

        assetManager.load("graphics/Zombies/Zombie/Zombie.gif", Texture.class);
        assetManager.load("graphics/Zombies/Zombie/Zombie.png", Texture.class);
        assetManager.load("graphics/Zombies/Zombie/ZombieAttack.png", Texture.class);
        assetManager.load("graphics/Zombies/Zombie/ZombieHead.png", Texture.class);
        assetManager.load("graphics/Zombies/Zombie/ZombieDie.png", Texture.class);
        assetManager.load("graphics/Zombies/Zombie/ZombieLostHead.png", Texture.class);
        assetManager.load("graphics/Zombies/Zombie/ZombieLostHeadAttack.png", Texture.class);

        assetManager.load("graphics/Bullets/PeaNormal/PeaNormal_0.png", Texture.class);

        assetManager.load("graphics/Screen/SeedBank.png", Texture.class);
        assetManager.load("graphics/Screen/Sun.gif", Texture.class);
        assetManager.load("graphics/Screen/Sun.png", Texture.class);
        assetManager.load("graphics/Screen/car.png", Texture.class);

        assetManager.load("graphics/Screen/ZombiesAteYourBrains.png", Texture.class);
        assetManager.load("graphics/Screen/SeedPacket_Larger.png", Texture.class); // 种子包图标
        assetManager.load("graphics/Screen/SeedPacket_6f.png", Texture.class); // 种子包图标小尺寸
        assetManager.load("graphics/Screen/SeedPacket_5f.png", Texture.class); // 种子包图标小尺寸
    }

    private void loadSoundAssets() {
        // 这里可以添加音频资源的加载逻辑
        assetManager.load("graphics/audio/seedlift.ogg", Sound.class); // 从顶部选植物的音效
        assetManager.load("graphics/audio/tap2.ogg", Sound.class);      // 取消选择植物的音效
        assetManager.load("graphics/audio/buzzer.ogg", Sound.class);   // 无法选择植物的音效
        assetManager.load("graphics/audio/points.ogg", Sound.class);    // 收集阳光的音效
        assetManager.load("graphics/audio/plant1.ogg", Sound.class);     // 种植植物的音效1
        assetManager.load("graphics/audio/plant2.ogg", Sound.class);     // 种植植物的音效2
        assetManager.load("graphics/audio/chomp.ogg", Sound.class);    // 僵尸吃植物的音效1
        assetManager.load("graphics/audio/chompsoft.ogg", Sound.class);   // 僵尸吃植物的音效2
        assetManager.load("graphics/audio/gulp.ogg", Sound.class);     // 僵尸吃掉植物的音效
        assetManager.load("graphics/audio/limbs_pop.ogg", Sound.class); // 僵尸掉头的音效
        assetManager.load("graphics/audio/zombie_falling_1.ogg", Sound.class); // 僵尸倒地的音效1
        assetManager.load("graphics/audio/zombie_falling_2.ogg", Sound.class); // 僵尸倒地的音效2
        assetManager.load("graphics/audio/awooga.ogg", Sound.class);    // 第一只僵尸出现的音效
        assetManager.load("graphics/audio/lawnmower.ogg", Sound.class);    // 小推车的音效
        assetManager.load("graphics/audio/throw.ogg", Sound.class);    // 射豌豆的音效
        assetManager.load("graphics/audio/splat2.ogg", Sound.class); // 豌豆攻击普通僵尸的音效
        assetManager.load("graphics/audio/dirt_rise.ogg", Sound.class); // 土豆地雷出土的音效
        assetManager.load("graphics/audio/potato_mine.ogg", Sound.class); // 土豆地雷爆炸的音效
        assetManager.load("graphics/audio/scream.ogg", Sound.class);   // 失败时戴夫的尖叫声
    }

    private void loadMusicAssets() {
        // 这里可以添加音乐资源的加载逻辑
        assetManager.load("graphics/audio/Grasswalk.ogg", Music.class); // 背景音乐
        assetManager.load("graphics/audio/losemusic.ogg", Music.class); // 失败音乐
        assetManager.load("graphics/audio/The-King-Paperman.mp3", Music.class); // hyw
    }

    private void loadAnimationAssets() {
        // 这里可以添加动画资源的加载逻辑
        loadAnimation("Peashooter", "graphics/Plants/Peashooter/Peashooter.png", 13, 1170);
        loadAnimation("PeaSplat", "graphics/Bullets/PeaNormalExplode/pea_splats.png", 4, 200);
        loadAnimation("SunFlower", "graphics/Plants/SunFlower/SunFlower.png", 18, 1980);
        loadAnimation("SunFlowerBright", "graphics/Plants/SunFlower/SunFlowerBright.png", 18, 1980);
        loadAnimation("WallNut", "graphics/Plants/WallNut/WallNut.png", 16, 1920);
        loadAnimation("WallNut_Cracked1", "graphics/Plants/WallNut/Wallnut_cracked1.png", 11, 1100);
        loadAnimation("WallNut_Cracked2", "graphics/Plants/WallNut/Wallnut_cracked2.png", 15, 1500);
        loadAnimation("PotatoMine", "graphics/Plants/PotatoMine/Still.png", 1, 1000);
        loadAnimation("PotatoMine_ArmUp", "graphics/Plants/PotatoMine/ArmUp.png", 30, 1500);
        loadAnimation("PotatoMine_Armed", "graphics/Plants/PotatoMine/ArmedUp.png", 8, 1600);
        loadAnimation("PotatoMine_Explosion", "graphics/Plants/PotatoMine/Explosion.png", 1, 1000);

        loadAnimation("NormalZombie", "graphics/Zombies/Zombie/Zombie.png", 22, 2970);
        loadAnimation("NormalZombieDie", "graphics/Zombies/Zombie/ZombieDie.png", 10, 2000);
        loadAnimation("NormalZombieAttack", "graphics/Zombies/Zombie/ZombieAttack.png", 21, 2150);
        loadAnimation("NormalZombieLostHead", "graphics/Zombies/Zombie/ZombieLostHead.png", 18, 3240);
        loadAnimation("NormalZombieLostHeadAttack", "graphics/Zombies/Zombie/ZombieLostHeadAttack.png", 11, 1100);

        loadAnimation("Sun", "graphics/Screen/Sun.png", 29, 2900);
    }

    public void loadParticleEffects() {
        loadParticleEffect("Planting", "graphics/Particles/Planting.p", "graphics/Particles/dirtsmall");
        loadParticleEffect("PotatoMine_Up", "graphics/Particles/PotatoMine_Up.p", "graphics/Particles/dirtsmall");
        loadParticleEffect("PotatoMine_Explode", "graphics/Particles/PotatoMine_Explode.p", "graphics/Particles/potatomine");
    }

    public void loadAnimation(String key, String filepath, int frames, float duration) {
        Texture texture = assetManager.get(filepath, Texture.class);
        if (duration >= 10) {
            duration /= 1000f; // 转换为秒
        }
        AnimationClip animationClip = new AnimationClip(texture, frames, duration);
        animations.put(key, animationClip);
    }

    public void loadParticleEffect(String key, String effectFilePath, String imagesDir) {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal(effectFilePath), Gdx.files.internal(imagesDir));
        particleEffects.put(key, particleEffect);
    }

    public ParticleEffect getParticleEffect(String key) {
        ParticleEffect original = particleEffects.get(key);
        if (original != null) {
            ParticleEffect copy = new ParticleEffect(original);
            return copy; // 返回副本以避免状态冲突
        }
        return null;
    }

    public Texture getTexture(String filePath) {
        return assetManager.get(filePath, Texture.class);
    }

    public Sound getSound(String filePath) {
        return assetManager.get(filePath, Sound.class);
    }

    public void playSound(String filePath, float volume) {
        Sound sound = getSound(filePath);
        if (sound != null) {
            sound.play(volume);
        }
    }

    public void playSound(String filePath) {
        playSound(filePath, 1.0f);
    }

    public void playRandomSound(List<String> filePaths, float volume) {
        if (filePaths.isEmpty()) return;
        int index = (int) (Math.random() * filePaths.size());
        playSound(filePaths.get(index), volume);
    }

    public void playRandomSound(List<String> filePaths) {
        playRandomSound(filePaths, 1.0f);
    }

    public Music getMusic(String filePath) {
        return assetManager.get(filePath, Music.class);
    }

    public AnimationClip getAnimation(String key) {
        return animations.get(key);
    }


    public void unloadAsset(String filePath) {
        if (assetManager.isLoaded(filePath)) {
            assetManager.unload(filePath);
        }
    }
    /**
     * 获取底层的AssetManager实例
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * 在游戏退出时释放所有已加载的资源
     */
    public void dispose() {
        assetManager.dispose();
        animations.clear();
    }
}
