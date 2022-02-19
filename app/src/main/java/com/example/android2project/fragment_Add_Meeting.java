package com.example.android2project;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class fragment_Add_Meeting extends Fragment {

    TextInputEditText cityET,priceET,roomsET;

    Button submitMeeting,addImage;
    int counter=0;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    int price,rooms;
    String city,username;
    Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    ImageView goBackBtn;
    List<Uri> listImageUri;


    //Expandable cardViews
   CardView categoryCV, propertyAddressCV, propertyInfoCV, priceDateCV;
   LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout4;
   LinearLayout categoryTV, propertyAddressTV, propertyInfoTV, priceDateTV;

   //selecting category
   ImageView forRental, forSale;
   Boolean forRentalFlag = false;
   Boolean forSaleFlag = false;

    //Yul room number spinner
    AutoCompleteTextView autoCompleteRoomsTextView;

    //Yul parking amount spinner
    AutoCompleteTextView autoCompleteParkingTextView;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listImageUri=new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.yul_1,container,false);
        listImageUri.clear();
        cityET = rootView.findViewById(R.id.cityET);
        priceET = rootView.findViewById(R.id.askingPriceET);
        roomsET=rootView.findViewById(R.id.roomsET);
        submitMeeting = rootView.findViewById(R.id.submitMeeting);
        addImage=rootView.findViewById(R.id.addImage);
        goBackBtn=rootView.findViewById(R.id.goBackBtn);

        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        //expandable cardViews
        linearLayout1 = rootView.findViewById(R.id.category_info);
        linearLayout2 = rootView.findViewById(R.id.property_address_info);
        linearLayout3 = rootView.findViewById(R.id.property_info_info);
        linearLayout4 = rootView.findViewById(R.id.priceDate_info);
        categoryCV = rootView.findViewById(R.id.adding_apartment_first_CV);
        propertyAddressCV = rootView.findViewById(R.id.adding_apartment_second_CV);
        propertyInfoCV = rootView.findViewById(R.id.adding_apartment_third_CV);
        priceDateCV = rootView.findViewById(R.id.adding_apartment_fourth_CV);
        categoryTV = rootView.findViewById(R.id.category_title);
        propertyAddressTV = rootView.findViewById(R.id.address_title);
        propertyInfoTV = rootView.findViewById(R.id.property_info_title);
        priceDateTV = rootView.findViewById(R.id.priceDate_title);

        //clickable category
        forRental = rootView.findViewById(R.id.for_rental);
        forSale = rootView.findViewById(R.id.for_sale);


        forRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!forRentalFlag && forSaleFlag){
                    forRental.setImageResource(R.drawable.forrent_blackwhite);
                    forRentalFlag=true;
                    forSale.setImageResource(R.drawable.for_sale);
                    forSaleFlag=false;
                } else {
                    forRental.setImageResource(R.drawable.for_rent);
                    forRentalFlag=false;
                    forSale.setImageResource(R.drawable.forsale_blackwhite);
                    forSaleFlag=true;
                }
            }
        });

        forSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!forSaleFlag){
                    forSale.setImageResource(R.drawable.forsale_blackwhite);
                    forSaleFlag=true;
                } else {
                    forSale.setImageResource(R.drawable.for_sale);
                    forSaleFlag=false;
                }
            }
        });


        //expandable cardViews
        categoryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayout1.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(categoryCV, new AutoTransition());
                    linearLayout1.setVisibility(View.VISIBLE);
                }else{
                    TransitionManager.beginDelayedTransition(categoryCV, new AutoTransition());
                    linearLayout1.setVisibility(View.GONE);
                }
            }
        });

        propertyAddressTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayout2.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(propertyAddressCV, new AutoTransition());
                    linearLayout2.setVisibility(View.VISIBLE);
                }else{
                    TransitionManager.beginDelayedTransition(propertyAddressCV, new AutoTransition());
                    linearLayout2.setVisibility(View.GONE);
                }
            }
        });

        propertyInfoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayout3.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(propertyInfoCV, new AutoTransition());
                    linearLayout3.setVisibility(View.VISIBLE);
                }else{
                    TransitionManager.beginDelayedTransition(propertyInfoCV, new AutoTransition());
                    linearLayout3.setVisibility(View.GONE);
                }
            }
        });

        priceDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayout4.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(priceDateCV, new AutoTransition());
                    linearLayout4.setVisibility(View.VISIBLE);
                }else{
                    TransitionManager.beginDelayedTransition(priceDateCV, new AutoTransition());
                    linearLayout4.setVisibility(View.GONE);
                }
            }
        });

//        //Yul room number spinner
//        String [] roomNumberSpinner = getResources().getStringArray(R.array.room_number);
//        ArrayAdapter roomsArrayAdapter = new ArrayAdapter(getActivity(), R.layout.adding_apartment_listing, roomNumberSpinner);
//        autoCompleteRoomsTextView.setAdapter(roomsArrayAdapter);
//
//        //Yul parking amount spinner
//        String [] parkingNumberSpinner = getResources().getStringArray(R.array.parking_number);
//        ArrayAdapter parkingArrayAdapter = new ArrayAdapter(getActivity(), R.layout.adding_apartment_listing, parkingNumberSpinner);
//        autoCompleteParkingTextView.setAdapter(parkingArrayAdapter);

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment_Add_Meeting.this).commit();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listImageUri.clear();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setType("image/*");

                startActivityForResult(intent, 1);
            }
        });

        submitMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                city = cityET.getText().toString().trim();
                try {
                    price = Integer.parseInt(priceET.getText().toString());
                    rooms = Integer.parseInt(roomsET.getText().toString());
                }
                catch (Exception E)
                {
                    Toast.makeText(getActivity(), "Please fill all values", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(city.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please fill all values", Toast.LENGTH_SHORT).show();
                    return;
                }


                reference= FirebaseDatabase.getInstance().getReference("Users");
                user= FirebaseAuth.getInstance().getCurrentUser();
                userID = user.getUid();



                reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userProfile = snapshot.getValue(User.class);

                        if(userProfile!= null)
                        {
                            counter = userProfile.jobsCounter+1;
                            reference.child(userID +"/jobsCounter").setValue(counter);
                            username=userProfile.fullName;
                        }
                        uploadPicture();
                        addMeeting();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_LONG).show();
                    }
                });


            }
        });

        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void addMeeting()
    {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("kk:mm");
        String timeIn24Hours = formatter.format(currentDate);

        Apartment apartment = new Apartment(username,city,new SimpleDateFormat("dd-MM-yyyy").format(new Date()),price,rooms,user.getEmail(),counter,listImageUri.size(),timeIn24Hours,user.getUid());

        FirebaseDatabase.getInstance().getReference("House Offers")
                .child(userID).child(("Offer "+counter)).setValue(apartment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(),"Offer #"+counter+" for "+username+" has been created!",Toast.LENGTH_LONG).show();

                submitMeeting.setVisibility(View.GONE);
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment_Add_Meeting.this).commit();


            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            submitMeeting.setVisibility(View.VISIBLE);
            ClipData clipData = data.getClipData();
            if (clipData!= null)
            {
                for (int i=0;i<clipData.getItemCount();i++)
                {
                    listImageUri.add(clipData.getItemAt(i).getUri());
                }
            }
            else {
                listImageUri.add(data.getData());
            }
        }

    }

    private void uploadPicture() {
        for (int i = 0; i < listImageUri.size(); i++) {
            StorageReference fileRef = storageReference.child("House Pictures/" + user.getEmail() + "/House " + counter+"/Picture "+i);
            fileRef.putFile(listImageUri.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getActivity(), "Failed To Upload image", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    public void expand_first_CV(View view) {
        if(linearLayout1.getVisibility() == View.GONE){
            TransitionManager.beginDelayedTransition(categoryCV, new AutoTransition());
            linearLayout1.setVisibility(View.VISIBLE);
        }else{
            TransitionManager.beginDelayedTransition(categoryCV, new AutoTransition());
            linearLayout1.setVisibility(View.GONE);
        }
    }

}


