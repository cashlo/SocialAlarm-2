package com.cashlo.socialalarm.helper;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;

/**
 * Created by Cash on 13/04/2014.
 *
 */
public class TTSHelper {
    private static TextToSpeech tts = null;

    public static void initTTS(Context context) {
        if (tts == null) {
            tts = new TextToSpeech(context, null);
        }
    }


    public static void speak(String script) {
        if (tts == null)
            return;

        HashMap<String, String> ttsParams = new HashMap<String, String>();
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));


        tts.speak(script, TextToSpeech.QUEUE_ADD, ttsParams);
        tts.playSilence(1000, TextToSpeech.QUEUE_ADD, null);
    }

    public static void stop() {
        if (tts == null)
            return;

        tts.stop();
    }
}