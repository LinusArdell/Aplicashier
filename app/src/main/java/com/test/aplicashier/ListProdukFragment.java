package com.test.aplicashier;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListProdukFragment extends Fragment {

    private RecyclerView recyclerViewProduk;
    private ProdukAdapter produkAdapter;
    private List<Produk> produkList;
    private SearchView searchViewProduk;
    private DatabaseReference databaseProduk;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_produk, container, false);

        recyclerViewProduk = view.findViewById(R.id.recyclerViewProduk);
        searchViewProduk = view.findViewById(R.id.searchViewProduk);

        recyclerViewProduk.setLayoutManager(new LinearLayoutManager(getContext()));

        produkList = new ArrayList<>();

        produkAdapter = new ProdukAdapter(produkList);
        recyclerViewProduk.setAdapter(produkAdapter);

        databaseProduk = FirebaseDatabase.getInstance().getReference("produk");
        databaseProduk.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produkList.clear();
                for (DataSnapshot produkSnapshot : dataSnapshot.getChildren()) {
                    Produk produk = produkSnapshot.getValue(Produk.class);
                    produkList.add(produk);
                }
                produkAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Gagal mengambil data produk", Toast.LENGTH_LONG).show();
            }
        });

        searchViewProduk.setIconifiedByDefault(false);
        searchViewProduk.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return false;
            }
        });

        return view;
    }

    private void searchList(String text) {
        List<Produk> searchList = new ArrayList<>();
        for (Produk produk : produkList) {
            if (produk.getNama().toLowerCase().contains(text.toLowerCase()) ||
                    produk.getKode().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(produk);
            }
        }
        produkAdapter.searchDataList(searchList);
    }
}
