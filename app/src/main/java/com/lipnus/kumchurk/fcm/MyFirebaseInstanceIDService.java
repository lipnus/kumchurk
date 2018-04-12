package com.lipnus.kumchurk.fcm;

/**
 * Created by Sunpil on 2017-05-27.
 */

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.lipnus.kumchurk.GlobalApplication;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FCM";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //임시로 토큰을 어플리케이션에 저장(초기화면에서 이걸 이용해서 서버에 업로드)
        GlobalApplication.setUser_token(refreshedToken);

        //여기서 서버로 보내도 되는데, 회원정보 업데이트하면서 같이 보낸다
        //sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String token) {

        //Prefrence설정(0:읽기,쓰기가능)
//        SharedPreferences setting = getSharedPreferences("USERDATA", 0);
//        SharedPreferences.Editor editor = setting.edit();
//        editor.putString("user_tocken", token );
//        editor.commit();
    }
}
