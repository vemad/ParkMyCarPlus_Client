package com.otsims5if.pmc.pmc_android;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import java.util.Locale;

import api.authentification.AuthentificationServices;


public class MainActivity extends ActionBarActivity implements OnClickListener {

    private ProgressBar spinner;
    private Context context;
    private ProgressDialog loadingDialog;
    private Button connectionButton;

    private MainUserActivity mainUserActivity;


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        connectionButton = (Button) findViewById(R.id.connectButton);
        connectionButton.setOnClickListener(this);


        /*button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //spinner.setVisibility(View.VISIBLE);
                ProgressDialog mDialog = new ProgressDialog(getApplicationContext());
                mDialog.setMessage("Loading...");
                mDialog.setCancelable(false);
                mDialog.show();
            }
        });*/


    }

    ///



    public void displayUserInterface() {

        Intent intent = new Intent(this, MainUserActivity.class);
        //Intent intent = new Intent(this, PlaceInformation.class);

        startActivity(intent);

    }

    public void newUserInterface(View view) {

        Intent intent = new Intent(this, CreateNewUser.class);
        startActivity(intent);
        //  finish();
    }



    @Override
    protected void onDestroy() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            connectionButton.setEnabled(true);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
            v.setEnabled(false);
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    loadingDialog = new ProgressDialog(context);
                    loadingDialog.setTitle("Authentification en cours");
                    loadingDialog.setMessage("Veuillez patienter...");
                    loadingDialog.setCancelable(false);
                    loadingDialog.setIndeterminate(true);
                    loadingDialog.show();
                    AuthentificationServices.getInstance().authentificate("username", "password", null/*callback*/).execute();
                }

                @Override
                protected Void doInBackground(Void... arg0) {
                    try {
                        //Do something...
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if (loadingDialog != null) {
                        loadingDialog.dismiss();
                        connectionButton.setEnabled(true);
                        displayUserInterface();
                    }
                }

            };
            task.execute((Void[]) null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }




}
