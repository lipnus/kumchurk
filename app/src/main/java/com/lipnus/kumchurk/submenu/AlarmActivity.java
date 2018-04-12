package com.lipnus.kumchurk.submenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.IVolleyResult;
import com.lipnus.kumchurk.IntroActivity;
import com.lipnus.kumchurk.MainActivity;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.VolleyConnect;
import com.lipnus.kumchurk.data.Alarm_JSON;
import com.lipnus.kumchurk.firebaseModel.Alarm_fb;
import com.lipnus.kumchurk.firebaseModel.Comment_fb;
import com.lipnus.kumchurk.firebaseModel.MenuInfo_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.User_fb;
import com.lipnus.kumchurk.kum_class.CustomDialog;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AlarmActivity extends AppCompatActivity {

    //알람의 리스트뷰 어댑터
    ListView listview;
    AlarmListViewAdapter adapter;


    //volley와 리스너 (volly는 독립적인 클래스로 구현)
    IVolleyResult mResultCallback = null;
    VolleyConnect volley;

    //알람정보 데이터
    Alarm_JSON alJSON;

    //현재 표시된 알람의 개수
    int alCount = 0;

     //상단, 서브메뉴 컨트롤
    SubMenuControl smc;

    //다른 곳에서 여기를 finish()할 수 있도록 함.
    public static Activity ALActiviry;

    boolean lastItemVisibleFlag = false;


    //Firebase 참조
    private Firebase mRef;

    //리뷰가 몇개인지(로그때문에 필요)
    int reviewCount;
    int reviewCountCompare;
    int testcount;

    //콜백의 완료를 체크하기 위해 필요한 카운터들
    int heartCount;
    int heartCompareCount;
    int commentCount;
    int commentCompareCount;

    //그림 회전 다이얼로그
    CustomDialog customDialog;

    //본인의 모든 알림 정보가 저장되는곳
    List<Alarm_fb> tempAlarm_fbList; //날짜별 정렬전
    List<Alarm_fb> alarm_fbList; //날짜별 정렬후


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        //노티피케이션에서 바로 여기만 켜진 경우 체크
        appOnCheck();

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //현재의 뷰(서브메뉴에 넘김)
        View thisView = this.getWindow().getDecorView();

        //서브메뉴 생성
        smc = new SubMenuControl(this, thisView, "ALARM");

        //다른 곳에서 이 액티비티를 조작할 수 있게 함
        ALActiviry = AlarmActivity.this;

        //Volley 콜백함수
        initVolleyCallback();

        //리스트뷰 설정
        initList();

        //서버에서 데이터를 받아온다
        connect_fb_reUserReview();

    }


    public void initList(){

        //리스트뷰 Adapter 생성
        adapter = new AlarmListViewAdapter();

        //알람를 표시할 리스트뷰와 컨트롤을 위한 어댑터
        listview = (ListView) findViewById(R.id.al_listview);
        listview.setAdapter(adapter);

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
                //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
                    //TODO 화면이 바닦에 닿을때 처리
                    Log.d("LILI", "인생바닥침");

                    addAlarm();//10개추가
                }
            }

        });

    }

    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    //취소버튼
    @Override
    public void onBackPressed() {
        smc.backPress();
    }

    //상단바의 중간부분 타이틀을 터치하면 스크롤 위로 끌어올림
    public void onClicK_topmenu_title(View v){
        listview.smoothScrollToPosition( 0 );
    }


    //앱이 꺼진 상태에서 노티피케이션에 의해 이 액티비티만 달랑 켜진 경우 처음 페이지로 이동시킨다
    public void appOnCheck(){

        //확인은 메인액티비티를 통해서 함
        MainActivity mnActivity = (MainActivity) MainActivity.MNActivity;
        if(mnActivity != null){
            Log.d("ALTT", "메인켜짐");
        }else{
            Log.d("ALTT", "메인꺼짐");
            Intent iT = new Intent(this, IntroActivity.class);
            startActivity(iT);
            finish();

        }
    }









    //Firebase에서 데이터를 받아온다
    public void connect_fb_reUserReview(){

        //다이얼로그 켜기
        customDialog = new CustomDialog(this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //다이얼로그 뒷배경투명처리
        customDialog.setCancelable(true);//끌 수 있다
        customDialog.show(); // 보여주기


        //저장공간 초기화
        tempAlarm_fbList = new LinkedList<>();

        //카운터 초키화
        heartCount = 0;
        heartCompareCount = 0;
        commentCount = 0;
        commentCompareCount = 0;


        //내가 쓴 모든 리뷰들
        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/re_user_review" );
        mRef.orderByKey().equalTo( GlobalApplication.getUser_id() ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    if(dataSnapshot.getValue() == null){
                        customDialog.dismiss();
                    }

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren() ) {

                        Map reviewKeyMap = postSnapshot.getValue(Map.class);
                        Iterator<String> keySetIterator = reviewKeyMap.keySet().iterator();

                        Log.d("PRPR", "사이즈: " + reviewKeyMap.size());


                        while (keySetIterator.hasNext()) {
                            reviewCount++;
                            String reviewKey = keySetIterator.next();
                            Log.d("PRPR", "리뷰키: " + reviewKey + " / " + reviewCount);
                            connect_fb_menuReview(reviewKey, reviewCount);
                        }//while
                    }//for
                }catch(Exception e){
                    Log.d("PRPR", "에러: " + e);
                    customDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                customDialog.dismiss();
            }
        });

    }

    public void connect_fb_menuReview( String reviewKey ,final int tt){

        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/menu_review/" + reviewKey );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    MenuReview_fb menuReview_fb = dataSnapshot.getValue( MenuReview_fb.class );
                    menuReview_fb.key = dataSnapshot.getKey();

                    connect_fb_menuInfo(menuReview_fb, tt);
                }catch(Exception e){
                    Log.d("PRPR", "에러: " + e);
                    customDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                customDialog.dismiss();
            }
        });
    }

    //A와 B로 분기
    public void connect_fb_menuInfo(final MenuReview_fb menuReview_fb ,final int tt){

        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/menu_info/" + menuReview_fb.menu_id  );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    //리뷰키->리뷰받기->메뉴받기 순서
                    //메뉴 먼저받는게 직관적이지만 메뉴키는 리뷰안에 있어서 리뷰 먼저 받고 그 다음으로 메뉴를 받고, 리뷰에 대한 정보를 처리해야함함

                    MenuInfo_fb menuInfo_fb = dataSnapshot.getValue(MenuInfo_fb.class);
                    menuInfo_fb.key = dataSnapshot.getKey();

                    testcount++;
                    Log.d("PRPR", "메뉴명: " + menuInfo_fb.menu_name + " / " + tt + " / " + menuReview_fb.key);


                    //여기까지는 내가 쓴 리뷰와 그에 맞는 메뉴까지 받았다. 이제 두가지로 나뉜다.
                    Iterator<String> keySetIterator;

                    //==========================================================================
                    //A. heart보낸 사람 uid찾기
                    //==========================================================================
                    Map<String, String> heartMap = menuReview_fb.heart;
                    keySetIterator = heartMap.keySet().iterator();

                    //이 리뷰가 받은 좋아요를 수집
                    int heartMapSize = heartMap.size();


                    if (heartMapSize == 0) { //이 리뷰에서는 받은 좋아요가 하나도 없다

                    } else {
                        //좋아요를 날린 사용자의 정보를 알아온다
                        while (keySetIterator.hasNext()) {
                            heartCount++;

                            String uid = keySetIterator.next();
                            String heartDate = heartMap.get(uid);
                            Log.d("PRPR", "uid전송: " + uid);
                            connect_fb_heart_user(menuInfo_fb, menuReview_fb, uid, heartDate);
                        }
                    }


                    //==========================================================================
                    //B. comment찾기 -> (comment 보낸사람 uid찾기)
                    //==========================================================================
                    Map commentMap = menuReview_fb.comment;
                    keySetIterator = commentMap.keySet().iterator();

                    //이 리뷰가 받은 좋아요를 수집
                    int commentMapSize = heartMap.size();


                    if (commentMapSize == 0) { //이 리뷰에서는 댓글이 없다

                    } else {
                        //좋아요를 날린 사용자의 정보를 알아온다
                        while (keySetIterator.hasNext()) {

                            commentCount++;
                            String commentKey = keySetIterator.next();
                            Log.d("PRPR", "보낸사람 uid전송: " + commentKey);
                            connect_fb_comment(menuInfo_fb, menuReview_fb, commentKey);
                        }
                    }


                } catch (Exception e) {
                    Log.d("PRPR", "에러: " + e);
                    customDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                customDialog.dismiss();
            }
        });
    }






    //A(끝)
    public void connect_fb_heart_user(final MenuInfo_fb menuInfo_fb, final MenuReview_fb menuReview_fb, String uid, final String heartDate){

        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/user" );
        mRef.orderByKey().equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren() ) {

                        //유저정보를 받아옴
                        User_fb user_fb = postSnapshot.getValue( User_fb.class );
                        user_fb.uid = postSnapshot.getKey();

                        Log.d("PRPR", "나한테 하트누른애: "+ user_fb.nickname);

                        //하트용 Alarm객체를 생성
                        Alarm_fb alarm_fb = new Alarm_fb(
                                user_fb,
                                menuReview_fb,
                                menuInfo_fb,
                                "heart",
                                heartDate);

                        //순서 생각하지 말고 일단 집어넣음
                        tempAlarm_fbList.add( alarm_fb );

                        heartCompareCount++;
                        if(heartCount == heartCompareCount){
                            Log.d("PRPR", "하트전송끝!");
                        }


                    }
                }catch(Exception e){
                    Log.d("NNWW", "에러: " + e);
                    customDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                customDialog.dismiss();
            }
        });

    }


    //B-1
    public void connect_fb_comment(final MenuInfo_fb menuInfo_fb, final MenuReview_fb menuReview_fb, String commentKey){

        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/comment");
        mRef.orderByKey().equalTo( commentKey ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren() ) {

                        Comment_fb comment_fb = postSnapshot.getValue( Comment_fb.class );
                        comment_fb.key = postSnapshot.getKey();

                        connect_fb_comment_user(menuInfo_fb, menuReview_fb, comment_fb, comment_fb.uid);
                    }
                }catch(Exception e){
                    customDialog.dismiss();
                    Log.d("CMCM", "에러: " + e);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //B-2(끝)
    public void connect_fb_comment_user(final MenuInfo_fb menuInfo_fb, final MenuReview_fb menuReview_fb, final Comment_fb comment_fb, String uid){

        mRef = new Firebase( "https://fireapp-9ef47.firebaseio.com/Korea/user" );
        mRef.orderByKey().equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren() ) {

                        //유저정보를 받아옴
                        User_fb user_fb = postSnapshot.getValue( User_fb.class );
                        user_fb.uid = postSnapshot.getKey();

                        Log.d("PRPR", "나한테 댓글쓴애: "+ user_fb.nickname + ", " + user_fb.uid);

                        String commentDate = comment_fb.date.substring(0, 12);

                        //댓글용 Alarm객체를 생성
                        Alarm_fb alarm_fb = new Alarm_fb(
                                user_fb,
                                menuReview_fb,
                                menuInfo_fb,
                                comment_fb,
                                "comment",
                                commentDate);

                        //순서 생각하지 말고 일단 집어넣음
                        tempAlarm_fbList.add( alarm_fb );

                        commentCompareCount++;
                        if(commentCount==commentCompareCount){
                            Log.d("PRPR", "댓글전송 끝!");

                            customDialog.dismiss();
                            sortAlarm();
                        }
                    }
                }catch(Exception e){
                    Log.d("NNWW", "에러: " + e);
                    customDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                customDialog.dismiss();
            }
        });

    }








    public void sortAlarm(){

        //집어넣을 공간 초기화
        alarm_fbList = new LinkedList<>();


        //순서대로 집어넣는다
        for(int i=0; i<tempAlarm_fbList.size(); i++){

            //넣을놈 (tempAlarm_fbList는 아직 정렬이 안된상태다)
            Alarm_fb tempAlarm = tempAlarm_fbList.get(i);

            //넣을 것의 날짜
            Long inputDate = Long.parseLong( tempAlarm_fbList.get(i).date );

            //처음 2개 세팅
            if(i==0){
                alarm_fbList.add( tempAlarm );
                continue;
            }else if(i==1){
                Long listDate = Long.parseLong( alarm_fbList.get(0).menuReview.date );

                if(inputDate <= listDate) {
                    alarm_fbList.add(0, tempAlarm );
                    continue;
                }else{
                    alarm_fbList.add( tempAlarm );
                    continue;
                }
            }//처음 2개 세팅


            //날짜를 정렬하면서 입력(리스트 앞쪽이 오래된 것, 날짜크기는 작음)
            Boolean inputOk = false;
            for(int j=0; j<alarm_fbList.size() -1; j++){

                //제일 앞에 끼워넣기
                if(inputDate < Long.parseLong( alarm_fbList.get(j).date )){
                    alarm_fbList.add(0, tempAlarm);
                    inputOk = true;
                    break;
                }
                else if( Long.parseLong( alarm_fbList.get(j).date ) <= inputDate  ){

                    //사이에 끼워넣기
                    if(inputDate < Long.parseLong( alarm_fbList.get(j+1).date )){

                        alarm_fbList.add(j+1, tempAlarm);
                        inputOk = true;
                        break;
                    }
                }
            }//for

            //위의 조건이 충족되지 않은 경우 이걸로 넣음(가장 최신, 가장 큰 날짜숫자)
            if(inputOk == false){
                alarm_fbList.add( tempAlarm );
            }

        }//for(i)


        for(int i=0; i<alarm_fbList.size(); i++){
            Log.d("PRPR", "정렬후날짜: " + alarm_fbList.get(i).date);
        }

        //날짜가 오름차순이니 내림차순으로 변경해준다
        Collections.reverse(alarm_fbList);

        //넣자!
        addAlarm();
    }


    //무한스크롤을 이용하여 5개씩 보여준다
    public void addAlarm(){

        //알람이 1개이상 존재하는 경우
        if(alarm_fbList != null && alarm_fbList.size() > 0){

            //더이상 추가할 것이 없음
            if(alCount == alarm_fbList.size()){
                Toast.makeText(getApplicationContext(), "아쉽지만 여기까지입니다", Toast.LENGTH_SHORT).show();
            }

            //15개가 되지 않을때 남은 것들 추가
            else if( (alarm_fbList.size() - alCount) < 15 ){
                for(int i=alCount; i<alarm_fbList.size(); i++){

                    adapter.addItem( alarm_fbList.get(i) );
                }
                alCount = alJSON.alarmData.size();
            }

            //15개씩 추가
            else{
                for(int i=alCount; i<alCount+15; i++){
                    adapter.addItem( alarm_fbList.get(i) );
                }
                alCount=alCount+15;
            }
            adapter.notifyDataSetChanged(); //리스트 새로고침
        }

    }








    public void connect(){

        String url = "http://kumchurk.ivyro.net/app/download_alarm2.php";

        //서버로 메뉴이름, 식당이름, 아이디을 보내서 그에 해당하는 데이터를 받아온다
        Map<String, String> params = new HashMap<>();
        params.put("uid", GlobalApplication.getUser_id() );


        //값을 받아올 리스너, Context, url, post로 보낼 것들의 key와 value들을 담은 해쉬맵
        volley = new VolleyConnect(mResultCallback, this, url, params);
    } //데이터 다운로드 volley
    void initVolleyCallback(){
        mResultCallback = new IVolleyResult() {
            @Override
            public void notifySuccess(String response) {
                //전송의 결과를 받는 부분
                Log.d("ALAL", response);
                jsonToJava(response);
            }

            @Override
            public void notifyError(VolleyError error) {
                //전송 시 에러가 생겼을 때 받는 부분
                Log.d("VOVO", "(기본)에러: "+ error);
            }
        };
    } //데이터 다운로드 callback
    void jsonToJava(String jsonStr){
        Log.d("VOVO", "jsonToJava()");

        Gson gson = new Gson();
        alJSON = gson.fromJson(jsonStr, Alarm_JSON.class);
        addAlarm();

    } //다운받은 Json데이터들을 객체화

}
