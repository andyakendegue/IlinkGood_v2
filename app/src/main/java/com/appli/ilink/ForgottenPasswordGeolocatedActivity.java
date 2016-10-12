package com.appli.ilink;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ForgottenPasswordGeolocatedActivity extends AppCompatActivity {
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_TAG = "tag";
    private static final String REGISTER_URL = "http://ilink-app.com/app/";
    private Button btnModifyPassword;
    private EditText editTextPassword1;
    private EditText editTextPassword2;
    private EditText editTextPhone;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_forgotten_password_geolocated);
        this.btnModifyPassword = (Button) findViewById(R.id.btnModifyPassword);
        this.editTextPassword2 = (EditText) findViewById(R.id.editTextPassword2);
        this.editTextPassword1 = (EditText) findViewById(R.id.editTextPassword1);
        this.editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        this.btnModifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyPassword();
            }
        });
    }

    private void modifyPassword() {

        final String password1 = editTextPassword1.getText().toString().trim();
        final String password2 = editTextPassword2.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();

        // Test values
        if ((!editTextPassword1.getText().toString().equals("")) && (!editTextPassword2.getText().toString().equals("")) && (!editTextPhone.getText().toString().equals(""))) {

            if (editTextPassword1.getText().toString().length() == editTextPassword2.getText().toString().length()) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.equalsIgnoreCase(Config.LOGIN_SUCCESS)) {
                                    Toast.makeText(ForgottenPasswordGeolocatedActivity.this, "Changement de mot de passe reussi! Reconnectez-vous avec le numero de telephone et le nouveau mot de passe.", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(ForgottenPasswordGeolocatedActivity.this, LoginActivity.class);
                                    startActivity(i);

                                    finish();
                                }else {
                                    Toast.makeText(ForgottenPasswordGeolocatedActivity.this, response, Toast.LENGTH_LONG).show();

                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ForgottenPasswordGeolocatedActivity.this, "Connexion au serveur impossible", Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put(KEY_PHONE, phone);
                        params.put(KEY_PASSWORD, password1);
                        params.put(KEY_CATEGORY, "geolocated");
                        params.put(KEY_TAG, "chgpass");
                        return params;
                    }




                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Les mots de passe entres ne correspondent pas. Essayez de nouveau!", Toast.LENGTH_SHORT).show();

            }
        }  else {
            Toast.makeText(getApplicationContext(),
                    "Un ou plusieurs champs sont vides", Toast.LENGTH_SHORT).show();

        }
    }
}
