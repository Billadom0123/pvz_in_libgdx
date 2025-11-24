package com.example.plantsvszombies.AssetImpl;

public class SoundInstance {

    private final Long id;
    private final long startMillis;
    private final boolean looping;

    public SoundInstance(Long id) {
        this.id = id;
        this.startMillis = System.currentTimeMillis();
        this.looping = false;
    }

    public SoundInstance(Long id, boolean looping) {
        this.id = id;
        this.startMillis = System.currentTimeMillis();
        this.looping = looping;
    }

    public Long getId() {
        return id;
    }

    public Long getStart() {
        return startMillis;
    }

    public boolean isFinished(Long now, Long length) {
        if (looping) return false;
        return ((now - startMillis) >= length);
    }
}
