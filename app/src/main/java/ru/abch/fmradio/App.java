package ru.abch.fmradio;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import ru.abch.fmradio.android_serialport_api.SerialPort;

public class App extends Application {
    static App instance = null;
    static String TAG = "App";
    static SharedPreferences prefs;
    static String port;
    static int speed, channel, volume;
    public static int state;
    public static final int SETTINGS = 0, MAIN = 1;
    private static SerialPort serialPort = null;
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
        instance = this;
        prefs= PreferenceManager.getDefaultSharedPreferences(instance);
        speed = Integer.parseInt(prefs.getString(getResources().getString(R.string.speed_key), "9600"));
        port = prefs.getString(getResources().getString(R.string.port_key),"");
        if(port.length() > 0 && !port.contains("dev")) {
            port = "/dev/" + port;
        }
        volume = prefs.getInt("volume", 10);
        channel = prefs.getInt("channel", 10290);
        Log.d(TAG,"Port " + port + " speed " + speed + " channel " + channel + " volume " + volume);
    }
    public static String getPort() {
        return port;
    }
    public static int getSpeed() {
        return speed;
    }
    public static int getChannel() {
        return channel;
    }
    public static int getVolume() {
        return volume;
    }
    public static void setChannel(int ch) {
        if (ch < 8800) channel = 8800;
        else if (ch > 10800) channel = 10800;
        else channel = ch;
        prefs.edit().putInt("channel",channel).apply();
    }
    public static void setVolume(int vol) {
        if(vol < 0) volume = 0;
        else if (vol > 15) volume = 15;
        else volume = vol;
        prefs.edit().putInt("volume", volume).apply();
    }
    static {
        System.loadLibrary("serial-port");
    }
    public static SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (serialPort == null) {
            /* Check parameters */
            if (port.length() == 0) {
                throw new InvalidParameterException();
            }
            serialPort = new SerialPort(new File(port), speed, 0);
        }
        return serialPort;
    }
    public static void closeSerialPort() {
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
    }
}
