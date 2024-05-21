package com.example.zomato;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zomato.Adapters.PlaceYourOrderAdapter;
import com.example.zomato.Model.Menu;
import com.example.zomato.Model.RestaurantModel;
import com.example.zomato.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class PlaceYourOrderActivity extends AppCompatActivity {
    private EditText inputAddress, inputCity, inputState, inputZip, inputCardNumber, inputCardExpiry, inputCardPin;
    private RecyclerView cartItemsRecyclerView;
    private TextView tvSubtotalAmount, tvDeliveryChargeAmount, tvDeliveryCharge, tvTotalAmount, toamount;
    private SwitchCompat switchDelivery;
    String TAG = "payment error";
    private boolean isDeliveryOn;
    private PlaceYourOrderAdapter placeYourOrderAdapter;
    String name, contact, email, amount, UserID1;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Button click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_your_order);
        RestaurantModel restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tvSubtotalAmount = findViewById(R.id.tvSubtotalAmount);
        tvDeliveryChargeAmount = findViewById(R.id.tvDeliveryChargeAmount);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        email = getIntent().getStringExtra("Email");
        name=getIntent().getStringExtra("UserName");
        contact=getIntent().getStringExtra("UserContact");
        click = findViewById(R.id.pay);


        cartItemsRecyclerView = findViewById(R.id.cartItemsRecyclerView);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(PlaceYourOrderActivity.this,GetLocation.class);
                i.putExtra("RestaurantModel", restaurantModel);
                i.putExtra("Amount", amount);
                i.putExtra("Email",email);
                i.putExtra("UserContact",contact);
                i.putExtra("UserName",name);
                startActivity(i);

            }
        });

        /*try{
            click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(PlaceYourOrderActivity.this, SetActivity.class);
                    i.putExtra("UserEmail", email);
                    i.putExtra("RestaurantModel", restaurantModel);
                    i.putExtra("Amount", amount);

                }
            });*/

            initRecyclerView(restaurantModel);
            calculateTotalAmount(restaurantModel);
        }/*
        catch (Exception e) {
            Toast.makeText(this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/


    private void calculateTotalAmount(RestaurantModel restaurantModel) {
        float subTotalAmount = 0f;

        for (Menu m : restaurantModel.getMenus()) {
            subTotalAmount += m.getPrice() * m.getTotalInCart();
        }

        tvSubtotalAmount.setText("₹" + String.format("%.2f", subTotalAmount));
        tvDeliveryChargeAmount.setText("₹" + String.format("%.2f", restaurantModel.getDelivery_charge()));
        subTotalAmount += restaurantModel.getDelivery_charge();
        tvTotalAmount.setText(String.format("%.2f", subTotalAmount));
        amount = tvTotalAmount.getText().toString();


    }

    private void initRecyclerView(RestaurantModel restaurantModel) {
        cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeYourOrderAdapter = new PlaceYourOrderAdapter(restaurantModel.getMenus());
        cartItemsRecyclerView.setAdapter(placeYourOrderAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1000) {
            setResult(Activity.RESULT_OK);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                //do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}