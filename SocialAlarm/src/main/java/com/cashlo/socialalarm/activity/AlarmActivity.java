package com.cashlo.socialalarm.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cashlo.socialalarm.R;
import com.cashlo.socialalarm.helper.TTSHelper;
import com.cashlo.socialalarm.helper.UserDataStorageHelper;


public class AlarmActivity extends Activity implements View.OnClickListener {

    private int mSnoozeMinutes = 5;

    private TextView mSnoozeTextView;
    private TextView mCloseTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        mSnoozeTextView = (TextView) findViewById(R.id.snooze);
        mCloseTextView = (TextView) findViewById(R.id.close);

        mSnoozeTextView.setText(getString(R.string.snooze, mSnoozeMinutes));

        mSnoozeTextView.setOnClickListener(this);
        mCloseTextView.setOnClickListener(this);

        String speech = UserDataStorageHelper.getUserData(this, UserDataStorageHelper.USER_DATA_SPEECH);
        if (TextUtils.isEmpty(speech)) {
            speech = getString(R.string.backup_speech);
            TTSHelper.speak(speech);
        } else {
            TTSHelper.speak(speech);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.snooze:
                snooze();
                break;
            case R.id.close:
                finish();
                break;
        }
    }

    private void snooze() {
        Intent intent = new Intent(this, AlarmActivity.class);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        mSnoozeMinutes * 60 * 1000, alarmIntent);
        finish();
    }
}
