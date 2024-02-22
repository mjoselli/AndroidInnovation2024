package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    RecyclerView mainRecyclerView;
    Button addButton;
    ProductArrayAdapter adapter = new ProductArrayAdapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        addButton = findViewById(R.id.addButton);
        mainRecyclerView.setAdapter(adapter);
        mainRecyclerView.setLayoutManager(
                new LinearLayoutManager(MainActivity.this)
        );
        for (int i = 0; i < 20; i++) {
            Product p = new Product();
            p.name = "Item " + i;
            p.quantity = i;
            Singleton.getInstance().products.add(p);
        }
        addButton.setOnClickListener(view -> {
            Product p = new Product();
            p.name = "New Item";
            p.quantity = 5;
            Singleton.getInstance().products.add(p);
            adapter.notifyItemInserted(Singleton.getInstance().products.size()-1);
        });
        adapter.setClickListener(new ProductArrayAdapter.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Product p = Singleton.getInstance().products.get(position);
                p.name = p.name + " clicked";
                adapter.notifyItemChanged(position);
            }
            @Override
            public void onItemLongClick(View view, int position) {
                Singleton.getInstance().products.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
    }
}