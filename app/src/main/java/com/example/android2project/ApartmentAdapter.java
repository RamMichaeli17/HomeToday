package com.example.android2project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.example.android2project.activities.SignInActivity;
import com.example.android2project.listeners.UserListener;
import com.example.android2project.models.chatUser;
import com.example.android2project.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class ApartmentAdapter extends RecyclerView.Adapter<ApartmentAdapter.ApartmentViewHolder> implements Filterable {

    Context context;
    private ArrayList<Apartment> apartments;
    private ArrayList<Apartment> apartmentsFull;
    private final List<chatUser> chatUsers;
    private final UserListener userListener;
    RequestManager glide;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    int counter = 0, i;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    boolean isLoggedIn;


    public ApartmentAdapter(Context context, ArrayList<Apartment> apartments, List<chatUser> chatUsers, UserListener userListener) {
        glide = Glide.with(context);
        this.userListener = userListener;
        this.chatUsers = chatUsers;
        this.context=context;
        this.apartmentsFull = apartments;
        this.apartments = new ArrayList<>(apartmentsFull);

    }

    @Override
    public Filter getFilter() {
        return newsFilter;
    }

    private final Filter newsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Apartment> filteredApartments = new ArrayList<>();

            if(constraint == null || constraint.length() ==0)
                filteredApartments.addAll(apartmentsFull);
            else if(isNumeric((String)constraint)){
                int budget = Integer.parseInt((String)constraint);
                for( Apartment apar : apartmentsFull){
                    if (apar.getPrice() <= budget )
                        filteredApartments.add(apar);
                }
            }

            else
            {
                String FilterPattern = constraint.toString().toLowerCase().trim();

                for( Apartment apar : apartmentsFull){
                    if (apar.getCity().toLowerCase().contains(FilterPattern))
                        filteredApartments.add(apar);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredApartments;
            results.count = filteredApartments.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            apartments.clear();
            apartments.addAll((ArrayList)filterResults.values);
            notifyDataSetChanged();

        }
    };

    interface ApartmentListener {
        void onApartmentClicked(int position, View view);
    }

    private ApartmentListener listener;

    public void setListener(ApartmentListener listener) {
        this.listener = listener;
    }

    public class ApartmentViewHolder extends RecyclerView.ViewHolder {

        TextView apartmentNameTv, sellerNameTv, publishDateTv, favTv, chatTv, priceTV, roomsTV,hoursAgoTV,forRent;
        RoundedImageView profilePic;
        ImageSlider imageSlider;
        ImageView removeOffer,fav_clicked;

        public ApartmentViewHolder(View itemView) {
            super(itemView);
            sellerNameTv = itemView.findViewById(R.id.seller_name);
            apartmentNameTv = itemView.findViewById(R.id.apartment_name_tv);
            publishDateTv = itemView.findViewById(R.id.publish_date_tv);
            favTv = itemView.findViewById(R.id.fav_tv);
            chatTv = itemView.findViewById(R.id.chat_tv);
            priceTV = itemView.findViewById(R.id.priceTV);
            roomsTV = itemView.findViewById(R.id.roomsTV);
            hoursAgoTV = itemView.findViewById(R.id.hoursAgoTV);
            removeOffer=itemView.findViewById(R.id.removeOfferBtn);
            fav_clicked=itemView.findViewById(R.id.fav_bt_clicked);
            forRent=itemView.findViewById(R.id.for_rental);

            imageSlider = itemView.findViewById(R.id.apartment_pic_slider);

            profilePic = itemView.findViewById(R.id.profile_pic);

            storage = FirebaseStorage.getInstance();
            storageReference=storage.getReference();
        }
    }

    @NonNull
    @Override
    public ApartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_yul, parent, false);
        ApartmentViewHolder apartmentViewHolder = new ApartmentViewHolder(view);

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            isLoggedIn = false;
        else {
            isLoggedIn = true;
        }

        return apartmentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ApartmentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        Apartment apartment = apartments.get(position);

        holder.sellerNameTv.setText( capitalizeStr(apartment.getSellerName()) );
        holder.apartmentNameTv.setText(capitalizeStr(apartment.getCity()));
        holder.publishDateTv.setText(apartment.getDate());
        holder.priceTV.setText(String.format("%,d", apartment.getPrice()));
        holder.roomsTV.setText(Integer.toString(apartment.getRooms()));
        holder.hoursAgoTV.setText(apartment.getTime());
        if(apartment.isForRental())
            holder.forRent.setText("For rental");
        else
            holder.forRent.setText("For sale");

        if (isLoggedIn && apartment.getSellerEmail().equals(user.getEmail()))
        {
            holder.removeOffer.setVisibility(View.VISIBLE);
        }


        StorageReference pictureRef = storageReference.child("Profile pictures/" + apartment.getSellerEmail());
        pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                  glide.load(uri).into(holder.profilePic);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                holder.profilePic.setImageResource(R.drawable.ic_baseline_person_24);
            }
        });

        loadImageSlider(apartment, holder.imageSlider);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),ViewingApartmentActivity.class);
                intent.putExtra("apartment",apartment);
                view.getContext().startActivity(intent);
            }
        });
        holder.favTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!isLoggedIn)
                {
                    notLoggedInDialog();
                    return;
                }

                if (apartment.getUndo()==0) {
                    holder.fav_clicked.setVisibility(View.VISIBLE);
                    holder.favTv.setText(R.string.undo);
                    apartment.setUndo(1);
                    FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("userFavourites").child(apartment.getSellerName() + " offer " + apartment.getOfferCounter()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(view.getContext(), apartment.getCity() + R.string.is_already_in_fav, Toast.LENGTH_SHORT).show();

                            }
                            else {
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(user.getUid()).child("userFavourites").child(apartment.getSellerName() + " offer " + apartment.getOfferCounter()).setValue(apartment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(view.getContext(), R.string.added + apartment.getCity() + R.string.to_favourites, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else
                {
                    apartment.setUndo(0);
                    holder.fav_clicked.setVisibility(View.GONE);
                    holder.favTv.setText(R.string.favourite);
                    undoFav(apartment);
                    Toast.makeText(view.getContext(), R.string.removed_from_fav, Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.removeOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                DeleteRealtimeDatabase(apartment);
                                DeletePictures(apartment);

                                apartments.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(holder.getAdapterPosition(), apartments.size());
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage(R.string.are_you_sure_delete+ apartment.getCity()+"?").setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
            }
        });


        holder.chatTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isLoggedIn)
                {
                    notLoggedInDialog();
                    return;
                }

                if (apartment.getSellerEmail().equals(user.getEmail())) {
                    Toast.makeText(view.getContext(), R.string.this_is_our_offer, Toast.LENGTH_SHORT).show();
                    return;
                }

                counter = 0;
                db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.get("email").equals(user.getEmail()))
                                            counter--;
                                        if (document.get("email").equals(apartment.getSellerEmail())) {
                                            userListener.onUserClicked(chatUsers.get(counter));
                                            break;
                                        } else
                                            counter++;
                                    }
                                }
                            }
                        });

            }
        });


    }

    private void DeleteRealtimeDatabase(Apartment apartment) {
        System.out.println("getSellerUID = "+apartment.getSellerUID()+" Offer "+apartment.getOfferCounter());
        DatabaseReference myReference = FirebaseDatabase.getInstance().getReference("House Offers");
        myReference.child(apartment.getSellerUID()).child("Offer "+apartment.getOfferCounter()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@android.support.annotation.NonNull DatabaseError error) {
                Toast.makeText(context.getApplicationContext(), R.string.something_wrong,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadImageSlider(Apartment apartment, ImageSlider imageSlider) {
        List<SlideModel> slideModels;
        slideModels = new ArrayList<>();
        for (i = 0; i < apartment.getNumOfPictures(); i++) {
            StorageReference pictureRef = storageReference.child("House Pictures/" + apartment.getSellerEmail() + "/House " + apartment.getOfferCounter() + "/Picture " + i);
            pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    slideModels.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
                    imageSlider.setImageList(slideModels, ScaleTypes.FIT);
//                    slideModels.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
//                    if(i==apartment.getNumOfPictures()-1) {
//                        holder.imageSlider.setImageList(slideModels, ScaleTypes.FIT);
//                        slideModels.clear();
//                    }
                }
            });
        }
    }

    private void DeletePictures(Apartment apartment) {
        List<SlideModel> slideModels;
        slideModels = new ArrayList<>();
        for (i = 0; i < apartment.getNumOfPictures(); i++) {
            StorageReference pictureRef = storageReference.child("House Pictures/" + apartment.getSellerEmail() + "/House " + apartment.getOfferCounter() + "/Picture " + i);
            pictureRef.delete();

        }
    }





    @Override
    public int getItemCount() {
        return apartments.size();
    }

    public String capitalizeStr(String str)
    {
        return  (str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase());
    }

    public void undoFav(Apartment apartment)
    {
        DatabaseReference myReference = FirebaseDatabase.getInstance().getReference("Users");
        myReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userFavourites").child(apartment.getSellerName()+" offer "+apartment.getOfferCounter()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();

            }

            @Override
            public void onCancelled(@android.support.annotation.NonNull DatabaseError error) { }
        });
    }

    public void notLoggedInDialog()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        context.startActivity(new Intent(context.getApplicationContext(), SignInActivity.class));
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.you_are_not_logged_in).setPositiveButton(R.string.login, dialogClickListener)
                .setNegativeButton(R.string.back, dialogClickListener).show();
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
