package com.example.zomato;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.zomato.Adapters.PlaceYourOrderAdapter;
import com.example.zomato.Model.Menu;
import com.example.zomato.Model.RestaurantModel;

public class orderdetails extends AppCompatActivity {
    private RecyclerView cartItemsRecyclerView;
    private TextView tvSubtotalAmount, tvDeliveryChargeAmount, tvDeliveryCharge, tvTotalAmount, toamount;
    private PlaceYourOrderAdapter placeYourOrderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetails);
        RestaurantModel restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tvSubtotalAmount = findViewById(R.id.tvSubtotalAmount);
        tvDeliveryChargeAmount = findViewById(R.id.tvDeliveryChargeAmount);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        cartItemsRecyclerView = findViewById(R.id.cartItemsRecyclerView);
        initRecyclerView(restaurantModel);
        calculateTotalAmount(restaurantModel);
    }
    private void calculateTotalAmount(RestaurantModel restaurantModel) {
        float subTotalAmount = 0f;

        for (Menu m : restaurantModel.getMenus()) {
            subTotalAmount += m.getPrice() * m.getTotalInCart();
        }

        tvSubtotalAmount.setText("₹" + String.format("%.2f", subTotalAmount));
        tvDeliveryChargeAmount.setText("₹" + String.format("%.2f", restaurantModel.getDelivery_charge()));
        subTotalAmount += restaurantModel.getDelivery_charge();
        tvTotalAmount.setText(String.format("%.2f", subTotalAmount));


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
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
