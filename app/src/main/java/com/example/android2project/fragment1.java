package com.example.android2project;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class fragment1 extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference database;
    Adapter myAdapter;
    ArrayList<Meeting> list;
    FloatingActionButton fab;
    TabLayout tabLayout;

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

        recyclerView=rootView.findViewById(R.id.recyclerViewMeetings);
        database = FirebaseDatabase.getInstance().getReference("Meetings");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
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

        list = new ArrayList<>();
        myAdapter = new Adapter(rootView.getContext(),list);
        recyclerView.setAdapter(myAdapter);


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    for(DataSnapshot dataSnapshot2 : dataSnapshot.getChildren())
                    {
                        Meeting meeting = dataSnapshot2.getValue(Meeting.class);
                        list.add(meeting);
                    }
                }
                myAdapter.notifyDataSetChanged();
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
