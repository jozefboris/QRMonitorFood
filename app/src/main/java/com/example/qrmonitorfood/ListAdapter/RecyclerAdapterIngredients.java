package com.example.qrmonitorfood.ListAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.qrmonitorfood.Database.Ingredients;
import com.example.qrmonitorfood.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterIngredients extends RecyclerView.Adapter<RecyclerAdapterIngredients.MyViewHolder> implements Filterable {

    private List<Ingredients> productsList;
    private List<Ingredients> productsListFull;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.titleList);
            genre = (TextView) view.findViewById(R.id.genre);
        }
    }

    public RecyclerAdapterIngredients(List<Ingredients> productsList)
    {

        productsListFull = new ArrayList<>(productsList);
        this.productsList = productsList;
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
        Ingredients movie = productsList.get(position);
        holder.title.setText(movie.getTitle());
        holder.genre.setText("zlozky");

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    public void updateList(List<Ingredients> list){

        productsList = new ArrayList<>();
        productsList.addAll(list);
        notifyDataSetChanged();


    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Ingredients> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(productsListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Ingredients item : productsListFull) {
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
