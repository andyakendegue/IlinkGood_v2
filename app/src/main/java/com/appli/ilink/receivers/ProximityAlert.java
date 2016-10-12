package com.appli.ilink.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ProximityAlert extends BroadcastReceiver {
    public static final String EVENT_ID_INTENT_EXTRA = "EventIDIntentExtraKey";

    public void onReceive(Context context, Intent intent) {
        Log.v("gauntface", "Proximity Alert Intent Received, eventID = " + intent.getLongExtra(EVENT_ID_INTENT_EXTRA, -1));
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
