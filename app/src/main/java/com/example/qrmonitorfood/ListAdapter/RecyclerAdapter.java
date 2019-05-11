package com.example.qrmonitorfood.ListAdapter;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.qrmonitorfood.Database.Product;
import com.example.qrmonitorfood.R;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> implements Filterable {

    private List<Product> productsList;
    private List<Product> productsListFull;
    private SparseBooleanArray selectedItems;
    private ArrayList<String> selectList;
    private int type;


    /**
     * Metoda pre vymazanie výberu
     */
    public void clearSelections() {
        selectedItems.clear();
        selectList.clear();
        notifyDataSetChanged();
    }


    /**
     * Vrati pocet vybraných
     * @return pocet položiek
     */
    public int getSelectedCountItem() {
        return selectList.size();
    }

    /**
     * Vrati list s vyberom
     * @return list dfsf
     */
    public List<String> getSelectedItems() {
        return selectList;
    }

    /*
    View holder pre zobrazenie položky
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, genre, batch;
        private CheckBox checkBox;
        private ImageButton imageButton;

        private MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.titleList);
            genre = (TextView) view.findViewById(R.id.genre);
            batch = (TextView) view.findViewById(R.id.batch);
            imageButton = view.findViewById(R.id.list_image);
            checkBox = (CheckBox) view.findViewById(R.id.brand_select);



        }



          }


    public RecyclerAdapter(List<Product> productsList, int type)
    {
        selectedItems = new SparseBooleanArray();
        productsListFull = new ArrayList<>(productsList);
        this.productsList = productsList;
        selectList = new ArrayList<String>();
        this.type = type;
    }


    /**
     * Metoda pre manipulaciu so vyberovým zoznamom
     * @param position položky
     * @param id položky
     */
    public void onClick(int position, String id) {
        if (!selectedItems.get(position))
        {
            selectedItems.put(position,true);
            selectList.add(id);
            notifyItemChanged(position);
        }
        else
        {
            selectedItems.put(position,false);
            selectList.remove(id);
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

    /**
     * Vrati celý list produktov
     * @return list
     */
    public List<Product> getNew() {
        return productsList;
    }


    /**
     * Radenie zoznamu produktov podla datumu výroby vzostupne
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByDateOfMadeAsc(){

        Collections.sort(productsList, new Comparator<Product>() {
            DateFormat f = new SimpleDateFormat("dd.MM.yyyy");
            @Override
            public int compare(Product o1, Product o2) {
                try {
                    return f.parse(o1.getDateOfMade()).compareTo(f.parse(o2.getDateOfMade()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });




    }

    /**
     * Radenie zoznamu produktov podla datumu spotreby vzostupne
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByDateOfExpidationAsc(){


        Collections.sort(productsList, new Comparator<Product>() {
            DateFormat f = new SimpleDateFormat("dd.MM.yyyy");
            @Override
            public int compare(Product o1, Product o2) {
                try {
                    return f.parse(o1.getDateExpiration()).compareTo(f.parse(o2.getDateExpiration()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

    }

    /**
     * Radenie zoznamu produktov podla nazvu vzostupne
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByTitleAsc(){

        productsList.sort(new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });


    }

    /**
     * Radenie zoznamu produktov podla šarže vzostupne
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByBatchAsc(){

        productsList.sort(new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getBatch().compareTo(o2.getBatch());
            }
        });
    }


    /**
     * Radenie zoznamu produktov podla datumu výroby zostupne
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByDateOfMadeDesc(){

        Collections.sort(productsList, new Comparator<Product>() {
            DateFormat f = new SimpleDateFormat("dd.MM.yyyy");
            @Override
            public int compare(Product o2, Product o1) {
                try {
                    return f.parse(o1.getDateOfMade()).compareTo(f.parse(o2.getDateOfMade()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

    /**
     * Radenie zoznamu produktov podla datumu spotreby zostupne
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByDateOfExpidationDesc(){


        Collections.sort(productsList, new Comparator<Product>() {
            DateFormat f = new SimpleDateFormat("dd.MM.yyyy");
            @Override
            public int compare(Product o2, Product o1) {
                try {
                    return f.parse(o1.getDateExpiration()).compareTo(f.parse(o2.getDateExpiration()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

    /**
     * Radenie zoznamu produktov podla nazvu zostupne
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByTitleDesc(){

        productsList.sort(new Comparator<Product>() {
            @Override
            public int compare(Product o2, Product o1) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
    }

    /**
     * Radenie zoznamu produktov podla šarže zostupne
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByBatchDesc(){

        productsList.sort(new Comparator<Product>() {
            @Override
            public int compare(Product o2, Product o1) {
                return o1.getBatch().compareTo(o2.getBatch());
            }
        });
    }


    /**
     * Zobrazenia položky podla typu, aky ma dana aktivita nastavené
     * @param holder view holder
     * @param position pozicia položky
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Product element = productsList.get(position);

        holder.genre.setText(element.getDateOfMade()+ " - " + element.getDateExpiration() );
        holder.title.setText(element.getTitle()  );
        holder.batch.setText("Šarža: " + element.getBatch());

        if(type == 4){
            holder.batch.setVisibility(View.INVISIBLE);
        }

        if(type == 1){
            holder.imageButton.setVisibility(View.VISIBLE);
        }

        if(type == 2){
            holder.checkBox.setVisibility(View.VISIBLE);
            if (selectedItems.get(position))
            {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(true);

            } else {
                holder.checkBox.setChecked(false);
            }

        }

        if(type == 0) {
            if (selectedItems.get(position)) {
                holder.itemView.setBackgroundColor(Color.GRAY);
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    /**
     * Vrati velkost arraylistu zo zoznamom potravín
     * @return pocet položiek
     */

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    /**
     * Vrati filter vyhladavanie v zozname
     * @return filter
     */
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }


    /**
     * Aktualizovanie zoznamu potravín
     * @param list list vsetkých produktov
     */
    public void updateList(List<Product> list){

        productsList = new ArrayList<>();
        productsList.addAll(list);
        notifyDataSetChanged();


    }


    /**
     * Metoda pre filter na vyhladavanie v zozname
     */

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
