package com.cashlo.socialalarm.fragment;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cashlo.socialalarm.AlarmActivity;
import com.cashlo.socialalarm.AlarmHelper;
import com.cashlo.socialalarm.R;
import com.cashlo.socialalarm.service.GetSpeechIntentService;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AlarmSetupFragment extends Fragment implements View.OnClickListener {

    private TextView mTestAlarmTextView;
    private TextView mSaveAlarmButton;
    private TimePicker mAlarmTimePicker;


    public AlarmSetupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetSpeechIntentService.getFacebookSpeech(getActivity()); // We can already try to get the speech, we will need it later
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_alarm_setup, container, false);

        mTestAlarmTextView = (TextView) rootView.findViewById(R.id.alarm_test);
        mTestAlarmTextView.setOnClickListener(this);

        mSaveAlarmButton =  (TextView) rootView.findViewById(R.id.save_alarm);
        mSaveAlarmButton.setOnClickListener(this);

        mAlarmTimePicker = (TimePicker) rootView.findViewById(R.id.alarm_time_picker);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.alarm_test:
               new getNewsfeedTask().execute(this);
                break;
            case R.id.save_alarm:
                saveAlarm();
        }
    }

    private void saveAlarm() {
        Context context = getActivity();
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmActivity.class);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, mAlarmTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, mAlarmTimePicker.getCurrentMinute());

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getActivity(context, 0, intent, 0));
    }

    private class getNewsfeedTask extends AsyncTask<Fragment, Void, Void> {
        @Override
        protected Void doInBackground(Fragment... params) {
            Fragment fragment = params[0];
            AlarmHelper.speakNewsfeed(fragment);
            return null;
        }
    }
}
