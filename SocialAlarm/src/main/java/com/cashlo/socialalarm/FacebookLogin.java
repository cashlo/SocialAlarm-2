package com.cashlo.socialalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class FacebookLogin extends Fragment implements View.OnClickListener {

    private static final String TAG = "FacebookLogin";
    private static final List<String> PERMISSIONS = Arrays.asList("read_stream", "friends_birthday", "user_friends");

    private LoginButton authButton;
    private Button mSpeakButton, mStopSpeakButton;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private UiLifecycleHelper uiHelper;

    public FacebookLogin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TTSHelper.initTTS(getActivity());
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_facebook_login, container, false);
        setupButtons(view);
        return view;
    }

    private void setupButtons(View view){
        authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(PERMISSIONS);

        mSpeakButton = (Button) view.findViewById(R.id.speak_button);
        mSpeakButton.setOnClickListener(this);

        mStopSpeakButton = (Button) view.findViewById(R.id.stop_speak_button);
        mStopSpeakButton.setOnClickListener(this);
    }

    private void showhideButtons(Boolean show){
        if(show){
            mSpeakButton.setVisibility(View.VISIBLE);
            mStopSpeakButton.setVisibility(View.VISIBLE);
        } else {
            mSpeakButton.setVisibility(View.GONE);
            mStopSpeakButton.setVisibility(View.GONE);
        }
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            //requestPermissions(getActivity(), session);
            showhideButtons(true);
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            showhideButtons(false);
        }
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

    private static void requestPermissions(Activity activity, Session session){
        List<String> permissions = session.getPermissions();
        if (!isSubsetOf(PERMISSIONS, permissions)) {
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(activity, PERMISSIONS);
            session.requestNewReadPermissions(newPermissionsRequest);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.speak_button:
                speakNewsfeed();
                break;
            case R.id.stop_speak_button:
                TTSHelper.stop();
        }
    }


    private void speakNewsfeed(){

        Session session = Session.getActiveSession();

        StringBuilder speechBuilder = new StringBuilder();

        String todaysDate = new SimpleDateFormat("MM/dd").format(new Date());
        todaysDate = "02/06";

        String mFriendsWithBirthdayTodayQuery = "SELECT name FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()) AND substr(birthday_date,0,5) = '" + todaysDate + "'";
        Bundle params = new Bundle();
        params.putString("q", mFriendsWithBirthdayTodayQuery);

        Request request = new Request(session, "/fql", params, HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        Log.i(TAG, "Got results: " + response.toString());

                        GraphObject friendsWithBirthday = response.getGraphObject();

                        if(friendsWithBirthday == null){
                            return;
                        }

                        //Log.i("FB", response.toString());
                        JSONArray friends = (JSONArray) friendsWithBirthday.getProperty("data");
                        for (int i = 0; i < friends.length(); i++) {
                            try {
                                JSONObject friend = (JSONObject) friends.get(i);
                                if (friend.has("name")) {
                                    String friendName = friend.getString("name").toString();
                                    Log.i("Facebook Result", friendName + "'s birthday!");

                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    }
                }
        );
        Request.executeBatchAsync(request);

        Request.newGraphPathRequest(session, "me/home", new Request.Callback() {

            @Override
            public void onCompleted(Response response) throws JSONException {
                // TODO Auto-generated method stub

                GraphObject home = response.getGraphObject();

                if(home == null){
                    return;
                }

                //Log.i("FB", response.toString());
                JSONArray feed = (JSONArray) home.getProperty("data");
                //Log.i("FB", feed.getClass().getCanonicalName());
                for (int i = 0; i < feed.length(); i++) {
                    try {
                        JSONObject post = (JSONObject) feed.get(i);
                        if (post.has("message")) {
                            String message = post.getString("message").toString();
                            message = message.replaceAll("\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]", "");
                            String englishMessage = message.replaceAll("[^a-zA-Z\\s]", "");
                            Log.i("fb", englishMessage + " " + englishMessage.length() + " " + message.length());
                            if (englishMessage.length() * 2 > message.length()) {
                                TTSHelper.speak(post.getJSONObject("from").getString("name") + " says " + message);
                            }
                            Log.i("Facebook Result", post.getJSONObject("from").getString("name") + ": " + post.getString("message").toString());
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).executeAsync();
    }
}
