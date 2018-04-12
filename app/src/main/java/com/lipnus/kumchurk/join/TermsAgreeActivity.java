package com.lipnus.kumchurk.join;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.client.Firebase;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.IntroActivity;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.detailpage.introduce.TermsPersonalActivity;
import com.lipnus.kumchurk.detailpage.introduce.TermsServiceActivity;
import com.lipnus.kumchurk.firebaseModel.User_fb;
import com.tsengvn.typekit.TypekitContextWrapper;

public class TermsAgreeActivity extends AppCompatActivity {

    //Preference선언 (한번 로그인이 성공하면 자동으로 처리하는데 이용)
    SharedPreferences setting;
    SharedPreferences.Editor editor;


    ImageView logo;
    ImageView cancel;
    Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_agree);

        logo = (ImageView) findViewById(R.id.agree_logo_iv);
        cancel = (ImageView) findViewById(R.id.agree_cancel_iv);



        //Prefrence설정(0:읽기,쓰기가능)
        setting = getSharedPreferences("USERDATA", 0);
        editor= setting.edit();

        //로고
        Glide.with(this)
                .load( R.drawable.kumlogo2 )
                .into( logo );

        //캔슬사진
        Glide.with(this)
                .load(R.drawable.cancel_blk)
                .centerCrop()
                .into(cancel);


        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    //X터치
    public void onClick_agreeFinish(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("종료하시겠어요?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //무반응
                    }
                });
        builder.show();
    }

    //동의하기 터치
    public void onClick_agress_terms(View v){

        connect_fb_user_upload( GlobalApplication.getUser_id() );
    }

    //서비스 이용약관 보기
    public void onClick_agreeSeeTerms(View v){
        Log.d("URUR", "agreeSeeTerms");
        Intent iT = new Intent(getApplicationContext(), TermsServiceActivity.class);
        startActivity(iT);
    }

    //개인정보처리방침 보기
    public void onClick_agreeSeePersonal(View v){
        Log.d("URUR", "agreeSeePersonal");
        Intent iT = new Intent(getApplicationContext(), TermsPersonalActivity.class);
        startActivity(iT);
    }

    //로고 눌렀을 때
    public void onClick_agree_logo(View v){
        YoYo.with(Techniques.Bounce)
                .duration(700)
                .playOn( logo );
    }



    //신규 사용자로 서버에 등록한다
    public void connect_fb_user_upload(String uid){

        Log.d("URUR", "TermsAgree - 서버에 등록");

        User_fb user_fb = new User_fb();
        user_fb.email = GlobalApplication.getUser_email();
        user_fb.nickname = GlobalApplication.getUser_nickname();
        user_fb.username = GlobalApplication.getUser_nickname();
        user_fb.profile_image = GlobalApplication.getUser_image();
        user_fb.thumbnail = GlobalApplication.getUser_thumbnail();
        user_fb.token = GlobalApplication.getUser_token();


        //파이어베이스에 업로드
        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/user/" + uid);
        mRef.setValue( user_fb );

        userCheckEnd();

    }

    //인증 마침
    public void userCheckEnd(){

        Log.d("URUR", "TermsAgree - 프레퍼런스에 등록");

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
