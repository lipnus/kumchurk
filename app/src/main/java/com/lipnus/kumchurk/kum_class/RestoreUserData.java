package com.lipnus.kumchurk.kum_class;

import android.content.Context;
import android.content.SharedPreferences;

import com.lipnus.kumchurk.GlobalApplication;

/**
 * Created by Sunpil on 2017-06-10.
 */

public class RestoreUserData {

    Context context;

    public RestoreUserData(Context context){
        this.context = context;
    }

    //에러가 나서 어플리케이션의 정보가 날아갔을때, 프레퍼런스에서 읽어와서 어플리케이션의 값을 복구해준다
    public void restoreApplication(){

        //Preference선언 (한번 로그인이 성공하면 자동으로 처리하는데 이용)
        SharedPreferences setting;
        SharedPreferences.Editor editor;

        //Prefrence설정(0:읽기,쓰기가능)
        setting = context.getSharedPreferences("USERDATA", 0);
        editor= setting.edit();

        //프레퍼런스에서 정보를 읽어와서 Application에 저장
        GlobalApplication.setUser_id(setting.getString("user_id", "2013210059"));
        GlobalApplication.setUser_nickname(setting.getString("user_nickname", "유령"));
        GlobalApplication.setUser_image(setting.getString("user_image", "image_error"));
        GlobalApplication.setUser_thumbnail(setting.getString("user_thumbnail", "thumbnail_error"));
        editor.commit();

    }

}
