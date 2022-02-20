package com.example.android2project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.activities.ChatActivity;
import com.example.android2project.adapters.UserAdapter;
import com.example.android2project.listeners.UserListener;
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
import java.util.List;

public class fragment3 extends Fragment implements UserListener {

    RecyclerView recyclerView;
    DatabaseReference database;
    List<Apartment> list;



    List<chatUser> chatUsers = new ArrayList<>();


    ApartmentAdapterFavourites adapter;
    List<Apartment> apartments;
    private PreferenceManager preferenceManager;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(getActivity());
        getUsers();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_fragment3,container,false);


        database = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userFavourites");

        apartments = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.recyclerViewMeetings);
        adapter = new ApartmentAdapterFavourites(getActivity(), apartments, chatUsers,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        list = new ArrayList<>();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                list=apartments;
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                        Apartment apartment = dataSnapshot.getValue(Apartment.class);
                        list.add(apartment);
                }
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
                        if(chatUsers.size()>0) {
                            // UserAdapter userAdapter = new UserAdapter(chatUsers, this);
                            // binding.usersRecyclerView.setAdapter(userAdapter);
                            // binding.usersRecyclerView.setVisibility(View.VISIBLE);

                        } else {
                            //showErrorMessage();
                        }
                    } else {
                        //showErrorMessage();
                    }
                });

    }
}
