package com.example.android2project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText fullnameET,ageET,emailET,passwordET;
    Button registerUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        System.out.println(" QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ ");

        mAuth = FirebaseAuth.getInstance();

        registerUser = findViewById(R.id.buttonSignUp);

        fullnameET = findViewById(R.id.inputName);
        //ageET=findViewById(R.id.ageET);
        emailET=findViewById(R.id.inputEmail);
        passwordET=findViewById(R.id.inputPassword);

        registerUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();
                String fullname = fullnameET.getText().toString().trim();

                // TODO: 17/02/2022
                String age = "NAME";

                if(fullname.isEmpty())
                {
                    fullnameET.setError("Full name is required!");
                    fullnameET.requestFocus();
                    return;
                }

                if (age.isEmpty())
                {
                    ageET.setError("Age is required!");
                    ageET.requestFocus();
                    return;
                }
                if (email.isEmpty())
                {
                    emailET.setError("Email is required!");
                    emailET.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailET.setError("Please provide valid email");
                    emailET.requestFocus();
                    return;
                }

                if(password.isEmpty())
                {
                    passwordET.setError("Password is required!");
                    passwordET.requestFocus();
                    return;
                }

                System.out.println(" EMAIL = "+email+"\nPASSWORD ="+password);
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    User user = new User(fullname,age,email,0);

                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                startActivity(new Intent(RegisterUser.this,loggedInActivity.class));
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        });
    }
}