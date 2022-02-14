package com.example.android2project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class loggedInActivity extends AppCompatActivity {


    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    Button logoutBtn,addMeetingBtn;
    TextView nameTV,emailTV,ageTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        user=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();


        nameTV=findViewById(R.id.three);
        emailTV=findViewById(R.id.four);
        ageTV=findViewById(R.id.five);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile!= null)
                {
                    String fullName = userProfile.fullName;
                    String email = userProfile.email;
                    String age = userProfile.age;

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


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(loggedInActivity.this,"Successfully signed out",Toast.LENGTH_LONG).show();
                startActivity(new Intent(loggedInActivity.this,MainActivity.class));
            }
        });
    }
}