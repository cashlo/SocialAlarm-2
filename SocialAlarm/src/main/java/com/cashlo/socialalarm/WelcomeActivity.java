package com.cashlo.socialalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.cashlo.socialalarm.fragment.AlarmSetupFragment;
import com.cashlo.socialalarm.fragment.GreetingSelectionFragment;
import com.cashlo.socialalarm.fragment.WelcomeFragment;


public class WelcomeActivity extends FragmentActivity {

    private String mFirstName;
    private String mLastName;
    private String mGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        TTSHelper.initTTS(this);
        if (savedInstanceState == null) {
            SharedPreferences mFacebookUserDate = getSharedPreferences("FB", Context.MODE_PRIVATE);
            int alarmSetupStep = mFacebookUserDate.getInt("step", 0);
            Fragment fragment;
            switch (alarmSetupStep){
                case 0:
                    fragment = new WelcomeFragment();
                    break;
                case 1:
                    fragment = new GreetingSelectionFragment();
                    break;
                case 2:
                    fragment = new AlarmSetupFragment();
                    break;
                default:
                    fragment = new WelcomeFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    public String getFirstName() {
        if(mFirstName == null){
            SharedPreferences mFacebookUserDate = getSharedPreferences("FB", Context.MODE_PRIVATE);
            return mFacebookUserDate.getString("first_name", "");
        } else {
            return mFirstName;
        }
    }

    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getLastName() {
        if(mLastName == null){
            SharedPreferences mFacebookUserDate = getSharedPreferences("FB", Context.MODE_PRIVATE);
            return mFacebookUserDate.getString("last_name", "");
        } else {
            return mLastName;
        }
    }

    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getmGreeting() {
        return mGreeting;
    }

    public void setmGreeting(String mGreeting) {
        this.mGreeting = mGreeting;
    }
}
