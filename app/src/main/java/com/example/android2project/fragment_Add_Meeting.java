package com.example.android2project;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class fragment_Add_Meeting extends Fragment {

    TextInputEditText cityET,priceET, addressET, floorET, totalFloorsET, squareMeterET, dateET;
    RadioButton acRB,elevatorRB,storeroomRB,balconyRB,mamadRB,kosherKitchenRB,renovatedRB,furnishedRB;
    boolean ac = false,elevator = false,storeroom = false,balcony = false,mamad = false,kosherKitchen = false,renovated = false,furnished = false;
    Button submitMeeting,addImage;
    int counter=0;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    int price,rooms,floor,totalFloors,squareMeter,parkings;
    String city,username, address, date,itemRooms="",itemParking="";
    Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    ImageView goBackBtn;
    List<Uri> listImageUri;

    String[] roomItems =  {"1","2","3","4","5","6","7","8","9","10"};
    String[] parkingItems = {"0","1","2","3"};

    //Expandable cardViews
   CardView categoryCV, propertyAddressCV, propertyInfoCV, priceDateCV;
   LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout4;
   LinearLayout categoryTV, propertyAddressTV, propertyInfoTV, priceDateTV;

   //selecting category
   ImageView forRental, forSale;
   Boolean forRentalFlag = false;
   Boolean forSaleFlag = false;

    AutoCompleteTextView autoCompleteRoomsTextView;
    AutoCompleteTextView autoCompleteParkingTextView;
    ArrayAdapter<String> roomAdapterItems;
    ArrayAdapter<String> parkingAdapterItems;





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
        cityET = rootView.findViewById(R.id.auto_complete_text1);
        addressET = rootView.findViewById(R.id.auto_complete_text2);
        floorET = rootView.findViewById(R.id.auto_complete_text3);
        totalFloorsET = rootView.findViewById(R.id.auto_complete_text4);
        squareMeterET = rootView.findViewById(R.id.auto_complete_text);
        acRB = rootView.findViewById(R.id.viewing_apartment_ac_CB);
        elevatorRB = rootView.findViewById(R.id.viewing_apartment_elevator_CB);
        storeroomRB = rootView.findViewById(R.id.viewing_apartment_storeroom_CB);
        balconyRB = rootView.findViewById(R.id.viewing_apartment_balcony_CB);
        mamadRB = rootView.findViewById(R.id.viewing_apartment_mamad_CB);
        kosherKitchenRB = rootView.findViewById(R.id.viewing_apartment_kk_CB);
        renovatedRB = rootView.findViewById(R.id.viewing_apartment_renovated_CB);
        furnishedRB = rootView.findViewById(R.id.viewing_apartment_furniture_CB);
        priceET = rootView.findViewById(R.id.askingPriceET);
        dateET = rootView.findViewById(R.id.dateET);
        submitMeeting = rootView.findViewById(R.id.submitMeeting);
        addImage=rootView.findViewById(R.id.addImage);
        goBackBtn=rootView.findViewById(R.id.goBackBtn);

        autoCompleteParkingTextView = rootView.findViewById(R.id.auto_complete_parking_number);
        parkingAdapterItems = new ArrayAdapter<String>(rootView.getContext(),R.layout.room_number_list,parkingItems);
        autoCompleteParkingTextView.setAdapter(parkingAdapterItems);

        autoCompleteParkingTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemParking = adapterView.getItemAtPosition(i).toString();
            }
        });

        autoCompleteRoomsTextView = rootView.findViewById(R.id.auto_complete_room_number);
        roomAdapterItems = new ArrayAdapter<String>(rootView.getContext(),R.layout.room_number_list,roomItems);
        autoCompleteRoomsTextView.setAdapter(roomAdapterItems);

        autoCompleteRoomsTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemRooms = adapterView.getItemAtPosition(i).toString();
            }
        });





        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        ((loggedInActivity)getActivity()).disableTabLayout();

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
                if(!forRentalFlag) {
                    forRental.setImageResource(R.drawable.for_rent);
                    forRentalFlag=true;
                    forSaleFlag=false;
                    forSale.setImageResource(R.drawable.for_sale_blackwhite);
                }
            }
        });

        forSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!forSaleFlag) {
                    forSale.setImageResource(R.drawable.for_sale);
                    forSaleFlag = true;
                    forRentalFlag = false;
                    forRental.setImageResource(R.drawable.for_rent_blackwhite);
                }
            }
        });

        acRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ac){
                    ac = true;
                    acRB.setChecked(true);
                }
                else {
                    ac = false;
                    acRB.setChecked(false);
                }
            }
        });

        elevatorRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!elevator){
                    elevator = true;
                    elevatorRB.setChecked(true);
                }
                else {
                    elevator = false;
                    elevatorRB.setChecked(false);
                }
            }
        });

        storeroomRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!storeroom){
                    storeroom = true;
                    storeroomRB.setChecked(true);
                }
                else {
                    storeroom = false;
                    storeroomRB.setChecked(false);
                }
            }
        });
        balconyRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!balcony){
                    balcony = true;
                    balconyRB.setChecked(true);
                }
                else {
                    balcony = false;
                    balconyRB.setChecked(false);
                }
            }
        });
        mamadRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mamad){
                    mamad = true;
                    mamadRB.setChecked(true);
                }
                else {
                    mamad = false;
                    mamadRB.setChecked(false);
                }
            }
        });
        kosherKitchenRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!kosherKitchen){
                    kosherKitchen = true;
                    kosherKitchenRB.setChecked(true);
                }
                else {
                    kosherKitchen = false;
                    kosherKitchenRB.setChecked(false);
                }
            }
        });
        renovatedRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!renovated){
                    renovated = true;
                    renovatedRB.setChecked(true);
                }
                else {
                    renovated = false;
                    renovatedRB.setChecked(false);
                }
            }
        });
        furnishedRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!furnished){
                    furnished = true;
                    furnishedRB.setChecked(true);
                }
                else {
                    furnished = false;
                    furnishedRB.setChecked(false);
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


        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((loggedInActivity)getActivity()).enableTabLayout();
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
                if(
                        cityET.getText().toString().trim().isEmpty() ||
                        addressET.getText().toString().trim().isEmpty() ||
                        floorET.getText().toString().trim().isEmpty() ||
                        totalFloorsET.getText().toString().trim().isEmpty() ||
                        squareMeterET.getText().toString().trim().isEmpty() ||
                        priceET.getText().toString().trim().isEmpty() ||
                        dateET.getText().toString().trim().isEmpty() ||
                        itemRooms.isEmpty() ||
                        itemParking.isEmpty() ||
                        dateET.getText().toString().trim().isEmpty() ||
                        listImageUri.size()==0 ||
                        (forRentalFlag==false && forSaleFlag==false)
                ) {
                    Toast.makeText(getActivity(), getString(R.string.please_fill_all_values), Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    city = cityET.getText().toString().trim();
                    address = addressET.getText().toString().trim();
                    floor = Integer.parseInt(floorET.getText().toString());
                    totalFloors = Integer.parseInt(totalFloorsET.getText().toString());
                    squareMeter = Integer.parseInt(squareMeterET.getText().toString());
                    price = Integer.parseInt(priceET.getText().toString());
                    date = dateET.getText().toString().trim();
                    rooms = Integer.parseInt(itemRooms);
                    parkings = Integer.parseInt(itemParking);
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
                        Toast.makeText(getActivity(),getString(R.string.something_wrong),Toast.LENGTH_LONG).show();
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

        Apartment apartment = new Apartment(
                price,
                rooms,
                counter,
                listImageUri.size(),
                floor,
                totalFloors,
                squareMeter,
                parkings,
                username,
                address,
                new SimpleDateFormat("dd-MM-yyyy").format(new Date()),
                user.getEmail(),
                timeIn24Hours,
                user.getUid(),
                date,
                city,
                forRentalFlag,
                ac,
                elevator,
                storeroom,
                balcony,
                mamad,
                kosherKitchen,
                renovated,
                furnished);
        FirebaseDatabase.getInstance().getReference("House Offers")
                .child(userID).child(("Offer "+counter)).setValue(apartment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(),getString(R.string.single_offer)+" " +counter+" "+ getString(R.string.for1)+ " " +username+" "+ getString(R.string.has_been_created),Toast.LENGTH_LONG).show();

                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment_Add_Meeting.this).commit();


            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
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
                            Toast.makeText(getActivity(), getString(R.string.failed_to_upload), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }


}


