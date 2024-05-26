package com.test.aplicashier;

public class Produk {
    private String kode;
    private String nama;
    private int harga;

    public Produk(String kode, String nama, int harga) {
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
    }

    public String getKode() {
        return kode;
    }

    public String getNama() {
        return nama;
    }

    public int getHarga() {
        return harga;
    }
}