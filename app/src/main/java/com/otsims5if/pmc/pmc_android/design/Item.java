package com.otsims5if.pmc.pmc_android.design;

/**
 * Created by Iler on 04/03/2015.
 */
public class Item{
    public final String text;
    public final int icon;
    public Item(String text, Integer icon) {
        this.text = text;
        this.icon = icon;
    }
    @Override
    public String toString() {
        return text;
    }
}
