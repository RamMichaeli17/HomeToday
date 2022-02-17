package com.example.android2project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

public class ApartmentAdapter extends RecyclerView.Adapter<ApartmentAdapter.ApartmentViewHolder> {

    Context context;
    private List<Apartment> apartments;
    RequestManager glide;

    public ApartmentAdapter(Context context, List<Apartment> apartments) {
        this.apartments=apartments;
        glide= Glide.with(context);
    }
    interface ApartmentListener {
        void onApartmentClicked(int position, View view);
    }

    private ApartmentListener listener;

    public void setListener(ApartmentListener listener) {
        this.listener = listener;
    }
    public class ApartmentViewHolder extends RecyclerView.ViewHolder {

        TextView apartmentNameTv, sellerNameTv, publishDateTv, favTv, chatTv,priceTV,roomsTV;
        ImageView apartmentPic, profilePic;

        public ApartmentViewHolder(View itemView) {
            super(itemView);
            sellerNameTv =itemView.findViewById(R.id.seller_name);
            apartmentNameTv =itemView.findViewById(R.id.apartment_name_tv);
            publishDateTv =itemView.findViewById(R.id.publish_date_tv);
            favTv =itemView.findViewById(R.id.fav_tv);
            chatTv =itemView.findViewById(R.id.chat_tv);
            priceTV=itemView.findViewById(R.id.priceTV);
            roomsTV=itemView.findViewById(R.id.roomsTV);


            apartmentPic =itemView.findViewById(R.id.apartment_pic);
            profilePic =itemView.findViewById(R.id.profile_pic);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onApartmentClicked(getAdapterPosition(),view);
                }
            });

            // TODO: 02/17/22 go to chat 
            chatTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("HEEEEEEELLLLLLLLOOOOOOOOO RRRRRAAAAAMMMMMM");
                }
            });
        }
    }

    @NonNull
    @Override
    public ApartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_yul, parent, false);
        ApartmentViewHolder apartmentViewHolder = new ApartmentViewHolder(view);
        return apartmentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ApartmentViewHolder holder, int position) {
        Apartment apartment=apartments.get(position);
        holder.sellerNameTv.setText(apartment.getSellerName());
        holder.apartmentNameTv.setText(apartment.getApartmentName());
        holder.publishDateTv.setText(apartment.getDate());
        holder.priceTV.setText(String.format("%,d", apartment.getPrice()));


        holder.roomsTV.setText(Integer.toString(apartment.getRooms()));
       // holder.chatTv.setText(apartment.getName());

        glide.load(apartment.getProfilePic()).into(holder.profilePic);

        if(apartment.getPostPic()==0){
            holder.apartmentPic.setVisibility(View.GONE);
        }else{
            holder.apartmentPic.setVisibility(View.VISIBLE);
            glide.load(apartment.getPostPic()).into(holder.apartmentPic);
        }

    }

    @Override
    public int getItemCount() {
        return apartments.size();
    }
}
