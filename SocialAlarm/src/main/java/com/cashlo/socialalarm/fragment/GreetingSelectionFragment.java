package com.cashlo.socialalarm.fragment;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cashlo.socialalarm.R;
import com.cashlo.socialalarm.activity.WelcomeActivity;
import com.cashlo.socialalarm.helper.TTSHelper;
import com.cashlo.socialalarm.helper.UserDataStorageHelper;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class GreetingSelectionFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView mGreetingList;
    private ArrayAdapter<String> mArrayAdapter;
    private TextView mNextTextView;

    private String mGreeting;

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
        mNextTextView = (TextView) rootView.findViewById(R.id.greeting_selection_next);

        Resources resources = getResources();

        mArrayAdapter.add(resources.getString(R.string.greeting_1, welcomeActivity.getFirstName()));
        mArrayAdapter.add(resources.getString(R.string.greeting_2, welcomeActivity.getFirstName()));
        mArrayAdapter.add(resources.getString(R.string.greeting_3,  welcomeActivity.getFirstName()));

        mGreetingList.setAdapter(mArrayAdapter);
        mGreetingList.setOnItemClickListener(this);

        mNextTextView.setOnClickListener(this);

        mGreeting = resources.getString(R.string.greeting_1, welcomeActivity.getFirstName());

        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mGreeting = mArrayAdapter.getItem(position);
        view.setSelected(true);
        TTSHelper.speak(mGreeting);
    }

    private void moveToNextFragment(){
        UserDataStorageHelper.storeUserProgress(getActivity(), 2);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        AlarmSetupFragment alarmSetupFragment = new AlarmSetupFragment();
        transaction.replace(R.id.container, alarmSetupFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.greeting_selection_next){
            WelcomeActivity welcomeActivity = (WelcomeActivity) getActivity();
            welcomeActivity.setmGreeting(mGreeting);
            UserDataStorageHelper.storeUserData(getActivity(), UserDataStorageHelper.USER_DATA_GREETING, mGreeting);
            moveToNextFragment();
        }
    }
}
