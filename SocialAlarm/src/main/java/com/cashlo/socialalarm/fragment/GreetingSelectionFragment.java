package com.cashlo.socialalarm.fragment;



import android.content.Context;
import android.content.SharedPreferences;
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
import com.cashlo.socialalarm.TTSHelper;
import com.cashlo.socialalarm.WelcomeActivity;
import com.cashlo.socialalarm.service.GetSpeechIntentService;

import org.w3c.dom.Text;

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

        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mGreeting = mArrayAdapter.getItem(position);
        view.setSelected(true);
        TTSHelper.speak(mGreeting);
    }

    private void moveToNextFragment(){
        SharedPreferences mFacebookUserDate = getActivity().getSharedPreferences("FB", Context.MODE_PRIVATE);
        mFacebookUserDate.edit().putInt("step", 2).apply();
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
            SharedPreferences mFacebookUserDate = getActivity().getSharedPreferences("FB", Context.MODE_PRIVATE);
            mFacebookUserDate.edit().putString("greeting",mGreeting).apply();
            GetSpeechIntentService.getFacebookSpeech(getActivity()); // We can already try to get the speech, we will need it later
            moveToNextFragment();
        }
    }
}
