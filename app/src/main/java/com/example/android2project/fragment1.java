package com.example.android2project;

import android.content.Context;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class fragment1 extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference database;
    List<Apartment> list;
    FloatingActionButton fab;
    TabLayout tabLayout;



    // YUL

    ApartmentAdapter adapter;
    List<Apartment> apartments;
    // RecyclerView recyclerView;
    //Button favouriteHomePageBt= findViewById(R.id.fav_tv);
    //Boolean favVisibleFlag = false;



    //

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_fragment1,container,false);


        database = FirebaseDatabase.getInstance().getReference("House Offers");
/*
        recyclerView=rootView.findViewById(R.id.recyclerViewMeetings);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
*/
        fab=rootView.findViewById(R.id.floatingActionButton);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.testFrameLayout,new fragment_Add_Meeting());
                fragmentTransaction.commit();

            }
        });
        apartments = new ArrayList<>();
        apartments.add(new Apartment(R.drawable.profile_pic_check,R.drawable.apartment1, "Yossi Cohen", "Ashdod", "03/02/2020",1500000,5));
        apartments.add(new Apartment(R.drawable.profile_pic_check,R.drawable.apartment2, "Shlomi Barel", "Tel Aviv","02/02/2022",2200000,4));
        apartments.add(new Apartment(R.drawable.profile_pic_check,R.drawable.apartment3, "Yonit Levi", "Bat Yam","15/08/2021",1800000,5));
        apartments.add(new Apartment(R.drawable.profile_pic_check,R.drawable.apartment4, "Matan Adler", "Eilat","29/10/2019",3200000,6));

        recyclerView = rootView.findViewById(R.id.recyclerViewMeetings);
        adapter = new ApartmentAdapter(getActivity(), apartments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setListener(new ApartmentAdapter.ApartmentListener() {
            @Override
            public void onApartmentClicked(int position, View view) {

                Apartment mission = apartments.get(position);
                adapter.notifyItemChanged(position);
            }
        });

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
}
