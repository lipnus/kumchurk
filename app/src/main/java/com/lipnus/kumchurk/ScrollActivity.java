package com.lipnus.kumchurk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lipnus.kumchurk.detailpage.CommentActivity;
import com.lipnus.kumchurk.detailpage.ReviewWrtieActivity;
import com.lipnus.kumchurk.fcm.SendFcm;
import com.lipnus.kumchurk.firebaseModel.MainData_fb;
import com.lipnus.kumchurk.firebaseModel.MenuInfo_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;
import com.lipnus.kumchurk.firebaseModel.ScrollData;
import com.lipnus.kumchurk.firebaseModel.ScrollDataList;
import com.lipnus.kumchurk.firebaseModel.User_fb;
import com.lipnus.kumchurk.kum_class.CustomDialog;
import com.lipnus.kumchurk.kum_class.GuideDialog;
import com.lipnus.kumchurk.kum_class.ScrollMenuRecommend;
import com.lipnus.kumchurk.kum_class.SimpleFunction;
import com.lipnus.kumchurk.map.MapActivity;
import com.lipnus.kumchurk.submenu.PersonalSpaceActivity;
import com.tsengvn.typekit.TypekitContextWrapper;
import com.xgc1986.parallaxPagerTransformer.ParallaxPagerTransformer;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.relex.circleindicator.CircleIndicator;

import static com.lipnus.kumchurk.R.id.menu_indicator;
import static com.lipnus.kumchurk.R.id.menu_pager;
import static com.lipnus.kumchurk.R.id.res_indicator;
import static com.lipnus.kumchurk.R.id.res_pager;
import static com.lipnus.kumchurk.R.id.scrollview;
import static com.lipnus.kumchurk.kum_class.SimpleFunction.displayPrice;


/**
 * Created by Sunpil on 2017-02-01.
 * 음식메뉴 넘길 때 쓰이는 뷰페이저의 어탭터는 따로 클래스를 만들지 않고 내부클래스로 구현하였다(2017-02-01)
 */

/*===========================

============================= */

public class ScrollActivity extends AppCompatActivity {

    Context context;

    private String nowResKey;
    private String nowMenuKey;
    private String nowReviewKey;

    ScrollView scrollView;
    TextView overTv;
    TextView viewpagerCount;
    TextView reviewTv;

    ImageView heart_iv;
    ImageView fuck_iv;
    ImageView comment_iv;
    ImageView upload_iv;

    TextView heart_tv;
    TextView fuck_tv;
    TextView comment_tv;

    ImageView faceIv;
    TextView idTv;
    TextView dateTv;

    FrameLayout resPicLayout;

    LinearLayout foodCommentLayout;
    LinearLayout restrantInfoLinear;
    LinearLayout scoreLinear;
    LinearLayout optionMenuLinear;

    //식당의 정보출력
    TextView resNameTv;
    TextView resInfoTv;
    TextView resTimeInfoTv;
    TextView resThemeTv;
    TextView resLeftTimeTv;
    TextView resDistanceTv;
    TextView resAddInfoTv;
    TextView resAddInfoTitleTv;

    //해당 식당의 메뉴리스트 위에 텍스트뷰
    TextView menulistTitleTv;

    //해당 식당의 음식메뉴들을 출력하는 리스트뷰
    ListView listview;
    ListViewAdapter adapter;

    //추천메뉴
    ImageView[] recommendIv = new ImageView[8];
    TextView[] recommend_nameTv = new TextView[8];
    TextView[] recommend_priceTv = new TextView[8];
    TextView[] recommend_resTv = new TextView[8];

    //다음으로 가는 화살표
    ImageView nextArrowIv;

    //음식메뉴를 넘기는 뷰페이저와 어댑터
    private ViewPager menuPager;
    private PagerAdapter menuPagerAdapter;
    private int MENU_PAGER_NUM = 1; //뷰페이저가 몇개인지(기본은 1개)
    private int menu_pagerPosition; //현재 뷰페이저가 몇번째인지(onPostResume에서 0으로 초기화되고, 뷰페이서 리스너에서 업데이트됨)
    private CircleIndicator menu_circleIndicator;

    //식당사진 넘기는 뷰페이저와 어댑터
    private ViewPager resPager;
    private PagerAdapter resPagerAdapter;
    private final int RES_PAGER_NUM = 2; //뷰페이저가 몇개인지(기본은 1개)
    private CircleIndicator res_circleIndicator;


    //왼쪽, 오른쪽 이동 애니매이션 객체
    Animation translateRightAnim;
    Animation fadeIn;
    Animation fadeOut;
    int resAnimCount = 0; //레스토랑 소개부분의 애니매이션 단계(0~2)
    double scroll_Location_Check = 0; //위로 스크롤 하는 것을 체크

    //댓글 유도 텍스트
    TextView comment_connect_tv;

    //ScrollActivity가 Task에 쌓이지 않게 하기 위해서 이런 처리를 한다
    public static Activity SCActivity;



    //Firebase 참조
    private Firebase mRef;

    //firebase에서 받은 메뉴정보를 저장
    List<ScrollData> sDList;
    ResInfo_fb resInfo_fb;

    //ScrollDataList는 카테고리가 같은 식당들의 묶음이다. 이놈은 그 묶음들을 담고있는 묶음
    List<ScrollDataList> sDLList;


    //다운로드가 끝난 것을 체크(review호출 수 = user호출 수 임을 이용)
    int reviewFbCount;
    int userFbCount;

    int menuCount; //메뉴가 몇개인지
    int menuCompareCount;

    //그림 회전 다이얼로그
    CustomDialog customDialog;

    //추천메뉴
    MainData_fb recommendData;

    //이 액티비티를 몇번 호출하는지(donwonloadFinish에서 체크)
    int scCallCount;

    //Preference선언 (가이드를 한번만 보는지 확인)
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    //가이드 다이얼로그
    GuideDialog guideDialog;
    FrameLayout guideLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);

        context = this;

        //이 액티비티를 몇번 호출하는지
        scCallCount = 0;

        //background상태에서 오랜 시간이 지나서 application의 객체가 소멸되었을때 모든 액티비티 지우고 인트로화면으로 이동
        if(GlobalApplication.mainData_fbList==null){
//            Log.d("FFRR", "어플리케이션 객체 소멸");
            Intent intent = new Intent(getApplicationContext(),IntroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //toolbarColor(); //툴바의 색상을 흰색으로 변경

        //ScrollActivity가 Task에 쌓이지 않게 하기 위해
        SCActivity = ScrollActivity.this;

        //앞의 엑티비티로부터 식당이름과 메뉴이름을 받는다
        Intent iT = getIntent();
        nowResKey = iT.getExtras().getString("res_key");
        nowMenuKey = iT.getExtras().getString("menu_key");
        nowReviewKey = iT.getExtras().getString("review_key");

        Log.d("SSFF", "받은식당키:  " + nowResKey + "  메뉴키: " + nowMenuKey + "  리뷰키: " + nowReviewKey);

        //여기에 다 처박아두고 onCreate에는 리스너만 놔뒀다
        initSetting();



        //뷰페이저에 변화가 있을때의 리스너
        menuPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //뷰페이저가 넘어갈때마다 pagerPosition에다가 뷰페이저의 현재위치를 저장
                menu_pagerPosition = position;

                //뷰페이저 번호에 맞게 하단부분의 리뷰내용을 표시
                addReview(position);

                //뷰페이저 번호에 맞게 하트, 빠큐 아이콘 표시
                addVote(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //onScrollChangedListener은 SDK 23이상에서만 동작한다 23버전 이하에서는 꿩대신 닭으로 onTouchListener를 사용
        if(Build.VERSION.SDK_INT >=23 ){
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    scrollChanged();
                }
            });
        }else { //버전이 낮은경우
            scrollView.setOnTouchListener(new View.OnTouchListener() {
                private ViewTreeObserver observer;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    scrollChanged();
                    return false;
                }
            });
        }

        connect_fb_resInfo();

        //가이드 표시
        showGuide();

    }//onCreate끝

    public void initSetting(){


        //추천메뉴
        recommendIv[0] = (ImageView) findViewById(R.id.recommendIv1);
        recommendIv[1] = (ImageView) findViewById(R.id.recommendIv2);
        recommendIv[2] = (ImageView) findViewById(R.id.recommendIv3);
        recommendIv[3] = (ImageView) findViewById(R.id.recommendIv4);
        recommendIv[4] = (ImageView) findViewById(R.id.recommendIv5);
        recommendIv[5] = (ImageView) findViewById(R.id.recommendIv6);
        recommendIv[6] = (ImageView) findViewById(R.id.recommendIv7);
        recommendIv[7] = (ImageView) findViewById(R.id.recommendIv8);


        recommend_nameTv[0] = (TextView) findViewById(R.id.recommend_nameTv1);
        recommend_nameTv[1] = (TextView) findViewById(R.id.recommend_nameTv2);
        recommend_nameTv[2] = (TextView) findViewById(R.id.recommend_nameTv3);
        recommend_nameTv[3] = (TextView) findViewById(R.id.recommend_nameTv4);
        recommend_nameTv[4] = (TextView) findViewById(R.id.recommend_nameTv5);
        recommend_nameTv[5] = (TextView) findViewById(R.id.recommend_nameTv6);
        recommend_nameTv[6] = (TextView) findViewById(R.id.recommend_nameTv7);
        recommend_nameTv[7] = (TextView) findViewById(R.id.recommend_nameTv8);

        recommend_priceTv[0] = (TextView) findViewById(R.id.recommend_priceTv1);
        recommend_priceTv[1] = (TextView) findViewById(R.id.recommend_priceTv2);
        recommend_priceTv[2] = (TextView) findViewById(R.id.recommend_priceTv3);
        recommend_priceTv[3] = (TextView) findViewById(R.id.recommend_priceTv4);
        recommend_priceTv[4] = (TextView) findViewById(R.id.recommend_priceTv5);
        recommend_priceTv[5] = (TextView) findViewById(R.id.recommend_priceTv6);
        recommend_priceTv[6] = (TextView) findViewById(R.id.recommend_priceTv7);
        recommend_priceTv[7] = (TextView) findViewById(R.id.recommend_priceTv8);

        recommend_resTv[0] = (TextView) findViewById(R.id.recommend_resTv1);
        recommend_resTv[1] = (TextView) findViewById(R.id.recommend_resTv2);
        recommend_resTv[2] = (TextView) findViewById(R.id.recommend_resTv3);
        recommend_resTv[3] = (TextView) findViewById(R.id.recommend_resTv4);
        recommend_resTv[4] = (TextView) findViewById(R.id.recommend_resTv5);
        recommend_resTv[5] = (TextView) findViewById(R.id.recommend_resTv6);
        recommend_resTv[6] = (TextView) findViewById(R.id.recommend_resTv7);
        recommend_resTv[7] = (TextView) findViewById(R.id.recommend_resTv8);

        //식당의 정보출력
        resNameTv = (TextView)findViewById(R.id.resNameTv);
        resInfoTv= (TextView)findViewById(R.id.resInfoTv);
        resTimeInfoTv = (TextView) findViewById(R.id.resTimeInfoTv);
        resThemeTv= (TextView)findViewById(R.id.resThemeTv);
        resLeftTimeTv = (TextView) findViewById(R.id.resLeftTimeTv);
        resDistanceTv= (TextView)findViewById(R.id.resDistanceTv);
        resAddInfoTv = (TextView) findViewById(R.id.res_addInfo_tv);
        resAddInfoTitleTv = (TextView) findViewById(R.id.res_addInfo_title_tv);

        //아래의 4개 옵션버튼을 담고있는 레이아웃
        optionMenuLinear = (LinearLayout) findViewById(R.id.sc_option_MenuLinear);

        //하트, 빠큐, 댓글, 업로드 아이콘과 그 밑의 텍스트
        heart_iv = (ImageView) findViewById(R.id.heart_iv);
        fuck_iv = (ImageView) findViewById(R.id.fuck_iv);
        comment_iv = (ImageView) findViewById(R.id.comment_iv);
        upload_iv = (ImageView) findViewById(R.id.upload_iv);

        heart_tv = (TextView) findViewById(R.id.heart_tv);
        fuck_tv = (TextView) findViewById(R.id.fuck_tv);
        comment_tv = (TextView) findViewById(R.id.comment_tv);

        //댓글 유도 텍스트
        comment_connect_tv = (TextView) findViewById(R.id.comment_connect_tv);

        //날짜
        dateTv = (TextView) findViewById(R.id.sc_date_Tv);

        //다음으로 가는 화살표
        nextArrowIv = (ImageView) findViewById(R.id.sc_nextarrow_iv);

        scrollView = (ScrollView) findViewById(scrollview);
        overTv = (TextView) findViewById(R.id.overTextView);
        reviewTv = (TextView) findViewById(R.id.reviewTv); //댓글(리뷰의 본문)
        viewpagerCount = (TextView) findViewById(R.id.viewPagerPageTv);

        faceIv = (ImageView) findViewById(R.id.faceIv);
        idTv = (TextView) findViewById(R.id.idTv);


        resPicLayout = (FrameLayout) findViewById(R.id.res_pic_layout); //식당사진 위의 검은색 필터

        foodCommentLayout = (LinearLayout) findViewById(R.id.foodCommentLinear);
        restrantInfoLinear = (LinearLayout) findViewById(R.id.resturantInfoLinear);
        scoreLinear = (LinearLayout) findViewById(R.id.scoreLinear); //별점매기는부분

        //메뉴리스트 위의 텍스트뷰
        menulistTitleTv = (TextView) findViewById(R.id.menu_list_title_tv);

        //애니매이션
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein_restaurant);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right_restaurant_pic);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);

        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        fadeIn.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);
        fadeOut.setAnimationListener(animListener);

        // Adapter 생성
        adapter = new ListViewAdapter() ;

        // 식당메뉴 리스트뷰와 어댑터
        listview = (ListView) findViewById(R.id.menu_listview);
        listview.setAdapter(adapter);

        //메뉴 뷰페이저(어댑터는 서버에서 데이터를 다운받은 시점에 한다)
        menuPager = (ViewPager) findViewById(menu_pager);

        //뷰페이저의 전환효과(false, true에 의해서 앞뒤의 레이어 순위가 바뀜)
//        menuPager.setPageTransformer(true, new DepthPageTransformer() );

        ParallaxPagerTransformer pt = new ParallaxPagerTransformer((R.id.imageView));
        pt.setBorder(0);
        pt.setSpeed(0.2f);
        menuPager.setPageTransformer(false, pt);


        //식당 뷰페이저
        resPager = (ViewPager) findViewById(res_pager);




    }

    public void initAnimation(){
        //애니매이션
        YoYo.with(Techniques.RotateIn)
                .duration(400)
                .playOn( faceIv );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //애니매이션
        overridePendingTransition(R.anim.fadein, R.anim.translate_right);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        try{

            //Firebase에서 데이터를 받아서 sDList를 채워넣는다

        }
        catch (Exception e){
            finish();
        }

    }

    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }




    public void onClick_next(View v){


        //끝까지 오면 처음으로 돌아감
        if( menuPager.getCurrentItem() == menuPagerAdapter.getCount()-1 ){
            menuPager.setCurrentItem(0, false);
        }else{
            menuPager.setCurrentItem(menuPager.getCurrentItem() + 1, true);
        }


        Log.d("VPager", "뷰페이저 개수: " + menuPagerAdapter.getCount() );
        Log.d("VPager", "현재: " + menuPager.getCurrentItem() );

    } //다음메뉴로( > 버튼)
    public void onClick_comment(View v){

        //현재의 메뉴객체
        ScrollData scrollData = nowMenuRef();



        //리뷰가 있어야 댓글도 쓴다
        if(scrollData.menuReviews.size() > 0){

//            nowReviewKey = scrollData.menuReviews.get(menu_pagerPosition).key;

            Intent iT = new Intent(this, CommentActivity.class);
            iT.putExtra("review_key", scrollData.menuReviews.get(menu_pagerPosition).key );
            iT.putExtra("review_token", scrollData.users.get(menu_pagerPosition).token ); //리뷰쓴사람 token
            iT.putExtra("callFrom", "ScrollActivity");

            Log.d("CMCM", "보낸Intent: " + scrollData.menuReviews.get(menu_pagerPosition).key);

            startActivity(iT);
        }else{
            Toast.makeText(getApplicationContext(), "리뷰가 있어야 댓글을 달 수 있어요.", Toast.LENGTH_LONG).show();
        }

    }//댓글로 연결
    public void onClick_reviewUpload(View v){

        ScrollData scrollData = nowMenuRef();
        String menuName = scrollData.menuInfo.menu_name;
        String menuKey = scrollData.menuInfo.key;

        String resName = resInfo_fb.res_name;

        Intent iT = new Intent(this, ReviewWrtieActivity.class);
        iT.putExtra("res_name", resName);
        iT.putExtra("menu_name", menuName);
        iT.putExtra("menu_key", menuKey);
        iT.putExtra("callFrom", "ScrollActivity");
        startActivity(iT);

    } //업로드(+버튼)
    public void onClick_resMoreInfo(View v){
        Intent iT = new Intent(getApplicationContext(), MapActivity.class);
        iT.putExtra("res_latitude", Double.parseDouble(resInfo_fb.latitude) );
        iT.putExtra("res_longitude", Double.parseDouble(resInfo_fb.longitude) );
        iT.putExtra("res_location", resInfo_fb.location );
        iT.putExtra("res_name", resInfo_fb.res_name );
        iT.putExtra("res_phone", resInfo_fb.phone );
        startActivity(iT);
    }//식당사진을 눌러서 상세정보보기(지도)
    public void onClick_scroll_profile(View v){

        //지금 보고있는 애가 누군지
        ScrollData scrollData = nowMenuRef();
        String uid = scrollData.users.get(menu_pagerPosition).uid;

        Log.d("PRPR", "보낸 uid: " + uid);

        Intent iT = new Intent(this, PersonalSpaceActivity.class);
        iT.putExtra("uid", uid);
        startActivity(iT);
    }//프로필 얼굴 클릭
    public void onClick_scroll_reviewText(View v){


    }//리뷰글 터치시 상세리뷰로 이동

    //하트
    public void onClick_heart(View v){

        //지금 메뉴정보
        ScrollData nowMenuData = new ScrollData();

        //지금 메뉴가 해당하는 열려있는 카테고리를 찾는다
        for(int i=0; i<sDList.size(); i++){
            if( sDList.get(i).menuInfo.key.equals( nowMenuKey ) ){
                nowMenuData = sDList.get(i);
                break;
            }//if
        }//for

        MenuReview_fb nowReview = nowMenuData.menuReviews.get(menu_pagerPosition);

        //이 리뷰에 눌러진 하트,뻐큐 정보
        Map heartMap = nowReview.heart;
        Map fuckMap = nowReview.fuck;

        //내가 DB에 보낼 하트, 뻐큐
        Map myHeart = new HashMap();
        Map myRelationHeart = new HashMap(); //re_user_heart에 업로드할것
        Map myFuck = new HashMap();
        Map myRelationFuck = new HashMap(); //re_user_fuck에 업로드할것


        //하트가 눌러진 상태에서 터치를 함
        if(heartMap.get( GlobalApplication.getUser_id() ) != null){

            myHeart.put(GlobalApplication.getUser_id(), null); //null값으로 업데이트 하면 삭제와 같은 효과
            myRelationHeart.put(nowReview.key, null);
            heartMap.remove( GlobalApplication.getUser_id() ); //클라이언트 내부처리
        }
        //하트가 비어있는 상태에서 터치를 함
        else{
            //하트추가
            myHeart.put(GlobalApplication.getUser_id(), SimpleFunction.getTodayDate() ); //새롭게 추가
            myRelationHeart.put(nowReview.key, true);
            heartMap.put( GlobalApplication.getUser_id(), SimpleFunction.getTodayDate()); //클라이언트 내부처리

            //뻐큐제거
            if(heartMap.get( GlobalApplication.getUser_id() ) != null){
                myFuck.put(GlobalApplication.getUser_id(), null);
                myRelationFuck.put(nowReview.key, null);
                fuckMap.remove( GlobalApplication.getUser_id() ); //클라이언트 내부처리
            }

            //FCM알람 전송(내 글이 아닐경우)
            if(!GlobalApplication.getUser_id().equals(nowMenuData.users.get(menu_pagerPosition).uid)){
                SendFcm.sendFcmData(nowMenuData.users.get(menu_pagerPosition).token, GlobalApplication.getUser_nickname(),
                        "회원님의 " + nowMenuData.menuInfo.menu_name + " 리뷰를 좋아합니다.", GlobalApplication.getUser_thumbnail());
            }


        }

        //menu_review안의 heart에 업로드
        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/menu_review/" + nowReview.key);
        mRef.child("heart").updateChildren(myHeart);

        //re_user_heart에 업로드
        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/re_user_heart");
        mRef.child( GlobalApplication.getUser_id() ).updateChildren(myRelationHeart);

        //menu_review안의 fuck에 업로드
        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/menu_review/" + nowReview.key);
        mRef.child("fuck").updateChildren(myFuck);

        //re_user_fuck에 업로드
        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/re_user_fuck");
        mRef.child( GlobalApplication.getUser_id() ).updateChildren(myRelationFuck);


        Log.d("HHTT", "하트누름");

        //파이어베이스는 연결해 실패해도 오프라인에 저장해놨다가 지가 알아서 잘 한다고 하니 믿고..(클라이언트 내부에서 한게 적용됨)
        addVote(menu_pagerPosition);

        //하트에 애니메이션
        YoYo.with(Techniques.RubberBand)
                .duration(700)
                .playOn( heart_iv );


    }

    //뻐큐
    public void onClick_fuck(View v){

        //지금 메뉴정보
        ScrollData nowMenuData = new ScrollData();

        //지금 메뉴가 해당하는 열려있는 카테고리를 찾는다
        for(int i=0; i<sDList.size(); i++){
            if( sDList.get(i).menuInfo.key.equals( nowMenuKey ) ){
                nowMenuData = sDList.get(i);
                break;
            }//if
        }//for

        MenuReview_fb nowReview = nowMenuData.menuReviews.get(menu_pagerPosition);

        //이 리뷰에 눌러진 하트,뻐큐 정보
        Map heartMap = nowReview.heart;
        Map fuckMap = nowReview.fuck;

        //내가 DB에 보낼 하트, 뻐큐
        Map myHeart = new HashMap();
        Map myRelationHeart = new HashMap(); //re_user_heart에 업로드할것
        Map myFuck = new HashMap();
        Map myRelationFuck = new HashMap(); //re_user_fuck에 업로드할것


        //뻐큐가 눌러진 상태에서 터치를 함
        if(fuckMap.get( GlobalApplication.getUser_id() ) != null){

            //빠큐제거
            myFuck.put(GlobalApplication.getUser_id(), null); //null값으로 업데이트 하면 삭제와 같은 효과
            myRelationFuck.put(nowReview.key, null);
            fuckMap.remove( GlobalApplication.getUser_id() ); //클라이언트 내부처리
        }
        //뻐큐가 비어있는 상태에서 터치를 함
        else{

            //빠큐생성
            myFuck.put(GlobalApplication.getUser_id(), SimpleFunction.getTodayDate() ); //새롭게 추가
            myRelationFuck.put(nowReview.key, true);
            fuckMap.put( GlobalApplication.getUser_id(), SimpleFunction.getTodayDate()); //클라이언트 내부처리

            //하트제거
            if(heartMap.get( GlobalApplication.getUser_id() ) != null){
                myHeart.put(GlobalApplication.getUser_id(), null); //null값으로 업데이트 하면 삭제와 같은 효과
                myRelationHeart.put(nowReview.key, null);
                heartMap.remove( GlobalApplication.getUser_id() ); //클라이언트 내부처리
            }
        }

        //menu_review안의 heart에 업로드
        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/menu_review/" + nowReview.key);
        mRef.child("heart").updateChildren(myHeart);

        //re_user_heart에 업로드
        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/re_user_heart");
        mRef.child( GlobalApplication.getUser_id() ).updateChildren(myRelationHeart);

        //menu_review안의 fuck에 업로드
        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/menu_review/" + nowReview.key);
        mRef.child("fuck").updateChildren(myFuck);

        //re_user_fuck에 업로드
        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/re_user_fuck");
        mRef.child( GlobalApplication.getUser_id() ).updateChildren(myRelationFuck);


        Log.d("HHTT", "뻐큐누름");


        //파이어베이스는 연결해 실패해도 오프라인에 저장해놨다가 지가 알아서 잘 한다고 하니 믿고..(클라이언트 내부에서 한게 적용됨)
        addVote(menu_pagerPosition);

        //뻐큐에 애니매이션
        YoYo.with(Techniques.Tada)
                .duration(700)
                .playOn( fuck_iv );
    }

    //제일 하단의 추천메뉴
    public void onClick_recommend(View v){

        //아직 리뷰 다운받기 전이면 클릭불가
        if(recommendData==null){ return; }

        Intent iT = new Intent(getApplicationContext(), ScrollActivity.class);

        //현재 액티비티는 끈다
        finish();

        switch (v.getId()){
            case R.id.recommendLinear1:
                iT.putExtra("res_key", recommendData.resInfo.get(0).key);
                iT.putExtra("menu_key", recommendData.menuInfo.get(0).key);
                iT.putExtra("review_key", recommendData.menuReview.get(0).key);
                startActivity(iT);
                break;
            case R.id.recommendLinear2:
                iT.putExtra("res_key", recommendData.resInfo.get(1).key);
                iT.putExtra("menu_key", recommendData.menuInfo.get(1).key);
                iT.putExtra("review_key", recommendData.menuReview.get(1).key);
                startActivity(iT);
                break;
            case R.id.recommendLinear3:
                iT.putExtra("res_key", recommendData.resInfo.get(2).key);
                iT.putExtra("menu_key", recommendData.menuInfo.get(2).key);
                iT.putExtra("review_key", recommendData.menuReview.get(2).key);
                startActivity(iT);
                break;
            case R.id.recommendLinear4:
                iT.putExtra("res_key", recommendData.resInfo.get(3).key);
                iT.putExtra("menu_key", recommendData.menuInfo.get(3).key);
                iT.putExtra("review_key", recommendData.menuReview.get(3).key);
                startActivity(iT);
                break;
            case R.id.recommendLinear5:
                iT.putExtra("res_key", recommendData.resInfo.get(4).key);
                iT.putExtra("menu_key", recommendData.menuInfo.get(4).key);
                iT.putExtra("review_key", recommendData.menuReview.get(4).key);
                startActivity(iT);
                break;
            case R.id.recommendLinear6:
                iT.putExtra("res_key", recommendData.resInfo.get(5).key);
                iT.putExtra("menu_key", recommendData.menuInfo.get(5).key);
                iT.putExtra("review_key", recommendData.menuReview.get(5).key);
                startActivity(iT);
                break;
            case R.id.recommendLinear7:
                iT.putExtra("res_key", recommendData.resInfo.get(6).key);
                iT.putExtra("menu_key", recommendData.menuInfo.get(6).key);
                iT.putExtra("review_key", recommendData.menuReview.get(6).key);
                startActivity(iT);
                break;
            case R.id.recommendLinear8:
                iT.putExtra("res_key", recommendData.resInfo.get(7).key);
                iT.putExtra("menu_key", recommendData.menuInfo.get(7).key);
                iT.putExtra("review_key", recommendData.menuReview.get(7).key);
                startActivity(iT);
                break;

        }
    }

    //우측하단 동그라미 메뉴
    public void onClick_scroll_map(View v){
        Intent iT = new Intent(getApplicationContext(), MapActivity.class);
        iT.putExtra("res_latitude", Double.parseDouble(resInfo_fb.latitude) );
        iT.putExtra("res_longitude", Double.parseDouble(resInfo_fb.longitude) );
        iT.putExtra("res_location", resInfo_fb.location );
        iT.putExtra("res_name", resInfo_fb.res_name );
        iT.putExtra("res_phone", resInfo_fb.phone );
        startActivity(iT);
    }
    public void onClick_scroll_review(View v){
        ScrollData scrollData = nowMenuRef();
        String menuName = scrollData.menuInfo.menu_name;
        String menuKey = scrollData.menuInfo.key;

        String resName = resInfo_fb.res_name;

        Intent iT = new Intent(this, ReviewWrtieActivity.class);
        iT.putExtra("res_name", resName);
        iT.putExtra("menu_name", menuName);
        iT.putExtra("menu_key", menuKey);
        iT.putExtra("callFrom", "ScrollActivity");
        startActivity(iT);
    }
    public void onClick_scroll_report(View v){

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("plain/text");
        String[] tos = {"ktikti7679@naver.com", "aisxk@naver.com"};

        intent.putExtra(Intent.EXTRA_EMAIL, tos);
        intent.putExtra(Intent.EXTRA_SUBJECT, "[쿰척] 잘못된 정보를 제보합니다");
        intent.putExtra(Intent.EXTRA_TEXT, "식당이름: " + resInfo_fb.res_name + "\n(잘못된 부분을 알려주시면 신속히 처리하겠습니다)" );

        startActivity(intent);




    }



    //===============================================================================================
    //Firebase에서 데이터 다운로드
    //===============================================================================================
    public void connect_fb_resInfo(){

        //다이얼로그 켜기
        customDialog = new CustomDialog(this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //다이얼로그 뒷배경투명처리
        customDialog.setCancelable(true);
        customDialog.show(); // 보여주기

        //리스트 초기화
        sDList = new ArrayList<>();

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/res_info");
        mRef.orderByKey().equalTo( nowResKey ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{
                    reviewFbCount = 0;
                    userFbCount = 0;

                    menuCount = 0;
                    menuCompareCount = 0;

                    //이 식당의 메뉴목록들을 가져온다
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren() ) {

                        //이건 전역적으로 선언된 것
                        resInfo_fb = postSnapshot.getValue( ResInfo_fb.class );

                        Map map = resInfo_fb.menu_id;
                        Iterator<String> keySetIterator = map.keySet().iterator();

                        menuCount = map.size(); //이 식당의 메뉴개수

                        //메뉴들을 찾는다
                        while (keySetIterator.hasNext()) {
                            String menuKey = keySetIterator.next();
                            connect_fb_menuInfo(menuKey);
                        }
                    }//메뉴를 꺼내는 부분

                }catch(Exception e){
                    Log.d("SSFF", "에러: " + e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void connect_fb_menuInfo(String menuKey){
        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_info/" + menuKey);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{
                    menuCompareCount++;

                    //식당의 메뉴들
                    MenuInfo_fb menuInfo_fb = dataSnapshot.getValue( MenuInfo_fb.class );
                    menuInfo_fb.key = dataSnapshot.getKey();

                    Log.d("SSFF", "메뉴: " + menuInfo_fb.menu_name);

                    //메뉴입력(하위의 리뷰가 없다면 메뉴정보만 입력, 리뷰가 있다면 리뷰와 유저 정보까지 찾아서 입력)
                    ScrollData scrollData = new ScrollData();
                    scrollData.menuInfo = menuInfo_fb;

                    Map map = menuInfo_fb.review_id;
                    Iterator<String> keySetIterator = map.keySet().iterator();

                    //이 메뉴가 가진 리뷰의 수 체크
                    reviewFbCount += map.size();

                    //리뷰가 없는경우
                    if(map.size() == 0){
                        sDList.add( scrollData );
                    }
                    //리뷰가 있는경우
                    else{

                        //리뷰를 찾는다
                        while (keySetIterator.hasNext()) {
                            String reviewKey = keySetIterator.next();
                            connect_fb_menuReview(reviewKey, menuInfo_fb);
                        }//while
                    }//else



                    //리뷰가 하나도 없는경우(이곳을 통해 종결)
                    if(reviewFbCount == 0 && (menuCount <= menuCompareCount) ){
                        Log.d("SSFF", "리뷰가 하나도 없음. connet_fb_menuInfo에서 종결");
                        downloadFinish();
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

    public void connect_fb_menuReview(String reviewKey, final MenuInfo_fb menuInfo_fb){
        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_review/" + reviewKey );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    MenuReview_fb menuReview_fb = dataSnapshot.getValue( MenuReview_fb.class );
                    menuReview_fb.key = dataSnapshot.getKey();

                    Log.d("SSFF", "리뷰: " + menuReview_fb.review_image);
                    connect_fb_user( menuReview_fb.uid, menuInfo_fb, menuReview_fb );

                }catch(Exception e){
                    Log.d("SSFF", "에러: " + e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void connect_fb_user(final String uid, final MenuInfo_fb menuInfo_fb, final MenuReview_fb menuReview_fb){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/user/" );
        mRef.orderByKey().equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    userFbCount++;

                    //각 리뷰 당 글은 하나씩
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren() ) {

                        User_fb user_fb = postSnapshot.getValue( User_fb.class );
                        user_fb.uid = uid;

                        //이 메뉴가 등록되어 있으면 리뷰를 그 안에다가 집어넣는다(리뷰가 여러개일 경우 하나의 메뉴집단으로 묶음)
                        Boolean alreadyExist = false;
                        for(int i=0; i<sDList.size(); i++){
                            if( sDList.get(i).menuInfo.menu_name.equals( menuInfo_fb.menu_name ) ){

                                sDList.get(i).menuReviews.add( menuReview_fb );
                                sDList.get(i).users.add( user_fb );
                                alreadyExist = true;
                            }//if
                        }//for

                        //이 리뷰 메뉴이름으로 등록된 것이 없으면 새로운 메뉴를 추가
                        if(alreadyExist==false){
                            ScrollData scrollData = new ScrollData();
                            scrollData.menuInfo = menuInfo_fb;
                            scrollData.menuReviews.add( menuReview_fb );
                            scrollData.users.add( user_fb );
                            sDList.add(scrollData);
                        }

                        Log.d("SSFF", "리뷰작성자: " + user_fb.nickname + userFbCount + "/" + reviewFbCount );
                    }

                    if(userFbCount == reviewFbCount){
                        Log.d("SSFF", "ScrollAvtivity에서 정보 다운로드 끝");
                        downloadFinish();
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





    //가이드
    public void showGuide(){

        guideLayout = (FrameLayout) findViewById(R.id.guilde_scroll_layout);


        //Prefrence설정(0:읽기,쓰기가능)
        setting = getSharedPreferences("GUIDE", 0);
        editor= setting.edit();

        //프레퍼런스에 아이디가 저장되어 있는지 확인
        Boolean guildeVertical = setting.getBoolean("guide_scroll", false);


        if(guildeVertical == false){

            guideLayout.setVisibility(View.VISIBLE);
            guideLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //지금 레이아웃은 제거
                    guideLayout.setVisibility(View.GONE);

                    //가이드를 한번 봤다는 사실을 프레퍼런스에 기억시킴
                    editor.putBoolean("guide_scroll", true);
                    editor.commit();

                    //가이드 다이얼로그
                    guideDialog = new GuideDialog(context, 1);
                    guideDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //다이얼로그 뒷배경투명처리
                    guideDialog.setCancelable(true);
                    guideDialog.show(); // 보여주기

                }
            });

            ImageView guideIv = (ImageView) findViewById(R.id.guide_scroll_iv);
            Glide.with(this)
                    .load(R.drawable.guide_scroll_horizontal)
                    .into(guideIv);
        }

    }

    //Firebase로부터 다운로드 완료 후 데이터들을 배치
    public void downloadFinish(){

        //프사 애니매이션
        initAnimation();

        Log.d("IINN", "downloadFinish()" );

        //다이얼로그 제거
        customDialog.dismiss();

        //음식사진 페이저 출력
        addMenuPager();

        //리뷰 출력(사진에 맞게, 여기에 넣는 숫자가 처음으로 뜨는 뷰페이저 하단의 위치 / 뷰페이저 자체의 위치는 reviewIndex에서 set함
        int rIndex = reviewIndex();
        addReview( rIndex );
        addVote( rIndex );

        //레스토랑 정보 출력
        addResInfo();

        //메뉴들을 카테고리별로 묶는다
        menuListCategorized();

        //이미지가 있는 카테고리를 앞쪽으로 이동
        sortCategory();

        //메뉴들을 리스트뷰에 출력
        addMenuList();

        //JoinLastActivity에서 받은 mainData를 재활용하여 추천메뉴 표시
        addRecommendMenu();

        scCallCount++;
    }

    //최상단 뷰페이저 출력
    public void addMenuPager(){

        //메뉴들 정보를 가진 데이터
        ScrollData nowMenuData = new ScrollData();

        //지금 메뉴가 해당하는 열려있는 카테고리를 찾는다
        for(int i=0; i<sDList.size(); i++){
            if( sDList.get(i).menuInfo.key.equals( nowMenuKey ) ){
                nowMenuData = sDList.get(i);
                break;
            }
        }

        //메뉴정보 표시
        double price1 = Double.parseDouble( nowMenuData.menuInfo.price1 );
        double price2 = Double.parseDouble( nowMenuData.menuInfo.price2 );
        double price3 = Double.parseDouble( nowMenuData.menuInfo.price3 );
        String strPrice = displayPrice(price1, price2, price3);
        overTv.setText(nowMenuData.menuInfo.menu_name + " "+ strPrice); //메뉴사진 위에 오버랩되는 메뉴이름과 가격

        //리뷰개수
        int reviewCount = nowMenuData.menuReviews.size();

        //리뷰가 하나도 없는경우(기본사진 1개를 업로드할 자리 1개가 필요)
        if(reviewCount==0){
            MENU_PAGER_NUM = 1;

            heart_tv.setText("");
            fuck_tv.setText("");
            reviewTv.setText("아직 등록된 리뷰가 없는 미지의 메뉴입니다. \n처음으로 리뷰를 올려보세요!");
            idTv.setText("");

            Glide.with(this)
                    .load("")
                    .into(faceIv);
        }

        //1개 이상의 리뷰가 있는 경우
        else{
            MENU_PAGER_NUM = reviewCount;
        }



        //어댑터에 데이터 입력
        FoodPagerFragment.PlaceholderFragment.addMenu( nowMenuData );

        //뷰페이저의 어댑터를 생성한다(volley로부터 데이터를 받은 이후에 그것을 토대로 Pager의 페이지수를 정하고 어댑터를 생성한다)
        menuPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        menuPager.setAdapter(menuPagerAdapter);

        //인디케이터
        menu_circleIndicator = (CircleIndicator) findViewById(menu_indicator);

        //인디케이터와 뷰페이저를 연결
        menu_circleIndicator.setViewPager(menuPager);

        //음식페이저의 현제 페이지 지정
        menuPager.setCurrentItem(menu_pagerPosition, true);


    }

    //리뷰 출력★
    public void addReview(int position){

        //입력받은 포지션의 리뷰를 보여준다.

        //1. 최초에 1번 호출(사진과 동일한 리뷰위치로)
        //2. 뷰페이저 리스너에서 뷰페이저가 바뀔때마다 호출



        //지금 메뉴정보
        ScrollData nowMenuData = new ScrollData();

        //지금 메뉴가 해당하는 열려있는 카테고리를 찾는다
        for(int i=0; i<sDList.size(); i++){
            if( sDList.get(i).menuInfo.key.equals( nowMenuKey ) ){
                nowMenuData = sDList.get(i);
                break;
            }//if
        }//for

        //리뷰가 하나도 없으면 보여줄 것도 없으니 종료
        if(nowMenuData.menuReviews.size() == 0){ return; }


        //뷰페이저와 일치하는 리뷰 경로
        MenuReview_fb nowReview = nowMenuData.menuReviews.get(position);
        User_fb nowUser = nowMenuData.users.get(position);


        //댓글 수 표시
        int comment = nowReview.comment.size();
        comment_tv.setText("" + comment);


        //날짜표시
        String date = nowReview.date;
        date = SimpleFunction.timeGap(date);
        dateTv.setText(date);

        //닉네임
        idTv.setText( nowUser.nickname );

        //프사
        Glide.with(this)
                .load( nowUser.thumbnail )
                .placeholder(R.drawable.face_basic)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(this))
                .into(faceIv);
        faceIv.setScaleType(ImageView.ScaleType.FIT_XY);

        //화살표
        Glide.with(this)
                .load( R.drawable.right_arrow )
                .into(nextArrowIv);
        nextArrowIv.setScaleType(ImageView.ScaleType.FIT_XY);


        //리뷰내용 표시
        reviewTv.setText(nowReview.memo);

        //댓글유도 텍스트
        if(comment == 0){
            comment_connect_tv.setText("댓글 달기");
        }else{
            comment_connect_tv.setText(comment + "개의 댓글 모두보기");
        }

    }

    //내 투표정보 출력★
    public void addVote(int position){

        //지금 메뉴정보
        ScrollData nowMenuData = new ScrollData();

        //지금 메뉴가 해당하는 열려있는 카테고리를 찾는다
        for(int i=0; i<sDList.size(); i++){
            if( sDList.get(i).menuInfo.key.equals( nowMenuKey ) ){
                nowMenuData = sDList.get(i);
                break;
            }//if
        }//for

        //리뷰가 하나도 없으면 보여줄 것도 없으니 종료
        if(nowMenuData.menuReviews.size() == 0){ return; }

        MenuReview_fb nowReview = nowMenuData.menuReviews.get(position);




        //하트 출력
        Map heartMap = nowReview.heart;
        Map fuckMap = nowReview.fuck;

        if(heartMap.get( GlobalApplication.getUser_id() ) != null){
            Glide.with(this)
                    .load( R.drawable.small_menu_heart2 )
                    .into(heart_iv);
        }else{
            Glide.with(this)
                    .load( R.drawable.small_menu_heart )
                    .into(heart_iv);
        }


        if(fuckMap.get( GlobalApplication.getUser_id() ) != null){
            Glide.with(this)
                    .load( R.drawable.small_menu_fuck2 )
                    .into(fuck_iv);
        }else {
            Glide.with(this)
                    .load( R.drawable.small_menu_fuck )
                    .into(fuck_iv);
        }


        //해당 리뷰의 하트, 빠큐, 댓글 업데이트
        int heart = nowReview.heart.size();
        int fuck = nowReview.fuck.size();
        heart_tv.setText("" + heart);
        fuck_tv.setText("" + fuck);




    }

    //식당정보표시
    public void addResInfo(){

        Log.d("SSFF", "addResInfo");

        //거리
        String distance="현재위치 미확인";
        String disMin="";

        int dis = SimpleFunction.distance(Double.parseDouble(resInfo_fb.latitude), Double.parseDouble(resInfo_fb.longitude), "meter");

        if(dis!=-1){//gps가 제대로 구해지지 않으면 -1을 리턴한다(SimpleFunction클래스 참조)
            distance = dis + "m";
            disMin = SimpleFunction.distanceMinute(Double.parseDouble(resInfo_fb.latitude), Double.parseDouble(resInfo_fb.longitude));
        }


        //식당 영업시간
        String time = resInfo_fb.res_time.restime_weekday; //서버에서 받은 HHmmHHmm으로 된 시간데이터
        String resOpenTime = time.substring(0,2) + ":" + time.substring(2,4) + "-" + time.substring(4,6) + ":" + time.substring(6,8);
        String displayTime=""; //남은 시간을 표시해줌



        SimpleDateFormat sdf = new SimpleDateFormat( "HHmm", Locale.KOREA ); //시간형식
        Time nowT  = new Time(System.currentTimeMillis());
        sdf.format(nowT);

        try{
            //날짜 때문에 시간차이가 이상하게 벌어질 수 있음. 이런식으로 시간만 입력하면, 1970년 1월 1일로 세팅되서 동일날짜에서 시간만 비교가능
            java.util.Date nowTime = sdf.parse( sdf.format(nowT) ); //현재시간
            java.util.Date openTime = sdf.parse(time.substring(0,4)); //식당 오픈시간
            java.util.Date closeTime = sdf.parse(time.substring(4,8)); //식당 마감시간
            java.util.Date noonTime = sdf.parse("1200"); //정오(새벽을 구분하기 위해 사용)

            Log.d("TITI", nowTime + " " + openTime  + " " + closeTime + " ");
            Log.d("TITI", nowTime.getTime() + " " + openTime.getTime()  + " " + closeTime.getTime() + " ");
            Log.d("TITI", "정오와의 차이:" + (noonTime.getTime() - closeTime.getTime()) );


            long diff, diffMin, diffHour; //시간차(밀리초, 분, 시간)




            //문닫는 시간이 새벽인경우
            //======================================================================================
            if( (closeTime.getTime() < noonTime.getTime()) ){

                //지금새벽
                if( (nowTime.getTime() < noonTime.getTime()) ){
                    Log.d("TTII", "새벽");
                    displayTime = "야심한 밤에도 영업중";
                    diff = closeTime.getTime() - nowTime.getTime();
                }
                //안새벽
                else{
                    Log.d("TTII", "안새벽");
                    diff = (1000*60*60*24) + closeTime.getTime() - nowTime.getTime();
                }

                diffMin = diff/(60*1000)%60;
                diffHour = diff/(60*60*1000);


                Log.d("TTII", "새벽의 마감까지 " +diffHour+"시간 " + diffMin +"분 남았습니다");

                //마감 4시간 이하에는 남은시간 표시
                if(diffHour<0 && diffMin < 0){
                    displayTime = "영업시간 종료되었습니다";
                }
                else if(diffHour < 4){
                    if(diffHour==0){
                        displayTime = "마감까지 "+ diffMin +"분 남았습니다";
                    }else{
                        displayTime = "마감까지 "+diffHour+"시간 " + diffMin +"분 남았습니다";
                    }
                }
            }

            //======================================================================================

            else if(nowTime.getTime() < openTime.getTime()){//오픈 전
                diff = openTime.getTime() - nowTime.getTime();
                diffMin = diff/(60*1000)%60;
                diffHour = diff/(60*60*1000);
                if(diffHour==0){
                    displayTime = "오픈까지 "+ diffMin +"분 남았습니다";
                }else{
                    displayTime = "오픈까지 "+diffHour+"시간 " + diffMin +"분 남았습니다";
                }

            }else if(openTime.getTime() <= nowTime.getTime() && nowTime.getTime() <= closeTime.getTime()){ //영업중

                diff = closeTime.getTime() - nowTime.getTime();
                diffMin = (diff/(60*1000))%60;
                diffHour = diff/(60*60*1000);

                if(diffHour > 3){ //마감까지 3시간 이상 남았을때
                    displayTime = "";
                }else{ //마감까지 3시간 이하로 남았을때
                    if(diffHour==0){
                        displayTime = "마감까지 "+ diffMin +"분 남았습니다";
                    }else{
                        displayTime = "마감까지 "+diffHour+"시간 " + diffMin +"분 남았습니다";
                    }
                }

            }else if(closeTime.getTime() < nowTime.getTime()){//마감
                displayTime = "영업시간 종료되었습니다";
            }

        }catch (Exception e){}


        if(resInfo_fb.res_time.restime_weekday.equals("00000000")){//24시간 운영
            resOpenTime = "24시간 운영";
            displayTime = "";
        }else if(resInfo_fb.res_time.restime_weekday.equals("00000001")){//영업시간 미확인
            resOpenTime = "영업시간 미확인";
            displayTime = "";
        }


        //식당의 정보출력
        resNameTv.setText( resInfo_fb.res_name );
        resInfoTv.setText( resInfo_fb.res_category );
        resTimeInfoTv.setText(resOpenTime);
        if(!displayTime.equals("")){ resLeftTimeTv.setVisibility(View.VISIBLE); }
        resThemeTv.setText( resThemeStr() );
        resLeftTimeTv.setText( displayTime );
        resDistanceTv.setText( disMin + "(" + distance + ")"  );


        //부가정보는 있을때만 출력
        if( resInfo_fb.extra_info != ""){
            resAddInfoTitleTv.setVisibility(View.VISIBLE);
            resAddInfoTv.setVisibility(View.VISIBLE);
            resAddInfoTv.setText(resInfo_fb.extra_info);
        }


        Log.d("PICPIC", "식당이미지:"+resInfo_fb.image.image1 );
        Log.d("PICPIC", "식당이미지:"+resInfo_fb.image.image2 );


        //식당 이미지 뷰페이저 출력
        ResPagerFragment.PlaceholderFragment.addRes( resInfo_fb );
        resPagerAdapter = new ScreenSlidePagerAdapter2(getSupportFragmentManager());
        resPager.setAdapter(resPagerAdapter);


        //인디케이터
        res_circleIndicator = (CircleIndicator) findViewById(res_indicator);
        res_circleIndicator.setViewPager(resPager);
    }

    //메뉴들을 카테고리별로 분류
    public void menuListCategorized(){

        String menuCategory;

        //운반용 객체들
        ScrollData temp; //식당
        ScrollDataList tempList; //식당묶음(같은 카테고리) -> 이 묶음을 sDLList에 넣음

        //공간 초기화
        sDLList = new ArrayList<>();

        //==========================================================================================
        //기준을 잡기 위해 가장 처음(0) 건 그냥 넣어준다
        //==========================================================================================

        temp = sDList.get(0);
        menuCategory = temp.menuInfo.menu_category;

        //카테고리가 같은 식당묶음 생성
        tempList = new ScrollDataList();
        tempList.scrollDatas.add( temp );
        tempList.category = menuCategory;
        if( temp.menuReviews.size() != 0 ){ tempList.haveReview = true; }

        //식당묶음을 리스트에 넣음 : 1개의 묶음(그 안에는 1개의 식당)
        sDLList.add(tempList);


        //분류를 시작한다
        for(int i=1; i<sDList.size(); i++){

            Boolean alreadyExist = false;
            for(int j=0; j<sDLList.size(); j++){

                //이미 존재하는 카테고리인 경우
                if(sDLList.get(j).category.equals( sDList.get(i).menuInfo.menu_category ) ){

                    //사진이 있는경우
                    if( sDList.get(i).menuReviews.size() != 0 ){
                        sDLList.get(j).haveReview = true; //이 식당집단은 사진을 가지고 있다
                        sDLList.get(j).scrollDatas.add( 0, sDList.get(i) ); //사진이 있으면 제일 앞쪽에 넣어줌
                    }
                    //사진이 없는경우
                    else{
                        sDLList.get(j).scrollDatas.add( sDList.get(i) );
                    }
                    alreadyExist = true;
                }//if
            }//for(j)


            //존재하지 않는 경우 새로 생성
            if(alreadyExist == false){
                temp = sDList.get(i);
                menuCategory = temp.menuInfo.menu_category;
                tempList = new ScrollDataList();
                tempList.scrollDatas.add( temp );
                tempList.category = menuCategory;
                if( temp.menuReviews.size() != 0 ){ tempList.haveReview = true; }

                sDLList.add(tempList);
            }//if

        }//for(i)


        Log.d("SSFF", "menuListCategorized(SSCC확인)");

        //제대로 분류되었는지 확인(i는 식당묶음index, j는 식당index)
        for(int i=0; i<sDLList.size(); i++){

            Log.d("SSCC", "======================================================");
            Log.d("SSCC", "" + sDLList.get(i).category + "(" + sDLList.get(i).scrollDatas.size() + ")");
            Log.d("SSCC", "======================================================");

            for(int j=0; j<sDLList.get(i).scrollDatas.size(); j++){
                Log.d("SSCC", "" + sDLList.get(i).scrollDatas.get(j).menuInfo.menu_name);
            }//for(j)
        }//for(i)

        Log.d("SSCC", "원본");
        for(int i=0; i<sDList.size(); i++){
            Log.d("SSCC", "" + sDList.get(i).menuInfo.menu_name);
        }
    }

   //그림(리뷰)가 존재하는 카테고리를 앞쪽으로 옮긴다
    public void sortCategory(){

        //리뷰 있는건 여기로 잠시 옮겨놓음
        List<ScrollDataList> temp = new ArrayList<>();

        for(int i=0; i<sDLList.size(); i++){

            //이 카테고리는 리뷰를 보유하고 있다
            if(sDLList.get(i).haveReview == true){
                temp.add( sDLList.get(i) );
                sDLList.remove(i);
                i--;
            }
        }

        for(int i=0; i<temp.size(); i++){
            sDLList.add(0, temp.get(i));
        }

    }

    //해당 카테고리에서 리뷰(그림) 있는게 몇개인지 반환
    public int categoryImgCount(int categoryIndex){

        int count = 0;

        for(int i=0; i<sDLList.get( categoryIndex ).scrollDatas.size(); i++){

            if(sDLList.get(categoryIndex).scrollDatas.get(i).menuReviews.size() != 0){
                count++;
            }
        }//for

        return count;
    }

    //해당 식당의 메뉴들을 표시
    public void addMenuList(){

        //sDLList2는 sDLList에서 이미지가 있는것만 모은 리스트
//        List<ScrollDataList> sDLList = closeAllMenuList();

        //재호출 시 기존의 데이터들은 삭제
        adapter.removeAllItem();

        Log.d("LSLS", "addMenuList");

        //메뉴리스트 제목설정
        menulistTitleTv.setText("" + resInfo_fb.res_name + "의 메뉴");

        //===============================
        //리스트에 소분류 별로 넣는다
        //===============================

        Log.d("LSLS", "카테고리 수: " + this.sDLList.size() );

        for(int i=0; i<sDLList.size(); i++){ //카테고리의 제목을 리스트에 삽입

            Log.d("LSLS", "카테고리: "+ this.sDLList.get(i).category);
            String menuCategory = this.sDLList.get(i).category;
            adapter.addItem(false, "", "", "", "",
                    false, "", "", "", "",
                    true, menuCategory,
                    nowResKey);

            Log.d("LSLS", "전체개수:" + sDLList.get(i).scrollDatas.size() + " 이미지있는거: " + categoryImgCount(i));
            for(int j=0; j<categoryImgCount(i); j=j+2){ //각 카테고리에 속하는 메뉴들을 리스트에 삽입

                Log.d("LSLS", sDLList.get(i).scrollDatas.get(j).menuInfo.menu_name);

                ScrollData path1 = sDLList.get(i).scrollDatas.get(j);


                //왼쪽꺼
                String left_name = path1.menuInfo.menu_name;
                double left_price = Double.parseDouble( path1.menuInfo.price1 );
                double left_price2 = Double.parseDouble( path1.menuInfo.price2 );
                double left_price3 = Double.parseDouble( path1.menuInfo.price3 );
                String l_price = displayPrice(left_price, left_price2, left_price3);
                String l_img = "";
                if(path1.menuReviews.size() != 0){ l_img = path1.menuReviews.get(0).review_image; }



                //리스트가 두개씩 표시(j와 j+1이 모두 범위내부)
                if(j+1 < categoryImgCount(i) ) {

                    Log.d("LSLS", sDLList.get(i).scrollDatas.get(j+1).menuInfo.menu_name);
                    ScrollData path2 = sDLList.get(i).scrollDatas.get(j+1);

                    //오른쪽꺼
                    String right_name = path2.menuInfo.menu_name;
                    double right_price = Double.parseDouble( path2.menuInfo.price1 );
                    double right_price2 = Double.parseDouble( path2.menuInfo.price2 );
                    double right_price3 = Double.parseDouble( path2.menuInfo.price3 );
                    String r_price = displayPrice(right_price, right_price2, right_price3);
                    String r_img = "";
                    if(path2.menuReviews.size() != 0){ r_img = path2.menuReviews.get(0).review_image; }

                    //리스트에 두개 집어넣음
                    adapter.addItem(true, left_name, l_price, l_img, path1.menuInfo.key,
                            true, right_name, r_price, r_img, path2.menuInfo.key,
                            false, "",
                            nowResKey);
                }
                else{//j가 마지막(j+1은 넘어감)

                    //리스에 하나(길쭉하게)집어넣음
                    adapter.addItem(true, left_name, l_price, l_img, path1.menuInfo.key,
                            false, "", "", "", "",
                            false, "",
                            nowResKey);
                }

            }//for문(j) 끝
        }//for문(i) 끝



        SimpleFunction.setListViewHeightBasedOnChildren(listview);
    }

    //그림이 없는 메뉴들을 펼친다
    public void openMenuList(String category, int pos){

        //해당카테고리에서 사진이 있는 것과 없는 두 집단으로 분류



        //사진없는 애들을 임시로 담을 리스트
        List<ScrollData> temp = new ArrayList<>();

        int haveImgCount = 0;//해당 카테고리 안에서 이미지(리뷰)를 가진 메뉴가 몇개인지
        int categoryIndex=0; //해당 카테고리 묶음이 몇번째인지


        //해당 카테고리 집단을 찾는다
        for(int i=0; i<sDLList.size(); i++){
            if(sDLList.get(i).category.equals(category)){
                categoryIndex = i;
                Log.d("OPOP", "카테고리: " + category);
                break;
            }//if
        }//for


        //사진있는 애들의 개수를 세고, 사진없는 애들로 묶음을 만든다
        for(int i=0; i<sDLList.get(categoryIndex).scrollDatas.size(); i++){

            Log.d("OPOP", sDLList.get(categoryIndex).scrollDatas.get(i).menuInfo.menu_name + ": ??");

            //리뷰가 있다
            if(sDLList.get(categoryIndex).scrollDatas.get(i).menuReviews.size() != 0){
                haveImgCount++;
                Log.d("OPOP", sDLList.get(categoryIndex).scrollDatas.get(i).menuInfo.menu_name + ": ㅇㅇ");
            }
            //리뷰가 없다
            else{
                temp.add( sDLList.get(categoryIndex).scrollDatas.get( i ) );
                Log.d("OPOP", sDLList.get(categoryIndex).scrollDatas.get(i).menuInfo.menu_name + ": ㄴㄴ");
            }
        }//for


        //두개가 한칸에 들어가기 때문에 list의 position처리에 유의
        if(haveImgCount%2 == 0){
            haveImgCount = haveImgCount/2;
        }else{
            haveImgCount = (haveImgCount+1)/2;
        }

        Log.d("OPOP", "이미지 있는 칸 개수: " + haveImgCount);



        //사진있는 애들 밑에 사진없는 애들 리스트를 끼워넣는다
        for(int i=0; i<temp.size(); i=i+2){ //각 카테고리에 속하는 메뉴들을 리스트에 삽입

            ScrollData path1 = temp.get(i);

            //왼쪽꺼
            String left_name = path1.menuInfo.menu_name;
            double left_price = Double.parseDouble( path1.menuInfo.price1 );
            double left_price2 = Double.parseDouble( path1.menuInfo.price2 );
            double left_price3 = Double.parseDouble( path1.menuInfo.price3 );
            String l_price = displayPrice(left_price, left_price2, left_price3);
            String l_img = "";


            //리스트가 두개씩 표시(j와 j+1이 모두 범위내부)
            if(i+1 < temp.size() ) {

                ScrollData path2 = temp.get(i+1);

                //오른쪽꺼
                String right_name = path2.menuInfo.menu_name;
                double right_price = Double.parseDouble( path2.menuInfo.price1 );
                double right_price2 = Double.parseDouble( path2.menuInfo.price2 );
                double right_price3 = Double.parseDouble( path2.menuInfo.price3 );
                String r_price = displayPrice(right_price, right_price2, right_price3);
                String r_img = "";

                //리스트에 두개 집어넣음
                adapter.addItem(true, left_name, l_price, l_img, path1.menuInfo.key,
                        true, right_name, r_price, r_img, path2.menuInfo.key,
                        false, "",
                        nowResKey,
                        pos + haveImgCount +1);
            }
            else{//i가 마지막(i+1은 넘어감)

                //리스에 하나(길쭉하게)집어넣음
                adapter.addItem(true, left_name, l_price, l_img, path1.menuInfo.key,
                        false, "", "", "", "",
                        false, "",
                        nowResKey,
                        pos + haveImgCount +1);
            }
        }//for



        //리스트뷰 크기 세팅(새로고침)
        SimpleFunction.setListViewHeightBasedOnChildren(listview);

    }

    //그림이 없는 메뉴들을 닫는다
    public void closeMenuList(String category, int pos){

        int categoryIndex = 0;
        int allCount=0;
        int imgCount=0;
        int noImgCount=0;

        //해당 카테고리 집단을 찾는다
        for(int i=0; i<sDLList.size(); i++){
            if(sDLList.get(i).category.equals(category)){
                categoryIndex = i;
                break;
            }//if
        }//for

        //해당 카테고리 집단의 개수
        allCount = sDLList.get(categoryIndex).scrollDatas.size();

        //이미지 있는 거의 개수
        imgCount = categoryImgCount( categoryIndex );

        //이미지 없는거의 개수
        noImgCount = allCount - imgCount;

        //===================================
        //개수들을 칸수로 다 바꾼다
        //===================================
        if(imgCount%2==0){
            imgCount = imgCount/2;
        }else{
            imgCount = (imgCount+1)/2;
        }


        if(noImgCount%2==0){
            noImgCount = noImgCount/2;
        }else{
            noImgCount = (noImgCount+1)/2;
        }

        //이미지가 없는 애들은 제거(지우면서 전체 인덱스가 줄어듦에 유의)
        for(int i=0; i<noImgCount; i++){
            adapter.removeItem(pos + imgCount + 1);
        }

        //리스트뷰 크기 세팅(새로고침)
        SimpleFunction.setListViewHeightBasedOnChildren(listview);

    }

    //현재 메뉴 객체를 반환(call by ref)
    public ScrollData nowMenuRef(){

        //지금 메뉴정보
        ScrollData nowMenuData = new ScrollData();

        //지금 메뉴가 해당하는 열려있는 카테고리를 찾는다
        for(int i=0; i<sDList.size(); i++){
            if( sDList.get(i).menuInfo.key.equals( nowMenuKey ) ){
                nowMenuData = sDList.get(i);
                break;
            }//if
        }//for

        return nowMenuData;

    }

    //추천메뉴 표시
    public void addRecommendMenu(){

        String[] category = new String[] { resInfo_fb.res_category };

        ScrollMenuRecommend sCRecommend = new ScrollMenuRecommend(  );
        recommendData = sCRecommend.categorizedSelect( 8, category, resInfo_fb.res_name );


        for(int i=0; i<8; i++){

            //추천메뉴이미지
            Glide.with(this)
                    .load( recommendData.menuReview.get(i).review_image )
                    .placeholder(R.drawable.loading)
                    .centerCrop()
                    .thumbnail(0.3f)
                    .into(recommendIv[i]);
            recommendIv[i].setScaleType(ImageView.ScaleType.FIT_XY);

            recommend_nameTv[i].setText( recommendData.menuInfo.get(i).menu_name );

            double price1 = Double.parseDouble( recommendData.menuInfo.get(i).price1 );
            double price2 = Double.parseDouble( recommendData.menuInfo.get(i).price2 );
            double price3 = Double.parseDouble( recommendData.menuInfo.get(i).price3 );
            String price = SimpleFunction.displayPrice(price1, price2, price3);
            recommend_priceTv[i].setText( price );
            recommend_resTv[i].setText("'" + recommendData.resInfo.get(i).res_name +"'");
        }



        //클릭이벤트는 onClick이벤트로 따로 모아놓았음



    }

    //첫 호출일 경우 리뷰키를 입력하면 몇번째인지 번호를 반환
    public int reviewIndex(){

        int reValue = 0;

        //이전 엑티비티에서 리뷰키를 호출했었고, 이 엑티비티가 최초로 호출된 경우
        if(nowMenuKey != "" && scCallCount == 0){
            for(int i=0; i<nowMenuRef().menuReviews.size(); i++){
                if( nowMenuRef().menuReviews.get(i).key.equals( nowReviewKey ) ){

                    menu_pagerPosition = i;
                    menuPager.setCurrentItem(i, false); //뷰페이저 조정
                    reValue = i; //뷰페이저 하단의 정보들은 이 인덱스를 이용
                }//if
            }//for
        }//if


        Log.d("IINN", "받은것: " + nowReviewKey + " 인덱스: " + reValue );
        return reValue;
    }

    //CommentAcvitivy에서 보낸 댓글수를 여기서 업데이트한다
    public void catchCommentCount(int count){
        comment_tv.setText("" + count);
    }






























    //스크롤이 변화하였을때의 처리 - onCreate안의 스크롤리스너에서 여기를 호출
    public void scrollChanged(){

        try{
            float scrollY = scrollView.getScrollY();
            Log.d("SCSC", "scroll: " + scrollY);


            //최상단부 메뉴 그림의 스크롤에 따른 변환효과
            if(scrollY < 500){
                menuPager.setScaleX( 1 + (scrollY/2500) );
                menuPager.setScaleY( 1 + (scrollY/2500) );
                overTv.setAlpha(1 - (scrollY/500));
                menuPager.setTranslationY(scrollY*1.1f);

                menu_circleIndicator.setAlpha(1-scrollY/500);
                nextArrowIv.setTranslationX(scrollY);

                optionMenuLinear.setTranslationX(scrollY * 1.5f);
                optionMenuLinear.setAlpha(1-scrollY/500);

            }
            else if(scrollY < 1600){
                menuPager.setScaleX( 1.1f + (scrollY/5000) );
                menuPager.setScaleY( 1.1f + (scrollY/5000) );
                menuPager.setTranslationY(scrollY*1.1f);
                optionMenuLinear.setAlpha(1-scrollY/1600);
            }


            //식당소개부분의 작동범위
            if(900 < scrollY && scrollY <1500) {

                if (resAnimCount == 0){ //등장하지 않음
                    if(scrollY > scroll_Location_Check){//아래로 스크롤 했을때
                        resAnimCount = 1;
                        resPicLayout.setVisibility(View.VISIBLE);
                        resPicLayout.startAnimation(translateRightAnim);
                        restrantInfoLinear.setVisibility(View.VISIBLE);
                        restrantInfoLinear.startAnimation(fadeIn);
                    }
                }

                else if(resAnimCount==2){//등장이 완료되었을때

//                    if(scrollY < scroll_Location_Check){//위로 스크롤 했을때
//                        Log.d("SCSC", "위로스크롤");
//                        resAnimCount=3;
//                        resPicLayout.startAnimation(fadeOut);
//                        resPicIv.startAnimation(fadeOut);
//                        resPicIv2.startAnimation(fadeOut);
//                        restrantInfoLinear.startAnimation(fadeOut);
//                    }
                }

                scroll_Location_Check = scrollY; //스크롤의 위치를 체크한 뒤 다음 루프에서 검사
            }



        }catch(Exception e){Log.d("DDD", "오류: " + e);}

    }

    //메뉴 뷰페이저의 어댑터
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FoodPagerFragment.PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            //뷰페이저의 총 페이지수를 리턴한다
            return MENU_PAGER_NUM;
        }
    }

    //식당 뷰페이저의 어댑터
    private class ScreenSlidePagerAdapter2 extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter2(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ResPagerFragment.PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            //뷰페이저의 총 페이지수를 리턴한다
            return RES_PAGER_NUM;
        }
    }


    //애니매이션 리스너
    private class SlidingPageAnimationListener implements Animation.AnimationListener{
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d("SCSCL", "애니매이션End 리스너: " + resAnimCount);

//            if(resAnimCount==1){
//                resAnimCount=2;
//                Log.d("SCSCL", "이거 왜 안돼 시발");
//
//            }else if(resAnimCount==3){
//                //위로 스크롤한 경우
//                resPicLayout.setVisibility(View.INVISIBLE);
//                restrantInfoLinear.setVisibility(View.INVISIBLE);
//                resAnimCount=0;
//            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }


    //식당의 테마들을 하나의 텍스트로 합쳐서 반환
    public String resThemeStr(){

        String themeStr = "";
        int count = 0;

        Map map = resInfo_fb.res_theme;
        Iterator<String> keySetIterator = map.keySet().iterator();

        while (keySetIterator.hasNext()) {

            String theme = keySetIterator.next();

            if(count == 0){
                themeStr = theme;
                count++;
            }else{
                themeStr = themeStr + ", " + theme;
            }
        }//while

        return themeStr;
    }



}//소스끝



