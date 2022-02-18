package com.example.android2project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.android2project.activities.ChatActivity;
import com.example.android2project.activities.SignInActivity;
import com.example.android2project.listeners.UserListener;
import com.example.android2project.models.chatUser;
import com.example.android2project.utilities.Constants;
import com.example.android2project.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.List;

public class ApartmentAdapter extends RecyclerView.Adapter<ApartmentAdapter.ApartmentViewHolder> {

    Context context;
    private List<Apartment> apartments;
    private final List<chatUser> chatUsers;
    private final UserListener userListener;
    RequestManager glide;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    int counter=0;

    public ApartmentAdapter(Context context, List<Apartment> apartments, List<chatUser> chatUsers,UserListener userListener) {
        this.apartments=apartments;
        glide= Glide.with(context);
        this.userListener=userListener;
        this.chatUsers = chatUsers;
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

//            // TODO: 02/17/22 go to chat
//            chatTv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
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
    public void onBindViewHolder(@NonNull ApartmentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Apartment apartment=apartments.get(position);
        holder.sellerNameTv.setText(apartment.getSellerName());
        holder.apartmentNameTv.setText(apartment.getApartmentName());
        holder.publishDateTv.setText(apartment.getDate());
        holder.priceTV.setText(String.format("%,d", apartment.getPrice()));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        holder.chatTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(apartment.getSellerEmail().equals(user.getEmail()))
                {
                    Toast.makeText(view.getContext(), "This is your offer! ", Toast.LENGTH_SHORT).show();
                    return;
                }

                    counter=0;
                    db = FirebaseFirestore.getInstance();
                    db.collection("users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if(document.get("email").equals(user.getEmail()))
                                                counter--;
                                            if(document.get("email").equals(apartment.getSellerEmail()) ) {
                                                userListener.onUserClicked(chatUsers.get(counter));
                                                break;
                                            }
                                            else
                                                counter++;
                                        }
                                    }
                                }
                            });

            }
        });

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
