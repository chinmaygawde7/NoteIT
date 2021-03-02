package com.example.noteit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

// This code is of the login Activity

public class MainActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText passwordEditText;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        if (mAuth.getCurrentUser() != null) {

        }
    }

    public void onLogin(View view) {

        //Check if we can log in the user
        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            logIn();


                        } else {


                            // Sign up Process

                            mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                logIn();
                                            } else {

                                                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                            }

                                            // ...
                                        }
                                    });


                        }

                    }
                });


    }

    public void logIn() {
        // Move to Next Activity

        Intent intent = new Intent(this, NotesActivity.class);
        startActivity(intent);

    }
}

