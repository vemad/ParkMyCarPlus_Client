package com.otsims5if.pmc.pmc_android;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import api.user.GetUserCallback;
import api.user.User;
import api.user.UserServices;
import api.user.SignupCallback;


public class CreateNewUser extends ActionBarActivity {

    private ImageView userProfil;
    private ImageView leftArrow;
    private ImageView rightArrow;
    private boolean isManProfil = true;

    private EditText username;
    private EditText textPassword;
    private EditText passwordConfirmation;

    private Exception CreateException;
    private String message_returned = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        message_returned = "qqq";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);
        userProfil = (ImageView) findViewById(R.id.imageProfil);
        leftArrow = (ImageView) findViewById(R.id.left);
        rightArrow = (ImageView) findViewById(R.id.right);

        username = (EditText) findViewById(R.id.username);
        textPassword = (EditText) findViewById(R.id.textPassword);
        passwordConfirmation = (EditText) findViewById(R.id.passwordConfirmation);

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

    private class CreateLogin  extends SignupCallback {
        protected void callback(Exception e,String msg){
            CreateException = e;
            message_returned = msg;
            System.out.println("Exception e :" +e);
            System.out.println("message_returned2  :" +message_returned);

        }
    }



    public void sendtoserver(View v) {

        final Intent intent = new Intent(this, MainUserActivity.class);
        //Intent intent = new Intent(this, PlaceInformation.class);
        final String password = textPassword.getText().toString();
        final String passConfirmation = passwordConfirmation.getText().toString();
        final String user = username.getText().toString();
        System.out.println(passConfirmation +" "+password);

        //new user
        if(password.equals(passConfirmation)){
            System.out.println("test");
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    UserServices.getInstance().signup(user, password, new CreateLogin()).execute();
                }

                @Override
                protected Void doInBackground(Void... arg0) {return null;}
                @Override
                protected void onPostExecute(Void result) {
                    System.out.println("message: " + message_returned);
                    intent.putExtra("name_user", user);
                  //  startActivity(intent);
                    finish();
                }

        };task.execute((Void[]) null);
        }else {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.new_user_toast,
            (ViewGroup) findViewById(R.id.layoutToast));
            Toast toast = new Toast(getApplicationContext());
            toast.setView(view);
            toast.show();
        }

    }
}
