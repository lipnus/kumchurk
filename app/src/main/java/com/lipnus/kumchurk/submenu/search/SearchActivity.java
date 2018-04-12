package com.lipnus.kumchurk.submenu.search;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.firebaseModel.MenuInfo_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;
import com.lipnus.kumchurk.firebaseModel.Search_fb;
import com.lipnus.kumchurk.firebaseModel.User_fb;
import com.lipnus.kumchurk.kum_class.CustomDialog;
import com.lipnus.kumchurk.kum_class.SimpleFunction;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    EditText searchEt;
    View searchV;

    //리스트뷰와 어댑터
    ListView listview;
    Search_Res_ListViewAdapter adapter;

    //Firebase 참조
    private Firebase mRef;

    //나온 검색어의 개수
    int slCount=0;

    //리스트뷰 바닥터치 확인
    boolean lastItemVisibleFlag = false;

    //뷰페이저
    private ViewPager searchPager;
    private Search_ViewPager_Adapter searchPagerAdapter;
    int pagerPosition; //(0,1,2)

    //뷰페이저 위의 탭뷰
    TabLayout tabview;

    int userCount;
    int menuCount;
    int menuCompareCount;
    int resCount;

    //메뉴에 걸리는게 하나도 없을 경우 전체 검색이 끝났을 때 다이얼로그를 종료시키기 위해서
    int searchCount;
    int searchCompareCount;

    CustomDialog customDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        context = this;

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //EditText 조작
        searchEditText();

        //뷰페이저
        searchPager = (ViewPager) findViewById(R.id.searchPager);
//        ParallaxPagerTransformer pt = new ParallaxPagerTransformer((R.id.search_listview));
//        pt.setBorder(4);
//        pt.setSpeed(0.3f);
//        searchPager.setPageTransformer(true, pt);


        //탭뷰
        tabview = (TabLayout) findViewById(R.id.search_tabview);
        tabview.setupWithViewPager(searchPager);


        GlobalApplication.search_fbList = new LinkedList<>();
        GlobalApplication.search_resInfoList = new ArrayList<>();
        GlobalApplication.search_userList = new ArrayList<>();
        pagerPosition = 0; //뷰페이저 위치의 초깃값은 0

        //초깃값으로 전체 유저를 보여준다
        connect_fb_user("");

        //뷰페이저 초기화
        pagerReset(null, null, null);

    }


    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.search_translate_left);
    }


    //왼쪽 상단의 뒤로가기
    public void onClick_BacktoMain(View v){
        finish();
        overridePendingTransition(R.anim.fadein, R.anim.search_translate_left);
    }


    //뷰페이서 재설정
    public void pagerReset(String title1, String title2, String title3){
        searchPagerAdapter = new Search_ViewPager_Adapter( getFragmentManager() );
        searchPagerAdapter.chageTabTitle(title1, title2, title3);
        searchPager.setAdapter(searchPagerAdapter);


        //페이지
        searchPager.setCurrentItem(pagerPosition);

        //뷰페이저에 변화가 있을때의 리스너
        searchPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //뷰페이저가 넘어갈때마다 pagerPosition에다가 뷰페이저의 현재위치를 저장
                pagerPosition = position;
                Log.d("SSCC", "페이지번호: "+ pagerPosition);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //search EditText 조작
    public void searchEditText(){

        //포커스를 얻었을 때
        searchEt = (EditText) findViewById(R.id.search_et);

        searchEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b == true){
                    Log.d("FFCC", "포커스 얻음");
//                    searchV.setVisibility(View.VISIBLE);
                }
            }
        });


        //텍스트에 변화가 있을 때
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchCode = searchEt.getText().toString();
                if(searchCode.equals("")){
                    GlobalApplication.search_resInfoList = new ArrayList<ResInfo_fb>();
                    GlobalApplication.search_fbList = new LinkedList<Search_fb>();
                    Log.d("SSCC", "리셋");
                    //초깃값으로 전체 유저를 보여준다
                    connect_fb_user("");
//                    pagerReset("메뉴", "식당", "유저");

                }else{
                    //즉각검색은 과부하가 많이 걸려서 하지 않기로...
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });



        //검색버튼 눌렀을 때
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                //검색어가져오기, 키보드끄기
                String searchCode = searchEt.getText().toString();
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

                switch (i) {
                    case EditorInfo.IME_ACTION_SEARCH:

                        //검색버튼
                        if(searchCode.equals("")){
                            GlobalApplication.search_resInfoList = new ArrayList<>();
                            GlobalApplication.search_fbList = new LinkedList<Search_fb>();
                            GlobalApplication.search_userList = new ArrayList<User_fb>();


                        }else{

                            //다이얼로그
                            customDialog = new CustomDialog(context);
                            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //다이얼로그 뒷배경투명처리
                            customDialog.setCancelable(false);
                            customDialog.show(); // 보여주기

                            //식당검색요청
                            GlobalApplication.search_resInfoList = new ArrayList<>();
                            connect_fb_resInfo( searchCode );

                            //메뉴검색요청
                            GlobalApplication.search_fbList = new LinkedList<Search_fb>();
                            connect_fb_menuInfo( searchCode );

                            //유저검색요청
                            GlobalApplication.search_userList = new ArrayList<User_fb>();
                            connect_fb_user( searchCode );
                        }
                        imm.hideSoftInputFromWindow( searchEt.getWindowToken(), 0); //키보드끄기
                        break;
                    default:

                        //엔터키
                        if(searchCode.equals("")){
                            GlobalApplication.search_resInfoList = new ArrayList<>();
                            GlobalApplication.search_fbList = new ArrayList<>();

                        }else{

                            //다이얼로그
                            customDialog = new CustomDialog(context);
                            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //다이얼로그 뒷배경투명처리
                            customDialog.setCancelable(false);
                            customDialog.show(); // 보여주기

                            //식당검색요청
                            GlobalApplication.search_resInfoList = new ArrayList<>();
                            connect_fb_resInfo(searchCode); //검색요청

                            //메뉴검색요청
                            GlobalApplication.search_fbList = new LinkedList<Search_fb>();
                            connect_fb_menuInfo( searchCode );

                            //유저검색요청
                            GlobalApplication.search_userList = new ArrayList<User_fb>();
                            connect_fb_user( searchCode );
                        }
                        imm.hideSoftInputFromWindow( searchEt.getWindowToken(), 0); //키보드끄기
                        return false;
                }
                return true;


            }
        });

    }




    //메뉴검색
    public void connect_fb_menuInfo(final String searchCode){

        Log.d("SSCC", "connect_fb_menuInfo 호출: " + searchCode);

        menuCount = 0;
        menuCompareCount = 0;
        searchCount = 0;

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_info");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                try{
                    searchCount++;

                    if(searchCount > 10000){
                        //만개면 대충 다 했다치고 끈다
                        customDialog.dismiss();
                    }

                    MenuInfo_fb menuInfo_fb = dataSnapshot.getValue( MenuInfo_fb.class );
                    menuInfo_fb.key = dataSnapshot.getKey();

                    if(menuInfo_fb.menu_name == null ){
                        Log.d("SSCC", "이름없는메뉴: " + menuInfo_fb.key );
                    }else{

                        //검색어를 포함하는 식당이름이 있을 경우
                        if(menuInfo_fb.menu_name.contains( searchCode )){

                            menuCount++;

                            Map map = menuInfo_fb.review_id;
                            Iterator<String> keySetIterator = map.keySet().iterator();


                            //리뷰가 없는경우(바로 식당정보 찾는다)
                            if(map.size() == 0){
                                connect_fb_resInfo_2(menuInfo_fb, null);
                            }

                            //리뷰가 있는경우(리뷰정보 찾고 식당정보로 넘어간다)
                            else{

                                //리뷰는 진리의 랜덤뽑기
                                int rotto = SimpleFunction.getRandom(0, map.size()-1 );
                                int count=0;

                                Log.d("SSCC", "로또: " + rotto );

                                //리뷰를 찾는다
                                while (keySetIterator.hasNext()) {
                                    String reviewKey = keySetIterator.next();

                                    if(count==rotto){
                                        Log.d("SSCC", "로또성공, 리뷰키: " + reviewKey );
                                        connect_fb_menuReview(reviewKey, menuInfo_fb);
                                    }
                                    count++;
                                }//while
                            }//else

                        }//if(검색어에 포함되는 식당이름이 존재)
                    }//if 메뉴이름 null 에러피하기
                }
                catch(Exception e){
                    Log.d("SSCC", "connect_fb_menuInfo에러: " + e);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void connect_fb_menuReview(final String reviewKey, final MenuInfo_fb menuInfo_fb){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_review/" + reviewKey );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    MenuReview_fb menuReview_fb = dataSnapshot.getValue( MenuReview_fb.class );
                    menuReview_fb.key = dataSnapshot.getKey();
                    Log.d("SSCC", "리뷰찾기");
                    connect_fb_resInfo_2(menuInfo_fb, menuReview_fb);

                }catch(Exception e){
                    Log.d("SSCC", "connect_fb_menuReview에러: " + e);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void connect_fb_resInfo_2(final MenuInfo_fb menuInfo_fb, final MenuReview_fb menuReview_fb){



        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/res_info/" + menuInfo_fb.res_id);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                customDialog.dismiss();
                menuCompareCount++;

                try{
                    //이건 전역적으로 선언된 것
                    ResInfo_fb resInfo_fb = dataSnapshot.getValue( ResInfo_fb.class );
                    resInfo_fb.key = dataSnapshot.getKey();


                    //===============================================================================
                    //메뉴가 속한 식당을 검색결과에 추가한다(중복체크 후)
                    //===============================================================================
                    boolean resOverlay = false;

                    for(int i=0; i<GlobalApplication.search_resInfoList.size(); i++){
                        if(resInfo_fb.res_name.equals( GlobalApplication.search_resInfoList.get(i).res_name )){
                            resOverlay = true;
                            break;
                        }//if
                    }//for

                    if(resOverlay==false){
                        resCount++;
                        GlobalApplication.search_resInfoList.add(resInfo_fb);
                        //뷰페이저에 띄우는 검색결과는 아래쪽의 메뉴의 결과와 함께 보여준다
                    }
                    //===============================================================================


                    //리뷰(사진)있음(앞쪽부터 채운다)
                    if(menuReview_fb != null){

                        Search_fb search_fb = new Search_fb(resInfo_fb, menuInfo_fb, menuReview_fb );
                        GlobalApplication.search_fbList.add( 0, search_fb );
                    }
                    else{ //리뷰(사진)없음(뒷쪽부터 채운다)

                        Search_fb search_fb = new Search_fb(resInfo_fb, menuInfo_fb, new MenuReview_fb());
                        GlobalApplication.search_fbList.add( search_fb );
                    }

                    //뷰페이저를 리셋한다
                    if(menuCompareCount >= menuCount) {
                        if (userCount == 0) {
                            pagerReset("메뉴(" + menuCount + ")", "식당(" + resCount + ")", null);
                        } else {
                            pagerReset("메뉴(" + menuCount + ")", "식당(" + resCount + ")", "유저(" + userCount + ")");
                        }
                    }


                    Log.d("SSCC", "뷰페이저 안의 리스트뷰에 입력: " + menuInfo_fb.menu_name );

               }catch(Exception e){
                    Log.d("SSCC", "connect_fb_resInfo_2에러: " + e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }




    //식당검색
    public void connect_fb_resInfo(final String searchText){

        resCount = 0;

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/res_info");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                try{
                    ResInfo_fb resInfo_fb = dataSnapshot.getValue( ResInfo_fb.class );
                    resInfo_fb.key = dataSnapshot.getKey();

                    //검색어를 포함하는 식당이름이 있을 경우
                    if(resInfo_fb.res_name.contains(searchText)){

                        resCount++;
                        Log.d("SSCC", "식당: " + resInfo_fb.res_category);
                        GlobalApplication.search_resInfoList.add(resInfo_fb);

                        pagerReset(null, "식당(" + resCount + ")", null);
                    }
                }catch(Exception e){
                    Log.d("SSCC", "에러!!: " + e);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //유저검색
    public void connect_fb_user(final String searchText){

        Log.d("SSCC", "호출");

        //일단 하나도 표시 안하게 바꿈
        pagerReset(null, null, "유저");

        userCount = 0;

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/user");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                try{
                    User_fb user_fb  = dataSnapshot.getValue( User_fb.class );
                    user_fb.uid = dataSnapshot.getKey();


                    //검색어를 포함하는 유저가 있는경우
                    if(user_fb.nickname.contains(searchText)){

                        userCount++;
                        Log.d("SSCC", "유저: " + user_fb.nickname);
                        GlobalApplication.search_userList.add(user_fb);

                        pagerReset(null, null, "유저(" + userCount + ")");
                    }
                }catch (Exception e){
                    Log.d("SSCC", "connect_fb_user에러: " + e);
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
