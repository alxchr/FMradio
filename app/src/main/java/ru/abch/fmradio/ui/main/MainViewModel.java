package ru.abch.fmradio.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.abch.fmradio.App;

public class MainViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    public String station = "", RDSText = "qwerty";
    int level = 0, freq = 10000;
    MutableLiveData<String> stationData, RDSData;
    MutableLiveData<Integer> levelData, freqData;
    public LiveData<String> getStation() {
        if(stationData == null) {
            stationData = new MutableLiveData<>();
            loadStation();
        }
        return stationData;
    }
    public void loadStation(int f, String rdsName) {
        if(rdsName != null && rdsName.length() > 0) station = rdsName;
        else {
            station = f/100. + " Mhz";
        }
        stationData.postValue(station);
    }
    private void loadStation() {
        station = App.getChannel()/100. + " Mhz";
        stationData.postValue(station);
    }
    public LiveData<String> getRDSText() {
        if(RDSData == null) {
            RDSData = new MutableLiveData<>();
            loadRDS();
        }
        return RDSData;
    }
    private void loadRDS() {
        RDSData.postValue(RDSText);
    }
    public void loadRDS(String txt) {
        RDSText = txt;
        RDSData.postValue(RDSText);
    }
    public LiveData<Integer> getRSSI() {
        if(levelData == null) {
            levelData = new MutableLiveData<>();
            loadRSSI();
        }
        return levelData;
    }
    public void loadRSSI(int l) {
        level = l;
        levelData.postValue(level);
    }
    private void loadRSSI() {
        levelData.postValue(level);
    }
    private void loadFreq() {
        freqData.postValue(freq);
    }
    public void loadFreq(int f) {
        freq = f;
        freqData.postValue(freq);
    }
    public LiveData<Integer> getFreq() {
        if(freqData == null) {
            freqData = new MutableLiveData<>();
            loadFreq();
        }
        return freqData;
    }
}