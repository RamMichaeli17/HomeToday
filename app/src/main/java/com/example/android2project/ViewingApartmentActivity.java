package com.example.android2project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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

    TextView price, address, rooms, floor, squareMeter, enteringDate, totalBuildingFloors, parkings;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    ImageSlider imageSlider;
    AppCompatImageView back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewing_apartment);

        Apartment apartment = getIntent().getParcelableExtra("apartment");
        price = findViewById(R.id.viewing_apartment_price);
        address = findViewById(R.id.viewing_apartment_address);
        rooms = findViewById(R.id.viewing_apartment_rooms_TV);
        floor = findViewById(R.id.viewing_apartment_floor_TV);
        squareMeter = findViewById(R.id.viewing_apartment_meter_TV);
        enteringDate = findViewById(R.id.viewing_apartment_entering_date_TV);
        totalBuildingFloors = findViewById(R.id.viewing_apartment_total_building_floors_TV);
        parkings = findViewById(R.id.viewing_apartment_parkingTV);
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
//                    slideModels.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
//                    if(i==apartment.getNumOfPictures()-1) {
//                        holder.imageSlider.setImageList(slideModels, ScaleTypes.FIT);
//                        slideModels.clear();
//                    }
                }
            });
        }




        price.setText(apartment.getPrice()+" ₪");
        address.setText(apartment.getAddress());
        rooms.setText(apartment.getRooms()+"");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}