package com.aaars.b.Fragments;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aaars.b.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModuleDesignFragment extends Fragment {

    private Button mDo;
    private EditText input;
    private int parameter;

    public ModuleDesignFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_module_design, container, false);

        mDo = view.findViewById(R.id.click);
        input = view.findViewById(R.id.input);

        final TextView tv = view.findViewById(R.id.tv);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getContext().registerReceiver(null, ifilter);

        final int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        tv.setText("" + level);

        mDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parameter = Integer.parseInt(input.getText().toString());
                if(level < parameter) {
                    tv.setTextColor(getResources().getColor(R.color.green));
                }
                else {
                    tv.setTextColor(getResources().getColor(R.color.orange));
                }
            }
        });



        return view;
    }

}
