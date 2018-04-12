package com.lipnus.kumchurk.detailpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lipnus.kumchurk.R;
import com.tsengvn.typekit.TypekitContextWrapper;

import uk.co.senab.photoview.PhotoView;

public class ProfileImgActivity extends AppCompatActivity {

    //닉네임과 프로필 이미지
    String nickName;
    String imagePath;

    //프사가 나올 이미지뷰
    PhotoView profilePv;

    //X표
    ImageView cancelIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_img);

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        profilePv = (PhotoView) findViewById(R.id.profile_photoview);
        cancelIv = (ImageView) findViewById(R.id.profile_cancel_iv);



        //앞의 엑티비티로부터 닉네임과 프로필사진 경로를 받는다
        Intent iT = getIntent();
        nickName = iT.getExtras().getString("nickname");
        imagePath = iT.getExtras().getString("image_path");

        //프로필사진
        Glide.with(this)
                .load(imagePath)
                .placeholder(R.drawable.noimg)
                .centerCrop()
                .thumbnail(0.3f)
                .into(profilePv);
        profilePv.setScaleType(ImageView.ScaleType.FIT_XY);

        //캔슬사진
        Glide.with(this)
                .load(R.drawable.cancel)
                .centerCrop()
                .into(cancelIv);
        cancelIv.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }


    public void onClick_profileFinish(View v){
        finish();
    }


}
