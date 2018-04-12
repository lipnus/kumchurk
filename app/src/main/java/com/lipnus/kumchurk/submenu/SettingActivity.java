package com.lipnus.kumchurk.submenu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.lipnus.kumchurk.IntroActivity;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.detailpage.introduce.AboutUsActivity;
import com.lipnus.kumchurk.detailpage.introduce.TermsPersonalActivity;
import com.lipnus.kumchurk.detailpage.introduce.TermsServiceActivity;
import com.lipnus.kumchurk.kum_class.CustomDialog;
import com.tsengvn.typekit.TypekitContextWrapper;

public class SettingActivity extends AppCompatActivity {

    //그림 회전 다이얼로그
    CustomDialog customDialog;

    //상단, 서브메뉴 컨트롤
    SubMenuControl smc;

    //버전표시 텍스트
    TextView versionTv;

    //Preference선언 (한번 로그인이 성공하면 자동으로 처리하는데 이용)
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    Context context;

    public static Activity STActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //현재의 뷰(서브메뉴에 넘김)
        View thisView = this.getWindow().getDecorView();

        //서브메뉴 생성
        smc = new SubMenuControl(this, thisView, "SETTING");

        //다른 곳에서 이 액티비티를 조작할 수 있게 함
        STActivity = SettingActivity.this;

        //버전명 표시
        versionTv = (TextView) findViewById(R.id.setting_version_tv);
        versionTv.setText("현재버전 v" + versionName() );

        context = this;

    }

    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    //버전체크
    public String versionName(){
        PackageInfo pi = null;
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return pi.versionName;
    }

    public void onClick_setting_personal_info(View v){
        Intent iT = new Intent(getApplicationContext(), TermsPersonalActivity.class);
        startActivity(iT);
    }

    public void onClick_setting_rule(View v){
        Intent iT = new Intent(getApplicationContext(), TermsServiceActivity.class);
        startActivity(iT);
    }

    public void onClick_setting_about(View v){
        Intent iT = new Intent(this, AboutUsActivity.class);
        startActivity(iT);
    }

    public void onClick_setting_version(View v){
        //애니매이션
        YoYo.with(Techniques.RubberBand)
                .duration(400)
                .playOn( versionTv );
    }

    public void onClick_setting_logout(View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage( "로그아웃 하시겠어요?" );
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //다이얼로그 켜기
                        customDialog = new CustomDialog(context);
                        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //다이얼로그 뒷배경투명처리
                        customDialog.setCancelable(true);
                        customDialog.show(); // 보여주기

                        //카카오톡 로그아웃
                        UserManagement.requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                Log.d("URUR", "로그아웃");

                                customDialog.dismiss();
                                preferenceLogout();
                            }
                        });
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();

    }


    //로그아웃 처리(프레퍼런스)
    public void preferenceLogout(){

        //Prefrence설정(0:읽기,쓰기가능)
        setting = getSharedPreferences("USERDATA", 0);
        editor= setting.edit();

        //noId로 해놓으면 introActivity에서 로그인 안한걸로 체크된다
        editor.putString("user_id", "noId" );
        editor.commit();

        Intent iT = new Intent(this, IntroActivity.class);
        iT.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        iT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(iT);
    }
}
