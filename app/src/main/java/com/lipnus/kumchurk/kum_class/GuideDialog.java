package com.lipnus.kumchurk.kum_class;

/**
 * Created by Sunpil on 2017-04-16.
 */

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lipnus.kumchurk.R;

public class GuideDialog extends Dialog {

    ImageView guideIv;

    public GuideDialog(Context context, int type) {
        super(context);

        // 지저분한(?) 다이얼 로그 제목을 날림
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 다이얼로그에 박을 레이아웃
        setContentView(R.layout.dialog_guilde);
        guideIv = (ImageView) findViewById(R.id.guilde_iv);


        //메인 세로스크롤 설명
        if(type==0){
            setContentView(R.layout.dialog_guilde);
            guideIv = (ImageView) findViewById(R.id.guilde_iv);

            Glide.with( context )
                    .load( R.drawable.guide_main_vertical)
                    .into( guideIv );
        }

        //메뉴설명 세로스크롤 가이드
        if(type==1){
            setContentView(R.layout.dialog_guilde_2);
            guideIv = (ImageView) findViewById(R.id.guilde_iv);

            Glide.with( context )
                    .load( R.drawable.guide_scroll_vertical)
                    .into( guideIv );
        }


        //중간부분을 클릭해도 없어져야함
        guideIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }

        });


    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
