package com.avadhut.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class BooksActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText bookEditText;
    private TextView textBooksList;
    private StringBuilder books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        Button btnAddBook = findViewById(R.id.btnAddBook);
        bookEditText = findViewById(R.id.bookEditText);
        textBooksList = findViewById(R.id.textBooksList);
        books = new StringBuilder();

        // firebase initialization
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // getting userId
        String userId = "";
        if (currentUser != null) {
            userId = currentUser.getUid();
            getBooksForUserId(userId);
        } else {
            Toast.makeText(this, "User not logged in, please login", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finishAfterTransition();
        }

        String finalUserId = userId;
        btnAddBook.setOnClickListener(v -> {
            String bookName = bookEditText.getText().toString();
            if (bookName.isEmpty()) {
                bookEditText.setError("Please enter valid book name");
                return;
            }
            bookEditText.setError(null);

            Map<String, Object> user = new HashMap<>();
            user.put("userId", finalUserId);
            user.put("bookName", bookName);

            db.collection("books")
                    .add(user)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(this, "Book Added!", Toast.LENGTH_SHORT).show();
                        getBooksForUserId(finalUserId);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document", e);
                    });
        });
    }

    private void getBooksForUserId(String userId) {
        db.collection("books")
                .whereEqualTo("userId", userId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            books = new StringBuilder();
                            int count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> data = document.getData();

                                if (data.containsKey("bookName") && data.get("bookName") != null) {
                                    count++;
                                    books.append(count)
                                            .append(". ")
                                            .append(data.get("bookName").toString())
                                            .append("\n");
                                    textBooksList.setText(books);
                                }

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private static final String TAG = "BooksActivity";
}