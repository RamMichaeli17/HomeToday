package com.example.android2project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class fragment_Add_Meeting extends Fragment {

    EditText meetingGameET,meetingDateET;
    Button submitMeeting,addImage,goBackBtn;
    int counter=0;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    String game,date;
    Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;



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

        View rootView = inflater.inflate(R.layout.fragment_add_meeting,container,false);

        meetingDateET = rootView.findViewById(R.id.meetingDateET);
        meetingGameET = rootView.findViewById(R.id.meetingGameET);
        submitMeeting = rootView.findViewById(R.id.submitMeeting);
        addImage=rootView.findViewById(R.id.addImage);
        goBackBtn=rootView.findViewById(R.id.goBackBtn);

        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment_Add_Meeting.this).commit();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        submitMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                game = meetingGameET.getText().toString().trim();
                date = meetingDateET.getText().toString().trim();


                reference= FirebaseDatabase.getInstance().getReference("Users");
                user= FirebaseAuth.getInstance().getCurrentUser();
                userID = user.getUid();



                reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userProfile = snapshot.getValue(User.class);

                        if(userProfile!= null)
                        {
                            counter = userProfile.jobsCounter+1;
                            reference.child(userID +"/jobsCounter").setValue(counter);
                        }
                        uploadPicture();
                        addMeeting();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_LONG).show();
                    }
                });


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


    public void addMeeting()
    {
        Meeting meeting = new Meeting(game,date);

        FirebaseDatabase.getInstance().getReference("Meetings")
                .child(userID).child(("Meeting"+counter)).setValue(meeting).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(),"Meeting #"+counter+" has been created!",Toast.LENGTH_LONG).show();

                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment_Add_Meeting.this).commit();


            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1 && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }

    private void uploadPicture()
    {
        StorageReference fileRef = storageReference.child("Meeting Pictures/"+user.getEmail()+"/Meeting"+counter);

        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), "Failed To Upload image", Toast.LENGTH_LONG).show();
                    }
                });
    }
}


