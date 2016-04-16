package promignis.com.stylebot;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by sahebjot on 4/16/16.
 */
public class JsInterface {
    Context mContext;

    JsInterface(Context c) {
        mContext = c;
    }

    @android.webkit.JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
    }

    @android.webkit.JavascriptInterface
    public void LogThis(String log) {
        Log.d("TESTING!", log);
    }

    @android.webkit.JavascriptInterface
    public void speakThis(String toSpeak) {
        ((MainActivity)mContext).textToSpeech(toSpeak, null);
    }

    @android.webkit.JavascriptInterface
    public void askThis(String toAsk, final String callback) {
        ((MainActivity)mContext).textToSpeech(toAsk, new Runnable() {
            public void run() {
                ((MainActivity) mContext).startSpeaking(callback);
            }
        });
    }



}
