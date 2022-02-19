package com.example.android2project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.internal.Storage;
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

import java.util.UUID;

public class loggedInActivity extends AppCompatActivity {


    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Uri imageUri;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>0)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    Button logoutBtn,addMeetingBtn,pictureBtn,allMeetingsBtn;
    ImageView pictureIV;
    TextView nameTV,emailTV,ageTV,hiTV;
    int jobCounter=0;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);

        tabLayout.setupWithViewPager(viewPager);
        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new fragment1(),"MAIN");
        vpAdapter.addFragment(new fragment2(),"CHAT");
        vpAdapter.addFragment(new fragment3(),"SOMETHING");
        viewPager.setAdapter(vpAdapter);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.navigation_view);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Android Project");
        setSupportActionBar(toolbar);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);


        user=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        pictureBtn=findViewById(R.id.pictureBtn);
        pictureIV=findViewById(R.id.pictureIV);

        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getTitle().equals("Log out"))
                    logoutBtn.performClick();
                else if(item.getTitle().equals("My Profile"))
                {

                    startActivity(new Intent(loggedInActivity.this,MyProfile.class));
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction transaction = fragmentManager.beginTransaction();
//                    transaction.add(R.id.drawer_layout,new profileFragment(),"profile_fragment");
//                    transaction.addToBackStack(null);
//                    transaction.commit();
                }
                item.setChecked(true);
                drawerLayout.closeDrawers();
                Toast.makeText(loggedInActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });


/*        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });


        hiTV=findViewById(R.id.two);
        nameTV=findViewById(R.id.three);
        emailTV=findViewById(R.id.four);
        ageTV=findViewById(R.id.five);


        // Load user profile image

        StorageReference profileRef = storageReference.child("Profile pictures/"+user.getEmail());
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(pictureIV);

            }
        });
        profileRef.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pictureIV.setImageDrawable(getDrawable(R.drawable.ic_baseline_person_24));
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

                    jobCounter=userProfile.jobsCounter;

                    nameTV.setText(fullName);
                    emailTV.setText(email);
                    ageTV.setText(age);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(loggedInActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
            }
        });

        logoutBtn=findViewById(R.id.logoutBtn);
        addMeetingBtn=findViewById(R.id.addMeeting);
        addMeetingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(loggedInActivity.this,AddMeeting.class));
            }
        });

        allMeetingsBtn=findViewById(R.id.allMeetings);
        allMeetingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(loggedInActivity.this,AllMeetings.class));
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(loggedInActivity.this,"Successfully signed out",Toast.LENGTH_LONG).show();
                startActivity(new Intent(loggedInActivity.this,ShouldBeDeleted2.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                                            Picasso.get().load(uri).into(pictureIV);
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
        }*/
    }

}