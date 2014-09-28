package com.cashlo.socialalarm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Cash on 29/09/2014.
 */
public class AlarmHelper {
    private static final String TAG = "AlarmHelper";

    public static void speakNewsfeed(Fragment fragment){

        Session session = Session.openActiveSession(fragment.getActivity(), fragment, true, null);

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
