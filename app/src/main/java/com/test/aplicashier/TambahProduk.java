package com.test.aplicashier;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    }

    private void tambahProduk() {
        String kode = editTextKodeBarcode.getText().toString().trim();
        String nama = editTextNamaProduk.getText().toString().trim();
        String hargaStr = editTextHargaProduk.getText().toString().trim();

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
}