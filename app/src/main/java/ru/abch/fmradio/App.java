package ru.abch.fmradio;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;

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
    private static final ArrayList<Integer> defaultFreqs =
            new  ArrayList<>(Arrays.asList(9910,9950,10000,10040,10080,10200,10290,10350,10450,10590,10630));
    public static ArrayList<Integer> freqsList;
    private static boolean mute;
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
        freqsList = getArrayList("freqs");
        if (freqsList == null) freqsList = defaultFreqs;
        mute = prefs.getBoolean(getResources().getString(R.string.mute_key), true);
        Log.d(TAG, "Load freqs list size " + freqsList.size() + " mute " + mute);
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
        else channel = Math.min(ch, 10800);
        prefs.edit().putInt("channel",channel).apply();
    }
    public static void setVolume(int vol) {
        if(vol < 0) volume = 0;
        else volume = Math.min(vol, 15);
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
    public static void saveArrayList(ArrayList<Integer> list, String key){
        SharedPreferences sp = prefs;
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
        Log.d(TAG, "Saved freqs list size " + list.size());
    }
    public static ArrayList<Integer> getArrayList(String key){
        SharedPreferences sp = prefs;
        Gson gson = new Gson();
        String json = sp.getString(key, null);
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        return gson.fromJson(json, type);
    }
    public static boolean getMute() {
        return mute;
    }
    public static void setMute(boolean state) {
        mute = state;
        prefs.edit().putBoolean("mute", mute).apply();
    }
}
