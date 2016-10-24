package com.appli.ilink;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appli.ilink.app.AppController;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

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
    public static final String KEY_ACTIVE = "active";
    private static final String REGISTER_URL = "https://ilink-app.com/app/";
    private static String mdp;
    private static String phone;
    private TextView ListPays;
    private Spinner ListReseau;
    private Button buttonRegister, btnRetryConnect;
    private GoogleApiClient client;
    private String e_pays;
    private String e_reseau =null;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordRepeat;
    private EditText editTextPhone;
    private EditText firstname;
    private EditText lastname;
    private String[] paysItem;
    private String[] reseauItem;

    private TelephonyManager tm;
    private String networkCountryISO;
    private String SIMCountryISO;
    private PhoneNumberUtil util = null;

    private String TAG = "tag";
    private String pays = "";
    private List<String> listReseau ;
    String phoneSend;
    private CoordinatorLayout clRegisterSimple;
    private View sbView;
    private TextView snacktextView;
    private Snackbar snackbar;
    private MaterialDialog pDialog;



    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new MaterialDialog.Builder(RegisterSimpleActivity.this)
                    .title("Attendez svp!")
                    .content("Vérification de la connexion réseau")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                if (Util.Operations.isOnline(RegisterSimpleActivity.this)) {

                    return true;
                } else {
                     hidePDialog();
                    callSnackbar("Pas de connexion internet");


                }

            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {



            try {
                hidePDialog();
                registerUser();
            } catch (NumberParseException e) {
                hidePDialog();
                //e.printStackTrace();
                //
                callSnackbar(e.getMessage());

            }
            // Start Registering
        }

        @Override
        protected void onCancelled() {

        }
    }



    /* renamed from: com.appli.ilink.RegisterSimpleActivity.3 */
    class C15693 implements Listener<String> {
        C15693() {
        }

        public void onResponse(String response) {
            hidePDialog();
            if (response.equalsIgnoreCase(Config.LOGIN_SUCCESS)) {
                Toast.makeText(RegisterSimpleActivity.this, "Enregistrement reussi! Recuperez le code de validation qui vous a ete envoye, ensuite, reconnectez-vous avec le numero de telephone et le mot de passe specifie lors de votre enregistrement.", Toast.LENGTH_LONG).show();
                RegisterSimpleActivity.this.startActivity(new Intent(RegisterSimpleActivity.this, LoginActivity.class));
                RegisterSimpleActivity.this.finish();
                return;
            }

            callSnackbar("Probleme lors de l'enregistrement : "+response);
        }
    }

    /* renamed from: com.appli.ilink.RegisterSimpleActivity.4 */
    class C15704 implements ErrorListener {
        C15704() {
        }

        public void onErrorResponse(VolleyError error) {
            hidePDialog();
            callSnackbar("Impossible de se connecter au serveur : "+error.getMessage());
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
            params.put(KEY_FIRSTNAME, this.val$prenom);
            params.put(KEY_LASTNAME, this.val$nom);
            params.put(KEY_PASSWORD, this.val$password);
            params.put(KEY_EMAIL, this.val$email);
            params.put(KEY_PHONE, phoneSend);
            params.put(KEY_NETWORK, e_reseau);
            params.put(KEY_COUNTRY, pays);
            params.put(KEY_CATEGORY, "utilisateur");
            params.put(KEY_ACTIVE, "non");
            params.put(KEY_TAG, "register");
            return params;
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_simple);
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
        this.ListPays = (TextView) findViewById(R.id.CountryCode);
        this.ListReseau = (Spinner) findViewById(R.id.Network);
        this.listReseau = new ArrayList<String>();
        this.clRegisterSimple = (CoordinatorLayout) findViewById(R.id
                .clRegisterSimple);





        // Start display Full Country Code
        /*

        Recuperer le code iso du pays

         */
        tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        networkCountryISO = tm.getNetworkCountryIso();
        SIMCountryISO = tm.getSimCountryIso();

        //firstname.setText(networkCountryISO+SIMCountryISO);

        /*

        Recuperer le nom complet du pays et l'afficher

         */
        if(networkCountryISO.equalsIgnoreCase(SIMCountryISO)) {
            Locale loc = new Locale("",networkCountryISO);
            loc.getDisplayCountry();
            pays = loc.getDisplayCountry();

        }

        ListPays.setText(pays);

        getnetworkList();


        // End display Full Country Code
        this.client = new Builder(this).addApi(AppIndex.API).build();
    }

    public void populateSpinner() {
        ArrayAdapter<String> reseauAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listReseau);
        //Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
        reseauAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ListReseau.setAdapter(reseauAdapter);
        ListReseau.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //e_reseau = parent.getItemAtPosition(position).toString();
                e_reseau = String.valueOf(listReseau.get(position)) ;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                //e_reseau = reseauItem[0].toString();

                e_reseau = String.valueOf(listReseau.get(0)) ;

            }

        });
    }


    public void onClick(View v) {
        if (v == this.buttonRegister) {

                new UserRegisterTask().execute();

        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    private void registerUser() throws NumberParseException {
        pDialog = new MaterialDialog.Builder(RegisterSimpleActivity.this)
                .title("Attendez svp!")
                .content("Enregistrement en cours...")
                .progress(true, 0)
                .cancelable(false)
                .show();
        String prenom = this.firstname.getText().toString().trim();
        String nom = this.lastname.getText().toString().trim();
        String password = this.editTextPassword.getText().toString().trim();
        String email = this.editTextEmail.getText().toString().trim();
        String phone = this.editTextPhone.getText().toString().trim();
        String member_code = "0";
        Phonenumber.PhoneNumber phoneNumber = null;


        // Debut concatenation numero de phone avec code pays
        if (util == null) {
            util = PhoneNumberUtil.createInstance(getApplicationContext());
        }

        tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        networkCountryISO = tm.getNetworkCountryIso();

        phoneNumber = util.parse(phone, networkCountryISO.toUpperCase());
        phoneSend = util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);



        // Fin concatenation numero de phone avec code pays
        if (this.lastname.getText().toString().equals(BuildConfig.VERSION_NAME) || this.editTextPassword.getText().toString().equals(BuildConfig.VERSION_NAME) || this.firstname.getText().toString().equals(BuildConfig.VERSION_NAME) || this.editTextPhone.getText().toString().equals(BuildConfig.VERSION_NAME) || this.editTextEmail.getText().toString().equals(BuildConfig.VERSION_NAME)) {
            callSnackbar("Un ou plusieurs champs sont vides");
        } else if (this.editTextPhone.getText().toString().length() <= 4) {
            callSnackbar("Le pseudonyme doit \u00eatre au minimum de 5 caract\u00e8res");
        } else if (this.editTextPassword.getText().toString().length() != this.editTextPasswordRepeat.getText().toString().length()) {
            callSnackbar("Les mots de passe entres ne correspondent pas. Essayez de nouveau!");
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
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterSimpleActivity.this);
        builder.setTitle(getString(R.string.dialog_title));
        builder.setMessage(getString(R.string.dialog_message));
        builder.setCancelable(false);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getnetworkList();
                    }
                });

       /* String negativeText = getString(R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });*/

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    public void getnetworkList() {
        /*

        Recuperer la liste de reseaux

         */

        String tag_json_arry = "json_array_req";



        pDialog = new MaterialDialog.Builder(RegisterSimpleActivity.this)
                .title("Attendez svp!")
                .content("Recuperation de la liste des réseaux")
                .progress(true, 0)
                .cancelable(false)
                .show();
        //String url = "https://ilink-app.com/app/select/locations.php";
        String url = "https://ilink-app.com/app/select/network.php";
        Map<String, String> params = new HashMap<String, String>();

        params.put(KEY_COUNTRY, pays);
        params.put(KEY_TAG, "getuser");
        // Creating volley request obj
        CustomRequest movieReq = new CustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        hidePDialog();
                        Log.d(TAG, response.toString());


                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);


                                //In onresume fetching value from sharedpreference




                                /*Iterator<?> keys = obj.keys(); // get the keys of the jsonObject
                                while(keys.hasNext()) {
                                    //iterrate over them
                                    String key = (String)keys.next();
                                    if(obj.optString(key).trim()!=null) {

                                        String data = obj.optString(key);

                                        if (data.length()==0) {

                                        } else {
                                            listReseau.add(obj.optString(key));

                                        }


                                    } else {

                                    }

                                }
                                */


                                Iterator<?> iter = obj.keys();
                                reseauItem = new String[obj.length()];
                                for (int j = 0; j< obj.length(); j++)
                                {

                                    String key = (String) iter.next();






                                    if(obj.optString(key).trim()!=null) {

                                        String data = obj.optString(key);

                                        if (data.length()==0) {

                                        } else {

                                            if(data.equalsIgnoreCase(pays)) {

                                            } else {
                                                listReseau.add(obj.optString(key));
                                            }






                                        }


                                    } else {

                                    }


                                }
                                //Toast.makeText(RegisterActivity.this, listReseau.toString(), Toast.LENGTH_LONG).show();

                                populateSpinner();



                            } catch (JSONException e) {
                                hidePDialog();
                                showLocationDialog();
                                }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
                showLocationDialog();


            }
        });
        // Set timeout request
        movieReq.setRetryPolicy(new DefaultRetryPolicy(5*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        movieReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq, tag_json_arry);
    }

    public void callSnackbar(String errorMessage) {
        snackbar = Snackbar.make(clRegisterSimple, errorMessage, Snackbar.LENGTH_INDEFINITE).setAction("REESSAYER", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new UserRegisterTask().execute();

            }
        });
        sbView = snackbar.getView();
        snacktextView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color

        snacktextView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}
