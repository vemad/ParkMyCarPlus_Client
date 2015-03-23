package com.otsims5if.pmc.pmc_android;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Created by hk on 04/03/2015.
 */
public class InformationUser extends ActionBarActivity {

    ProgressBar bar;
    TextView text_score;
    TextView user_name;
    TextView user_level;
    View progress;
    ImageView icon_user;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);
        bar = (ProgressBar) findViewById(R.id.circularProgressbar);
        progress = (View) findViewById(R.id.gradient);

        text_score = (TextView) findViewById(R.id.text_score);
        user_name = (TextView) findViewById(R.id.username);
        user_level = (TextView) findViewById(R.id.niveau);
        icon_user = (ImageView) findViewById(R.id.imageView);


        Bundle extras = getIntent().getExtras();
        String name_user =  extras.getString("name");
        String score =  extras.getString("score");
        String level_name =  extras.getString("level_name");
        String start_score =  extras.getString("Start_Score");
        String NextLevelScore = "700";//extras.getString("NextLevelScore");


        user_level.setText("Niveau: "+level_name);
        text_score.setText(score+"/"+NextLevelScore+"\nPoints");
        user_name.setText("Utilisateur: " + name_user);
         //Touriste
        //Utilisateur confirmé
        //Citoyen
        //Maire //346
        //Prefet//495
        //Ministre//581
        Resources res = getResources();
        if(level_name.equals("Touriste")){icon_user.setImageResource(R.drawable.tourist);}
        if(level_name.equals("Utilisateur confirmé")){icon_user.setImageResource(R.drawable.confirmed);}
        if(level_name.equals("Citoyen")){icon_user.setImageResource(R.drawable.citizen);}
        if(level_name.equals("Maire")){icon_user.setImageResource(R.drawable.mayor);}
        if(level_name.equals("Prefet")){icon_user.setImageResource(R.drawable.prefect);}
        if(level_name.equals("Ministre")){icon_user.setImageResource(R.drawable.minister);}

        if(Integer.parseInt(score) >=(Integer.parseInt(NextLevelScore)-Integer.parseInt(NextLevelScore)/2.0)){
            bar.setProgressDrawable(res.getDrawable(R.drawable.progress_blue));
        }
        if(Integer.parseInt(score) >=(Integer.parseInt(NextLevelScore)-Integer.parseInt(NextLevelScore)/4.0)){
            bar.setProgressDrawable(res.getDrawable(R.drawable.progress_red));}

        //else --> green

        bar.setMax(Integer.parseInt(NextLevelScore));
        bar.setProgress(Integer.parseInt(score));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_undo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            System.out.println("test");
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }





    public void sendtoserver(View v) {
 /*
        Intent intent = new Intent(this, MainActivity.class);
        //Intent intent = new Intent(this, PlaceInformation.class);
        startActivity(intent);
        finishActivityFromChild(this,2);
        finish();
*/
      /*  Intent intent = new Intent(this, MainUserActivity.class);
        //Intent intent = new Intent(this, PlaceInformation.class);

        startActivity(intent);*/
        finish();
    }
}
