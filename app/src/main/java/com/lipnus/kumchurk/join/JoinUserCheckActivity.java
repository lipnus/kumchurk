package com.lipnus.kumchurk.join;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.IVolleyResult;
import com.lipnus.kumchurk.IntroActivity;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.VolleyConnect;
import com.lipnus.kumchurk.firebaseModel.User_fb;
import com.tsengvn.typekit.TypekitContextWrapper;

public class JoinUserCheckActivity extends AppCompatActivity {

    //Preference선언 (한번 로그인이 성공하면 자동으로 처리하는데 이용)
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    //volley, 리스너
    IVolleyResult mResultCallback = null;
    VolleyConnect volley;

    //Firebase 참조
    private Firebase mRef;

    TextView letdigit;
    ImageView logo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_loading);


        //Prefrence설정(0:읽기,쓰기가능)
        setting = getSharedPreferences("USERDATA", 0);
        editor= setting.edit();

        letdigit = (TextView) findViewById( R.id.joinLastTv );
        logo = (ImageView) findViewById(R.id.joinLastIv);


        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //connect (사용자정보를 서버로 전송)
        connect_fb_user( GlobalApplication.getUser_id() );

    }



    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }



    //파이어베이스에서 가입정보 확인
    public void connect_fb_user(final String uid){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/user/" + uid);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    //가입되어 있는 사용자
                    if(dataSnapshot.getValue( User_fb.class )!=null){

                        Log.d("URUR", "가입되어있는 사용자입니다");

                        User_fb user_fb = dataSnapshot.getValue( User_fb.class );
                        user_fb.uid = dataSnapshot.getKey();


                        //Application을 서버에서 받은 정보로 업데이트(닉네임과 프로필사진)
                        GlobalApplication.setUser_nickname( user_fb.nickname );
                        GlobalApplication.setUser_image( user_fb.profile_image );
                        GlobalApplication.setUser_thumbnail( user_fb.thumbnail );

                        //token은 firebase로 업로드
                        connect_fb_token(uid);

                        //메인으로 이동
                        userCheckEnd();


                    }else{

                        Log.d("URUR", "미가입 사용자입니다");

                        //약관동의 창을 띄운다
                        Intent iT = new Intent(getApplicationContext(), TermsAgreeActivity.class);
                        startActivity(iT);
                        finish(); //액티비티 종료
                    }


                }catch(Exception e){
                    Log.d("URUR", "에러: " + e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }



    //토큰 업로드
    public void connect_fb_token(String uid){
        //파이어베이스에 업로드
        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/user/" + uid + "/token");
        mRef.setValue( GlobalApplication.getUser_token() );
    }

//    //신규 사용자로 서버에 등록한다
//    public void connect_fb_user_upload(String uid){
//
//        User_fb user_fb = new User_fb();
//        user_fb.email = GlobalApplication.getUser_email();
//        user_fb.nickname = GlobalApplication.getUser_nickname();
//        user_fb.username = GlobalApplication.getUser_nickname();
//        user_fb.profile_image = GlobalApplication.getUser_image();
//        user_fb.thumbnail = GlobalApplication.getUser_thumbnail();
//        user_fb.token = GlobalApplication.getUser_token();
//
//
//        //파이어베이스에 업로드
//        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/user/" + uid);
//        mRef.setValue( user_fb );
//
//        //메인으로 이동
//        userCheckEnd();
//    }

    //이미가입한 사용자가 인증을 마침(신규 사용자는 약관동의하러 TermsAgreeActivity로 감)
    public void userCheckEnd(){

        //1. Application의 정보를 프레퍼런스에 저장(6개)
        editor.putString("user_id", GlobalApplication.getUser_id() );
        editor.putString("user_nickname", GlobalApplication.getUser_nickname() );
        editor.putString("user_image", GlobalApplication.getUser_image() );
        editor.putString("user_thumbnail", GlobalApplication.getUser_thumbnail() );
        editor.putString("user_email", GlobalApplication.getUser_email());
        editor.putString("user_token", GlobalApplication.getUser_token());
        editor.commit();

        //2. 메인으로 돌아간다
        Intent iT = new Intent(getApplicationContext(), IntroActivity.class);
        startActivity(iT);
        finish(); //액티비티 종료
    }


}
