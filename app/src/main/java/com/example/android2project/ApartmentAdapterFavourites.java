package com.example.android2project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
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

import java.util.List;

public class ApartmentAdapterFavourites extends RecyclerView.Adapter<ApartmentAdapterFavourites.ApartmentViewHolder> {

    Context context;
    private List<Apartment> apartments;
    private final List<chatUser> chatUsers;
    private final UserListener userListener;
    RequestManager glide;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    int counter = 0, i;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    public ApartmentAdapterFavourites(Context context, List<Apartment> apartments, List<chatUser> chatUsers, UserListener userListener) {
        this.apartments = apartments;
        glide = Glide.with(context);
        this.userListener = userListener;
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

        TextView apartmentNameTv, sellerNameTv, publishDateTv, unFavTV, chatTv,hoursAgoTV;
        RoundedImageView profilePic;


        public ApartmentViewHolder(View itemView) {
            super(itemView);
            sellerNameTv = itemView.findViewById(R.id.seller_name);
            apartmentNameTv = itemView.findViewById(R.id.apartment_name_tv);
            publishDateTv = itemView.findViewById(R.id.publish_date_tv);
            unFavTV = itemView.findViewById(R.id.unfav_tv);
            chatTv = itemView.findViewById(R.id.chat_tv);
            hoursAgoTV = itemView.findViewById(R.id.hoursAgoTV);

            profilePic = itemView.findViewById(R.id.profile_pic);

            storage = FirebaseStorage.getInstance();
            storageReference=storage.getReference();

        }
    }

    @NonNull
    @Override
    public ApartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_yul_favourites, parent, false);
        ApartmentViewHolder apartmentViewHolder = new ApartmentViewHolder(view);
        return apartmentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ApartmentViewHolder holder, @SuppressLint("RecyclerView") int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Apartment apartment = apartments.get(position);
        holder.sellerNameTv.setText( capitalizeStr(apartment.getSellerName()) );
        holder.apartmentNameTv.setText(capitalizeStr(apartment.getCity()));
        holder.publishDateTv.setText(apartment.getDate());
        holder.hoursAgoTV.setText(apartment.getTime());

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

        holder.unFavTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                DeleteRealtimeDatabase(apartment);

                                Toast.makeText(view.getContext(), view.getContext().getString(R.string.removed) +" " + apartment.getAddress()+" " + view.getContext().getString(R.string.from_fav),Toast.LENGTH_SHORT).show();
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
                builder.setMessage(view.getContext().getString(R.string.are_you_sure_delete)+" " + apartment.getAddress()+"?").setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
            }
        });


        holder.chatTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


    @Override
    public int getItemCount() {
        return apartments.size();
    }

    public String capitalizeStr(String str)
    {
        return  (str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase());
    }

    private void DeleteRealtimeDatabase(Apartment apartment) {
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

}
