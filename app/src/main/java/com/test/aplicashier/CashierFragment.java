package com.test.aplicashier;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CashierFragment extends Fragment {

    private EditText totalBelanjaEditText;
    private EditText jumlahBayarEditText;
    private EditText kembalianEditText;
    private TextView kembalianDetailTextView;
    private Button scanButton;
    private Button hitungButton;
    private Button buttonListProduk;

    private int totalBelanja = 0;
    private Map<String, Integer> itemDetails = new HashMap<>();

    private DatabaseReference databaseProduk;
    private List<Produk> produkList = new ArrayList<>(); // List untuk menyimpan data produk dari Firebase

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cashier, container, false);

        totalBelanjaEditText = view.findViewById(R.id.totalBelanjaEditText);
        jumlahBayarEditText = view.findViewById(R.id.jumlahBayarEditText);
        kembalianEditText = view.findViewById(R.id.kembalianEditText);
        kembalianDetailTextView = view.findViewById(R.id.kembalianDetailTextView);
        scanButton = view.findViewById(R.id.scanButton);
        hitungButton = view.findViewById(R.id.hitungButton);
        buttonListProduk = view.findViewById(R.id.buttonListProduk);

        // Initialize Firebase reference
        databaseProduk = FirebaseDatabase.getInstance().getReference("produk");

        // Fetch products from Firebase
        fetchProductsFromFirebase();

        buttonListProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllInputs();
            }
        });

        jumlahBayarEditText.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    jumlahBayarEditText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp,.\\s]", "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.0;
                    }

                    String formatted = formatRupiah((int) parsed);

                    current = formatted;
                    jumlahBayarEditText.setText(formatted);
                    jumlahBayarEditText.setSelection(formatted.length());

                    jumlahBayarEditText.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(CashierFragment.this);
                integrator.setPrompt("Scan a barcode or QR code");
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        hitungButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitungKembalian();
            }
        });

        return view;
    }

    private void fetchProductsFromFirebase() {
        databaseProduk.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produkList.clear();
                for (DataSnapshot produkSnapshot : dataSnapshot.getChildren()) {
                    Produk produk = produkSnapshot.getValue(Produk.class);
                    produkList.add(produk);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(getContext(), "Gagal mengambil data produk", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Scan gagal atau dibatalkan
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Scan dibatalkan");
                builder.setPositiveButton("OK", null);
                builder.show();
            } else {
                // Scan berhasil
                String kode = result.getContents();
                Produk produk = getProdukByKode(kode);
                if (produk != null) {
                    showJumlahDialog(produk);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Produk tidak ditemukan");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Produk getProdukByKode(String kode) {
        for (Produk produk : produkList) { // Iterate through the Firebase product list
            if (produk.getKode().equals(kode)) {
                return produk;
            }
        }
        return null;
    }

    private void showJumlahDialog(final Produk produk) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Masukkan Qty : ");
        final EditText input = new EditText(getActivity());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int jumlah = Integer.parseInt(input.getText().toString());
                int totalHarga = produk.getHarga() * jumlah;
                totalBelanja += totalHarga;
                totalBelanjaEditText.setText(formatRupiah(totalBelanja));
                itemDetails.put(produk.getNama(), jumlah);
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void hitungKembalian() {
        try {
            String bayarStr = jumlahBayarEditText.getText().toString().replace("Rp ", "").replace(".", "").replace(",", "");
            int bayar = Integer.parseInt(bayarStr);
            if (bayar < totalBelanja) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Jumlah bayar kurang dari total belanja");
                builder.setPositiveButton("OK", null);
                builder.show();
                return;
            }

            int kembalian = bayar - totalBelanja;
            kembalianEditText.setText(formatRupiah(kembalian));

            // Rincian kembalian
            kembalianDetailTextView.setText(rincianKembalian(kembalian));
        } catch (NumberFormatException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Masukkan jumlah bayar yang valid");
            builder.setPositiveButton("OK", null);
            builder.show();
        }
    }

    private String rincianKembalian(int kembalian) {
        StringBuilder hasil = new StringBuilder();
        int[] pecahan = {100000, 50000, 20000, 10000, 5000, 2000, 1000, 500, 200, 100};

        for (int uang : pecahan) {
            if (kembalian >= uang) {
                int jumlah = kembalian / uang;
                kembalian %= uang;
                hasil.append(jumlah).append("x uang ").append(formatRupiah(uang)).append("\n");
            }
        }

        return hasil.toString();
    }

    private String formatRupiah(int number) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(number).replace("Rp", "Rp ").replace(",00", "");
    }

    private void clearAllInputs() {
        totalBelanja = 0;
        totalBelanjaEditText.setText("");
        jumlahBayarEditText.setText("");
        kembalianEditText.setText("");
        kembalianDetailTextView.setText("");
        itemDetails.clear();
    }
}
