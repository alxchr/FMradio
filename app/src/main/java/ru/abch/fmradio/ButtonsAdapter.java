package ru.abch.fmradio;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class ButtonsAdapter extends BaseAdapter {
    Context ctx;
    String TAG= "ButtonsAdapter" ;
    LayoutInflater lInflater;
    ArrayList<Integer> freqsList;
    public ButtonsAdapter(Context context, ArrayList<Integer> freqsList) {
        ctx = context;
        this.freqsList = freqsList;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return freqsList.size();
    }

    @Override
    public Object getItem(int i) {
        return freqsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.button_item, parent, false);
        }
        view.setTag(freqsList.get(i));
        ((androidx.appcompat.widget.AppCompatButton) view).setText(String.valueOf(freqsList.get(i)/100.));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int freq = (int) view.getTag();
                Log.d(TAG, "Click f " + freq);
                App.setChannel(freq);
                ((MainActivity) ctx).mViewModel.loadStation(freq,"");
                Intent radioIntent = new Intent(ctx, RadioService.class);
                radioIntent.putExtra("freq", freq);
                radioIntent.putExtra("mute", App.getMute());
                ctx.startService(radioIntent);
            }
        });
        return view;
    }
}
