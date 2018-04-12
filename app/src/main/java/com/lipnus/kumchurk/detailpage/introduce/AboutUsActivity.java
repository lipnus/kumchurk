package com.lipnus.kumchurk.detailpage.introduce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.submenu.SubMenuControl;
import com.tsengvn.typekit.TypekitContextWrapper;



public class AboutUsActivity extends AppCompatActivity {

    ImageView logo;
    ImageView noodle;
    TextView subtitle;

    //상단, 서브메뉴 컨트롤
    SubMenuControl smc;

    //액티비티 선언.
    public static Activity AUActiviry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        logo = (ImageView) findViewById(R.id.aboutus_logo_iv);
        noodle = (ImageView) findViewById(R.id.aboutus_noodle_iv);
        subtitle = (TextView) findViewById(R.id.aboutus_subtitle);

        //로고
        Glide.with(this)
                .load( R.drawable.kumlogo2 )
                .into( logo );

        //국수
        Glide.with(this)
                .load( R.drawable.noddle_logo2 )
                .into( noodle );

        //버전정보
        subtitle.setText("Kumchurk v" + versionName());

        //다른 곳에서 이 액티비티를 조작할 수 있게 함
        AUActiviry = AboutUsActivity.this;

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //현재의 뷰(서브메뉴에 넘김)
        View thisView = this.getWindow().getDecorView();

        //서브메뉴 생성
        smc = new SubMenuControl(this, thisView, "ABOUT US", false);

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

    //로고 눌렀을 때
    public void onClick_about_logo(View v){
        YoYo.with(Techniques.Bounce)
                .duration(700)
                .playOn( logo );
    }

    //국수 눌렀을 때
    public void onClick_about_noodle(View v){
        YoYo.with(Techniques.RubberBand)
                .duration(700)
                .playOn( noodle );
    }

    //About us 눌렀을 때
    public void onClick_topmenu_title(View v){
        Toast.makeText(getApplicationContext(), "쿰척", Toast.LENGTH_SHORT).show();
    }

    //이메일
    public void onClick_aboutus_email(View v){

        Uri uri=null;

        switch (v.getId() ){
            case R.id.aboutus_sunpil:
                uri = Uri.parse("mailto:sunpil13@korea.ac.kr");
                break;

            case R.id.aboutus_tail:
                uri = Uri.parse("mailto:ktikti7679@naver.com");
                break;

            case R.id.aboutus_sueun:
                uri = Uri.parse("mailto:gleamingyou@naver.com");
                break;

            case R.id.aboutus_yongsu:
                uri = Uri.parse("mailto:paul6290@korea.ac.kr");
                break;

            case R.id.aboutus_hyunjae:
                uri = Uri.parse("mailto:aisxk@naver.com");
                break;
        }

        Intent iT = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(iT);

    }
}
