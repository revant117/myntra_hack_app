package promignis.com.stylebot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sahebjot on 4/17/16.
 */
public class ItemsFragment extends Fragment {

    String url  =  "http://myntra-hackathon.herokuapp.com/get-myntra";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_items, null);
        api_call(url);
        return v;
    }

    public void setupRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView rv = (RecyclerView) getActivity().findViewById(R.id.rv);
        rv.setLayoutManager(mLayoutManager);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter();
        rv.setAdapter(recyclerAdapter);
        rv.setItemAnimator(new DefaultItemAnimator());
        ArrayList<Item> items = new ArrayList<>();
        // add items

    }

    public void api_call(String url){
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
            public void onResponse(Call call, Response response)  throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Log.d("api", response.body().string());
                JSONObject jsonObj = new JSONObject(response.body().string());
            }
        });
    }
}
