package com.cashlo.socialalarm;

import android.content.Context;
import android.speech.tts.TextToSpeech;

/**
 * Created by Cash on 13/04/2014.
 */
public class TTSHelper {
    private static TextToSpeech tts = null;

    public static void initTTS(Context context){
        if(tts == null){
            tts = new TextToSpeech(context, null);
        }
    }


    public static void speak(String script){
        if(tts == null)
            return;

        tts.speak(script,  TextToSpeech.QUEUE_ADD, null);
        tts.playSilence(1000, TextToSpeech.QUEUE_ADD, null);
    }

    public static void stop(){
        if(tts == null)
            return;

        tts.stop();
    }
}