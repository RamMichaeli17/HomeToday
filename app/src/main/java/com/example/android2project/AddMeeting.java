package com.example.android2project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class AddMeeting extends AppCompatActivity {

    EditText meetingGameET,meetingDateET;
    Button submitMeeting,addImage;
    int counter=0;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    String game,date;
    Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);

        meetingDateET = findViewById(R.id.meetingDateET);
        meetingGameET = findViewById(R.id.meetingGameET);
        submitMeeting = findViewById(R.id.submitMeeting);
        addImage=findViewById(R.id.addImage);

        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();

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
                user=FirebaseAuth.getInstance().getCurrentUser();
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
                        Toast.makeText(AddMeeting.this,"Something went wrong",Toast.LENGTH_LONG).show();
                    }
                });


            }
        });

    }

    public void addMeeting()
    {
        Meeting meeting = new Meeting(game,date);

        FirebaseDatabase.getInstance().getReference("Meetings")
                .child(userID).child(("Meeting"+counter)).setValue(meeting).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AddMeeting.this,"Meeting #"+counter+" has been created!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(AddMeeting.this,loggedInActivity.class));

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
                        Toast.makeText(getApplicationContext(), "Failed To Upload image", Toast.LENGTH_LONG).show();
                    }
                });
    }
}