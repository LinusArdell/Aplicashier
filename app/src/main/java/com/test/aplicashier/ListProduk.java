package com.test.aplicashier;

import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListProduk extends AppCompatActivity {

    private RecyclerView recyclerViewProduk;
    private ProdukAdapter produkAdapter;
    private List<MainActivity.Produk> produkList = new ArrayList<>();
    private SearchView searchViewProduk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_produk);

        recyclerViewProduk = findViewById(R.id.recyclerViewProduk);
        searchViewProduk = findViewById(R.id.searchViewProduk);

        recyclerViewProduk.setLayoutManager(new LinearLayoutManager(this));

        // Tambahkan produk ke dalam list
        produkList.add(new MainActivity.Produk("8992753282401","123 BENDERA COKLAT 300G", 19600));
        produkList.add(new MainActivity.Produk("711844110069","ABC KECAP MANIS SEDANG 620ML", 17400));
        produkList.add(new MainActivity.Produk("711844120105","ABC SAMBAL MANIS PEDAS 135ML", 4200));
        // Tambahkan produk lainnya sesuai kebutuhan

        produkAdapter = new ProdukAdapter(produkList);
        recyclerViewProduk.setAdapter(produkAdapter);

        // Setup SearchView
        searchViewProduk.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                produkAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                produkAdapter.filter(newText);
                return false;
            }
        });
    }
}
