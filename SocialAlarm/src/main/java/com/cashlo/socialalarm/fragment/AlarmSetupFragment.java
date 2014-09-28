package com.cashlo.socialalarm.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cashlo.socialalarm.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AlarmSetupFragment extends Fragment {


    public AlarmSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_setup, container, false);
    }


}
