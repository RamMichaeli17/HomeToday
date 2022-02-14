package com.example.android2project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddMeeting extends AppCompatActivity {

    EditText meetingGameET,meetingDateET;
    Button submitMeeting;
    int counter;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);

        meetingDateET = findViewById(R.id.meetingDateET);
        meetingGameET = findViewById(R.id.meetingGameET);
        submitMeeting = findViewById(R.id.submitMeeting);

        submitMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String game = meetingGameET.getText().toString().trim();
                String date = meetingDateET.getText().toString().trim();


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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddMeeting.this,"Something went wrong",Toast.LENGTH_LONG).show();
                    }
                });



                Meeting meeting = new Meeting(game,date);
                FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(("Meeting"+counter)).setValue(meeting).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

            }
        });
    }
}