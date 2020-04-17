package com.example.myutils.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TTS {
    private TextToSpeech textToSpeech;
    private String TTS_ID = "TTS";

    public TTS(Context context){
        textToSpeech = new TextToSpeech(context,new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int r = textToSpeech.setLanguage(Locale.CHINESE);
                }
            }
        });
    }

    public int speakTTS(String string){
        List<String> strings = new ArrayList<>();
        int max = TextToSpeech.getMaxSpeechInputLength();
        if(string.length() > max) {
            int speakFlag = TextToSpeech.SUCCESS;
            for (int i = 0; i < (string.length() / max)+1; i++) {
                int flag = textToSpeech.speak(string.substring(i * max, (i + 1) * max - 1), TextToSpeech.QUEUE_ADD, null, TTS_ID);   //目前 param:null 還 ok
                if(flag == TextToSpeech.ERROR){
                    return speakFlag = TextToSpeech.ERROR;
                }
            }
            return speakFlag;
        } else {
            return textToSpeech.speak(string, TextToSpeech.QUEUE_FLUSH, null, TTS_ID);   //目前 param:null 還 ok
        }
    }

    public void shutdownTTS(){
        textToSpeech.shutdown();
    }

    public int stopSpeaking() {
        int stopFlag = TextToSpeech.ERROR;
        if (textToSpeech.isSpeaking()){
            stopFlag = textToSpeech.stop();
        }
        return stopFlag;
    }

    /*private Bundle setBundle(){
        return new Bundle(){};
    }*/ // you could use and volume pan to get it more realistic, first get the angle of g_glass and the phone, then you could set it in pan, then set the volume with distance param
}
