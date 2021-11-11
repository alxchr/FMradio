package ru.abch.fmradio.ui.main;

import static android.content.Context.AUDIO_SERVICE;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import ru.abch.fmradio.App;
import ru.abch.fmradio.MainActivity;
import ru.abch.fmradio.R;
import ru.abch.fmradio.rulerpicker.RulerValuePicker;
import ru.abch.fmradio.rulerpicker.RulerValuePickerListener;

public class MainFragment extends Fragment {
    public static MainFragment newInstance() {
        return new MainFragment();
    }
    RulerValuePicker rulerValuePicker;
    SeekBar sbRadioVolume;
    LiveData<String> rdsText, station;
    LiveData<Integer> rssi;
    TextView tvRSSI, tvRDSText, tvStation;
    private static final String TAG = "MainFragment";
    AudioManager am;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        rulerValuePicker = view.findViewById(R.id.ruler_picker);
        sbRadioVolume = view.findViewById(R.id.radio_volume);
        tvRDSText = view.findViewById(R.id.tv_rds);
        tvRSSI = view.findViewById(R.id.tv_rssi);
        tvStation = view.findViewById(R.id.tv_station);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        am = (AudioManager) requireActivity().getSystemService(AUDIO_SERVICE);
        rulerValuePicker.selectValue(App.getChannel()/10);
        rulerValuePicker.setValuePickerListener(new RulerValuePickerListener() {
            @Override
            public void onValueChange(int selectedValue) {
                App.setChannel(selectedValue * 10);
                ((MainActivity) requireActivity()).mViewModel.loadStation(selectedValue * 10,"");
            }

            @Override
            public void onIntermediateValueChange(int selectedValue) {

            }
        });
        sbRadioVolume.setProgress(App.getVolume());
        sbRadioVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                App.setVolume(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rssi = ((MainActivity) requireActivity()).mViewModel.getRSSI();
        rssi.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String rssi = "RSSI " + integer;
                tvRSSI.setText(rssi);
                Log.d(TAG, rssi);
            }
        });
        rdsText = ((MainActivity) requireActivity()).mViewModel.getRDSText();
        rdsText.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String rds) {
                tvRDSText.setText(rds);
                Log.d(TAG, rds);
            }
        });
        station = ((MainActivity) requireActivity()).mViewModel.getStation();
        station.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvStation.setText(s);
                Log.d(TAG, s);
            }
        });
    }

}