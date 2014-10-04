package com.cashlo.socialalarm.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.cashlo.socialalarm.helper.UserDataStorageHelper;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GetSpeechIntentService extends IntentService {

    private static final String TAG = "GetSpeechIntentService";

    private static final String GET_FACEBOOK_SPEECH = "com.cashlo.socialalarm.service.action.GET_FACEBOOK_SPEECH";

    private static final String PARAM_FACEBOOK_SESSION = "FACEBOOK_SESSION";

    private static final String FACEBOOK_SPEECH_FINISHED = "FACEBOOK_SPEECH_FINISHED";
    private static final String EXTRA_SPEECH = "SPEECH";

    public static void getFacebookSpeech(Activity activity) {
        Session session = Session.openActiveSession(activity, true, null);
        Intent intent = new Intent(activity, GetSpeechIntentService.class);
        intent.setAction(GET_FACEBOOK_SPEECH);
        intent.putExtra(PARAM_FACEBOOK_SESSION, session);
        activity.startService(intent);
    }

    public GetSpeechIntentService() {
        super("GetSpeechIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (GET_FACEBOOK_SPEECH.equals(action)) {
                Session session = (Session) intent.getSerializableExtra(PARAM_FACEBOOK_SESSION);
                handleGetFacebookSpeech(session);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleGetFacebookSpeech(Session session) {
        final StringBuilder speechBuilder = new StringBuilder();

        String greeting = UserDataStorageHelper.getUserData(this, UserDataStorageHelper.USER_DATA_GREETING);
        speechBuilder.append(greeting);

        getFriendsBirthdayStringAndWait(session, speechBuilder);
        getNewsFeedAndWait(session, speechBuilder);

        String speech = speechBuilder.toString();
        Log.i(TAG, speech);
        UserDataStorageHelper.storeUserData(this, UserDataStorageHelper.USER_DATA_SPEECH, speech);
        sendFacebookSpeechFinishedBoardcast(speech);
    }

    private void getNewsFeedAndWait(Session session, final StringBuilder speechBuilder) {
        Request.newGraphPathRequest(session, "me/home", new Request.Callback() {

            @Override
            public void onCompleted(Response response) throws JSONException {
                // TODO Auto-generated method stub

                GraphObject home = response.getGraphObject();

                if (home == null) {
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
                            Log.i(TAG, englishMessage + " " + englishMessage.length() + " " + message.length());
                            if (englishMessage.length() * 2 > message.length()) {
                                String friendsaid = post.getJSONObject("from").getString("name") + " says " + message + ". ";
                                //TTSHelper.speak(friendsaid);
                                speechBuilder.append(friendsaid);
                            }
                            Log.i("Facebook Result", post.getJSONObject("from").getString("name") + ": " + post.getString("message").toString());
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).executeAndWait();
    }

    private void getFriendsBirthdayStringAndWait(Session session, final StringBuilder speechBuilder) {
        String todaysDate = new SimpleDateFormat("MM/dd").format(new Date());

        String mFriendsWithBirthdayTodayQuery = "SELECT name FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()) AND substr(birthday_date,0,5) = '" + todaysDate + "'";
        Bundle params = new Bundle();
        params.putString("q", mFriendsWithBirthdayTodayQuery);

        new Request(session, "/fql", params, HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        Log.i(TAG, "Got results: " + response.toString());

                        GraphObject friendsWithBirthday = response.getGraphObject();

                        if(friendsWithBirthday == null){
                            return;
                        }

                        //Log.i("FB", response.toString());
                        ArrayList<String> friendNamesArray = new ArrayList<String>();
                        JSONArray friends = (JSONArray) friendsWithBirthday.getProperty("data");
                        for (int i = 0; i < friends.length(); i++) {
                            try {
                                JSONObject friend = (JSONObject) friends.get(i);
                                if (friend.has("name")) {
                                    String friendName = friend.getString("name").toString();
                                    friendNamesArray.add(friendName);
                                    Log.i(TAG, friendName + "'s birthday!");

                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        if(!friendNamesArray.isEmpty()){
                            speechBuilder.append("Today is the birthday of ");
                            speechBuilder.append(TextUtils.join(",", friendNamesArray));
                        }

                    }
                }
        ).executeAndWait();
    }

    private void sendFacebookSpeechFinishedBoardcast(String speech){
        Intent intent = new Intent(FACEBOOK_SPEECH_FINISHED);
        intent.putExtra(EXTRA_SPEECH, speech);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
