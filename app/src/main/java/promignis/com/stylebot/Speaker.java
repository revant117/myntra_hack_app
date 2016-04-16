package promignis.com.stylebot;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by sahebjot on 4/16/16.
 */
public class Speaker implements OnInitListener {
    private TextToSpeech tts;
    private boolean ready = false;
    private Runnable currentRunnable = null;
    private Context mContext;
    private int counter = 0;
    Speaker(Context c) {
        mContext = c;
        tts = new TextToSpeech(c, this);
    }
    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS) {
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    counter += 1;
                    if(currentRunnable != null && counter == 2) {
                        Log.d("JS!", "RUNNING!");
                        currentRunnable.run();
                        counter = 0;
                    }
                }

                @Override
                public void onError(String utteranceId) {
                }
            });
            tts.setLanguage(Locale.ENGLISH);
            ready = true;
        }
    }

    public void speak(String text){
        if(ready) {
            HashMap<String, String> hash = new HashMap<String,String>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                    String.valueOf(AudioManager.STREAM_NOTIFICATION));
            hash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");
            tts.speak(text, TextToSpeech.QUEUE_ADD, hash);

        }
    }


    public void pause(int duration) {
        tts.playSilence(duration, TextToSpeech.QUEUE_ADD, null);
    }

    public void destroy(){
        tts.shutdown();
    }

    public void setRunnable(Runnable r) {
        currentRunnable = r;
    }
}
