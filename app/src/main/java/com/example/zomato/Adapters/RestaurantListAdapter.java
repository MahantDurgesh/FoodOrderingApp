package com.example.zomato.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zomato.Model.RestaurantModel;
import com.example.zomato.R;

import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;

 public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.MyViewHolder> implements Filterable{
     private List<RestaurantModel> restaurantModelList;
     private RestaurantListClickListener clickListener;
     ArrayList<RestaurantModel> backup;

     public RestaurantListAdapter(List<RestaurantModel> restaurantModelList, RestaurantListClickListener clickListener) {
         this.restaurantModelList = restaurantModelList;
         this.clickListener = clickListener;
     }

     public void updateData(List<RestaurantModel> restaurantModelList) {
         this.restaurantModelList = restaurantModelList;
         notifyDataSetChanged();
         backup= new ArrayList<>(restaurantModelList);
     }

     @NonNull
     @Override
     public RestaurantListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
         return  new MyViewHolder(view);
     }

     @Override
     public void onBindViewHolder(@NonNull RestaurantListAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
         holder.restaurantName.setText(restaurantModelList.get(position).getName());
         holder.restaurantAddress.setText("Address: "+restaurantModelList.get(position).getAddress());
         holder.restaurantHours.setText("Today's hours: " + restaurantModelList.get(position).getHours().getTodaysHours());

         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 clickListener.onItemClick(restaurantModelList.get(position));
             }
         });
         Glide.with(holder.thumbImage)
                 .load(restaurantModelList.get(position).getImage())
                 .into(holder.thumbImage);

     }

     @Override
     public int getItemCount() {
         return restaurantModelList.size();
     }

     @Override
     public Filter getFilter() {
         return filter;
     }
     Filter filter = new Filter() {
         @Override
         protected FilterResults performFiltering(CharSequence keyword) {
             ArrayList<RestaurantModel> filtereddata = new ArrayList<>();
             if (keyword.toString().isEmpty())
                 filtereddata.addAll(backup);
             else {
                 for (RestaurantModel obj : backup)
                 {
                     if (obj.getName().toString().toLowerCase().contains(keyword.toString().toLowerCase()))
                         filtereddata.add(obj);

                 }
             }
             FilterResults results= new FilterResults();
             results.values=filtereddata;
             return results;
         }

         @Override
         protected void publishResults(CharSequence constraint, FilterResults results) {
             restaurantModelList.clear();
             restaurantModelList.addAll((ArrayList<RestaurantModel>)results.values);
             notifyDataSetChanged();
         }
     };

     static class MyViewHolder extends RecyclerView.ViewHolder {
         TextView  restaurantName;
         TextView  restaurantAddress;
         TextView  restaurantHours;
         ImageView thumbImage;

         public MyViewHolder(View view) {
             super(view);
             restaurantName = view.findViewById(R.id.restaurantName);
             restaurantAddress = view.findViewById(R.id.restaurantAddress);
             restaurantHours = view.findViewById(R.id.restaurantHours);
             thumbImage = view.findViewById(R.id.thumbImage);

         }
     }

     public interface RestaurantListClickListener {
         public void onItemClick(RestaurantModel restaurantModel);
     }
 }
