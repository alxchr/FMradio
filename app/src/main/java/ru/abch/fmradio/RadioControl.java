package ru.abch.fmradio;

public class RadioControl {
    int freq, volume;
    boolean mute, seek;
    public RadioControl(int f, int v, boolean m, boolean s) {
        this.freq = f;
        this.volume = v;
        this.mute = m;
        this.seek = s;
    }
}
