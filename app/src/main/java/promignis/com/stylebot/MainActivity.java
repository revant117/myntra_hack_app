package promignis.com.stylebot;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import Utils.Constants;
import Utils.Helpers;
import Utils.URLStore;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String android_id;
    private SharedPreferences sp;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int CHECK_CODE = 0x1;
    private View startSpeaking;
    private Speaker speaker;
    public WebView browser;
    public String currentJsCallback;
    public ItemsFragment itemsFragment;
    public ScrollView scrollView;
    public LinearLayout scrollViewContentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        itemsFragment = new ItemsFragment();
        showItemsFragment();
        browser = (WebView)findViewById(R.id.webview);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new JsInterface(MainActivity.this), "Android");


        final RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        startSpeaking = findViewById(R.id.start_speaking);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollViewContentContainer = (LinearLayout) findViewById(R.id.scroll_view_content_container);

        startSpeaking.setTranslationY(1000);
        startSpeaking.animate().translationY(0)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000).withEndAction(new Runnable() {
            @Override
            public void run() {
                startSpeaking.animate().scaleX(5).scaleY(5)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1500).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        textToSpeech("Hi, I am style bot", null);
                        getJs(URLStore.BASE_URL);
                    }
                });
            }
        });
        TextView status = (TextView) findViewById(R.id.status);
        startSpeaking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeaking(";");
            }
        });
        setStatusBarColor();


        sp = Helpers.getSP(getApplicationContext(), Constants.INIT);
        String id = sp.getString(Constants.USER_ID, null);
        if( id != null) {
            status.setText("Status : Logged in with " + id);
        } else {
            android_id = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(Constants.USER_ID, android_id);
            editor.commit();
        }

        checkTTS();
        if(speaker != null) {
            Log.d("ERROR!", "NOT NULL");

        }

    }

    public void startSpeaking(String callback) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking!");
        currentJsCallback = callback;
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Not Supported!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    textToSpeech(result.get(0));
                    String strResult = result.get(0);
                    addToScrollViewByCustomer(strResult);
                    runCallback(strResult);
                }
                break;
            }
            case CHECK_CODE: {
                if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                    speaker = new Speaker(this);
                }else {
                    Intent install = new Intent();
                    install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(install);
                }
            }

        }
    }

    public void runCallback(final String result) {
        browser.post(new Runnable() {
            @Override
            public void run() {
                browser.loadUrl("javascript:" + currentJsCallback);
                browser.loadUrl("javascript:window.jsCallback('" + result + "');");
                currentJsCallback = null;
            }
        });
    }

    public void addToScrollViewByCustomer(String text) {
        final TextView tv = new TextView(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        tv.setLayoutParams(params);
        tv.setText(text);
        tv.setTextColor(getResources().getColor(R.color.white));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scrollViewContentContainer.addView(tv);
            }
        });
    }

    public void addToScrollViewByBot(String text) {
        final TextView tv = new TextView(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT;
        tv.setLayoutParams(params);
        tv.setText(text);
        tv.setTextColor(getResources().getColor(R.color.white));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scrollViewContentContainer.addView(tv);
            }
        });
    }

    public void textToSpeech(String toSpeak, Runnable r) {
        speaker.speak(toSpeak);
        addToScrollViewByBot(toSpeak);
        startSpeaking.animate().alpha(0.5f).scaleX(2).scaleY(2).setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run() {
                startSpeaking.animate().alpha(1).scaleX(1).scaleY(1).setDuration(500).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        startSpeaking.animate().alpha(0.5f).scaleX(2).scaleY(2).setDuration(500).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                startSpeaking.animate().alpha(5).scaleX(5).scaleY(5).setDuration(500);
                            }
                        });
                    }
                });
            }
        });
        if(r != null) {
            speaker.setRunnable(r);
        }
    }


    private void getJs(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                addJsToWebView(response.body().string());
            }
        });
    }

    private void addJsToWebView(final String js) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                browser.loadUrl("javascript:" + js);
            }
        });
    }

    private void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
    }

    public void showItemsFragment() {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        if(ft != null) {
            ft.add(R.id.fragment_container, itemsFragment);
            ft.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speaker.destroy();
    }
}
