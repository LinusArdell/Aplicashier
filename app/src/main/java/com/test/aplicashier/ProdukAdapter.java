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
    private List<Produk> produkListFull;

    public ProdukAdapter(List<Produk> produkList) {
        this.produkList = produkList;
        produkListFull = new ArrayList<>(produkList);
    }

    @NonNull
    @Override
    public ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produk, parent, false);
        return new ProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukViewHolder holder, int position) {
        Produk produk = produkList.get(position);
        holder.textViewKodeQR.setText(produk.getKode());
        holder.textViewNamaProduk.setText(produk.getNama());
        holder.textViewHargaProduk.setText(String.format("Rp %,d", produk.getHarga()));
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public void filter(String text) {
        produkList.clear();
        if (text.isEmpty()) {
            produkList.addAll(produkListFull);
        } else {
            text = text.toLowerCase();
            for (Produk produk : produkListFull) {
                if (produk.getNama().toLowerCase().contains(text)) {
                    produkList.add(produk);
                }
            }
        }
        notifyDataSetChanged();
    }

    class ProdukViewHolder extends RecyclerView.ViewHolder {
        TextView textViewKodeQR, textViewNamaProduk, textViewHargaProduk;

        ProdukViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewKodeQR = itemView.findViewById(R.id.textViewKodeQR);
            textViewNamaProduk = itemView.findViewById(R.id.textViewNamaProduk);
            textViewHargaProduk = itemView.findViewById(R.id.textViewHargaProduk);
        }
    }
}
