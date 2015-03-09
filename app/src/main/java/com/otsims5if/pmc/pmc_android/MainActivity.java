package com.otsims5if.pmc.pmc_android;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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


public class MainActivity extends ActionBarActivity implements OnClickListener {

    private ProgressBar spinner;
    private Context context;
    private ProgressDialog loadingDialog;
    private Button connectionButton;
    private LinearLayout loginBox;
    private MainUserActivity mainUserActivity;
    private RelativeLayout.LayoutParams portraitLayout;


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
        ImageView imgLogo = (ImageView) findViewById(R.id.imageLogo);
        imgLogo.setPadding(0,getResources().getConfiguration().screenHeightDp/2, 0, 0);

        loginBox = (LinearLayout) findViewById(R.id.loginBox);
        if(portraitLayout==null){
            portraitLayout = (RelativeLayout.LayoutParams) loginBox.getLayoutParams();
        }
        loginBox.setVisibility(View.GONE);


        Animation animTranslate  = AnimationUtils.loadAnimation(MainActivity.this, R.anim.translate);
        animTranslate.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) { }

            @Override
            public void onAnimationRepeat(Animation arg0) { }

            @Override
            public void onAnimationEnd(Animation arg0) {
                loginBox.setVisibility(View.VISIBLE);
                Animation animFade  = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade);
                loginBox.startAnimation(animFade);
            }
        });
        imgLogo.startAnimation(animTranslate);


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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen for landscape and portrait and set portrait mode always
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            //android:layout_below="@+id/imageLogo"
            //android:layout_centerHorizontal="true">

            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            p.addRule(RelativeLayout.BELOW, R.id.imageLogo);
            p.addRule(RelativeLayout.CENTER_HORIZONTAL);
            loginBox.setLayoutParams(p);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

            loginBox.setLayoutParams(portraitLayout);
        }
    }
}
