package com.example.qrmonitorfood.Aktivity;

import android.app.usage.UsageEvents;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qrmonitorfood.Database.Product;
import com.example.qrmonitorfood.Database.Zlozky;
import com.example.qrmonitorfood.IntentConstants;
import com.example.qrmonitorfood.R;

import java.util.ArrayList;
import java.util.Collection;

public class ListAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        String message = getIntent().getStringExtra(IntentConstants.INTENT_MESSAGE);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.product_list);

        ArrayList<Zlozky> questionArrayList = new ArrayList<>();
        questionArrayList.add(new Zlozky("kokos"));
        questionArrayList.add(new Zlozky("kkkkkk"));

        FAQAdapter adapter = new FAQAdapter(questionArrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra(IntentConstants.INTENT_MESSAGE, "Ukoncuji FAQ");
        setResult(RESULT_OK, intent);
        finish();

    }


    public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.QuestionViewHolder>{

        private ArrayList<Zlozky> questions;

        public FAQAdapter(ArrayList<Zlozky> questions) {
            this.questions = questions;
        }

        @Override
        public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(ListAddActivity.this)
                    .inflate(R.layout.row_product_item, parent, false);
            return new QuestionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(QuestionViewHolder holder, int position) {
            Zlozky question = questions.get(position);
            holder.question.setText(((Zlozky) question).getNazov());


        }

        @Override
        public int getItemCount() {
            return questions.size();
        }

        public class QuestionViewHolder extends RecyclerView.ViewHolder{

            public TextView question;

            public QuestionViewHolder(View itemView) {
                super(itemView);
                question = itemView.findViewById(R.id.title);
            }
        }


    }


}
