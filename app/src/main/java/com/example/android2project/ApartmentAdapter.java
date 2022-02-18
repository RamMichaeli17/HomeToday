package com.example.android2project;

import android.annotation.SuppressLint;
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
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.android2project.listeners.UserListener;
import com.example.android2project.models.chatUser;
import com.example.android2project.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
        ImageView profilePic;
        ImageSlider imageSlider;

        public ApartmentViewHolder(View itemView) {
            super(itemView);
            sellerNameTv =itemView.findViewById(R.id.seller_name);
            apartmentNameTv =itemView.findViewById(R.id.apartment_name_tv);
            publishDateTv =itemView.findViewById(R.id.publish_date_tv);
            favTv =itemView.findViewById(R.id.fav_tv);
            chatTv =itemView.findViewById(R.id.chat_tv);
            priceTV=itemView.findViewById(R.id.priceTV);
            roomsTV=itemView.findViewById(R.id.roomsTV);

            imageSlider=itemView.findViewById(R.id.apartment_pic_slider);

            profilePic =itemView.findViewById(R.id.profile_pic);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onApartmentClicked(getAdapterPosition(),view);
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
    public void onBindViewHolder(@NonNull ApartmentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Apartment apartment=apartments.get(position);
        holder.sellerNameTv.setText(apartment.getSellerName());
        holder.apartmentNameTv.setText(apartment.getApartmentName());
        holder.publishDateTv.setText(apartment.getDate());
        holder.priceTV.setText(String.format("%,d", apartment.getPrice()));
        holder.roomsTV.setText(Integer.toString(apartment.getRooms()));

        List<SlideModel> slideModels=new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.apartment1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.apartment2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.apartment3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.apartment4, ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/fir-gry-3f7dd.appspot.com/o/Profile%20pictures%2Fdavid%40gmail.com?alt=media&token=01bd1a53-f56a-4e18-bc98-9d7ca1d7cefe", ScaleTypes.FIT));;

        holder.imageSlider.setImageList(slideModels,ScaleTypes.FIT);



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



        glide.load(apartment.getProfilePic()).into(holder.profilePic);


    }

    @Override
    public int getItemCount() {
        return apartments.size();
    }
}
