package com.example.zomato;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zomato.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    TextInputEditText etRegEmail;
    TextInputEditText etRegPassword;
    TextView tvLoginHere;
    Button btnRegister;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    String UserID1, email,name,password,contact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPass);
        tvLoginHere = findViewById(R.id.tvLoginHere);
        btnRegister = findViewById(R.id.btnRegister);
        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(view ->{
            createUser();
        });

        tvLoginHere.setOnClickListener(view ->{
            startActivity(new Intent(SignUp.this, LoginActivity.class));
        });
    }

    private void createUser(){
          email= etRegEmail.getText().toString();
         password = etRegPassword.getText().toString();
        Log.d("user", "onCreate: "+name+""+contact+""+email);

        if (TextUtils.isEmpty(email)){
            etRegEmail.setError("Email cannot be empty");
            etRegEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            etRegPassword.setError("Password cannot be empty");
            etRegPassword.requestFocus();
            progressDialog.cancel();
        }
        else{
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        UserID1=mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference=firebaseFirestore.collection("Users").document(UserID1);
                        Map<String,Object> user= new HashMap<>();
                        user.put("Email",email);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent i = new Intent(SignUp.this, SetActivity.class);
                                i.putExtra("Email",email);
                                startActivity(i);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(e instanceof FirebaseNetworkException)
                                {
                                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();}
                                Toast.makeText(getApplicationContext(),"Values not stored",Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();

                            }
                        });
                        Toast.makeText(SignUp.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp.this, SetActivity.class));
                    }else{
                        Toast.makeText(SignUp.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}