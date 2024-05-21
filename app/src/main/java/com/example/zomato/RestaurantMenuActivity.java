package com.example.zomato;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zomato.Adapters.MenuListAdapter;
import com.example.zomato.Model.Menu;
import com.example.zomato.Model.RestaurantModel;
import com.example.zomato.R;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMenuActivity extends AppCompatActivity implements MenuListAdapter.MenuListClickListener{
    private List<Menu> menuList = null;
    private MenuListAdapter menuListAdapter;
    private List<Menu> itemsInCartList;
    private int totalItemInCart = 0;
    private TextView buttonCheckout;
    String name,contact,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);
        RestaurantModel restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        email=getIntent().getStringExtra("Email");
        name=getIntent().getStringExtra("UserName");
        contact=getIntent().getStringExtra("UserContact");
        menuList = restaurantModel.getMenus();

        initRecyclerView();


        buttonCheckout = findViewById(R.id.buttonCheckout);
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemsInCartList != null && itemsInCartList.size() <= 0) {
                    Toast.makeText(RestaurantMenuActivity.this, "Please add some items in cart.", Toast.LENGTH_SHORT).show();
                    return;
                }
                restaurantModel.setMenus(itemsInCartList);
                Intent i = new Intent(RestaurantMenuActivity.this, PlaceYourOrderActivity.class);
                i.putExtra("RestaurantModel", restaurantModel);
                i.putExtra("Email",email);
                i.putExtra("UserContact",contact);
                i.putExtra("UserName",name);
                startActivityForResult(i, 1000);
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView =  findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        menuListAdapter = new MenuListAdapter(menuList, this);
        recyclerView.setAdapter(menuListAdapter);
    }

    @Override
    public void onAddToCartClick(com.example.zomato.Model.Menu menu) {
        if(itemsInCartList == null) {
            itemsInCartList = new ArrayList<Menu>();
        }
        itemsInCartList.add(menu);
        totalItemInCart = 0;

        for(com.example.zomato.Model.Menu m : itemsInCartList) {
            totalItemInCart = totalItemInCart + m.getTotalInCart();
        }
        buttonCheckout.setText("Checkout (" +totalItemInCart +") items");
    }

    @Override
    public void onUpdateCartClick(com.example.zomato.Model.Menu menu) {
        if(itemsInCartList.contains(menu)) {
            int index = itemsInCartList.indexOf(menu);
            itemsInCartList.remove(index);
            itemsInCartList.add(index, menu);

            totalItemInCart = 0;

            for(com.example.zomato.Model.Menu m : itemsInCartList) {
                totalItemInCart = totalItemInCart + m.getTotalInCart();
            }
            buttonCheckout.setText("Checkout (" +totalItemInCart +") items");
        }
    }

    @Override
    public void onRemoveFromCartClick(com.example.zomato.Model.Menu menu) {
        if(itemsInCartList.contains(menu)) {
            itemsInCartList.remove(menu);
            totalItemInCart = 0;

            for(com.example.zomato.Model.Menu m : itemsInCartList) {
                totalItemInCart = totalItemInCart + m.getTotalInCart();
            }
            buttonCheckout.setText("Checkout (" +totalItemInCart +") items");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            //
            finish();
        }
    }
}