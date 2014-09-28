package com.cashlo.socialalarm.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cashlo.socialalarm.AlarmHelper;
import com.cashlo.socialalarm.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AlarmSetupFragment extends Fragment implements View.OnClickListener {

    private TextView mTestAlarmTextView;


    public AlarmSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_alarm_setup, container, false);

        mTestAlarmTextView = (TextView) rootView.findViewById(R.id.alarm_test);
        mTestAlarmTextView.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.alarm_test){
            AlarmHelper.speakNewsfeed(this);
        }

    }
}
