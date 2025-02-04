package com.example.mychat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Login extends AppCompatActivity {
    TextView logsignup;
    View button;
    EditText email, passcode;
    FirebaseAuth auth;
    String EmailPattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.loginbutton);
        email = findViewById(R.id.editTextlogEmail);
        passcode = findViewById(R.id.editTextlogPassword);
        logsignup = findViewById(R.id.logsignup);

        logsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, registration.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String Pass = passcode.getText().toString();

                if (TextUtils.isEmpty(Email)) {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(Pass)) {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Enter the Passcode", Toast.LENGTH_SHORT).show();
                } else if (!Email.matches(EmailPattern)) {
                    progressDialog.dismiss();
                    email.setError("Give a proper email!");
                } else if (Pass.length() < 6) {
                    progressDialog.dismiss();
                    passcode.setError("Passcode is too short");
                    Toast.makeText(Login.this, "Passcode needs to be more than 6 chars", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    auth.signInWithEmailAndPassword(Email, Pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    try {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(Login.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                                Toast.makeText(Login.this, "Email not registered. Please sign up.", Toast.LENGTH_SHORT).show();
                                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                                showIncorrectPasswordDialog();
                                            } else {
                                                Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(Login.this, "An unexpected error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if a user is already logged in
        if (auth.getCurrentUser() != null) {
            // Redirect to MainActivity if a user is logged in
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void showIncorrectPasswordDialog() {
        // Create and show the popup dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Incorrect Information");
        builder.setMessage("The password or email you entered is incorrect. Please check and try again.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Close the dialog when OK is clicked
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
