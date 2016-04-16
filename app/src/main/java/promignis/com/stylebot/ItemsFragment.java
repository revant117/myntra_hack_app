package promignis.com.stylebot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by sahebjot on 4/17/16.
 */
public class ItemsFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_items, null);
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
}
