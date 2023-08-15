package com.example.rkmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    Button button;
    EditText email, password;
    TextView signup;
     FirebaseAuth auth;
     android.app.ProgressDialog progressDialog;
    String emailpattern ="[a-zA-Z0-9_-]+@[a-z]+\\.+[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        auth = FirebaseAuth.getInstance();

        button = findViewById(R.id.logbutton);
        email = findViewById((R.id.logEmail));
        password = findViewById(R.id.logPassword);
        signup = findViewById(R.id.signuplog);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, registration.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String Email = email.getText().toString();
                String Password =password.getText().toString();

                if((TextUtils.isEmpty(Email))){
                    progressDialog.dismiss();
                    Toast.makeText(login.this, "Enter the email",Toast.LENGTH_SHORT).show();
                }
                else if((TextUtils.isEmpty(Password))){
                    progressDialog.dismiss();
                    Toast.makeText(login.this, "Enter the password",Toast.LENGTH_SHORT).show();
                }
                else if(!Email.matches(emailpattern)){
                    progressDialog.dismiss();
                    email.setError("type a valid email");
                } else if (Password.length()<6) {
                    progressDialog.dismiss();
                    password.setError("password should be more than 6 character");
                }

                else {
                    auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                progressDialog.show();
                                try {
                                    Intent intent = new Intent(login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(login.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(login.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }


            }
        });


    }
}