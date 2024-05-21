package com.example.zomato;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Settings extends AppCompatActivity {
    EditText newpassword;
    TextView email,name,contact;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    FirebaseUser user;
    DocumentReference reference;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    Button resetpassword,signout,orderdetail;
    String names,emailstring,cont,Uid;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent l=new Intent(Settings.this, MainActivity.class);
        startActivity(l);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        firebaseAuth=FirebaseAuth.getInstance();
        newpassword=findViewById(R.id.resetpasswordedittext);
        resetpassword=findViewById(R.id.resetpasswordbutton);
        signout=findViewById(R.id.signoutbutton);
        email=findViewById(R.id.emailsettings);
        name=findViewById(R.id.namesettings);
        contact=findViewById(R.id.contactsettings);
        user=firebaseAuth.getInstance().getCurrentUser();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressDialog=new ProgressDialog(Settings.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        if(firebaseAuth.getCurrentUser()!=null)
        {Uid=firebaseAuth.getCurrentUser().getUid();
            reference=firebaseFirestore.collection("Users").document(Uid);
            reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    emailstring=documentSnapshot.getString("Email");
                    names=documentSnapshot.getString("UserName");
                    cont=documentSnapshot.getString("UserContact");
                    Log.d("user", "onCreate: "+email);
                    email.setText(emailstring);
                    name.setText(names);
                    contact.setText(cont);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressDialog.cancel();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(e instanceof FirebaseNetworkException)
                    {
                        Toast.makeText(getApplicationContext(),"NO internet connection",Toast.LENGTH_SHORT).show();

                    }
                    Toast.makeText(getApplicationContext(),"Error data not fetched",Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressDialog.cancel();
                }
            });
            BottomNavigationView bottomNavigationView;
            bottomNavigationView=findViewById(R.id.bottom_navigation);
            Menu menu=bottomNavigationView.getMenu();
            MenuItem menuItem=menu.getItem(1);
            menuItem.setChecked(true);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.homeicon:
                            Intent l=new Intent(Settings.this, MainActivity.class);
                            startActivity(l);
                            break;
                        case R.id.settingsicon:
                            break;
                    }
                    return false;
                }
            });
            signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder signout= new AlertDialog.Builder(view.getContext());
                    signout.setTitle("Do you really want to signout ?");
                    signout.setMessage("Press YES to signout");
                    signout.setCancelable(false);
                    signout.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseAuth.getInstance().signOut();
                            Intent x=new Intent(Settings.this,SignUp.class);
                            startActivity(x);
                            finish();

                        }
                    });
                    signout.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    signout.create().show();
                }
            });


        }
    }
}