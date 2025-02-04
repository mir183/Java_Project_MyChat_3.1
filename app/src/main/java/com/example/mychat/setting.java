package com.example.mychat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class setting extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    ImageView setprofile, deleteAccount;
    EditText setname, setstatus;
    Button donebut;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    String email, passcode, currentProfilePic;
    Uri setImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        setprofile = findViewById(R.id.settingprofile);
        setname = findViewById(R.id.settingname);
        setstatus = findViewById(R.id.settingstatus);
        donebut = findViewById(R.id.donebut);
        deleteAccount = findViewById(R.id.deleteAccount);

        deleteAccount.setOnClickListener(view -> showDeleteConfirmationDialog());

        String userId = auth.getUid();
        if (userId == null) {
            Intent intent = new Intent(setting.this, Login.class);
            startActivity(intent);
            finish();
            return;
        }

        DatabaseReference reference = database.getReference().child("user").child(userId);
        StorageReference storageReference = storage.getReference().child("upload").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("mail").getValue(String.class);
                String passcode = snapshot.child("passcode").getValue(String.class);
                String name = snapshot.child("userName").getValue(String.class);
                String currentProfilePic = snapshot.child("profilepic").getValue(String.class);
                String status = snapshot.child("status").getValue(String.class);

                setname.setText(name);
                setstatus.setText(status);
                Picasso.get().load(currentProfilePic).into(setprofile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });

        setprofile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select picture"), 10);
        });

        donebut.setOnClickListener(v -> {
            String name = setname.getText().toString();
            String status = setstatus.getText().toString();
            if (name.isEmpty()) {
                setname.setError("Name cannot be empty");
                return;
            }
            if (setImageUri != null) {
                storageReference.putFile(setImageUri)
                        .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String finalImageUri = uri.toString();
                            saveUserData(name, email, passcode, finalImageUri, status, reference);
                        }))
                        .addOnFailureListener(e -> {
                            Toast.makeText(setting.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error uploading image: " + e.getMessage());
                        });
            } else {
                saveUserData(name, email, passcode, currentProfilePic, status, reference);
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        Dialog dialog = new Dialog(setting.this);
        dialog.setContentView(R.layout.dialog_delete_account);
        Button noButton = dialog.findViewById(R.id.noButton);
        Button yesButton = dialog.findViewById(R.id.yesButton);

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
                dialog.dismiss();
            }
        });

        dialog.show();
    }




    private void deleteAccount() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(setting.this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getUid();
        if (userId == null) {
            Toast.makeText(setting.this, "No user found", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = database.getReference().child("user").child(userId);
        userRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                auth.getCurrentUser().delete().addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        Toast.makeText(setting.this, "Account deleted", Toast.LENGTH_SHORT).show();
                        SharedPreferences preferences = getSharedPreferences("MyChatApp", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLoggedIn", false);
                        editor.apply();
                        Intent intent = new Intent(setting.this, Login.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(setting.this, "Failed to delete auth user", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(setting.this, "Failed to delete database user", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void saveUserData(String name, String email, String passcode, String profilePic, String status, DatabaseReference reference) {
        Users users = new Users(auth.getUid(), name, email, passcode, profilePic, status);
        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(setting.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(setting.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(setting.this, "Something went wrong...try again", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error saving data: " + task.getException().getMessage());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            setImageUri = data.getData();
            setprofile.setImageURI(setImageUri);
        }
    }
}