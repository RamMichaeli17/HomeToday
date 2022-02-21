package com.example.android2project;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class MyProfile extends Fragment {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Uri imageUri;
    ImageView backBtn;
    TextView nameTV,emailTV,ageTV, howManyOffers,locationTV;
    DrawerLayout drawerLayout;
    Boolean isLoggedIn;
    RoundedImageView profileImage;

    ItemViewModel viewModel;



    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_my_profile,container,false);

        profileImage=rootView.findViewById(R.id.profilePictureIV);
        nameTV=rootView.findViewById(R.id.profileFullNameTV);
        emailTV=rootView.findViewById(R.id.profileEmailTV);
        ageTV=rootView.findViewById(R.id.profileAgeTV);
        backBtn=rootView.findViewById(R.id.profileBackBtn);
        howManyOffers =rootView.findViewById(R.id.howManyFav);
        locationTV=rootView.findViewById(R.id.locationTV);

        viewModel=new ViewModelProvider(requireActivity()).get(ItemViewModel.class);


        viewModel.getSelectedItem().observe(getViewLifecycleOwner(),item->{
            locationTV.setText(item.get(0)+" "+item.get(1)+" "+item.get(3));
        });



        ((loggedInActivity)getActivity()).disableTabLayout();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((loggedInActivity)getActivity()).enableTabLayout();
                getActivity().getSupportFragmentManager().beginTransaction().remove(MyProfile.this).commit();

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isLoggedIn) {
                    Toast.makeText(getActivity(), R.string.log_in_to_assign_profile_picture, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        // Load user profile image

        if(isLoggedIn) {
            StorageReference profileRef = storageReference.child("Profile pictures/" + user.getEmail());
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profileImage);

                }
            });
            profileRef.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    profileImage.setImageResource(R.drawable.ic_baseline_orange_person_24);
                }
            });
        }
        else
        {
            profileImage.setImageResource(R.drawable.ic_baseline_orange_person_24);
        }



        // Load user data (email,age,name)
        if(isLoggedIn) {
            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if (userProfile != null) {
                        String fullName = userProfile.fullName;
                        String email = userProfile.email;
                        String age = userProfile.age;

                        int howMany=userProfile.jobsCounter;

                        nameTV.setText(fullName);
                        emailTV.setText(email);
                        ageTV.setText(age);
                        howManyOffers.setText(Integer.toString(howMany));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), R.string.something_wrong, Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
              nameTV.setText(R.string.none);
             emailTV.setText(R.string.none);
            ageTV.setText(R.string.none);
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();


        if (user == null)
            isLoggedIn = false;
        else {
            isLoggedIn = true;
            userID = user.getUid();

            reference = FirebaseDatabase.getInstance().getReference("Users");
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
        }


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1 && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadPicture();
        }
    }

    private void uploadPicture()
    {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle(R.string.uploading_image);
        pd.show();

        StorageReference fileRef = storageReference.child("Profile pictures/"+user.getEmail());

        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.uploading_image, Snackbar.LENGTH_LONG).show();

                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), R.string.failed_to_upload, Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                pd.setMessage(getString(R.string.just_a_moment));
            }
        });
    }
}