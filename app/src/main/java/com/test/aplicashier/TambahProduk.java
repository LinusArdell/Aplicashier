package com.test.aplicashier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.Locale;

public class TambahProduk extends AppCompatActivity {

    private EditText editTextKodeBarcode;
    private EditText editTextNamaProduk;
    private EditText editTextHargaProduk;
    private Button buttonTambahProduk;

    private DatabaseReference databaseProduk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_produk);

        // Mengaktifkan up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseProduk = FirebaseDatabase.getInstance().getReference("produk");

        editTextKodeBarcode = findViewById(R.id.editTextKodeBarcode);
        editTextNamaProduk = findViewById(R.id.editTextNamaProduk);
        editTextHargaProduk = findViewById(R.id.editTextHargaProduk);
        buttonTambahProduk = findViewById(R.id.buttonTambahProduk);

        buttonTambahProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahProduk();
            }
        });

        editTextHargaProduk.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    editTextHargaProduk.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp,.\\s]", "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.0;
                    }

                    String formatted = formatRupiah((int) parsed);

                    current = formatted;
                    editTextHargaProduk.setText(formatted);
                    editTextHargaProduk.setSelection(formatted.length());

                    editTextHargaProduk.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // This will handle the Up button
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // This will handle the selected menu item, including the Up button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void tambahProduk() {
        String kode = editTextKodeBarcode.getText().toString().trim();
        String nama = editTextNamaProduk.getText().toString().trim();
        String hargaStr = editTextHargaProduk.getText().toString().replace("Rp ", "").replace(".", "").replace(",", "").trim();

        if (!TextUtils.isEmpty(kode) && !TextUtils.isEmpty(nama) && !TextUtils.isEmpty(hargaStr)) {
            int harga = Integer.parseInt(hargaStr);

            String id = databaseProduk.push().getKey();

            Produk produk = new Produk(kode, nama, harga);

            if (id != null) {
                databaseProduk.child(kode).setValue(produk);
                Toast.makeText(this, "Produk berhasil ditambahkan", Toast.LENGTH_LONG).show();
                clearInputs();
            } else {
                Toast.makeText(this, "Terjadi kesalahan, coba lagi", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Silakan isi semua field", Toast.LENGTH_LONG).show();
        }
    }

    private void clearInputs() {
        editTextKodeBarcode.setText("");
        editTextNamaProduk.setText("");
        editTextHargaProduk.setText("");
    }

    private String formatRupiah(int number) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(number).replace("Rp", "Rp ").replace(",00", "");
    }
}
