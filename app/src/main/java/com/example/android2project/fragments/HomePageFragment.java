package com.example.android2project.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.R;
import com.example.android2project.activities.ChatActivity;
import com.example.android2project.activities.SignInActivity;
import com.example.android2project.adapters.ApartmentAdapter;
import com.example.android2project.BudgetDialog;
import com.example.android2project.listeners.UserListener;
import com.example.android2project.models.Apartment;
import com.example.android2project.models.chatUser;
import com.example.android2project.utilities.Constants;
import com.example.android2project.utilities.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomePageFragment extends Fragment implements UserListener, BudgetDialog.budgetDialogListener {

    RecyclerView recyclerView;
    DatabaseReference database;
    ArrayList<Apartment> list;
    FloatingActionButton fab;
    TabLayout tabLayout;
    boolean isLoggedIn;
    Context context;
    View rootView;
    int budget=0;

    private static HomePageFragment instance;

    List<chatUser> chatUsers = new ArrayList<>();

    ApartmentAdapter adapter;
    ArrayList<Apartment> apartments;
    private PreferenceManager preferenceManager;




    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(getActivity());
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            isLoggedIn = false;
        else {
            isLoggedIn = true;
            getUsers();
        }
        context = getContext();

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        instance= this;

        rootView = inflater.inflate(R.layout.fragment_home,container,false);


        database = FirebaseDatabase.getInstance().getReference("House Offers");
        fab=rootView.findViewById(R.id.floatingActionButton);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isLoggedIn)
                {
                    notLoggedInDialog();
                    return;
                }

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.testFrameLayout,new AddApartmentFragment());
                fragmentTransaction.commit();

            }
        });
        apartments = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.recyclerViewMeetings);



        list = new ArrayList<>();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                list=apartments;
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    for(DataSnapshot dataSnapshot2 : dataSnapshot.getChildren())
                    {
                        Apartment apartment = dataSnapshot2.getValue(Apartment.class);
                        list.add(apartment);
                    }
                }
                Collections.sort(apartments);
                adapter = new ApartmentAdapter(getActivity(), apartments, chatUsers, HomePageFragment.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    @Override
    public void onUserClicked(chatUser chatUser) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, chatUser);
        startActivity(intent);
    }

    private void getUsers() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult()!= null) {
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if(currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            chatUser chatUser = new chatUser();
                            chatUser.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            chatUser.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            chatUser.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            chatUser.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            chatUser.id = queryDocumentSnapshot.getId();
                            chatUsers.add(chatUser);
                        }
                    }
                });

    }

    public void notLoggedInDialog()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        startActivity(new Intent(context.getApplicationContext(), SignInActivity.class));
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.you_are_not_logged_in).setPositiveButton(R.string.login, dialogClickListener)
                .setNegativeButton(R.string.back, dialogClickListener).show();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item,menu);
        MenuItem menuItem = menu.findItem(R.id.search_action);
        MenuItem menuItem1 = menu.findItem(R.id.filter_action);

        menuItem1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                BudgetDialog BudgetDialog = new BudgetDialog();
                BudgetDialog.show(getActivity().getSupportFragmentManager(),"Budget dialog" );
                return true;
            }
        });



        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setQueryHint(getString(R.string.insert_city_name));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText,null);
                return false;
            }
        });



        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void applyBudget(int theBudget) {
        budget=theBudget;
        adapter.getFilter().filter(Integer.toString(theBudget),null);
    }

    public static HomePageFragment GetInstance()
    {
        return instance;
    }


}
