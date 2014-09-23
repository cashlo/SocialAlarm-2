package com.cashlo.socialalarm.fragment;



import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cashlo.socialalarm.R;
import com.cashlo.socialalarm.WelcomeActivity;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class GreetingSelectionFragment extends Fragment {

    private ListView mGreetingList;
    private ArrayAdapter<String> mArrayAdapter;

    public GreetingSelectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_greeting_selection, container, false);

        WelcomeActivity welcomeActivity = (WelcomeActivity) getActivity();

        mGreetingList = (ListView) rootView.findViewById(R.id.greeting_list);
        mArrayAdapter = new ArrayAdapter<String>(welcomeActivity, R.layout.greeting_selection_item);

        Resources resources = getResources();

        mArrayAdapter.add(resources.getString(R.string.greeting_1,  welcomeActivity.getFirstName()));
        mArrayAdapter.add(resources.getString(R.string.greeting_2,  welcomeActivity.getFirstName()));
        mArrayAdapter.add(resources.getString(R.string.greeting_3,  welcomeActivity.getFirstName()));

        mGreetingList.setAdapter(mArrayAdapter);

        return rootView;
    }


}
