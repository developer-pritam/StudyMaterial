package com.study_material.HomeFeed;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.study_material.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeFragment extends Fragment implements AdapterMethods {

    private static final String TAG = "SMSD";
    FirebaseFirestore db;
    FirebaseDatabase database;


    FirebaseAuth auth;
    View view;
    Boolean isFirst = true;
    ArrayList<FeedItemModel> feedList = new ArrayList<>();
    DocumentSnapshot lastVisible;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    FeedAdapter adapter;
    boolean isLoading = true;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (isFirst) {
            database = FirebaseDatabase.getInstance();
            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            view = inflater.inflate(R.layout.fragment_home, container, false);
            swipeRefreshLayout = view.findViewById(R.id.swipeRefreshView);

            progressBar = view.findViewById(R.id.loading);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getFeedData();
                }
            });

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.feedRecyclerView);
            adapter = new FeedAdapter(feedList, this, database, auth.getCurrentUser());
            swipeRefreshLayout.setRefreshing(true);
            getFeedData();

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);


        }
        return view;

    }

    private void getFeedDataAgain() {
        progressBar.setVisibility(View.VISIBLE);
        isLoading = true;
        try {
            //      Log.d(TAG, lastVisible.toString());

            db.collection("notes").orderBy("uploadedOn", Query.Direction.DESCENDING).startAfter(lastVisible).limit(15).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            if (task.getResult().size() == 0) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "No more data available", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String subjectName = document.getString("subject");
                                String branchName = document.getString("branch");
                                String course = document.getString("course");
                                String fileName = document.getString("fileName");
                                String title = document.getString("title");
                                String uri = document.getString("uri");
                                String userName = document.getString("userName");
                                String userUID = document.getString("userUID");
                                FeedItemModel feedItemModel = new FeedItemModel(false, document.getId(), subjectName, branchName, course, fileName, title, uri, userName, userUID);
                                isFavourite(feedItemModel, document.getId(), feedList.size());
                                feedList.add(feedItemModel);
                                lastVisible = document;


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());

                        }
                        adapter.notifyDataSetChanged();
                        isLoading = false;

                        progressBar.setVisibility(View.GONE);

                    });

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


    private void getFeedData() {
        isLoading = true;
        db.collection("notes").orderBy("uploadedOn", Query.Direction.DESCENDING).limit(20).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        feedList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Log.d(TAG, document.getId() + " => " + document.getString("name"));

                            String subjectName = document.getString("subject");
                            String branchName = document.getString("branch");
                            String course = document.getString("course");
                            String fileName = document.getString("fileName");
                            String title = document.getString("title");
                            String uri = document.getString("uri");
                            String userName = document.getString("userName");
                            String userUID = document.getString("userUID");
                         FeedItemModel feedItemModel=   new FeedItemModel(false, document.getId(), subjectName, branchName, course, fileName, title, uri, userName, userUID);
                          isFavourite(feedItemModel, document.getId(), feedList.size());
                            feedList.add(feedItemModel);
                            lastVisible = document;


                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    adapter.notifyDataSetChanged();
                    adapter.notifyItemRangeChanged(feedList.size() - task.getResult().size(), feedList.size());
                    swipeRefreshLayout.setRefreshing(false);
                    isLoading = false;
                });

    }

    private boolean isFavourite(FeedItemModel feedItemModel,String docID, int pos) {
//        Log.d(TAG, docID);
        DatabaseReference myRef = database.getReference("Users/" + auth.getCurrentUser().getUid() + "/favourites/");
        AtomicBoolean result = new AtomicBoolean(false);
        myRef.child(docID).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
            } else {

             feedItemModel.setIsFavourite(task.getResult().exists());
             adapter.notifyItemChanged(pos);
                Log.d(TAG, task.getResult().getValue() + docID + task.getResult().exists() + result.get());
            }
        });
        Log.d(TAG, docID + result.get());


        return result.get();
    }

    @Override
    public void loadMoreData() {
        if (!isLoading) {
            getFeedDataAgain();
        }
    }


}