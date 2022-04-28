package com.study_material.HomeFeed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.study_material.R;
import com.study_material.upload.CourseModel;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>{
    private ArrayList<FeedItemModel> listData;

    // RecyclerView recyclerView;


    public FeedAdapter(ArrayList<FeedItemModel> listData) {
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.feed_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FeedItemModel myListData = listData.get(position);
        holder.courseName.setText(myListData.getCourse());
        holder.subjectName.setText(myListData.getSubject());


    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
       TextView subjectName;
       TextView courseName;


        public ViewHolder(View itemView) {
            super(itemView);
            this.subjectName = (TextView) itemView.findViewById(R.id.subjectName);
            this.courseName = (TextView) itemView.findViewById(R.id.courseName);
        }
    }
}