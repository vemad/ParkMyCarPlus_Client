package com.otsims5if.pmc.pmc_android;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by hk on 04/03/2015.
 */
public class InformationUser extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);
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
