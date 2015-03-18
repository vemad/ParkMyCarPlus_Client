package com.otsims5if.pmc.pmc_android.design;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.otsims5if.pmc.pmc_android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.Density;
import api.favorite.Favorite;

/**
 * Created by Iler on 09/03/2015.
 */
public class StableArrayAdapter extends ArrayAdapter {

    HashMap<Favorite, Integer> mIdMap = new HashMap<Favorite, Integer>();
    ArrayList<Favorite> Target = new ArrayList<Favorite>();
    Map<Density, Integer> densityMap = new HashMap<Density, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId,
                              List<Favorite> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }

        //Initialize density map
        densityMap.put(Density.HIGH, R.drawable.high);
        densityMap.put(Density.MEDIUM, R.drawable.medium);
        densityMap.put(Density.LOW, R.drawable.low);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Target.size();
    }

    @Override
    public String getItem(int position) {
        // TODO Auto-generated method stub
        return (Target.get(position)).getAddress();
    }


    public Favorite getItemFromPosition(int position) {
        // TODO Auto-generated method stub
        return Target.get(position);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    public void add(Favorite item){
        Target.add(item);
    }

    public void remove(Favorite item){
        Target.remove(item);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //User super class to create the View
        View v = super.getView(position, convertView, parent);
        TextView tv = (TextView)v.findViewById(android.R.id.text1);

        tv.setTextColor(Color.BLACK);
        //v.setBackgroundColor(Color.BLACK);
        Favorite favorite = Target.get(position);
        int icon = 0;
        //Put the image on the TextView
        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, densityMap.get(favorite.getDensity()), 0);

        //Add margin between image and text (support various screen densities)
        int dp5 = (int) (5 * parent.getResources().getDisplayMetrics().density + 0.5f);
        tv.setCompoundDrawablePadding(dp5);

        return v;
    }

}
