package ru.abch.fmradio;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import ru.abch.fmradio.ui.main.MainFragment;
import ru.abch.fmradio.ui.main.MainViewModel;
import ru.abch.fmradio.ui.main.SettingsFragment;

public class MainActivity extends AppCompatActivity {
    public MainViewModel mViewModel;
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
}