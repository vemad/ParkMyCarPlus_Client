package com.otsims5if.pmc.pmc_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Iler on 18/02/2015.
 */
public class ProfilFragment extends PlaceholderFragment{

    public ProfilFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profil, container, false);
        return rootView;
    }
}
