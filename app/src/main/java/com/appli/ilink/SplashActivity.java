package com.appli.ilink;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.games.GamesStatusCodes;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT;

    /* renamed from: com.appli.ilink.SplashActivity.1 */
    class C15761 implements Runnable {
        C15761() {
        }

        public void run() {
            SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
            SplashActivity.this.finish();
        }
    }

    static {
        SPLASH_TIME_OUT = GamesStatusCodes.STATUS_ACHIEVEMENT_UNLOCK_FAILURE;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new C15761(), (long) SPLASH_TIME_OUT);
    }
}
