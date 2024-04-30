package ru.abch.fmradio;

public class RadioState {
    public int freq, volume, rssi;
    public boolean mute;
    public String name, info;
    RadioState(int f, int v) {
        this.freq = f;
        this.volume = v;
    }
}
