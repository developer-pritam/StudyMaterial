package com.study_material.HomeFeed;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study_material.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {


    View view;
Boolean isFirst = true;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    ArrayList<FeedItemModel> feedList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (isFirst) {
            view = inflater.inflate(R.layout.fragment_home, container, false);

        feedList.add(new FeedItemModel("DBMS", "CSE", "BTech", "Test", "Title", "sjv", "sv", "sv"));
        feedList.add(new FeedItemModel("DBMS", "CSE", "BTech", "Test", "Title", "sjv", "sv", "sv"));
        feedList.add(new FeedItemModel("DBMS", "CSE", "BTech", "Test", "Title", "sjv", "sv", "sv"));
        feedList.add(new FeedItemModel("DBMS", "CSE", "BTech", "Test", "Title", "sjv", "sv", "sv"));
        feedList.add(new FeedItemModel("DBMS", "CSE", "BTech", "Test", "Title", "sjv", "sv", "sv"));
        feedList.add(new FeedItemModel("DBMS", "CSE", "BTech", "Test", "Title", "sjv", "sv", "sv"));
        feedList.add(new FeedItemModel("DBMS", "CSE", "BTech", "Test", "Title", "sjv", "sv", "sv"));




            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.feedRecyclerView);
            FeedAdapter adapter = new FeedAdapter(feedList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
        return view;

    }
}