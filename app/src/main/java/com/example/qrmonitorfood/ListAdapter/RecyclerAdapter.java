package com.example.qrmonitorfood.ListAdapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.qrmonitorfood.Database.Product;
import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.R;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.valueOf;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> implements Filterable {

    private List<Product> productsList;
    private List<Product> productsListFull;
    private SparseBooleanArray selectedItems;
    private List<String> selectList;
    int selectedItemCount;
    RelativeLayout relativeLayout;

   

    public void clearSelections() {
        selectedItems.clear();
        selectList.clear();
        notifyDataSetChanged();
    }

 


    public int getSelectedItemCount() {
        return selectList.size();
    }

    public List<String> getSelectedItems() {
     /*   List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }*/
        return selectList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.titleList);
            genre = (TextView) view.findViewById(R.id.genre);
        }
    }

    public RecyclerAdapter(List<Product> productsList)
    {
        selectedItems = new SparseBooleanArray();
        productsListFull = new ArrayList<>(productsList);
        this.productsList = productsList;
        selectedItemCount = 0;
        selectList = new ArrayList<String>();
     //relativeLayout.findViewById(R.id.relative); 
    // relativeLayout.setBackgroundColor(WHITE);
    }

    public void onClick(View view, int position, String id) {
        if (!selectedItems.get(position))
        {
            selectedItems.put(position,true);
            selectedItemCount++;
            selectList.add(id);
            
           // listener.selectedItemCount(selectedItemCount); // calling the method in main activity Because: in our case mainActivity our created interface for clicklisteners
            notifyItemChanged(position);
        }
        else // if clicked item is already selected
        {
            selectedItems.put(position,false);
            selectedItemCount--;
            selectList.remove(id);
          //  listener.selectedItemCount(selectedItemCount); // calling the method in main activity Because: in our case mainActivity our created interface for clicklisteners
            notifyItemChanged(position);
        }

    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_row,
                parent, false);
        
        return new MyViewHolder(v);
    }




    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product movie = productsList.get(position);
        
        holder.genre.setText(movie.getDateOfMade()+ " - " + movie.getDateExpiration() + " " + IntentConstants.titleProducer);
        holder.title.setText(movie.getTitle());
            //holder.title.setText((CharSequence) productsList.get(position));

        if (selectedItems.get(position))
        {
            //selectedItemCount++;

            holder.itemView.setBackgroundColor(Color.GRAY);

        }
        else
        {
           holder.itemView.setBackgroundColor(Color.TRANSPARENT);

        }


        // holder.title.setBackgroundColor(GREEN);
      //  holder.genre.getDrawingCacheBackgroundColor()
      // holder.title.se productsList.get(position).setProduktId();


    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    public void updateList(List<Product> list){

        productsList = new ArrayList<>();
        productsList.addAll(list);
        notifyDataSetChanged();


    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(productsListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Product item : productsListFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }




        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productsList.clear();
            productsList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
