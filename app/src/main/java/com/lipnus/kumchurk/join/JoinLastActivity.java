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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.IVolleyResult;
import com.lipnus.kumchurk.MainActivity;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.VolleyConnect;
import com.lipnus.kumchurk.firebaseModel.MainData_fb;
import com.lipnus.kumchurk.firebaseModel.MenuInfo_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;
import com.lipnus.kumchurk.kum_class.MenuRecommend;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.List;

public class JoinLastActivity extends AppCompatActivity {

    //Preference선언 (한번 로그인이 성공하면 자동으로 처리하는데 이용)
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    //volley, 리스너
    IVolleyResult mResultCallback = null;
    VolleyConnect volley;

    //Firebase 참조
    private Firebase mRef;

    //리뷰가 몇개인지(콜백의 끝나는 지점을 알기위해 필요)
    int reviewCount;
    int reviewCountCompare;

    //Firebase에서 받은 원본이 저장되는 리스트
    List<ResInfo_fb> resInfo_fbList;
    List<MenuInfo_fb> menuInfo_fbList;
    List<MenuReview_fb> menuReview_fbList;

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

        YoYo.with(Techniques.FadeOut)
                .repeat(30)
                .duration(1000)
                .delay(300)
                .playOn( logo );


        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Firebase로부터 데이터를 받아온다
        connect_fb_menuReview();

    }




    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }


    //Firebase 접속
    public void connect_fb_menuReview(){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_review" );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    //리뷰갯수 초기화
                    reviewCount = 0;
                    reviewCountCompare = 0;

                    //저장공간 초기화
                    resInfo_fbList = new ArrayList<>();
                    menuInfo_fbList = new ArrayList<>();
                    menuReview_fbList = new ArrayList<>();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren() ) {

                        MenuReview_fb menuReview_fb = postSnapshot.getValue( MenuReview_fb.class );
                        menuReview_fb.key = postSnapshot.getKey();


                        boolean condition1 = menuReview_fb.heart.size() > 2 && menuReview_fb.fuck.size()<3 &&
                                Integer.parseInt(menuReview_fb.taste) > 0;

                        boolean condition2 = menuReview_fb.uid.equals("kumchurk"); //쿰척이가 쓴거면 프리패스

                        // 필터링(좋아요3개이상, 빠큐2개이하, 맛 보통이상)
                        if(condition1 || condition2){

                            Log.d("SSFF", "리뷰: " + menuReview_fb.key);
                            reviewCount++;
                            connect_fb_menuInfo(menuReview_fb);
                        }
                    }
                }catch(Exception e){
                    Log.d("SSFF", "에러: " + e);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void connect_fb_menuInfo(final MenuReview_fb menuReview_fb){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_info/" + menuReview_fb.menu_id );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{
                    MenuInfo_fb menuInfo_fb = dataSnapshot.getValue( MenuInfo_fb.class );
                    menuInfo_fb.key = dataSnapshot.getKey();

                    Log.d("SSFF", "메뉴");

                    //리뷰를 찾는다
                    connect_fb_resInfo(menuReview_fb, menuInfo_fb);

                }catch (Exception e){
                    Log.d("SSFF", "메뉴에러: " + e + " *리뷰키: " + menuReview_fb.key + " *메뉴키: " + menuReview_fb.menu_id);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void connect_fb_resInfo(final MenuReview_fb menuReview_fb, final MenuInfo_fb menuInfo_fb){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/res_info/" + menuInfo_fb.res_id );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                reviewCountCompare++;

                ResInfo_fb resInfo_fb = dataSnapshot.getValue( ResInfo_fb.class );
                resInfo_fb.key = dataSnapshot.getKey();

                Log.d("SSFF", "식당: " + resInfo_fb.res_name);


                //리스트에 입력(3개가 동일한 인덱스에 저장)
                resInfo_fbList.add(resInfo_fb);
                menuInfo_fbList.add(menuInfo_fb);
                menuReview_fbList.add(menuReview_fb);

                //다운로드 끝
                if(reviewCount == reviewCountCompare){

                    Log.d("SSFF", "다운로드 끝!");
//                    Toast.makeText(getApplicationContext(), "끝", Toast.LENGTH_SHORT).show();
                    addMainData();
                    Log.d("SSFF", "정렬 끝!");
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }




    //Firebase에서 받은 정보들을 정리
    public void addMainData(){

        MainData_fb mainData_fb = new MainData_fb();

        mainData_fb.resInfo = resInfo_fbList;
        mainData_fb.menuInfo = menuInfo_fbList;
        mainData_fb.menuReview = menuReview_fbList;




        //객체생성, 여기 생성자에서 원본을 GlobalApplication에 업로드해줌
        MenuRecommend menuRecommend = new MenuRecommend( mainData_fb );

        //이 메소드를 실행하면 GlobalApplication에 메인에 적합하게 정렬된 리스트를 업로드함
        menuRecommend.makeMainList();

        //MainActivity로 가자!
        Intent iT = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(iT);
        finish();
    }







}
