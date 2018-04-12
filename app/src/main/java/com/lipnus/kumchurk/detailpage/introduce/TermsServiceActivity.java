package com.lipnus.kumchurk.detailpage.introduce;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.submenu.SubMenuControl;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TermsServiceActivity extends AppCompatActivity {


    //상단, 서브메뉴 컨트롤
    SubMenuControl smc;

    TextView tV;

    //액티비티 선언.
    public static Activity TSActiviry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);


        tV = (TextView) findViewById(R.id.terms_tv);

        //다른 곳에서 이 액티비티를 조작할 수 있게 함
        TSActiviry = TermsServiceActivity.this;

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //현재의 뷰(서브메뉴에 넘김)
        View thisView = this.getWindow().getDecorView();

        //서브메뉴 생성
        smc = new SubMenuControl(this, thisView, "서비스 이용약관", false);


        //파일을 읽어와서 약관에 넣음
        String termStr = readTextFile( R.raw.terms );
        tV.setText( termStr );
    }


    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }



    //서비스 이용약관 제목 터치
    public void onClicK_topmenu_title(View v){
        Toast.makeText(getApplicationContext(), "쿰척", Toast.LENGTH_SHORT).show();
    }


    //약관을 읽어온다
    public String readTextFile(int path){

        InputStream is = this.getResources().openRawResource( path );

        String data = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = is.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = is.read();
            }

            data = new String(byteArrayOutputStream.toByteArray(),"UTF-8");
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}

