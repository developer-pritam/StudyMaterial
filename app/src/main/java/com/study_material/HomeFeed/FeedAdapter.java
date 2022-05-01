package com.study_material.HomeFeed;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.study_material.R;

import java.util.ArrayList;


public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private static final String TAG = "SMSD";
    DownloadManager manager;
    HomeFragment context;
    FirebaseDatabase database;
    FirebaseUser user;
    private ArrayList<FeedItemModel> listData;


    public FeedAdapter(ArrayList<FeedItemModel> listData, HomeFragment context, FirebaseDatabase database, FirebaseUser user) {
        this.listData = listData;
        this.context = context;
        this.database = database;
        this.user = user;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.feed_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.getAdapterPosition() == listData.size() - 3) {
            context.loadMoreData();
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FeedItemModel myListData = listData.get(position);
        holder.branchName.setText(myListData.getBranch());
        holder.title.setText(myListData.getTitle());
        holder.fileName.setText(myListData.getFileName());
        holder.uploadedBy.setText(myListData.getUserName());
        holder.subjectName.setText(myListData.getSubject());
        Log.d(TAG, myListData.getIsFavourite() + "b");

        if (myListData.getIsFavourite()) {
            holder.favouriteIcon.setImageResource(R.drawable.favorite_slected_icon);
        }


        holder.favouriteIcon.setOnClickListener(v -> {
            if (user.isAnonymous()  ) {
                Toast.makeText(context.getContext(), "You are not registered", Toast.LENGTH_SHORT).show();
            }else if ( myListData.getIsFavourite()){

                holder.favouriteIcon.setImageResource(R.drawable.ic_favorite);
                DatabaseReference myRef = database.getReference("Users/" + user.getUid() + "/favourites/" + myListData.getDocID());
                myListData.setIsFavourite(false);
                myRef.removeValue();

            } else{

                holder.favouriteIcon.setImageResource(R.drawable.favorite_slected_icon);
                DatabaseReference myRef = database.getReference("Users/" + user.getUid() + "/favourites/" + myListData.getDocID());
                myListData.setIsFavourite(true);
                myRef.setValue("Added");

            }
        });
        holder.downloadIcon.setOnClickListener(v -> {
            Log.d("SMSD", "Test");
            try {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(myListData.getUri()));
                request.setTitle(myListData.getFileName());
                // in order for this if to run, you must use the android 3.2 to compile your app
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

//                    request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS, "ALSupermarket");
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Study Material/" + myListData.getFileName());
                // // get download service and enqueue file
                manager = (DownloadManager) context.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                Toast.makeText(context.getContext(), "Download Started", Toast.LENGTH_SHORT).show();

                long index = manager.enqueue(request);
                context.getContext().registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                    }
                }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                Toast.makeText(context.getContext(), "Download completed", Toast.LENGTH_SHORT).show();

//                    manager.openDownloadedFile(index);
//                    uri = manager.getUriForDownloadedFile(index);
//                    Log.d("downloading url = ", uri.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }


    @Override
    public int getItemCount() {

        return listData.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName;
        TextView branchName;
        TextView uploadedBy;
        TextView title;
        TextView fileName;
        ImageView favouriteIcon;
        ImageView downloadIcon;


        public ViewHolder(View itemView) {
            super(itemView);
            this.subjectName = itemView.findViewById(R.id.subjectName);
            this.branchName = itemView.findViewById(R.id.branchName);
            this.uploadedBy = itemView.findViewById(R.id.uploadedBy);
            this.title = itemView.findViewById(R.id.titleText);
            this.fileName = itemView.findViewById(R.id.fileName);


            this.favouriteIcon = itemView.findViewById(R.id.favoriteIcon);
            this.downloadIcon = itemView.findViewById(R.id.downloadIcon);
        }
    }


}