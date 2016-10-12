package com.appli.ilink;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class Util {

    public static final class Operations {
        private Operations() throws InstantiationException {
            throw new InstantiationException("This class is not for instantiation");
        }

        public static boolean isOnline(Context context) {
            NetworkInfo netInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
    }

    private Util() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }
}
