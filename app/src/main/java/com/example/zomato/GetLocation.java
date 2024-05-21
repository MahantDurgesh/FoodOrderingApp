package com.example.zomato;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class GetLocation extends AppCompatActivity implements LocationListener, PaymentResultListener {
    String UserID1, email,name,amount,contact,city,state,Country,pin,locality;
    LocationManager locationManager;
    TextView tvCity, tvState, tvCountry, tvPin, tvLocality;
    String TAG="payment error";
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        name=getIntent().getStringExtra("UserName");
        amount=getIntent().getStringExtra("Amount");
        email=getIntent().getStringExtra("Email");
        contact=getIntent().getStringExtra("UserContact");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        tvCity = findViewById(R.id.tvCity);
        tvState = findViewById(R.id.tvState);
        tvCountry = findViewById(R.id.tvCountry);
        tvPin = findViewById(R.id.tvPin);
        tvLocality = findViewById(R.id.tvLocality);
        city=tvCity.getText().toString();
        state=tvState.getText().toString();
        Country=tvCountry.getText().toString();
        pin=tvPin.getText().toString();
        locality=tvLocality.getText().toString();
        Checkout.preload(getApplicationContext());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationEnabled();
        getLocation();
    }

    private void locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(GetLocation.this)
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to show Near Places around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            tvCity.setText(addresses.get(0).getLocality());
            tvState.setText(addresses.get(0).getAdminArea());
            tvCountry.setText(addresses.get(0).getCountryName());
            tvPin.setText(addresses.get(0).getPostalCode());
            tvLocality.setText(addresses.get(0).getAddressLine(0));
            Log.d("user", "onCreate: "+amount);
            if (tvLocality != null && tvCity!= null && tvState!=null && tvPin!=null && tvCountry!=null){
                startPayment();
            }
            else{
                Toast.makeText(this, "Wait until your Location details get fetch", Toast.LENGTH_SHORT).show();
            }
            /*if (addresses.isEmpty()){
            Toast.makeText(this, "Wait till the Location get fetch", Toast.LENGTH_SHORT).show();}
            else{
                startPayment();}*/

            //startPayment();
            /*Intent i=new Intent(GetLocation.this,OrderSucceessActivity.class);
            i.putExtra("City", city);
            i.putExtra("State",state);
            i.putExtra("Country",Country);
            i.putExtra("Pin",pin);
            i.putExtra("Locality",locality);*/
        } catch (Exception e) {
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void startPayment(){
        Checkout checkout=new Checkout();
        checkout.setKeyID("rzp_test_S7DrOQpfOB1q65");
        final Activity activity=this;
        try{
            JSONObject options=new JSONObject();
            options.put("name",name);
            options.put("description","FOOD PAYMENT");
            options.put("currency","INR");
            String payment=amount;
            Double amt =Double.parseDouble(amount.toString());
            amt=amt*100;
            options.put("amount",amt);
            options.put("prefill.contact",contact);
            options.put("prefill.email",email);
            checkout.open(activity,options);

        }catch(Exception e){
            Log.e(TAG,"error occures",e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        try {
            UserID1=firebaseAuth.getCurrentUser().getUid();
            DocumentReference documentReference=firebaseFirestore.collection("Users").document(UserID1);
            Intent i = new Intent(GetLocation.this, OrderSucceessActivity.class);
            startActivity(i);
            finish();
            FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
            documentReference.update(
                    "Total_cost",amount
            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                    }
                    else{
                        Toast.makeText(GetLocation.this, "Failed to update data", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e){
            Toast.makeText(this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onPaymentError(int i, String s) {
        try {
            Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

        }


    }
}