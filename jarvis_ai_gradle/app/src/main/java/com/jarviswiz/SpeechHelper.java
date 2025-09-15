package com.jarviswiz;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class SpeechHelper {
    private TextToSpeech tts;

    public SpeechHelper(Context context) {
        tts = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.US);
            }
        });
    }

    public void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
}
