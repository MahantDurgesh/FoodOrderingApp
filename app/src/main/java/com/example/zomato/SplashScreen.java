package com.example.zomato;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.zomato.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    DocumentReference reference;
    String Uid,Firename,FireContact,Fireemail;
    static int counter=0;
    static int duration=5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        progressBar=findViewById(R.id.progressbar);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        progress();
        start();
    }
    public void progress(){
        final Timer timer =new Timer();
        TimerTask timerTask= new TimerTask() {
            @Override
            public void run() {
                counter++;
                progressBar.setProgress(counter);
                if(counter==5000){
                    timer.cancel();
                }



            }
        };
        timer.schedule(timerTask,0,100);
    }
    public void start(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()  {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    Uid=firebaseAuth.getCurrentUser().getUid();
                    reference=firebaseFirestore.collection("Users").document(Uid);
                    reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Firename=documentSnapshot.getString("UserName");
                            FireContact=documentSnapshot.getString("UserContact");
                            Fireemail=documentSnapshot.getString("Email");
                                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                i.putExtra("Email",Fireemail);
                                i.putExtra("UserContact",FireContact);
                                i.putExtra("UserName",Firename);
                                startActivity(i);
                                finish();
            }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(e instanceof FirebaseNetworkException)
                            {
                                Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(getApplicationContext(),"Error data not fetched",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    startActivity(new Intent(SplashScreen.this, SignUp.class));
                    finish();
                }

            }
        },duration);



    }
}
