package ru.abch.fmradio.ui.main;

import static android.content.Context.AUDIO_SERVICE;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;

import ru.abch.fmradio.App;
import ru.abch.fmradio.ButtonsAdapter;
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
    ImageButton btSearch, btMute, btPlus;
    ButtonsAdapter buttonsAdapter;
    GridView gvButtons;
    LiveData<ArrayList<Integer>> freqsList;
    int iFreq = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radio2, container, false);
        rulerValuePicker = view.findViewById(R.id.ruler_picker);
        sbRadioVolume = view.findViewById(R.id.radio_volume);
        tvRDSText = view.findViewById(R.id.tv_rds);
        tvRSSI = view.findViewById(R.id.tv_rssi);
        tvStation = view.findViewById(R.id.tv_station);
        btSearch = view.findViewById(R.id.bt_search);
        buttonsAdapter = new ButtonsAdapter(getActivity(), App.freqsList);
        gvButtons = view.findViewById(R.id.gv_buttons);
        gvButtons.setAdapter(buttonsAdapter);
        btMute = view.findViewById(R.id.bt_mute);
        btPlus = view.findViewById(R.id.bt_plus);
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
                iFreq = freq;
                App.setChannel(freq);
                ((MainActivity) requireActivity()).mViewModel.loadStation(freq,"");
                radioIntent = new Intent(requireActivity(), RadioService.class);
                radioIntent.putExtra("mute",App.getMute());
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
//                radioIntent.putExtra("mute",App.getMute());
                radioIntent.putExtra("vol", i);
                requireActivity().startService(radioIntent);
                Log.d(TAG, "Set volume " + i);
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
            } else {
                tvRDSText.setText("");
            }
        });
        freq = ((MainActivity) requireActivity()).mViewModel.getFreq();
        freq.observe(getViewLifecycleOwner(), f -> rulerValuePicker.selectValue(f/10));
        String sF = "";
        try {
            iFreq = freq.getValue();
            sF = iFreq + " MHz";
        } catch (NullPointerException ignored) {

        }
        station = ((MainActivity) requireActivity()).mViewModel.getStation();
        String finalSF = sF;
        station.observe(getViewLifecycleOwner(), s -> {
            if(s != null && !s.trim().isEmpty()) {
                tvStation.setText(s);
                Log.d(TAG, s);
            } else {
                tvStation.setText(finalSF);
            }
        });


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
        radioIntent.putExtra("mute",App.getMute());
//        requireActivity().startService(radioIntent);
//        radioIntent = new Intent(requireActivity(), RadioService.class);
        radioIntent.putExtra("vol", App.getVolume());
        radioIntent.putExtra("freq", App.getChannel());
        requireActivity().startService(radioIntent);
        freqsList = ((MainActivity) requireActivity()).mViewModel.getFreqsData();
        freqsList.observe(getViewLifecycleOwner(), freqsList -> {
            Log.d(TAG, "New freqs list");
            buttonsAdapter = new ButtonsAdapter(getActivity(), freqsList);
            gvButtons.setAdapter(buttonsAdapter);
        });
        btMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean state = App.getMute();
                App.setMute(!state);
                Log.d(TAG, "Mute button " + App.getMute());
                radioIntent.putExtra("mute",!state);
                requireActivity().startService(radioIntent);
                if(App.getMute()) {
                    btMute.setImageDrawable(getResources().getDrawable(R.drawable.speaker_96px));
                } else {
                    btMute.setImageDrawable(getResources().getDrawable(R.drawable.mute_96px));
                }
            }
        });
        if(App.getMute()) {
            btMute.setImageDrawable(getResources().getDrawable(R.drawable.speaker_96px));
        } else {
            btMute.setImageDrawable(getResources().getDrawable(R.drawable.mute_96px));
        }
        btPlus.setOnClickListener(view -> {
            boolean notInList = true;
            if(iFreq > 0) {
                for (int f : App.freqsList) {
                    if(f == iFreq) {
                        notInList = false;
                        break;
                    }
                }
                if(notInList) {
                    boolean add = true;
                    Log.d(TAG, "Add freq " + iFreq + " lo list");
                    for (int i = 0; i < App.freqsList.size(); i++) {
                        if(iFreq < App.freqsList.get(i)) {
                            App.freqsList.add(i, iFreq);
                            add = false;
                            break;
                        }
                    }
                    if (add) App.freqsList.add(App.freqsList.size(), iFreq);
                    buttonsAdapter = new ButtonsAdapter(requireActivity(), App.freqsList);
                    gvButtons.setAdapter(buttonsAdapter);
                    App.saveArrayList(App.freqsList,"freqs");
                }
            }
        });
    }

}