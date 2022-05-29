package com.avadhut.firebasedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private boolean isLoggedIn = false;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        isLoggedIn = user != null;

        new Handler().postDelayed(() -> {
            if (isLoggedIn) {
                Intent homeIntent = new Intent(this, HomeActivity.class);
                startActivity(homeIntent);
                finishAfterTransition();
            } else {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finishAfterTransition();
            }
        }, 2_000L);

    }
}