package ru.abch.fmradio.ui.main;

import static android.content.Context.AUDIO_SERVICE;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import ru.abch.fmradio.RadioService;
import ru.abch.fmradio.rulerpicker.RulerValuePicker;
import ru.abch.fmradio.rulerpicker.RulerValuePickerListener;

public class MainFragment extends Fragment {
    public static MainFragment newInstance() {
        return new MainFragment();
    }
    RulerValuePicker rulerValuePicker;
    SeekBar sbRadioVolume;
    LiveData<String> rdsText, station;
    LiveData<Integer> rssi, freq;
    TextView tvRSSI, tvRDSText, tvStation;
    private static final String TAG = "MainFragment";
    AudioManager am;
    Intent radioIntent;
    ImageButton btSearch;
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
        btSearch = view.findViewById(R.id.bt_search);
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
                int freq = selectedValue *10;
                App.setChannel(freq);
                ((MainActivity) requireActivity()).mViewModel.loadStation(freq,"");
                radioIntent = new Intent(requireActivity(), RadioService.class);
                radioIntent.putExtra("freq", freq);
                requireActivity().startService(radioIntent);
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
                radioIntent = new Intent(requireActivity(), RadioService.class);
                radioIntent.putExtra("vol", i);
                requireActivity().startService(radioIntent);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rssi = ((MainActivity) requireActivity()).mViewModel.getRSSI();
        rssi.observe(getViewLifecycleOwner(), r -> {
            if(r != null) {
                String rssi = "RSSI " + r;
                tvRSSI.setText(rssi);
                Log.d(TAG, rssi);
            }
        });
        rdsText = ((MainActivity) requireActivity()).mViewModel.getRDSText();
        rdsText.observe(getViewLifecycleOwner(), rds -> {
            if(rds != null) {
                tvRDSText.setText(rds);
                Log.d(TAG, rds);
            }
        });
        station = ((MainActivity) requireActivity()).mViewModel.getStation();
        station.observe(getViewLifecycleOwner(), s -> {
            if(s != null) {
                tvStation.setText(s);
                Log.d(TAG, s);
            }
        });
        freq = ((MainActivity) requireActivity()).mViewModel.getFreq();
        freq.observe(getViewLifecycleOwner(), f -> rulerValuePicker.selectValue(f/10));
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioIntent = new Intent(requireActivity(), RadioService.class);
                radioIntent.putExtra("search", true);
                requireActivity().startService(radioIntent);
            }
        });
        radioIntent = new Intent(requireActivity(), RadioService.class);
        radioIntent.putExtra("run",true);
        requireActivity().startService(radioIntent);
    }

}