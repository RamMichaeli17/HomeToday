package com.example.android2project;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.android2project.activities.SignInActivity;
import com.example.android2project.utilities.Constants;
import com.example.android2project.utilities.PreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class loggedInActivity extends AppCompatActivity implements budget_dialog.budgetDialogListener {


    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Uri imageUri;
    DrawerLayout drawerLayout;
    public NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<View> touchablesToRestore = new ArrayList<View>();
    boolean isLoggedIn;
    FragmentTransaction fragmentTransaction;

    private PreferenceManager preferenceManager;

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.are_you_sure_exit)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        int pid = android.os.Process.myPid();
                        android.os.Process.killProcess(pid);
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();

    }

    Button logoutBtn,addMeetingBtn,pictureBtn,allMeetingsBtn;
    ImageView pictureIV;
    TextView nameTV,emailTV,ageTV,hiTV;
    int jobCounter=0;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            try {
                drawerLayout.openDrawer(Gravity.LEFT);
            } catch (Exception e) {
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        user=FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            isLoggedIn = false;
        else {
            isLoggedIn = true;
            userID = user.getUid();
            reference= FirebaseDatabase.getInstance().getReference("Users");
            getToken();
        }


        preferenceManager = new PreferenceManager(getApplicationContext());


        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);

        tabLayout.setupWithViewPager(viewPager);
        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new fragment1_homePage(),"Main");
        if(isLoggedIn) {
            vpAdapter.addFragment(new fragment2_chat(), "Chat");
            vpAdapter.addFragment(new fragment3(), "Favourites");
        }
        viewPager.setAdapter(vpAdapter);



        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.navigation_view);



        Toolbar toolbar = findViewById(R.id.toolbar);
        if (isLoggedIn)
            toolbar.setTitle("Home Today");
        else
            toolbar.setTitle(getString(R.string.guest));
        setSupportActionBar(toolbar);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);



        pictureBtn=findViewById(R.id.pictureBtn);
        pictureIV=findViewById(R.id.pictureIV);

        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        navigationView.getMenu().clear(); //clear old inflated items.
        if(isLoggedIn)
            navigationView.inflateMenu(R.menu.drawer_menu_logout); //inflate new items.
        else
            navigationView.inflateMenu(R.menu.drawer_menu_login); //inflate new items.

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getTitle().equals(R.string.login)) {
                    startActivity(new Intent(loggedInActivity.this, SignInActivity.class));
                }
                if (item.getTitle().equals(R.string.log_out)) {
                    firebaseSignOut();
                }
                else if(item.getTitle().equals(R.string.my_profile))
                {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.testFrameLayout,new MyProfile());
                    fragmentTransaction.commit();

                }
                else if(item.getTitle().equals(R.string.main))
                {
                    startActivity(new Intent(loggedInActivity.this,loggedInActivity.class));

                }
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

    }
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void galNavigation() {
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void firebaseSignOut() {
        showToast("Signing out...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    Toast.makeText(loggedInActivity.this,R.string.successfully_signed_out,Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(loggedInActivity.this, SignInActivity.class));
                })
                .addOnFailureListener(e -> showToast(getString(R.string.unable_to_sign_out)));
    }


    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken (String token) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN,token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
//                .addOnSuccessListener(unused -> showToast("Token updated successfully"))
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    public void disableTabLayout()
    {
        for(View v: tabLayout.getTouchables()){
            touchablesToRestore.add(v);
            v.setClickable(false);
        }
    }

    public void enableTabLayout()
    {
        for(View v: touchablesToRestore){
            v.setClickable(true);
        }
        touchablesToRestore.clear();
    }


    @Override
    public void applyBudget(int theBudget) {
        fragment1_homePage frag = fragment1_homePage.GetInstance();
        frag.applyBudget(theBudget);
    }
}