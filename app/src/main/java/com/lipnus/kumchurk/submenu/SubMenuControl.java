package com.lipnus.kumchurk.submenu;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.detailpage.CommentActivity;
import com.lipnus.kumchurk.detailpage.DetailReveiwActivity;
import com.lipnus.kumchurk.detailpage.ProfileModifyActivity;
import com.lipnus.kumchurk.detailpage.introduce.AboutUsActivity;
import com.lipnus.kumchurk.detailpage.introduce.TermsPersonalActivity;
import com.lipnus.kumchurk.detailpage.introduce.TermsServiceActivity;

import static com.lipnus.kumchurk.R.id.slide_menu_iv1;
import static com.lipnus.kumchurk.R.id.slide_menu_iv2;
import static com.lipnus.kumchurk.R.id.slide_menu_iv3;
import static com.lipnus.kumchurk.R.id.slide_menu_iv4;
import static com.lipnus.kumchurk.R.id.slide_menu_iv5;
import static com.lipnus.kumchurk.R.id.slide_menu_iv6;
import static com.lipnus.kumchurk.R.id.slide_menu_iv7;
import static com.lipnus.kumchurk.R.id.slide_menu_iv8;

/**
 * Created by Sunpil on 2017-05-06.
 */

public class SubMenuControl {

    Context context;

    //상단메뉴
    ImageView topRightIv;
    ImageView topLeftIv;
    TextView topTitleTv;

    //서브메뉴
    ImageView left_profileIv;
    TextView left_profileTv;
    ImageView[] leftIv = new ImageView[8];

    //음영 & 슬라이드 메뉴
    LinearLayout slideShadowLr;
    LinearLayout slidePageLr;

    //왼쪽, 오른쪽 이동 애니매이션 객체
    Animation translateLeftAnim;
    Animation translateRightAnim;

    //석삼유무
    boolean isSubMenu;


    //슬라이드메뉴가 나와있는지 들어갔는지
    boolean isPageOpen = false;

    String title;

    //생성자
    public SubMenuControl(Context context, View v, String title) {
        this.context = context;
        this.title = title;

        //석삼메뉴사용
        isSubMenu = true;

        topMenuInit(context, v, title);
        subMenuInit(context, v);
        animationInit(context, v);

        clickEvent_top();
        clickEvent_sub(v, context);
    }

    //생성자2 - 삼자메뉴가 없음
    public SubMenuControl(Context context, View v, String title, boolean isSubMenu) {
        this.context = context;
        this.title = title;

        //석삼메뉴 사용하지 않음
        this.isSubMenu = isSubMenu;

        topMenuInit(context, v, title);

        //뒤로버튼
        topLeftIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });
    }



    //최상단메뉴초기화
    public void topMenuInit(Context context, View v, String title){

        topRightIv = (ImageView) v.findViewById(R.id.topmenu_rightIv);
        topLeftIv = (ImageView) v.findViewById(R.id.topmenu_leftIv);
        topTitleTv = (TextView) v.findViewById(R.id.topmenu_titleTv);

        //뒤로가기 화살표
        Glide.with(context)
                .load( R.drawable.backarrow )
                .into(topLeftIv);

        //상단메뉴 이름
        topTitleTv.setText(title);
        //석삼
        Glide.with(context)
                .load( R.drawable.menu_black )
                .into(topRightIv);




        //석삼메뉴를 안쓰는경우
        if(isSubMenu==false){
            topRightIv.setVisibility(View.GONE);
        }



    }

    //슬라이드메뉴 초기화
    public void subMenuInit(Context context, View v){

        //슬라이드메뉴와 음영부분 레이아웃
        slideShadowLr = (LinearLayout) v.findViewById(R.id.slidingShadowLr);
        slidePageLr = (LinearLayout) v.findViewById(R.id.slidingMenuLr);

        //프사부분
        left_profileIv = (ImageView) v.findViewById(R.id.slide_profile_circle_iv);
        left_profileTv = (TextView) v.findViewById(R.id.slide_profile_tv);

//        //동그라미 프사
//        Glide.with(context)
//                .load( GlobalApplication.getUser_thumbnail() )
//                .placeholder(R.drawable.loading)
//                .bitmapTransform(new CropCircleTransformation(context))
//                .thumbnail(0.3f)
//                .into(left_profileIv);
//
//        //아이디
//        left_profileTv.setText( GlobalApplication.getUser_nickname() );

        left_profileIv.setVisibility(View.GONE);
        left_profileTv.setVisibility(View.GONE);

        //=========================================================
        //아래쪽 정사각형 그림으로 된 메뉴부분
        //=========================================================

        //할당
        leftIv[0] = (ImageView) v.findViewById(slide_menu_iv1);
        leftIv[1] = (ImageView) v.findViewById(slide_menu_iv2);
        leftIv[2] = (ImageView) v.findViewById(slide_menu_iv3);
        leftIv[3] = (ImageView) v.findViewById(slide_menu_iv4);
        leftIv[4] = (ImageView) v.findViewById(slide_menu_iv5);
        leftIv[5] = (ImageView) v.findViewById(slide_menu_iv6);
        leftIv[6] = (ImageView) v.findViewById(slide_menu_iv7);
        leftIv[7] = (ImageView) v.findViewById(slide_menu_iv8);

        //그림들의 경로(글라이드로 그림을 넣음)
        int[] leftPic = new int[8];
        leftPic[0] = R.drawable.left_menu1;
        leftPic[1] = R.drawable.left_menu2;
        leftPic[2] = R.drawable.left_menu3;
        leftPic[3] = R.drawable.left_menu4;
        leftPic[4] = R.drawable.left_menu5;
        leftPic[5] = R.drawable.left_menu6;
        leftPic[6] = R.drawable.left_menu7;
        leftPic[7] = R.drawable.left_menu8;

        for(int i=0; i<8; i++){
            Glide.with(context)
                    .load( leftPic[i] )
                    .centerCrop()
                    .into(leftIv[i]);
        }


    }

    //애니매이션 초기화
    public void animationInit(Context context, View v){

        translateLeftAnim = AnimationUtils.loadAnimation(context, R.anim.main_translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(context, R.anim.main_translate_right);

        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);


    }


    //상단, 검은 음영부분 클릭이벤트
    public void clickEvent_top(){


        //석삼 메뉴버튼
        topRightIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isPageOpen ==false){
                    slidePageLr.setVisibility(View.VISIBLE);
                    slidePageLr.startAnimation(translateLeftAnim);

                }else if(isPageOpen ==true){
                    slideShadowLr.setVisibility(View.GONE);
                    slidePageLr.startAnimation(translateRightAnim);
                }

            }
        });

        //뒤로버튼
        topLeftIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });


        //서브메뉴 나왔을 때 검은색 음영
        slideShadowLr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideShadowLr.setVisibility(View.GONE);
                slidePageLr.startAnimation(translateRightAnim);
            }
        });


    }

    //우측메뉴 클릭이벤트
    public void clickEvent_sub(View v, Context context){

        final Context c = context;

        //첫번째(메인메뉴)
        ((ImageView)v.findViewById(R.id.slide_menu_iv1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                finishActivity();

            }
        });

        //두번째(뉴스피드)
        ((ImageView)v.findViewById(R.id.slide_menu_iv2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iT = new Intent(c, NewsFeedActivity.class);
                c.startActivity(iT);
                finishActivity();
            }
        });

        //세번째()
        ((ImageView)v.findViewById(R.id.slide_menu_iv3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iT = new Intent(c, ReviewSearchActivity.class);
                c.startActivity(iT);
                finishActivity();

            }
        });

        //네번째()
        ((ImageView)v.findViewById(R.id.slide_menu_iv4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iT = new Intent(c, PersonalSpaceActivity.class);
                iT.putExtra("uid", GlobalApplication.getUser_id());
                c.startActivity(iT);
                finishActivity();

            }
        });

        //다섯번째()
        ((ImageView)v.findViewById(R.id.slide_menu_iv5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        //여섯번째()
        ((ImageView)v.findViewById(R.id.slide_menu_iv6)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iT = new Intent(c, AlarmActivity.class);
                c.startActivity(iT);
                finishActivity();

            }
        });

        //일곱번째()
        ((ImageView)v.findViewById(R.id.slide_menu_iv7)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iT = new Intent(c, SettingActivity.class);
                c.startActivity(iT);
                finishActivity();

            }
        });

        //여덟번째()
        ((ImageView)v.findViewById(R.id.slide_menu_iv8)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });




    }

    //취소버튼 눌렀을때
    public void backPress(){

        Log.d("SBSB", "backPress() 끝내기");

        //메뉴가 열려있으면 닫음
        if(isPageOpen==true){
            slideShadowLr.setVisibility(View.GONE);
            slidePageLr.startAnimation(translateRightAnim);
        }else if(isPageOpen==false){

            //현재 액티비티를 닫음
            finishActivity();
        }
    }


    //닫기설정
    public void finishActivity(){

        Log.d("SBSB", "finishActivity() 끝내기");

        //뉴스피드 닫음
        if(title.equals("NEWSFEED")==true){
            NewsFeedActivity nfActivity = (NewsFeedActivity) NewsFeedActivity.NFActiviry;
            nfActivity.finish();

            //애니매이션
            nfActivity.overridePendingTransition(R.anim.fadein, R.anim.translate_right);
        }

        //소식 닫음
        else if(title.equals("ALARM")==true){
            AlarmActivity alActivity = (AlarmActivity) AlarmActivity.ALActiviry;
            alActivity.finish();

            //애니매이션
            alActivity.overridePendingTransition(R.anim.fadein, R.anim.translate_right);
        }

        //댓글닫음
        else if(title.equals("COMMENT")==true){
            CommentActivity cmActivity = (CommentActivity) CommentActivity.CMActiviry;
            cmActivity.finish();

            //애니매이션
            cmActivity.overridePendingTransition(R.anim.fadein, R.anim.translate_right);
        }

        //설정 닫음
        else if(title.equals("SETTING")==true){
            SettingActivity stActivity = (SettingActivity) SettingActivity.STActivity;
            stActivity.finish();

            //애니매이션
            stActivity.overridePendingTransition(R.anim.fadein, R.anim.translate_right);
        }

        //리뷰닫음
        else if(title.equals("REVIEW")==true){
            DetailReveiwActivity drActivity = (DetailReveiwActivity) DetailReveiwActivity.DRActiviry;
            drActivity.finish();

            //애니매이션
            drActivity.overridePendingTransition(R.anim.fadein, R.anim.translate_right);
        }

        //나만의 공간
        else if(title.equals("PERSONAL SPACE")==true){
            PersonalSpaceActivity rvActivity = (PersonalSpaceActivity) PersonalSpaceActivity.PSActiviry;
            rvActivity.finish();

            //애니매이션
            rvActivity.overridePendingTransition(R.anim.fadein, R.anim.translate_right);
        }

        //프로필수정 닫음
        else if(title.equals("PROFILE")==true){
            ProfileModifyActivity pmActivity = (ProfileModifyActivity) ProfileModifyActivity.PMActiviry;
            pmActivity.finish();

            //애니매이션
            pmActivity.overridePendingTransition(R.anim.fadein, R.anim.translate_right);
        }

        //About us
        else if(title.equals("ABOUT US")==true){
            AboutUsActivity auActivity = (AboutUsActivity) AboutUsActivity.AUActiviry;
            auActivity.finish();

            //애니매이션
            auActivity.overridePendingTransition(R.anim.fadein, R.anim.translate_right);
        }

        //서비스 이용약관
        else if(title.equals("서비스 이용약관")==true){
            TermsServiceActivity tmActivity = (TermsServiceActivity) TermsServiceActivity.TSActiviry;
            tmActivity.finish();

            //애니매이션
            tmActivity.overridePendingTransition(R.anim.fadein, R.anim.translate_right);
        }

        //개인정보처리방침
        else if(title.equals("개인정보처리방침")==true){
            TermsPersonalActivity tpActivity = (TermsPersonalActivity) TermsPersonalActivity.TPActiviry;
            tpActivity.finish();

            //애니매이션
            tpActivity.overridePendingTransition(R.anim.fadein, R.anim.translate_right);
        }
    }

    //애니매이션 리스너
    private class SlidingPageAnimationListener implements Animation.AnimationListener{
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(isPageOpen==true){
                slidePageLr.setVisibility(View.GONE);
                isPageOpen=false;
            }
            else{
                slideShadowLr.setVisibility(View.VISIBLE);
                isPageOpen = true;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
