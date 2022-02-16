package com.example.android2project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
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
import com.squareup.picasso.Picasso;

public class MyProfile extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Uri imageUri;
    ImageView profileImage;
    TextView nameTV,emailTV,ageTV;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        user= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        profileImage=findViewById(R.id.profilePictureIV);

        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.navigation_view);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Android Project");
        setSupportActionBar(toolbar);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        navigationView.getMenu().getItem(1).setChecked(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@androidx.annotation.NonNull MenuItem item) {
                if (item.getTitle().equals("Log out")) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(MyProfile.this, "Successfully signed out", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MyProfile.this, MainActivity.class));
                }
                else if(item.getTitle().equals("Main"))
                {

                    startActivity(new Intent(MyProfile.this,loggedInActivity.class));
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction transaction = fragmentManager.beginTransaction();
//                    transaction.add(R.id.drawer_layout,new profileFragment(),"profile_fragment");
//                    transaction.addToBackStack(null);
//                    transaction.commit();
                }
                else if(item.getTitle().equals("Another Example"))
                    Toast.makeText(MyProfile.this, item+"", Toast.LENGTH_LONG).show();

                drawerLayout.closeDrawers();
                return true;
            }
        });



        nameTV=findViewById(R.id.profileFullNameTV);
        emailTV=findViewById(R.id.profileEmailTV);
        ageTV=findViewById(R.id.profileAgeTV);


        // Load user profile image

        StorageReference profileRef = storageReference.child("Profile pictures/"+user.getEmail());
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);

            }
        });
        profileRef.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profileImage.setImageDrawable(getDrawable(R.drawable.ic_baseline_person_24));
            }
        });



        // Load user data (email,age,name)

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile!= null)
                {
                    String fullName = userProfile.fullName;
                    String email = userProfile.email;
                    String age = userProfile.age;

                  //  jobCounter=userProfile.jobsCounter;

                    nameTV.setText(fullName);
                    emailTV.setText(email);
                    ageTV.setText(age);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyProfile.this,"Something went wrong",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadPicture();
        }
    }

    private void uploadPicture()
    {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("uploading Image...");
        pd.show();

        StorageReference fileRef = storageReference.child("Profile pictures/"+user.getEmail());

        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();

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
                        Toast.makeText(getApplicationContext(), "Failed To Upload", Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                pd.setMessage("Just a moment...");
            }
        });
    }
}