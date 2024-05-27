package com.test.aplicashier;

import java.util.ArrayList;
import java.util.List;

public class ProdukDatabase {

    private static List<Produk> produkList = new ArrayList<>();

    static {
        produkList.add(new Produk("8992753282401", "123 BENDERA COKLAT 300G", 19600));
        produkList.add(new Produk("711844110069", "ABC KECAP MANIS SEDANG 620ML", 17400));
        produkList.add(new Produk("711844120105", "ABC SAMBAL MANIS PEDAS 135ML", 4200));
        produkList.add(new Produk("8999909192034", "2.3.4 FILTER", 86000));
        produkList.add(new Produk("711844150003", "ABC SYRUP APOLO 580ML ORANGE", 11200));
        produkList.add(new Produk("8992772122245", "ADAM SARI 1PAPAN", 24800));
        produkList.add(new Produk("8999918183085", "AIM BISC 120G BUTTER COCONUT", 2900));
        produkList.add(new Produk("8993560066116", "AIR WICK CLICK SPR 15ML LAVENDER", 15400));
        produkList.add(new Produk("9555540000979", "ALFREDO 100G SGR FREE ALMOND", 18500));
        produkList.add(new Produk("8993417374234", "ESKULIN KIDS F.BODY WASH 175ML SPRI", 11600));
        produkList.add(new Produk("9556852990323", "EVER D.COOKIES 454G DANISH(BR MUDA)", 27000));
    }

    public static List<Produk> getProdukList() {
        return produkList;
    }
}
