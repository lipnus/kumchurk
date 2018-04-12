package com.lipnus.kumchurk.fcm;

import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sunpil on 2017-08-27.
 */

public class SendFcm  {




    public static void sendFcmData(final String token, final String title, final String text, final String img){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // FMC 메시지 생성 start
                    //===============================================================================
                    JSONObject root = new JSONObject();
                    JSONObject data = new JSONObject();

                    data.put("title", title);
                    data.put("text", text);
                    data.put("img", img);

                    root.put("data", data);
                    root.put("to", token);

                    Log.d("FCM", "보냄: " + root.toString());
                    //===============================================================================
                    // FMC 메시지 생성 end


                    final String SERVER_KEY = "AAAAZlK8U4E:APA91bHj9JHWw1QySeeLVCjjOqx8m6-Au5rW2JaB1" +
                            "ea7v00EZt4qZYqAHtIa8fulnhrtYXk-MKp6Q1SRWKkcO4VX8Z4mNtxDNCfzSTSFWgrcmdY4" +
                            "rRvHCtp1iOG_KnY4op7lH37h31He";
                    final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";

                    URL Url = new URL(FCM_MESSAGE_URL);
                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-type", "application/json");
                    OutputStream os = conn.getOutputStream();
                    os.write(root.toString().getBytes("utf-8"));
                    os.flush();
                    conn.getResponseCode();



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
