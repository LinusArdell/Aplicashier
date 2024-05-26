package com.test.aplicashier;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder> {

    private List<MainActivity.Produk> produkList;
    private List<MainActivity.Produk> produkListFull;

    public ProdukAdapter(List<MainActivity.Produk> produkList) {
        this.produkList = produkList;
        produkListFull = new ArrayList<>(produkList); // Copy of full list
    }

    @NonNull
    @Override
    public ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produk, parent, false);
        return new ProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukViewHolder holder, int position) {
        MainActivity.Produk produk = produkList.get(position);
        holder.textViewKodeQR.setText(produk.getKodeQR());
        holder.textViewNamaProduk.setText(produk.getNama());
        holder.textViewHargaProduk.setText(formatRupiah(produk.getHarga()));
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public static class ProdukViewHolder extends RecyclerView.ViewHolder {
        TextView textViewKodeQR;
        TextView textViewNamaProduk;
        TextView textViewHargaProduk;

        public ProdukViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewKodeQR = itemView.findViewById(R.id.textViewKodeQR);
            textViewNamaProduk = itemView.findViewById(R.id.textViewNamaProduk);
            textViewHargaProduk = itemView.findViewById(R.id.textViewHargaProduk);
        }
    }

    private String formatRupiah(int number) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(number).replace("Rp", "Rp ").replace(",00", "");
    }

    public void filter(String text) {
        produkList.clear();
        if (text.isEmpty()) {
            produkList.addAll(produkListFull);
        } else {
            text = text.toLowerCase();
            for (MainActivity.Produk item : produkListFull) {
                if (item.getNama().toLowerCase().contains(text)) {
                    produkList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
