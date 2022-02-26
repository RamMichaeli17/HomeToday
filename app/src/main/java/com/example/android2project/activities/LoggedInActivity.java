package com.example.android2project.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.android2project.fragments.MyProfileFragment;
import com.example.android2project.R;
import com.example.android2project.adapters.VPAdapter;
import com.example.android2project.BudgetDialog;
import com.example.android2project.fragments.HomePageFragment;
import com.example.android2project.fragments.ChatsFragment;
import com.example.android2project.fragments.FavoritesFragment;
import com.example.android2project.models.ItemViewModel;
import com.example.android2project.utilities.Constants;
import com.example.android2project.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoggedInActivity extends AppCompatActivity implements BudgetDialog.budgetDialogListener, LocationListener {


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
    Geocoder geocoder;
    private PreferenceManager preferenceManager;
    Handler handler = new Handler();
    final int LOCATION_PERMISSON_REQUEST = 1;
    LocationManager manager;
    ArrayList<String> theLocation;

    private ItemViewModel viewModel;


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.are_you_sure_exit)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DocumentReference documentReferenceOut;
                        PreferenceManager preferenceManagerOut = new PreferenceManager(getApplicationContext());
                        FirebaseFirestore databaseOut = FirebaseFirestore.getInstance();
                        documentReferenceOut = databaseOut.collection(Constants.KEY_COLLECTION_USERS)
                                .document(preferenceManagerOut.getString(Constants.KEY_USER_ID));
                        documentReferenceOut.update(Constants.KEY_AVAILABILITY,0).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                int pid = android.os.Process.myPid();
                                android.os.Process.killProcess(pid);
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            isLoggedIn = false;
        else {
            isLoggedIn = true;
            userID = user.getUid();
            reference = FirebaseDatabase.getInstance().getReference("Users");
            getToken();
        }



        // Profile Location

/*        if (Build.VERSION.SDK_INT >= 23) {
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSON_REQUEST);
            }
        }*/

        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);


        theLocation = new ArrayList<>();
        geocoder = new Geocoder(this);

        preferenceManager = new PreferenceManager(getApplicationContext());


        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.setupWithViewPager(viewPager);
        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new HomePageFragment(), getString(R.string.main));
        if (isLoggedIn) {
            vpAdapter.addFragment(new ChatsFragment(), getString(R.string.chat));
            vpAdapter.addFragment(new FavoritesFragment(), getString(R.string.favourites));
        }
        viewPager.setAdapter(vpAdapter);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);


        Toolbar toolbar = findViewById(R.id.toolbar);
        if (isLoggedIn)
            toolbar.setTitle("Home Today");
        else
            toolbar.setTitle(getString(R.string.guest));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        navigationView.getMenu().clear(); //clear old inflated items.
        if (isLoggedIn)
            navigationView.inflateMenu(R.menu.drawer_menu_logout); //inflate new items.
        else
            navigationView.inflateMenu(R.menu.drawer_menu_login); //inflate new items.


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getTitle().equals(getString((R.string.login)))) {
                    startActivity(new Intent(LoggedInActivity.this, SignInActivity.class));
                }
                if (item.getTitle().equals(getString(R.string.log_out))) {
                    firebaseSignOut();
                } else if (item.getTitle().equals(getString(R.string.my_profile))) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.testFrameLayout, new MyProfileFragment());
                    fragmentTransaction.commit();

                } else if (item.getTitle().equals(getString(R.string.main))) {
                    startActivity(new Intent(LoggedInActivity.this, LoggedInActivity.class));

                }
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });


        Criteria criteria = new Criteria();
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void navigationNew() {
        navigationView.getMenu().getItem(0).setChecked(true);
    }


    private void firebaseSignOut() {
        showToast(getString(R.string.sign_out));
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
                    Toast.makeText(LoggedInActivity.this, R.string.successfully_signed_out, Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(LoggedInActivity.this, SignInActivity.class));
                })
                .addOnFailureListener(e -> showToast(getString(R.string.unable_to_sign_out)));
    }


    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
//                .addOnSuccessListener(unused -> showToast("Token updated successfully"))
                .addOnFailureListener(e -> showToast(getString(R.string.update_token_failed)));
    }

    public void disableTabLayout() {
        for (View v : tabLayout.getTouchables()) {
            touchablesToRestore.add(v);
            v.setClickable(false);
        }
    }

    public void enableTabLayout() {
        for (View v : touchablesToRestore) {
            v.setClickable(true);
        }
        touchablesToRestore.clear();
    }


    @Override
    public void applyBudget(int theBudget) {
        HomePageFragment frag = HomePageFragment.GetInstance();
        frag.applyBudget(theBudget);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSON_REQUEST) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoggedInActivity.this);
                builder.setTitle("Attention").setMessage("The application must have location permission in order for it to work!")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        }).setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
            } else {
                manager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
//                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, LoggedInActivity.this);
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        final double lat = location.getLatitude();
        final double lng = location.getLongitude();



        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 2);
                    Address bestAddress = addresses.get(0);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            StringBuffer buffer = new StringBuffer(bestAddress.getSubThoroughfare());
                            buffer.reverse();
                            theLocation.clear();
                            theLocation.add(bestAddress.getCountryName());
                            theLocation.add(bestAddress.getThoroughfare());
                            theLocation.add((buffer.toString()));
                            theLocation.add(bestAddress.getLocality());

                            viewModel.setData(theLocation);


                        }
                    });


                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }




}