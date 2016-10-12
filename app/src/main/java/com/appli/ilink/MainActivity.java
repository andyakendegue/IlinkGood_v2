package com.appli.ilink;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btnHome1;
    private Button btnHome2;
    private Button btnHome3;
    private Button btnHome4;
    private Button btnHome5;
    private Button btnHome6;
    private boolean registered;

    public MainActivity() {

        this.registered = false;

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.fragment_accueil);
        getSupportActionBar().hide();
        this.btnHome1 = (Button) findViewById(R.id.btnHome1);
        this.btnHome2 = (Button) findViewById(R.id.btnHome2);
        this.btnHome3 = (Button) findViewById(R.id.btnHome3);
        this.btnHome6 = (Button) findViewById(R.id.btnHome6);
        btnHome1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }

        });

        btnHome2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //In onresume fetching value from sharedpreference
                SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                //Fetching the boolean value form sharedpreferences
                registered = sharedPreferences.getBoolean(Config.REGISTERED_SHARED_PREF, false);

                //If we will get true
                if(registered){
                    //Starting login activity
                    Intent intent = new Intent(MainActivity.this,LoginGeolocatedActivity.class);
                    startActivity(intent);
                }else {
                    geolocated();
                }


                /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage("Souhaitez-vous être localisé?");
                alertDialogBuilder.setPositiveButton("Oui",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {



                                //Intent i = new Intent(MainActivity.this, LocalisationActivity.class);
                                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                                startActivity(i);
                            }
                        });

                alertDialogBuilder.setNegativeButton("Non",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //Intent i = new Intent(MainActivity.this, LocalisationActivity.class);
                                Intent i = new Intent(MainActivity.this, RegisterSimpleActivity.class);
                                startActivity(i);

                            }
                        });

                //Showing the alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                */



            }

        });

        btnHome3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(MainActivity.this, AddSuperviseurActivity.class);
                Intent i = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(i);

            }

        });

        /*btnHome4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);

            }

        });

        btnHome5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);

            }

        });
        */

        btnHome6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(i);

            }

        });

    }

    //Logout function
    private void geolocated(){
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Que souhaitez vous faire?");
        alertDialogBuilder.setPositiveButton("Se Connecter",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        /*SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean(Config.REGISTERED_SHARED_PREF, true);


                        //Saving the sharedpreferences
                        editor.commit();*/

                        //Starting login activity
                        Intent intent = new Intent(MainActivity.this, LoginGeolocatedActivity.class);
                        startActivity(intent);
                        //finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("S'enregistrer",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
//Starting login activity
                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        //finish();
                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}
