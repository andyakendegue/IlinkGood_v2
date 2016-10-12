package com.appli.ilink;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TamponActivity extends AppCompatActivity {
    public static final String KEY_EMAIL = "email";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_TAG = "tag";
    public Button btnRetry;

    /* renamed from: com.appli.ilink.TamponActivity.1 */
    class C15801 implements OnClickListener {
        C15801() {
        }

        public void onClick(View view) {
            try {
                TamponActivity.this.user();
            } catch (JSONException e) {
                Toast.makeText(TamponActivity.this, "impossible de recuperer vos informations", Toast.LENGTH_LONG).show();
            }
        }
    }

    /* renamed from: com.appli.ilink.TamponActivity.2 */
    class C15812 implements Listener<JSONArray> {
        final /* synthetic */ SharedPreferences val$sharedPreferences;

        C15812(SharedPreferences sharedPreferences) {
            this.val$sharedPreferences = sharedPreferences;
        }

        public void onResponse(JSONArray response) {
            try {
                JSONObject obj = response.getJSONObject(0);
                Editor editor = this.val$sharedPreferences.edit();
                editor.putString(TamponActivity.KEY_EMAIL, obj.getString(TamponActivity.KEY_EMAIL));
                editor.putString(RegisterSimpleActivity.KEY_LASTNAME, obj.getString(RegisterSimpleActivity.KEY_LASTNAME));
                editor.putString(RegisterSimpleActivity.KEY_FIRSTNAME, obj.getString(RegisterSimpleActivity.KEY_FIRSTNAME));
                editor.putString(TamponActivity.KEY_PHONE, obj.getString(TamponActivity.KEY_PHONE));
                editor.putString(RegisterSimpleActivity.KEY_CATEGORY, obj.getString(RegisterSimpleActivity.KEY_CATEGORY));
                editor.putString(RegisterSimpleActivity.KEY_COUNTRY, obj.getString(RegisterSimpleActivity.KEY_COUNTRY));
                editor.putString(RegisterSimpleActivity.KEY_NETWORK, obj.getString(RegisterSimpleActivity.KEY_NETWORK));
                editor.putString(RegisterSimpleActivity.KEY_MEMBER_CODE, obj.getString(RegisterSimpleActivity.KEY_MEMBER_CODE));
                editor.putString(RegisterSimpleActivity.KEY_VALIDATE, obj.getString("active"));
                editor.putString(Config.VALIDATION_CODE_SHARED_PREF, obj.getString(Config.VALIDATION_CODE_SHARED_PREF));
                editor.putString(TamponActivity.KEY_LATITUDE, obj.getString(TamponActivity.KEY_LATITUDE));
                editor.putString(TamponActivity.KEY_LONGITUDE, obj.getString(TamponActivity.KEY_LONGITUDE));
                editor.commit();
                if (obj.getString("active").equalsIgnoreCase("non") && obj.getString(RegisterSimpleActivity.KEY_CATEGORY).equalsIgnoreCase("utilisateur")) {
                    TamponActivity.this.startActivity(new Intent(TamponActivity.this, ActivateActivity.class));
                    TamponActivity.this.finish();
                } else if (obj.getString("active").equalsIgnoreCase("oui") && obj.getString(RegisterSimpleActivity.KEY_CATEGORY).equalsIgnoreCase("utilisateur")) {
                    TamponActivity.this.startActivity(new Intent(TamponActivity.this, MapsActivity.class));
                    TamponActivity.this.finish();
                } else {
                    Toast.makeText(TamponActivity.this, "Impossible de vous connecter. Veuillez redemarrer l'application", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(TamponActivity.this, "Impossible de recuperer vos donnees", Toast.LENGTH_LONG).show();
                TamponActivity.this.btnRetry.setVisibility(View.VISIBLE);
            }
        }
    }

    /* renamed from: com.appli.ilink.TamponActivity.3 */
    class C15823 implements ErrorListener {
        C15823() {
        }

        public void onErrorResponse(VolleyError error) {
            Toast.makeText(TamponActivity.this, "Impossible de se connecter au serveur", Toast.LENGTH_LONG).show();
            TamponActivity.this.btnRetry.setVisibility(View.VISIBLE);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView((int) R.layout.activity_tampon);
        this.btnRetry = (Button) findViewById(R.id.button_retry);
        this.btnRetry.setVisibility(View.INVISIBLE);
        this.btnRetry.setOnClickListener(new C15801());
        try {
            user();
        } catch (JSONException e) {
            Toast.makeText(this, "impossible de recuperer vos informations", Toast.LENGTH_LONG).show();
        }
    }

    private void user() throws JSONException {
        String tag_json_arry = "json_array_req";
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, 0);
        String phone = sharedPreferences.getString(KEY_PHONE, "Not Available");
        Map<String, String> params = new HashMap();
        params.put(KEY_PHONE, phone);
        params.put(KEY_TAG, "getuser");
        Volley.newRequestQueue(this).add(new CustomRequest(1, "http://ilink-app.com/app/select/users_simple.php", params, new C15812(sharedPreferences), new C15823()));
    }
}
