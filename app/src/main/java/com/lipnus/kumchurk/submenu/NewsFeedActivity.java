package com.lipnus.kumchurk.submenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.fcm.SendFcm;
import com.lipnus.kumchurk.firebaseModel.MenuInfo_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.NewsFeed_fb;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;
import com.lipnus.kumchurk.firebaseModel.User_fb;
import com.lipnus.kumchurk.kum_class.CustomDialog;
import com.lipnus.kumchurk.kum_class.SimpleFunction;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsFeedActivity extends AppCompatActivity {

    //뉴스피드의 리스트뷰와 어뎁터
    ListView listview;
    NewsFeedListViewAdapter adapter;

    //현재 뉴스피드에 표시된 리뷰의 개수
    int nfCount = 0;

    //투표시에 호출한곳의 리스트번호(콜백이라 직접 전달하기 애매해서 여기를 통해서 전달)
    int listPosition;

    //어댑터나 다른 액티비티에서 이 액티비티에 넘겨주는 정보
    int heartCount;
    int fuckCount;

    //상단, 서브메뉴 컨트롤
    SubMenuControl smc;

    //다른 곳에서 여기를 finish()할 수 있도록 함.
    public static Activity NFActiviry;

    //리스트뷰 바닥에 닿는거 체크
    boolean lastItemVisibleFlag = false;

    //엑티비티를 호출한 개수
    int resumeCount=0;

    //Firebase 참조
    private Firebase mRef;

    //리뷰가 몇개인지(콜백의 끝나는 지점을 알기위해 필요)
    int reviewCount;
    int reviewCountCompare;

    //Firebase에서 받은 원본이 저장되는 리스트
    List<NewsFeed_fb> newsFeed_fbList;

    //그림 회전 다이얼로그
    CustomDialog customDialog;
    Context context;

    //CommentActivity로 갈 때 어디로가는지 저장해놓는다(CommentAdapter에서 조작)
    public int goCommentPosition;

    int rrr;
    int aaa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);



        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View thisView = this.getWindow().getDecorView() ;
        smc = new SubMenuControl(this, thisView, "NEWSFEED");

        //다른 곳에서 이 액티비티를 조작할 수 있게 함
        NFActiviry = NewsFeedActivity.this;


        //뉴스피드를 표시할 리스트뷰와 컨트롤을 위한 어댑터
        listview = (ListView) findViewById(R.id.nf_listview);

        //리스트뷰 Adapter 생성
        adapter = new NewsFeedListViewAdapter();
        listview.setAdapter(adapter);

        //서버에서 데이터를 받아옴
        connect_fb_menuReview();

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
                    addMenuReview();//5개추가
                }
            }

        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    } //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )

    //취소버튼
    @Override
    public void onBackPressed() {
        smc.backPress();
    }

    //재실행
    @Override
    protected void onPostResume() {
        super.onPostResume();


            Log.d("LSLS", "재접속");
            resumeCount++;

        if(resumeCount>1){
            //재실행의 경우 (CommentActivity를 갔다가 다시 돌아온는 경우가 대부분)
//            Log.d("CMCM", "재실행호출댓글번호: " + goCommentPosition);
//            refreshCommentCount(goCommentPosition);
        }

    }

    //상단바의 중간부분 타이틀을 터치하면 스크롤 위로 끌어올림
    public void onClicK_topmenu_title(View v){
        listview.smoothScrollToPosition( 0 );
    }







    //Firebase 접속(리뷰기준으로 찾아들어간다.
    public void connect_fb_menuReview(){

        //다이얼로그 켜기
        customDialog = new CustomDialog(this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //다이얼로그 뒷배경투명처리
        customDialog.setCancelable(true);
        customDialog.show(); // 보여주기

        //데이터들이 들어갈 리스트초기화
        newsFeed_fbList = new ArrayList<NewsFeed_fb>();


        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_review" );
        mRef.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    //리뷰갯수 초기화
                    reviewCount = 0;
                    reviewCountCompare = 0;


                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren() ) {

                        MenuReview_fb menuReview_fb = postSnapshot.getValue( MenuReview_fb.class );
                        menuReview_fb.key = postSnapshot.getKey();


                        Log.d("NNWW", reviewCount + " 리뷰: " + menuReview_fb.menu_id + " 키: " + menuReview_fb.key);

                        //빈걸 리스트에 넣어서 리스트에 인덱스를 하나 만든다(인덱스=reviewCount)
                        NewsFeed_fb newsFeed_fb = new NewsFeed_fb();
                        newsFeed_fbList.add(newsFeed_fb);
                        connect_fb_menuInfo(menuReview_fb, reviewCount);

                        reviewCount++;

                    }
                        Log.d("NNWW", "리뷰개수: " + reviewCount);


                }catch(Exception e){
                    Log.d("NNWW", "에러: " + e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void connect_fb_menuInfo(final MenuReview_fb menuReview_fb, final int count){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_info/" +menuReview_fb.menu_id );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    MenuInfo_fb menuInfo_fb = dataSnapshot.getValue( MenuInfo_fb.class );
                    menuInfo_fb.key = dataSnapshot.getKey();

                    Log.d("SSFF", "메뉴" + count);

                    //리뷰를 찾는다
                    connect_fb_resInfo(menuReview_fb, menuInfo_fb, count);

                }catch(Exception e){
                    Log.d("NNWW", "menuInfo에러: " + menuReview_fb.menu_id + " " + e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void connect_fb_resInfo(final MenuReview_fb menuReview_fb, final MenuInfo_fb menuInfo_fb, final int count){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/res_info/" + menuInfo_fb.res_id );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    ResInfo_fb resInfo_fb = dataSnapshot.getValue( ResInfo_fb.class );
                    resInfo_fb.key = dataSnapshot.getKey();

                    connect_fb_user(menuReview_fb, menuInfo_fb, resInfo_fb, count);

                    rrr++;
                    Log.d("NNWW", "식당count: "+ count + " rrr: " + rrr + " uid: " + menuReview_fb.uid);

                }catch(Exception e){
                    Log.d("NNWW", "에러: " + e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void connect_fb_user(final MenuReview_fb menuReview_fb, final MenuInfo_fb menuInfo_fb,
                                final ResInfo_fb resInfo_fb, final int count){

        Log.d("NNWW", count + " " + menuReview_fb.uid);

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/user" );
        mRef.orderByKey().equalTo( menuReview_fb.uid ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        reviewCountCompare++;

                        User_fb user_fb = postSnapshot.getValue(User_fb.class);
                        user_fb.uid = postSnapshot.getKey();

                        Log.d("NNWW", "a"+count + " " + menuReview_fb.uid + " 진행률: " + reviewCountCompare + "/" + reviewCount);


                        //리스트에 입력
                        newsFeed_fbList.get(count).menuReview = menuReview_fb;
                        newsFeed_fbList.get(count).menuInfo = menuInfo_fb;
                        newsFeed_fbList.get(count).resInfo = resInfo_fb;
                        newsFeed_fbList.get(count).user = user_fb;

                    }

                    //다운로드 끝
                    if (reviewCount == reviewCountCompare) {

                        Log.d("NNWW", "다운로드 끝!");

                        //리스트뷰 뒤집기
                        Collections.reverse(newsFeed_fbList);

                        //다이얼로그 제거
                        customDialog.dismiss();

                        addMenuReview();
                        Log.d("NNWW", "정렬 끝!");

                    }

                } catch (Exception e) {
                    Log.d("NNWW", "user에러: " + menuReview_fb.key + " " + e );
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }








    //CommentActivity로 갈때 어디로 갔는지 저장해 놓았다가, 갔다오면 거기 댓글수를 찾아서 업데이트 해줌
    public void refreshCommentCount(final int position){

        String reviewKey = newsFeed_fbList.get(position).menuReview.key;

        Log.d("CMCM", "받은 댓글번호: " + position);

        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/menu_review" );
        mRef.orderByKey().equalTo( reviewKey ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren() ) {

                        MenuReview_fb menuReview_fb = postSnapshot.getValue( MenuReview_fb.class );

                        Log.d("CMCM",  position + "번 댓글: " + menuReview_fb.comment.size() + "개" );
//
//                        Map commentMap = menuReview_fb.comment;
//                        commentMap.size();

                        //댓글부분만 업데이트 시켜줌
                        newsFeed_fbList.get(position).menuReview.comment = menuReview_fb.comment;
                        adapter.notifyDataSetChanged();

                    }

                    Log.d("NNWW", "리뷰개수: " + reviewCount);


                }catch(Exception e){
                    Log.d("NNWW", "에러: " + e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //Adapter에서 하트터치
    public void heartTouch(int position ){

        Log.d("NNWW", "리뷰위치: " + newsFeed_fbList.get(position).menuReview.memo );

        //adapter에 'call by refference'로 저장되어 있어서 원본을 바꾸면 adapter도 같은 데이터를 쓰기 때문에 적용이 된다.
        MenuReview_fb nowReview = newsFeed_fbList.get(position).menuReview;


        //이 리뷰에 눌러진 하트,뻐큐 정보
        Map heartMap = nowReview.heart;
        Map fuckMap = nowReview.fuck;


        //내가 DB에 '보낼' 하트, 뻐큐를 담고있는 map
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
            if(!GlobalApplication.getUser_id().equals(newsFeed_fbList.get(position).user.uid)){
                SendFcm.sendFcmData(newsFeed_fbList.get(position).user.token, GlobalApplication.getUser_nickname(),
                        "회원님의 " + newsFeed_fbList.get(position).menuInfo.menu_name + " 리뷰를 좋아합니다.", GlobalApplication.getUser_thumbnail());
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


        Log.d("NNWW", "하트누름");

        //업데이트
        adapter.notifyDataSetChanged();

    }

    //Adapter에서 빠큐터치
    public void fuckTouch(int position ){

        Log.d("NNWW", "리뷰위치: " + newsFeed_fbList.get(position).menuReview.memo );

        //adapter에 'call by refference'로 저장되어 있어서 원본을 바꾸면 adapter도 같은 데이터를 쓰기 때문에 적용이 된다.
        MenuReview_fb nowReview = newsFeed_fbList.get(position).menuReview;


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

        //업데이트
        adapter.notifyDataSetChanged();
    }

    //무한스크롤을 이용하여 5개씩 보여준다
    public void addMenuReview(){


        if(nfCount+5 > newsFeed_fbList.size()){
            Toast.makeText(getApplicationContext(), "마지막이예요", Toast.LENGTH_SHORT).show();
        }else{

            for(int i=nfCount; i<nfCount+5; i++){

                adapter.addItem(
                        newsFeed_fbList.get(i).menuInfo,
                        newsFeed_fbList.get(i).resInfo,
                        newsFeed_fbList.get(i).menuReview,
                        newsFeed_fbList.get(i).user
                );
            }

            nfCount = nfCount+5; //리스트의 개수 5개 추가
            adapter.notifyDataSetChanged(); //리스트 새로고침

        }


    }

    //댓글 개수의 업데이트를 체크해준다(CommentActivity가 끝나면서 호출해줌)
    public void catchComment(int listPosition){
        Log.d("CCMM", "캐치: " + listPosition);
        goCommentPosition = listPosition;
        refreshCommentCount(goCommentPosition);
    }



}
