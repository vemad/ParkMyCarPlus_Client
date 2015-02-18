package com.otsims5if.pmc.pmc_android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;


public class MainActivity extends ActionBarActivity implements OnClickListener {

    private ProgressBar spinner;
    private Context context;
    private ProgressDialog loadingDialog;
    private Button connectionButton;
    private MainUserActivity mainUserActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        connectionButton = (Button) findViewById(R.id.button);
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

    public void displayUserInterface() {

        Intent intent = new Intent(this, MainUserActivity.class);
        //Intent intent = new Intent(this, PlaceInformation.class);

        startActivity(intent);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
