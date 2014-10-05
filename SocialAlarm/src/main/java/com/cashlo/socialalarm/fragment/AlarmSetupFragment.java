package com.cashlo.socialalarm.fragment;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cashlo.socialalarm.R;
import com.cashlo.socialalarm.activity.AlarmActivity;
import com.cashlo.socialalarm.helper.TTSHelper;
import com.cashlo.socialalarm.helper.UserDataStorageHelper;
import com.cashlo.socialalarm.service.GetSpeechIntentService;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AlarmSetupFragment extends Fragment implements View.OnClickListener {

    private TextView mTestAlarmTextView;
    private TextView mTestAlarmStopTextView;

    private TextView mSaveAlarmButton;
    private TimePicker mAlarmTimePicker;
    private ProgressBar mTestAlarmProgressBar;

    private class SpeechBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mTestAlarmProgressBar.setVisibility(View.GONE);
            TTSHelper.speak(intent.getStringExtra(GetSpeechIntentService.EXTRA_SPEECH));
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this);
        }
    }


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

        mTestAlarmProgressBar = (ProgressBar) rootView.findViewById(R.id.alarm_test_loading);

        mTestAlarmStopTextView = (TextView) rootView.findViewById(R.id.alarm_test_stop);
        mTestAlarmStopTextView.setOnClickListener(this);

        mSaveAlarmButton =  (TextView) rootView.findViewById(R.id.save_alarm);
        mSaveAlarmButton.setOnClickListener(this);

        mAlarmTimePicker = (TimePicker) rootView.findViewById(R.id.alarm_time_picker);


        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.alarm_test:
                mTestAlarmStopTextView.setVisibility(View.VISIBLE);
                String speech = UserDataStorageHelper.getUserData(getActivity(), UserDataStorageHelper.USER_DATA_SPEECH);
                if(TextUtils.isEmpty(speech)){
                    startWaitingForSpeech();
                } else {
                    TTSHelper.speak(speech);
                }
                break;
            case R.id.alarm_test_stop:
                TTSHelper.stop();
                break;
            case R.id.save_alarm:
                saveAlarm();
        }
    }

    private void startWaitingForSpeech() {
        mTestAlarmProgressBar.setVisibility(View.VISIBLE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new SpeechBroadcastReceiver(),
                new IntentFilter(GetSpeechIntentService.FACEBOOK_SPEECH_FINISHED));
    }

    private void saveAlarm() {
        Context context = getActivity();
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmActivity.class);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, mAlarmTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, mAlarmTimePicker.getCurrentMinute());

        if (calendar.getTimeInMillis() - System.currentTimeMillis() <= 0) {
            calendar.add(Calendar.HOUR, 24);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getActivity(context, 0, intent, 0));
        } else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getActivity(context, 0, intent, 0));
        }

        long timeDifference = calendar.getTimeInMillis() - System.currentTimeMillis();
        Log.i("timeDifference", "time: " + formatDuration(timeDifference));
        Toast.makeText(getActivity(), "Alarm is set to " + formatDuration(timeDifference) + " from now", Toast.LENGTH_LONG).show();

        calendar.add(Calendar.MINUTE, -5);

        GetSpeechIntentService.scheduleFacebookSpeech(getActivity(), calendar);

    }

    private CharSequence formatDuration(long millis) {
        final Resources res = getResources();
        if (millis >= DateUtils.HOUR_IN_MILLIS) {
            final int hours = (int) ((millis + 1800000) / DateUtils.HOUR_IN_MILLIS);
            return res.getQuantityString(
                    R.plurals.duration_hours, hours, hours);
        } else if (millis >= DateUtils.MINUTE_IN_MILLIS) {
            final int minutes = (int) ((millis + 30000) / DateUtils.MINUTE_IN_MILLIS);
            return res.getQuantityString(
                    R.plurals.duration_minutes, minutes, minutes);
        } else {
            final int seconds = (int) ((millis + 500) / DateUtils.SECOND_IN_MILLIS);
            return res.getQuantityString(
                    R.plurals.duration_seconds, seconds, seconds);
        }
    }
}
