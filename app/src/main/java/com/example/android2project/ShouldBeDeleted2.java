package com.example.android2project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ShouldBeDeleted2 extends AppCompatActivity {

    private TextView register;

    EditText emailET,passwordET;
    Button signIn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        register = findViewById(R.id.textCreateNewAccount);

        signIn = findViewById(R.id.buttonSignIn);
        emailET = findViewById(R.id.inputEmail);
        passwordET = findViewById(R.id.inputPassword);




        mAuth=FirebaseAuth.getInstance();

      /*  register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ShouldBeDeleted2.this, ShouldBeDeleted.class));
            }
        });*/

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();

                if(email.isEmpty())
                {
                    emailET.setError("Email is required!");
                    emailET.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    emailET.setError("Invalid email");
                    emailET.requestFocus();
                    return;
                }

                if(password.isEmpty())
                {
                    passwordET.setError("Password is required");
                    passwordET.requestFocus();
                    return;
                }


                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            startActivity(new Intent(ShouldBeDeleted2.this, loggedInActivity.class));
                        }
                    }
                });
            }
        });



    }
}