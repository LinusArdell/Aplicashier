package com.test.aplicashier;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder> {

    private List<Produk> produkList;
    private List<Produk> produkListFull; // Full list for search filtering

    public ProdukAdapter(List<Produk> produkList) {
        this.produkList = produkList;
        produkListFull = new ArrayList<>(produkList); // Copy of full list
    }

    @NonNull
    @Override
    public ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produk, parent, false);
        return new ProdukViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukViewHolder holder, int position) {
        Produk produk = produkList.get(position);
        holder.kodeTextView.setText(produk.getKode());
        holder.namaTextView.setText(produk.getNama());
        holder.hargaTextView.setText(String.valueOf(produk.getHarga()));
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public void searchDataList(List<Produk> searchList) {
        produkList = searchList;
        notifyDataSetChanged();
    }

    class ProdukViewHolder extends RecyclerView.ViewHolder {
        TextView kodeTextView, namaTextView, hargaTextView;

        ProdukViewHolder(View itemView) {
            super(itemView);
            kodeTextView = itemView.findViewById(R.id.textViewKodeQR);
            namaTextView = itemView.findViewById(R.id.textViewNamaProduk);
            hargaTextView = itemView.findViewById(R.id.textViewHargaProduk);
        }
    }
}

