package com.otsims5if.pmc.pmc_android;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


public class CreateNewUser extends ActionBarActivity {

    private ImageView userProfil;
    private ImageView leftArrow;
    private ImageView rightArrow;
    private boolean isManProfil = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);
        userProfil = (ImageView) findViewById(R.id.imageProfil);
        leftArrow = (ImageView) findViewById(R.id.left);
        rightArrow = (ImageView) findViewById(R.id.right);

        leftArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeProfilType();
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeProfilType();
            }
        });

    }

    public void changeProfilType(){
        if(isManProfil) {
            userProfil.setImageResource(R.drawable.girl);
        }else{
            userProfil.setImageResource(R.drawable.man);
        }
        isManProfil = !isManProfil;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_create_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
        Intent intent = new Intent(this, MainUserActivity.class);
        //Intent intent = new Intent(this, PlaceInformation.class);

        startActivity(intent);
        finish();
    }
}
