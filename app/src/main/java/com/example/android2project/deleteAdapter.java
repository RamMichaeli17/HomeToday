package com.example.android2project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class deleteAdapter extends RecyclerView.Adapter<deleteAdapter.MyViewHolder>{

    Context context;
    ArrayList<Meeting> list;

    public deleteAdapter(Context context, ArrayList<Meeting> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.deletethis_gal_meeting,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Meeting meeting = list.get(position);
        holder.date.setText(meeting.getDate());
        holder.game.setText(meeting.getGame());
        holder.image.setImageResource(R.drawable.ic_baseline_person_24);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView date,game;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.dateTV);
            game = itemView.findViewById(R.id.gameTV);
            image = itemView.findViewById(R.id.imageIV);
        }
    }
}
