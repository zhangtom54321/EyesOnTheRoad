package io.github.plathatlabs.eyesontheroad;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(io.github.plathatlabs.eyesontheroad.R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Button signInButton = (Button) findViewById(io.github.plathatlabs.eyesontheroad.R.id.signInButton);
        signInButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                EditText emailBox = (EditText) findViewById(io.github.plathatlabs.eyesontheroad.R.id.emailBox);
                EditText passwordBox = (EditText) findViewById(io.github.plathatlabs.eyesontheroad.R.id.passwordBox);
                signIn(emailBox.getText().toString(), passwordBox.getText().toString());
            }
        });

        Button createAccountButton = (Button) findViewById(io.github.plathatlabs.eyesontheroad.R.id.createAccountButton);
        createAccountButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                EditText emailBox = (EditText) findViewById(io.github.plathatlabs.eyesontheroad.R.id.emailBox);
                EditText passwordBox = (EditText) findViewById(io.github.plathatlabs.eyesontheroad.R.id.passwordBox);
                createAccount(emailBox.getText().toString(), passwordBox.getText().toString());
            }
        });
    }



    protected void signIn(String email, String password) {
        if (email == null || email.length() == 0) {
            Toast.makeText(LoginActivity.this, "Please enter an email",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (password == null || password.length() == 0) {
            Toast.makeText(LoginActivity.this, "Please enter a password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Login", "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Login Successful!",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Login failed. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    protected void createAccount(String email, String password) {
        if (email == null || email.length() == 0) {
            Toast.makeText(LoginActivity.this, "Please enter an email",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (password == null || password.length() == 0) {
            Toast.makeText(LoginActivity.this, "Please enter a password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("CreateAccount", "createUserWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Account creation successful! Please sign in now.",
                                    Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("CreateAccount", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    protected void updateUI(FirebaseUser user) {
        if (user == null) { // error, try again
            return;
        }
        // otherwise start new activity
        Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
        intent.putExtra("email", user.getEmail());
        startActivity(intent);

    }
}
