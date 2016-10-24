package com.appli.ilink;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    private TextView textView;

    /* renamed from: com.appli.ilink.ProfileActivity.1 */
    class C15561 implements OnClickListener {
        C15561() {
        }

        public void onClick(DialogInterface arg0, int arg1) {
            Editor editor = ProfileActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, 0).edit();
            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
            editor.putString(TamponGeolocatedActivity.KEY_EMAIL, BuildConfig.VERSION_NAME);
            editor.commit();
            ProfileActivity.this.startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            ProfileActivity.this.finish();
        }
    }

    /* renamed from: com.appli.ilink.ProfileActivity.2 */
    class C15572 implements OnClickListener {
        C15572() {
        }

        public void onClick(DialogInterface arg0, int arg1) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.textView = (TextView) findViewById(R.id.textView);
        this.textView.setText("Current User: " + getSharedPreferences(Config.SHARED_PREF_NAME, 0).getString(TamponGeolocatedActivity.KEY_EMAIL, "Not Available"));
    }

    private void logout() {
        Builder alertDialogBuilder = new Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes", new C15561());
        alertDialogBuilder.setNegativeButton("No", new C15572());
        alertDialogBuilder.create().show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuLogout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }
}
