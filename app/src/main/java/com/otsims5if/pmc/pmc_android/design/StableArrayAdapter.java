package com.otsims5if.pmc.pmc_android.design;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Iler on 09/03/2015.
 */
public class StableArrayAdapter extends ArrayAdapter<String> {

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    ArrayList<String> Target = new ArrayList<String>();

    public StableArrayAdapter(Context context, int textViewResourceId,
                              List<String> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Target.size();
    }

    @Override
    public String getItem(int position) {
        // TODO Auto-generated method stub
        return Target.get(position);
    }

    @Override
    public int getPosition(String item) {
        // TODO Auto-generated method stub
        return super.getPosition(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void add(String item){
        Target.add(item);
    }

}
