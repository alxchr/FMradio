package ru.abch.fmradio;

import android.os.Parcel;
import android.os.Parcelable;

public class RadioStateParcel implements Parcelable {
    public int freq, volume, rssi;
    public boolean mute;
    public String name, info;

    protected RadioStateParcel(Parcel in) {
        freq = in.readInt();
        volume = in.readInt();
        rssi = in.readInt();
        mute = in.readByte() != 0;
        name = in.readString();
        info = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(freq);
        dest.writeInt(volume);
        dest.writeInt(rssi);
        dest.writeByte((byte) (mute ? 1 : 0));
        dest.writeString(name);
        dest.writeString(info);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RadioStateParcel> CREATOR = new Creator<RadioStateParcel>() {
        @Override
        public RadioStateParcel createFromParcel(Parcel in) {
            return new RadioStateParcel(in);
        }

        @Override
        public RadioStateParcel[] newArray(int size) {
            return new RadioStateParcel[size];
        }
    };
}
