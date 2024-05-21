package com.example.zomato;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SetActivity extends AppCompatActivity {
String name1,contact1,UserID1,email,amount;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    TextInputEditText etRegName;
    TextInputEditText etRegContact;
    Button set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        etRegName= findViewById(R.id.etRegName1);
        etRegContact=findViewById(R.id.etRegContact1);
        set=findViewById(R.id.btnset);

        email=getIntent().getStringExtra("Email");
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        amount=getIntent().getStringExtra("Amount");
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    try {
                        name1 = etRegName.getText().toString();
                        contact1 =etRegContact.getText().toString();
                        UserID1=firebaseAuth.getCurrentUser().getUid();
                        Log.d("user", "onCreate: "+etRegContact+""+etRegName);
                        DocumentReference documentReference=firebaseFirestore.collection("Users").document(UserID1);
                        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                        documentReference.update(
                                "UserName",name1,
                                "UserContact",contact1
                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Intent i=new Intent(SetActivity.this,MainActivity.class);
                                    i.putExtra("Email", email);
                                    i.putExtra("Amount", amount);
                                    i.putExtra("UserName",name1);
                                    i.putExtra("UserContact",contact1);
                                    startActivity(i);
                                }
                                else{
                                    Toast.makeText(SetActivity.this, "Failed to update data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } catch (Exception e){
                        Toast.makeText(SetActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
            }
        });

        }
        }
