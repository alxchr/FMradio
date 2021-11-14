package ru.abch.fmradio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import ru.abch.fmradio.ui.main.MainFragment;
import ru.abch.fmradio.ui.main.MainViewModel;
import ru.abch.fmradio.ui.main.SettingsFragment;

public class MainActivity extends AppCompatActivity {
    public MainViewModel mViewModel;
    IntentFilter stateFilter, searchFilter;
    private final String TAG = "MainActivity";
    RadioStateParcel state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
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
    }
    @Override
    public void onBackPressed() {
        if (App.state == App.SETTINGS) {
            gotoMainFragment();
        } else {
            super.onBackPressed();
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
            int prevF = (state == null)? 10000 : state.f;
            state = intent.getParcelableExtra("state");
            int f = state.f;
            int v = state.v;
            int r = state.r;
            boolean m = state.m;
            String n = state.n;
            String t = state.t;
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
            if(freqs != null) {
                Log.d(TAG, "Freqs array received, length " + freqs.length);
            }
        }
    };
}