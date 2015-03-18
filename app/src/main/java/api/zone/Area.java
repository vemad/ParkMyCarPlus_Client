package api.zone;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.Gradient;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import api.Density;

/**
 * Created by Iler on 17/03/2015.
 */
public class Area {
    private String id;
    protected LatLng center;
    protected List<LatLng> points;
    protected Density density;
    protected float intensity;
    private int randGreen = (int)(255*Math.random());
    private int randRed = (int)(255*Math.random());
    private int randBlue = (int)(255*Math.random());
    private int[] colorsOrange = {
            Color.argb(0, randRed, randGreen, randBlue), // green opaq
            Color.argb(255, randRed, randGreen, randBlue)    // green
    };

    float[] startPoints = {
            0, 1f
    };

    Gradient gradientRand;

    public Area(){
        this.points = new ArrayList<>();
        this.gradientRand = new Gradient(colorsOrange,startPoints);
    }

    public Area(String id){
        this.id = id;
        this.points = new ArrayList<>();
        this.gradientRand = new Gradient(colorsOrange,startPoints);
    }

    public String getId() {
        return id;
    }

    public void addPoints(LatLng point){
        points.add(point);
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void reorganizePoints(){
        //int halfNumber = points.size()/2;
        int i=1;
        Stack<LatLng> stack = new Stack<>();
        List<LatLng> newList = new ArrayList<>();
        newList.add(points.get(0));
        for(i=1; i<points.size(); i++){
            //if(i<halfNumber-1) {
            boolean isInTheSameLine = false;
            try{
                if(points.get(i).longitude==newList.get(newList.size()-1).longitude
                        && points.get(i).longitude==points.get(i+1).longitude) {
                    isInTheSameLine = true;
                }

            }catch(Exception e){

            }
            if(!isInTheSameLine){
                if (i % 2 == 0) {
                    newList.add(points.get(i));
                } else {
                    stack.push(points.get(i));
                }
                //i++;
                //}
            }
        }
        while(!stack.isEmpty()){
            newList.add(stack.pop());
        }
        points = newList;
    }

    public Gradient getGradientRand() {
        return gradientRand;
    }
}
