package com.test.aplicashier;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class ListProdukFragment extends Fragment {

    private RecyclerView recyclerViewProduk;
    private ProdukAdapter produkAdapter;
    private List<Produk> produkList = new ArrayList<>();
    private SearchView searchViewProduk;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_produk, container, false);

        recyclerViewProduk = view.findViewById(R.id.recyclerViewProduk);
        searchViewProduk = view.findViewById(R.id.searchViewProduk);

        recyclerViewProduk.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tambahkan produk ke dalam list
        produkList.add(new Produk("8992753282401","123 BENDERA COKLAT 300G", 19600));
        produkList.add(new Produk("711844110069","ABC KECAP MANIS SEDANG 620ML", 17400));
        produkList.add(new Produk("711844120105","ABC SAMBAL MANIS PEDAS 135ML", 4200));
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

        return view;
    }
}
