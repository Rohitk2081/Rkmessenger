package com.example.rkmessenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class registration extends AppCompatActivity {
    TextView loginbut;
    EditText rg_username, rg_email, rg_password, rg_cpassword;
    Button rg_signup;
    CircleImageView rg_profile;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    android.app.ProgressDialog progressDialog;
    Uri imageURI;
    String image;
    String emailpattern ="[a-zA-Z0-9_-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FirebaseApp.initializeApp(this);
        database= FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("establishing your account");
        progressDialog.setCancelable(false);

         loginbut = findViewById(R.id.loginbut);
         rg_username = findViewById(R.id.rgusername);
         rg_email = findViewById(R.id.rgemailadd);
         rg_password=findViewById(R.id.rgPassword);
         rg_cpassword=findViewById(R.id.rgCPassword3);
         rg_signup = findViewById(R.id.signupbutton);
         rg_profile = findViewById(R.id.profilerg);

         loginbut.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(registration.this, login.class);
                 startActivity(intent);
                 finish();
             }
         });

         rg_profile.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent();
                 intent.setType("image/*");
                 intent.setAction(Intent.ACTION_GET_CONTENT);
                 startActivityForResult(Intent.createChooser(intent, "select picture"),10);
             }
         });

         rg_signup.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String name = rg_username.getText().toString();
                 String email = rg_email.getText().toString();
                 String password = rg_password.getText().toString();
                 String cpassword =rg_cpassword.getText().toString();
                 String status = "Hey, i am using RK messenger";

                 if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)|| TextUtils.isEmpty(cpassword))
                 {
                     progressDialog.dismiss();
                     Toast.makeText(registration.this,"please enter valid info",Toast.LENGTH_SHORT).show();
                 } else if (!email.matches(emailpattern)) {
                     progressDialog.dismiss();
                     rg_email.setError("type a valid email");
                 } else if (password.length()<6) {
                     progressDialog.dismiss();
                     rg_password.setError("Password must be more than 6 characters");
                 } else if (!cpassword.equals(password)) {
                     progressDialog.dismiss();
                     rg_cpassword.setError("password doesn't matches.");
                 }else{
                     auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if(task.isSuccessful())
                             {

                                 String id = task.getResult().getUser().getUid();
                                 DatabaseReference reference = database.getReference().child("user").child(id);
                                 StorageReference storageReference = storage.getReference().child("upload").child(id);

                                 if(imageURI !=null)
                                 {
                                     storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                         @Override
                                         public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                             if(task.isSuccessful()){
                                                 storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                     @Override
                                                     public void onSuccess(Uri uri) {
                                                         image = uri.toString();
                                                         Users users = new Users(id,name,email,password,cpassword, status);
                                                         reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                 if(task.isSuccessful()){
                                                                     progressDialog.show();
                                                                     Intent intent = new Intent(registration.this, MainActivity.class);
                                                                     startActivity(intent);
                                                                     finish();
                                                                 }else {
                                                                     Toast.makeText(registration.this,"error in creating user", Toast.LENGTH_SHORT).show();
                                                                 }

                                                             }
                                                         });

                                                     }
                                                 });
                                             }
                                         }
                                     });
                                 }else{
                                     String status = "Hey, i am using RK messenger";
                                     image = "https://firebasestorage.googleapis.com/v0/b/rkmessenger-724a2.appspot.com/o/user.png?alt=media&token=19e9d3a4-ec53-4a04-bc33-ad95552ce710";
                                     Users users = new Users(id, name, email, password, image,status);
                                     reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if(task.isSuccessful()){
                                                 progressDialog.show();
                                                 Intent intent = new Intent(registration.this, MainActivity.class);
                                                 startActivity(intent);
                                                 finish();
                                             }else {
                                                 Toast.makeText(registration.this,"error in creating user", Toast.LENGTH_SHORT).show();
                                             }

                                         }
                                     });

                                 }
                             }else{
                                 Toast.makeText(registration.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                             }
                         }
                     });
                 }

             }
         });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10)
        {
            if(data!=null)
            {
                imageURI = data.getData();
                rg_profile.setImageURI(imageURI);
            }
        }
    }
}