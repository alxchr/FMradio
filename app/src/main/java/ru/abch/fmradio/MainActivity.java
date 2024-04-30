package ru.abch.fmradio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;

import ru.abch.fmradio.ui.main.MainFragment;
import ru.abch.fmradio.ui.main.MainViewModel;
import ru.abch.fmradio.ui.main.SettingsFragment;

public class MainActivity extends AppCompatActivity {
    public MainViewModel mViewModel;
    IntentFilter stateFilter, searchFilter;
    private final String TAG = "MainActivity";
    RadioStateParcel state;
    LiveData<ArrayList<Integer>> freqsData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            if (App.getPort().length() == 0) {
                App.state = App.SETTINGS;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new SettingsFragment())
                        .commitNow();
            } else {
                App.state = App.MAIN;
                gotoMainFragment();
            }
        }
        stateFilter = new IntentFilter();
        stateFilter.addAction("ru.abch.fmradio.state");
        stateFilter.addCategory("android.intent.action.DEFAULT");
        registerReceiver(stateReceiver, stateFilter);
        searchFilter = new IntentFilter();
        searchFilter.addAction("ru.abch.fmradio.freqs");
        searchFilter.addCategory("android.intent.action.DEFAULT");
        registerReceiver(freqsReceiver, searchFilter);
        freqsData = mViewModel.getFreqsData();
    }
    @Override
    public void onBackPressed() {
        if (App.state == App.SETTINGS) {
            gotoMainFragment();
        } else {
            super.onBackPressed();
            /*
            Intent radioIntent = new Intent(this, RadioService.class);
            radioIntent.putExtra("run",true);
            radioIntent.putExtra("mute",true);
            startService(radioIntent);

             */
            System.exit(0);
        }
    }
    void gotoMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow();
    }
    BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int prevF = (state == null)? 10000 : state.freq;
            state = intent.getParcelableExtra("state");
            int f = state.freq;
            int v = state.volume;
            int r = state.rssi;
            boolean m = state.mute;
            String n = state.name;
            String t = state.info;
            Log.d(TAG, intent.getAction() + " " + f + " " + v + " " + r + " " + m + " " + n + " " + t);
            mViewModel.loadRSSI(r);
            mViewModel.loadStation(f,n);
            mViewModel.loadRDS(t);
            if(f != prevF) mViewModel.loadFreq(f);
        }
    };
    BroadcastReceiver freqsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int [] freqs = intent.getIntArrayExtra("freqs");
            ArrayList<Integer> freqsList = new ArrayList<>();
            if(freqs != null) {
                for (Integer f : freqs) {
                    freqsList.add(f);
                }
                Log.d(TAG, "Freqs array received, length " + freqs.length);
                mViewModel.loadFreqsData(freqsList);
            }
        }
    };
}