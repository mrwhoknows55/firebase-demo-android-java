package com.avadhut.firebasedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // view initialization
        ImageView userImageView = findViewById(R.id.userIconImgView);
        TextView userNameTextView = findViewById(R.id.userNameTextView);
        Button btnSignOut = findViewById(R.id.btnSignOut);

        // firebase initialization
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            // get photo image url, name, email from currentUser object
            Uri photoUri = currentUser.getPhotoUrl();
            String imageURL = photoUri != null ? photoUri.toString() : "";
            String userName = "Name: " + currentUser.getDisplayName();
            String userEmail = "Email: " + currentUser.getEmail();

            // set user name & email
            userNameTextView.setText(userName + "\n" + userEmail);

            // set profile image
            Glide.with(this).load(imageURL).circleCrop().into(userImageView);
        } else {
            Toast.makeText(this, "User not logged in, please login", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finishAfterTransition();
        }

        btnSignOut.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finishAfterTransition();
        });
    }
}