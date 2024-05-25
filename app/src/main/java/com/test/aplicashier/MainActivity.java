package com.test.aplicashier;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText totalBelanjaEditText;
    private EditText jumlahBayarEditText;
    private EditText kembalianEditText;
    private TextView kembalianDetailTextView;
    private Button scanButton;
    private Button hitungButton;

    private int totalBelanja = 0;
    private Map<String, Integer> itemDetails = new HashMap<>();

    // Database produk (kode, nama, harga)
    private Map<String, Produk> database = new HashMap<String, Produk>() {{
        put("8992753282401", new Produk("8992753282401","123 BENDERA COKLAT 300G", 19600));
        put("711844110069", new Produk("711844110069","ABC KECAP MANIS SEDANG 620ML", 17400));
        put("711844120105", new Produk("711844120105","ABC SAMBAL MANIS PEDAS 135ML", 4200));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalBelanjaEditText = findViewById(R.id.totalBelanjaEditText);
        jumlahBayarEditText = findViewById(R.id.jumlahBayarEditText);
        kembalianEditText = findViewById(R.id.kembalianEditText);
        kembalianDetailTextView = findViewById(R.id.kembalianDetailTextView);
        scanButton = findViewById(R.id.scanButton);
        hitungButton = findViewById(R.id.hitungButton);

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
                new IntentIntegrator(MainActivity.this).initiateScan();
            }
        });

        hitungButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitungKembalian();
            }
        });

        // Di dalam method onCreate() di MainActivity.java
        Button listProdukButton = findViewById(R.id.buttonListProduk);
        listProdukButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListProduk.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Scan gagal atau dibatalkan
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Scan dibatalkan");
                builder.setPositiveButton("OK", null);
                builder.show();
            } else {
                // Scan berhasil
                String kode = result.getContents();
                if (database.containsKey(kode)) {
                    Produk produk = database.get(kode);
                    showJumlahDialog(produk);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Produk tidak ditemukan");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showJumlahDialog(final Produk produk) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Masukkan Jumlah");
        final EditText input = new EditText(this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                hasil.append(jumlah).append(" lembar uang ").append(formatRupiah(uang)).append("\n");
            }
        }

        return hasil.toString();
    }

    private String formatRupiah(int number) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(number).replace("Rp", "Rp ").replace(",00", "");
    }

    public static class Produk {
        private String kodeQR;
        private String nama;
        private int harga;

        public Produk(String kodeQR, String nama, int harga) {
            this.kodeQR = kodeQR;
            this.nama = nama;
            this.harga = harga;
        }

        public String getKodeQR() {
            return kodeQR;
        }

        public String getNama() {
            return nama;
        }

        public int getHarga() {
            return harga;
        }
    }
}
