package ru.abch.fmradio;

import android.os.Parcel;
import android.os.Parcelable;

public class RadioStateParcel implements Parcelable {
    public int f, v, r;
    public boolean m;
    public String n, t;

    protected RadioStateParcel(Parcel in) {
        f = in.readInt();
        v = in.readInt();
        r = in.readInt();
        m = in.readByte() != 0;
        n = in.readString();
        t = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(f);
        dest.writeInt(v);
        dest.writeInt(r);
        dest.writeByte((byte) (m ? 1 : 0));
        dest.writeString(n);
        dest.writeString(t);
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
