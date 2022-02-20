package com.example.android2project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ViewingApartmentActivity extends AppCompatActivity {

    TextView price, address, rooms, floor, squareMeter, enteringDate, totalBuildingFloors, parkings,city;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    ImageSlider imageSlider;
    AppCompatImageView back;
    CheckBox acCB,elevatorCB,storeroomCB,balconyCB,mamadCB,kosherKitchenCB,renovatedCB,furnishedCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewing_apartment);

        Apartment apartment = getIntent().getParcelableExtra("apartment");
        System.out.println("ram:"+apartment.getAddress());
        price = findViewById(R.id.viewing_apartment_price);
        address = findViewById(R.id.viewing_apartment_address);
        city = findViewById(R.id.viewing_apartment_city);
        rooms = findViewById(R.id.viewing_apartment_rooms_TV);
        floor = findViewById(R.id.viewing_apartment_floor_TV);
        squareMeter = findViewById(R.id.viewing_apartment_meter_TV);
        enteringDate = findViewById(R.id.viewing_apartment_entering_date_TV);
        totalBuildingFloors = findViewById(R.id.viewing_apartment_total_building_floors_TV);
        parkings = findViewById(R.id.viewing_apartment_parkingTV);
        acCB = findViewById(R.id.viewing_apartment_ac_CB);
        elevatorCB = findViewById(R.id.viewing_apartment_elevator_CB);
        storeroomCB = findViewById(R.id.viewing_apartment_storeroom_CB);
        balconyCB = findViewById(R.id.viewing_apartment_balcony_CB);
        mamadCB = findViewById(R.id.viewing_apartment_mamad_CB);
        kosherKitchenCB = findViewById(R.id.viewing_apartment_kk_CB);
        renovatedCB = findViewById(R.id.viewing_apartment_renovated_CB);
        furnishedCB = findViewById(R.id.viewing_apartment_furniture_CB);

        imageSlider = findViewById(R.id.viewing_apartment_pic);
        back = findViewById(R.id.imageBack);

        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        List<SlideModel> slideModels;
        slideModels = new ArrayList<>();
        for (int i = 0; i < apartment.getNumOfPictures(); i++) {
            StorageReference pictureRef = storageReference.child("House Pictures/" + apartment.getSellerEmail() + "/House " + apartment.getOfferCounter() + "/Picture " + i);
            pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    slideModels.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
                    imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                }
            });
        }




        price.setText(String.format("%,d", apartment.getPrice())+" ₪");
        address.setText(apartment.getAddress());
        city.setText(apartment.getCity());
        rooms.setText(apartment.getRooms()+"");
        floor.setText(apartment.getFloor()+"");
        squareMeter.setText(apartment.getSquareMeter()+"");
        enteringDate.setText(apartment.getEnteringDate());
        totalBuildingFloors.setText(apartment.getTotalFloors()+"");
        parkings.setText(apartment.getParkings()+"");

        if(apartment.isAirConditioner())
            acCB.setChecked(true);
        if(apartment.isElevator())
            elevatorCB.setChecked(true);
        if(apartment.isStoreroom())
            storeroomCB.setChecked(true);
        if(apartment.isBalcony())
            balconyCB.setChecked(true);
        if(apartment.isMamad())
            mamadCB.setChecked(true);
        if(apartment.isKosherKitchen())
            kosherKitchenCB.setChecked(true);
        if(apartment.isRenovated())
            renovatedCB.setChecked(true);
        if(apartment.isFurnished())
            furnishedCB.setChecked(true);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }
}