package com.cashlo.socialalarm.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cashlo.socialalarm.R;
import com.cashlo.socialalarm.TTSHelper;
import com.cashlo.socialalarm.WelcomeActivity;
import com.cashlo.socialalarm.helper.UserDataStorageHelper;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class WelcomeFragment extends Fragment {


    private static final String TAG = "FacebookLogin";
    private static final List<String> PERMISSIONS = Arrays.asList("read_stream", "friends_birthday", "user_friends");

    private LoginButton authButton;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private UiLifecycleHelper uiHelper;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TTSHelper.initTTS(getActivity().getApplicationContext());
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        setupButtons(rootView);
        return rootView;
    }


    private void setupButtons(View view){
        authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(PERMISSIONS);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    Log.i(TAG,"Requested user name");
                    if (user != null) {
                        WelcomeActivity welcomeActivity = (WelcomeActivity) getActivity();
                        welcomeActivity.setFirstName(user.getFirstName());
                        welcomeActivity.setLastName(user.getLastName());
                        UserDataStorageHelper.storeUserData(getActivity(), UserDataStorageHelper.USER_DATA_FIRST_NAME, user.getFirstName());
                        UserDataStorageHelper.storeUserData(getActivity(), UserDataStorageHelper.USER_DATA_LAST_NAME, user.getLastName());
                        Log.i("QWE", "User is "+ user.getFirstName());
                    }
                    moveToNextFragment();
                }
            }).executeAsync();

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    private void moveToNextFragment(){
        UserDataStorageHelper.storeUserProgress(getActivity(), 1);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        GreetingSelectionFragment greetingSelectionFragment = new GreetingSelectionFragment();
        transaction.replace(R.id.container, greetingSelectionFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private static boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }
}
