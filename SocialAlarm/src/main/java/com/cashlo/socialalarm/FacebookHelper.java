package com.cashlo.socialalarm;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;

import org.json.*;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Cash on 23/02/14.
 */
public class FacebookHelper {

    private static final List<String> PERMISSIONS = Arrays.asList("read_stream");

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

    /** Called when the user clicks the Send button
     * @throws NameNotFoundException
     * @throws NoSuchAlgorithmException */
    public static void speakMessage(final Activity activity) throws PackageManager.NameNotFoundException, NoSuchAlgorithmException {
        // Do something in response to button



        // start Facebook Login
        Session.StatusCallback cb = new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state, Exception exception) {
                // TODO Auto-generated method stub
                if(state.isOpened()){
                    // Check for publish permissions
                    requestPermissions(activity, session);
                    Request.newGraphPathRequest(session, "me/home", new Request.Callback() {

                        @Override
                        public void onCompleted(Response response) throws JSONException {
                            // TODO Auto-generated method stub
                            GraphObject home = response.getGraphObject();
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
                                            TTSHelper.speak(post.getJSONObject("from").getString("name") + " says " + message );
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
                    Request.executeGraphPathRequestAsync(session, "me/home", new Request.Callback() {

                        @Override
                        public void onCompleted(Response response) throws JSONException {
                            // TODO Auto-generated method stub
                            GraphObject home = response.getGraphObject();
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
                                            TTSHelper.speak(post.getJSONObject("from").getString("name") + " says " + message );
                                        }
                                        Log.i("Facebook Result", post.getJSONObject("from").getString("name") + ": " + post.getString("message").toString());
                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }

            }
        };

        Session session = Session.openActiveSession(activity, true, cb);

        if(session != null){

        }



    }

}
