package com.appli.ilink;

import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterSimpleActivity extends AppCompatActivity implements OnClickListener {
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_COUNTRY = "country_code";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_MEMBER_CODE = "member_code";
    public static final String KEY_NETWORK = "network";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_TAG = "tag";
    public static final String KEY_VALIDATE = "validate";
    private static final String REGISTER_URL = "http://ilink-app.com/app/";
    private static String mdp;
    private static String phone;
    private Spinner ListPays;
    private Spinner ListReseau;
    private Button buttonRegister;
    private GoogleApiClient client;
    private String e_pays;
    private String e_reseau;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordRepeat;
    private EditText editTextPhone;
    private EditText firstname;
    private EditText lastname;
    private String latitude;
    private LocationManager locationManager;
    private String longitude;
    private String[] paysItem;
    private String[] reseauItem;

    /* renamed from: com.appli.ilink.RegisterSimpleActivity.1 */
    class C15671 implements OnItemSelectedListener {
        C15671() {
        }

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            RegisterSimpleActivity.this.e_pays = parent.getItemAtPosition(position).toString();
            if (RegisterSimpleActivity.this.e_pays.equals("Burkina-Faso")) {
                RegisterSimpleActivity.this.reseauItem = RegisterSimpleActivity.this.getResources().getStringArray(R.array.network_burkina);
            } else if (RegisterSimpleActivity.this.e_pays.equals("Cameroun")) {
                RegisterSimpleActivity.this.reseauItem = RegisterSimpleActivity.this.getResources().getStringArray(R.array.network_cameroun);
            } else if (RegisterSimpleActivity.this.e_pays.equals("France")) {
                RegisterSimpleActivity.this.reseauItem = RegisterSimpleActivity.this.getResources().getStringArray(R.array.network_france);
            } else if (RegisterSimpleActivity.this.e_pays.equals("Gabon")) {
                RegisterSimpleActivity.this.reseauItem = RegisterSimpleActivity.this.getResources().getStringArray(R.array.network_gabon);
            }
            List<String> listReseau = new ArrayList();
            for (Object add : RegisterSimpleActivity.this.reseauItem) {
                listReseau.add(String.valueOf(add));
            }
            ArrayAdapter<String> reseauAdapter = new ArrayAdapter(RegisterSimpleActivity.this, android.R.layout.simple_spinner_item, listReseau);
            reseauAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            RegisterSimpleActivity.this.ListReseau.setAdapter(reseauAdapter);
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
            RegisterSimpleActivity.this.e_pays = RegisterSimpleActivity.this.paysItem[0].toString();
        }
    }

    /* renamed from: com.appli.ilink.RegisterSimpleActivity.2 */
    class C15682 implements OnItemSelectedListener {
        C15682() {
        }

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            RegisterSimpleActivity.this.e_reseau = parent.getItemAtPosition(position).toString();
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
            RegisterSimpleActivity.this.e_reseau = RegisterSimpleActivity.this.reseauItem[0].toString();
        }
    }

    /* renamed from: com.appli.ilink.RegisterSimpleActivity.3 */
    class C15693 implements Listener<String> {
        C15693() {
        }

        public void onResponse(String response) {
            if (response.equalsIgnoreCase(Config.LOGIN_SUCCESS)) {
                Toast.makeText(RegisterSimpleActivity.this, "Enregistrement reussi! Recuperez le code de validation qui vous a ete envoye, ensuite, reconnectez-vous avec le numero de telephone et le mot de passe specifie lors de votre enregistrement.", Toast.LENGTH_LONG).show();
                RegisterSimpleActivity.this.startActivity(new Intent(RegisterSimpleActivity.this, LoginActivity.class));
                RegisterSimpleActivity.this.finish();
                return;
            }
            Toast.makeText(RegisterSimpleActivity.this, "Probleme lors de l'enregistrement", Toast.LENGTH_LONG).show();
        }
    }

    /* renamed from: com.appli.ilink.RegisterSimpleActivity.4 */
    class C15704 implements ErrorListener {
        C15704() {
        }

        public void onErrorResponse(VolleyError error) {
            Toast.makeText(RegisterSimpleActivity.this, "Impossible de se connecter au serveur", Toast.LENGTH_LONG).show();
        }
    }

    /* renamed from: com.appli.ilink.RegisterSimpleActivity.5 */
    class C15715 extends StringRequest {
        final /* synthetic */ String val$email;
        final /* synthetic */ String val$nom;
        final /* synthetic */ String val$password;
        final /* synthetic */ String val$phone;
        final /* synthetic */ String val$prenom;

        C15715(int x0, String x1, Listener x2, ErrorListener x3, String str, String str2, String str3, String str4, String str5) {
            super(x0, x1, x2, x3);
            this.val$prenom = str;
            this.val$nom = str2;
            this.val$password = str3;
            this.val$email = str4;
            this.val$phone = str5;

        }

        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap();
            params.put(RegisterSimpleActivity.KEY_FIRSTNAME, this.val$prenom);
            params.put(RegisterSimpleActivity.KEY_LASTNAME, this.val$nom);
            params.put(RegisterSimpleActivity.KEY_PASSWORD, this.val$password);
            params.put(RegisterSimpleActivity.KEY_EMAIL, this.val$email);
            params.put(RegisterSimpleActivity.KEY_PHONE, this.val$phone);
            params.put(RegisterSimpleActivity.KEY_NETWORK, RegisterSimpleActivity.this.e_reseau);
            params.put(RegisterSimpleActivity.KEY_MEMBER_CODE, "0");
            params.put(RegisterSimpleActivity.KEY_LATITUDE, RegisterSimpleActivity.this.latitude);
            params.put(RegisterSimpleActivity.KEY_LONGITUDE, RegisterSimpleActivity.this.longitude);
            params.put(RegisterSimpleActivity.KEY_COUNTRY, RegisterSimpleActivity.this.e_pays);
            params.put(RegisterSimpleActivity.KEY_CATEGORY, "utilisateur");
            params.put(RegisterSimpleActivity.KEY_VALIDATE, "non");
            params.put(RegisterSimpleActivity.KEY_TAG, "register");
            return params;
        }
    }

    public RegisterSimpleActivity() {
        this.e_pays = "Gabon";
        this.ListPays = null;
        this.ListReseau = null;
        this.latitude = "0";
        this.longitude = "0";
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_register_simple);
        Intent in = getIntent();
        phone = in.getStringExtra(KEY_PHONE);
        mdp = in.getStringExtra(KEY_PASSWORD);
        getSupportActionBar().hide();
        this.firstname = (EditText) findViewById(R.id.firstname);
        this.lastname = (EditText) findViewById(R.id.lastname);
        this.editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        this.editTextPassword.setText(mdp);
        this.editTextPasswordRepeat = (EditText) findViewById(R.id.editTextPasswordRepeat);
        this.editTextPasswordRepeat.setText(mdp);
        this.editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        this.editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        this.editTextPhone.setText(phone);
        this.buttonRegister = (Button) findViewById(R.id.buttonRegister);
        this.buttonRegister.setOnClickListener(this);
        this.ListPays = (Spinner) findViewById(R.id.CountryCode);
        List<String> listePays = new ArrayList();
        this.paysItem = getResources().getStringArray(R.array.country_code);
        for (Object add : this.paysItem) {
            listePays.add(String.valueOf(add));
        }
        ArrayAdapter<String> paysAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listePays);
        paysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.ListPays.setAdapter(paysAdapter);
        this.ListPays.setOnItemSelectedListener(new C15671());
        this.ListReseau = (Spinner) findViewById(R.id.Network);
        List<String> listReseau = new ArrayList();
        this.reseauItem = getResources().getStringArray(R.array.network_gabon);
        for (Object add2 : this.reseauItem) {
            listReseau.add(String.valueOf(add2));
        }
        ArrayAdapter<String> reseauAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listReseau);
        reseauAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.ListReseau.setAdapter(reseauAdapter);
        this.ListReseau.setOnItemSelectedListener(new C15682());
        this.client = new Builder(this).addApi(AppIndex.API).build();
    }

    public void onClick(View v) {
        if (v == this.buttonRegister) {
            registerUser();
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    private void registerUser() {
        String prenom = this.firstname.getText().toString().trim();
        String nom = this.lastname.getText().toString().trim();
        String password = this.editTextPassword.getText().toString().trim();
        String email = this.editTextEmail.getText().toString().trim();
        String phone = this.editTextPhone.getText().toString().trim();
        String member_code = "0";
        if (this.lastname.getText().toString().equals(BuildConfig.VERSION_NAME) || this.editTextPassword.getText().toString().equals(BuildConfig.VERSION_NAME) || this.firstname.getText().toString().equals(BuildConfig.VERSION_NAME) || this.editTextPhone.getText().toString().equals(BuildConfig.VERSION_NAME) || this.editTextEmail.getText().toString().equals(BuildConfig.VERSION_NAME)) {
            Toast.makeText(getApplicationContext(), "Un ou plusieurs champs sont vides", Toast.LENGTH_SHORT).show();
        } else if (this.editTextPhone.getText().toString().length() <= 4) {
            Toast.makeText(getApplicationContext(), "Le pseudonyme doit \u00eatre au minimum de 5 caract\u00e8res", Toast.LENGTH_SHORT).show();
        } else if (this.editTextPassword.getText().toString().length() != this.editTextPasswordRepeat.getText().toString().length()) {
            Toast.makeText(getApplicationContext(), "Les mots de passe entres ne correspondent pas. Essayez de nouveau!", Toast.LENGTH_SHORT).show();
        } else if (this.latitude == null || this.longitude == null) {
            Toast.makeText(this, "Pas encore localise!", Toast.LENGTH_LONG).show();
        } else {
            Volley.newRequestQueue(this).add(new C15715(1, REGISTER_URL, new C15693(), new C15704(), prenom, nom, password, email, phone));
        }
    }

    public void onStart() {
        super.onStart();
        this.client.connect();
        AppIndex.AppIndexApi.start(this.client, Action.newAction(Action.TYPE_VIEW, "Register Page", Uri.parse("http://host/path"), Uri.parse("android-app://com.appli.ilink/http/host/path")));
    }

    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(this.client, Action.newAction(Action.TYPE_VIEW, "Register Page", Uri.parse("http://host/path"), Uri.parse("android-app://com.appli.ilink/http/host/path")));
        this.client.disconnect();
    }
}
