package com.lipnus.kumchurk.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FcmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String name = intent.getAction();

        Log.d("FCM","받음?");

        if(name.equals("com.lipnus.kumchurk")){

        }

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
