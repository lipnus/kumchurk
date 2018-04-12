package com.lipnus.kumchurk.detailpage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.IVolleyResult;
import com.lipnus.kumchurk.IntroActivity;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.ScrollActivity;
import com.lipnus.kumchurk.VolleyConnect;
import com.lipnus.kumchurk.data.ReviewComment;
import com.lipnus.kumchurk.data.ReviewComment_JSON;
import com.lipnus.kumchurk.fcm.SendFcm;
import com.lipnus.kumchurk.firebaseModel.Comment_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.User_fb;
import com.lipnus.kumchurk.kum_class.CustomDialog;
import com.lipnus.kumchurk.kum_class.RestoreUserData;
import com.lipnus.kumchurk.kum_class.SimpleFunction;
import com.lipnus.kumchurk.submenu.NewsFeedActivity;
import com.lipnus.kumchurk.submenu.SubMenuControl;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    //댓글의 리스트뷰 어댑터
    ListView listview;
    CommentListViewAdapter adapter;


    //volley와 리스너 (volly는 독립적인 클래스로 구현)
    IVolleyResult mResultCallback = null;
    VolleyConnect volley;

    //체크
    ImageView checkIv;


    //리뷰번호
    String reviewNum;

    //댓글적는곳
    EditText commentEt;

    //이 리뷰에 해당하는 댓글정보
    ReviewComment_JSON rCJ;

    //다른 곳에서 여기를 finish()할 수 있도록 함.
    public static Activity CMActiviry;

    //상단, 서브메뉴 컨트롤
    SubMenuControl smc;

    //이 댓글 페이지가 아이템인지 정보를 받음(단일 페이지인 경우 0)
    int listPosition;

    //리뷰아이디(Firebase Server)
    String reviewId;

    boolean lastItemVisibleFlag = false;

    private Firebase mRef;

    //파이어베이스에서 받은 값들을 저장하고 있는 리스트
    List<Comment_fb> comment_fbList;
    List<User_fb> user_fbList;
    int comment_size;
    int comment_count;

    //리뷰 키
    String nowReviewKey;

    //리뷰 쓴놈토큰
    String nowReviewTocken;

    //데이터베이스 참조
    private DatabaseReference mDatabase;

    //커스텀 다이얼로그
    CustomDialog customDialog;

    //호출한 곳(newsfeed일때만 중요)
    String callFrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //현재의 뷰(서브메뉴에 넘김)
        View thisView = this.getWindow().getDecorView();

        //서브메뉴 생성
        smc = new SubMenuControl(this, thisView, "COMMENT", false);

        //앞의 엑티비티로부터 리뷰번호를 받는다(php Server)
        Intent iT = getIntent();
        listPosition = iT.getExtras().getInt("newsfeed_position");
        nowReviewKey = iT.getExtras().getString("review_key");
        nowReviewTocken = iT.getExtras().getString("review_token");
        callFrom = iT.getExtras().getString("callFrom");
        Log.d("CMCM", "받은 Intent: " + nowReviewKey);


        //초기설정
        initSetting();

        //다른 곳에서 이 액티비티를 조작할 수 있게 함
        CMActiviry = CommentActivity.this;

        //Volley 콜백함수
        initVolleyCallback();

        //리스트뷰 설정
        initList();

        //서버에서 데이터를 받아온다
//        connect();

        //데이터를 받을 곳
        comment_fbList = new ArrayList<>();
        user_fbList = new ArrayList<>();


        //서버에서 댓글과 각 댓글을 쓴 사용자 정보를 알아온다
        connect_fb_review();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(GlobalApplication.mainData_fbList==null){
            Toast.makeText(this, "새로고침", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, IntroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    //자질구래한 것들 초기설정
    public void initSetting(){

        checkIv = (ImageView) findViewById(R.id.cm_check_iv);
        commentEt = (EditText) findViewById(R.id.cm_et);

        Glide.with(this)
                .load( R.drawable.check )
                .into(checkIv);
        checkIv.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    //리스트 초기화
    public void initList(){

        //리스트뷰 Adapter 생성
        adapter = new CommentListViewAdapter();

        //알람를 표시할 리스트뷰와 컨트롤을 위한 어댑터
        listview = (ListView) findViewById(R.id.cm_listview);
        listview.setAdapter(adapter);

        //바닥에 닿는 것을 체크(지금은 일단 사용안함)
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
                }
            }

        });

    }

    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    //꺼질때 정보전달
    @Override
    protected void onStop() {

        if(callFrom.equals("NewsFeedListViewAdapter")){
            Log.d("CCMM", "캐치보냄: " + listPosition);
            NewsFeedActivity nfActivity = (NewsFeedActivity) NewsFeedActivity.NFActiviry;
            nfActivity.catchComment(listPosition);
        }

        super.onStop();

    }

    //상단바의 중간부분 타이틀을 터치하면 스크롤 위로 끌어올림
    public void onClicK_topmenu_title(View v){
        listview.smoothScrollToPosition( 0 );
    }

    //댓글 업로드 터치
    public void onClick_comment_up_lr(View v){
        if(commentEt.getText().toString().equals("")){
            Toast.makeText(getApplication(), "댓글을 입력해주세요", Toast.LENGTH_LONG).show();
        }else{
            pushComment( commentEt.getText().toString() );
            commentEt.setText("");

            //키보드닫기
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow( commentEt.getWindowToken(), 0);
        }

    }




    //firebase에서 댓글을 가져옴★
    public void connect_fb_review(){

        //다이얼로그 켜기
        customDialog = new CustomDialog(this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //다이얼로그 뒷배경투명처리
        customDialog.setCancelable(true);//끌 수 없다
        customDialog.show(); // 보여주기

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_review");
        mRef.orderByKey().equalTo( nowReviewKey ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("CMCM", "connect_fb_review");
                comment_fbList.clear();
                user_fbList.clear();

                comment_size = 0;
                comment_count = 0;

                try{

                    //이 리뷰의 댓글목록
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren() ) {

                        MenuReview_fb menuReview_fb = postSnapshot.getValue( MenuReview_fb.class );

                        Map map = menuReview_fb.comment;
                        Iterator<String> keySetIterator = map.keySet().iterator();

                        //끝을 알기위해 코멘트 갯수 조사
                        comment_size = map.size();

                        if(comment_size == 0){ //하나도 없으면 다음으로 진행되지 않으니 여기서 리프레쉬(댓글1개있다가 삭제해보린 경우)

                            //다이얼로그 제거
                            customDialog.dismiss();

                            //어뎁터에 있는 기존데이터들은 삭제
                            adapter.removeAllItem();
                            adapter.notifyDataSetChanged(); //리스트 새로고침
                            listview.setSelection(adapter.getCount() - 1); //가장 아래쪽으로 스크롤다운
                        }

                        //메뉴들을 찾는다
                        while (keySetIterator.hasNext()) {
                            String commentKey = keySetIterator.next();
                            connect_fb_comment(commentKey);
                        }
                    }//메뉴를 꺼내는 부분

                }catch(Exception e){
                    customDialog.dismiss();
                    Log.d("SSFF", "에러: " + e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void connect_fb_comment(String commentKey){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/comment");
        mRef.orderByKey().equalTo( commentKey ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren() ) {

                        Comment_fb comment_fb = postSnapshot.getValue( Comment_fb.class );
                        comment_fb.key = postSnapshot.getKey();

                        Log.d("CMCM", "댓글: " + comment_fb.comment_text + "  -" + comment_fb.uid + ", "
                                + comment_fb.key + ", "  + comment_fb.date);

                        connect_fb_user(comment_fb);
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

    public void connect_fb_user(final Comment_fb comment_fb){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/user/" + comment_fb.uid);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    //user_fb_cout를 호출한 횟수
                    comment_count++;

                    User_fb user_fb = dataSnapshot.getValue(User_fb.class);
                    user_fb.uid = comment_fb.uid;

                    Log.d("CMCM", "유저정보: " + user_fb.nickname + ", " + comment_count);

                    comment_fbList.add(comment_fb);
                    user_fbList.add(user_fb);


                    //다 돌았을 때
                    if(comment_count == comment_size){

                        Log.d("CMCM", "다운끝");
                        customDialog.dismiss();
                        addCommentItem();
                    }

                } catch (Exception e) {
                    customDialog.dismiss();
                    Log.d("CMCM", "엒라: " + e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //user와 comment 값들을 정렬한 뒤에 listview Item에 넣음
    public void addCommentItem(){

        //어뎁터에 있는 기존데이터들은 삭제
        adapter.removeAllItem();

        List<ReviewComment> reviewCommentList = new LinkedList<>();

        Log.d("SSFF", "addCommentItem()" );

        for(int i=0; i<comment_fbList.size(); i++){

            //입력용 객체(fb이전의 것 그대로 사용)
            ReviewComment reviewComment = new ReviewComment();

            try{
                reviewComment.comment_user_id = comment_fbList.get(i).uid;
                reviewComment.comment_user_nickname= user_fbList.get(i).nickname;
                reviewComment.comment_user_image = user_fbList.get(i).profile_image;
                reviewComment.comment_user_thumbnail = user_fbList.get(i).thumbnail;

                reviewComment.comment_key = comment_fbList.get(i).key;
                reviewComment.comment_memo = comment_fbList.get(i).comment_text;
                reviewComment.comment_updated_at = comment_fbList.get(i).date;

                reviewComment.review_key = nowReviewKey;

                //넣을 댓글의 날짜
                Long inputDate = Long.parseLong( reviewComment.comment_updated_at );
//                reviewCommentList.add(reviewComment);

                Log.d("CCCM", i + "번째");

                //처음 2개 세팅
                if(i==0){
                    reviewCommentList.add( reviewComment );
                    continue;
                }else if(i==1){
                    Long listDate = Long.parseLong( reviewCommentList.get(0).comment_updated_at );

                    if(inputDate <= listDate) {
                        reviewCommentList.add(0, reviewComment);
                        continue;
                    }else{
                        reviewCommentList.add(reviewComment);
                        continue;
                    }
                }

                Boolean inputOk = false;

                Log.d("CCCM", i + "번째 배치");

                //날짜를 정렬하면서 입력(리스트 앞쪽이 오래된 것, 날짜크기는 작음)
                for(int j=0; j<reviewCommentList.size() -1; j++){

                    //제일 앞에 끼워넣기
                     if(inputDate < Long.parseLong( reviewCommentList.get(0).comment_updated_at )){
                        reviewCommentList.add(0, reviewComment);
                        inputOk = true;
                        break;
                    }
                    else if( Long.parseLong( reviewCommentList.get(j).comment_updated_at ) <= inputDate  ){

                        //사이에 끼워넣기
                        if(inputDate < Long.parseLong( reviewCommentList.get(j+1).comment_updated_at )){

                            reviewCommentList.add(j+1, reviewComment);
                            inputOk = true;
                            break;
                        }
                    }

                }

                //위의 조건이 충족되지 않은 경우 이걸로 넣음(가장 최신, 가장 큰 날짜숫자)
                if(inputOk == false){
                    reviewCommentList.add(reviewComment);
                }


            }catch (Exception e){
                Log.d("SSFF", "시발: " + e);
            }

        }


        //정렬완료된걸 하나씩 리스트에 넣어준다
        for(ReviewComment reviewComment : reviewCommentList){
            adapter.addItem(reviewComment);
        }


        Log.d("CMCM", "리스트뷰 업데이트");
        adapter.notifyDataSetChanged(); //리스트 새로고침
        listview.setSelection(adapter.getCount() - 1); //가장 아래쪽으로 스크롤다운


        //ScrollActivity에 코멘트 개수를 전달해준다
        ScrollActivity scActivity = (ScrollActivity) ScrollActivity.SCActivity;
        scActivity.catchCommentCount(comment_fbList.size());
    }






    // 댓글을 업로드한다
    public void pushComment(String commentText){

        Comment_fb comment_fb = new Comment_fb();
        comment_fb.comment_text = commentText;
        comment_fb.uid = GlobalApplication.getUser_id();
        comment_fb.date = SimpleFunction.getTodayDate2();

        //FCM알람 전송(내가 쓴 글이 아닐경우. uid가 없어서 토큰으로 내것인지 판단)
        if(!GlobalApplication.getUser_token().equals(nowReviewTocken)){
            SendFcm.sendFcmData(nowReviewTocken, GlobalApplication.getUser_nickname() + "님의 댓글", commentText, GlobalApplication.getUser_thumbnail());
        }



        Log.d("CMCM", "댓글업로드: " + comment_fb.comment_text + ", " + comment_fb.date);

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea");
        String commentKey = mRef.child("comment").push().getKey(); //키 값을 얻는다

        //comment에 업데이트
        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/comment" );
        mRef.child( commentKey ).setValue(comment_fb);


        Map commentMap = new HashMap();
        commentMap.put(commentKey, true);

        //menu_review의 해당 리뷰에 업데이트
        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_review/" + nowReviewKey );
        mRef.child( "comment" ).updateChildren( commentMap );


    }

    // 댓글을 삭제한다
    public void deleteComment(String commentKey, String reviewKey){

        //comment삭제
        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/comment" );
        mRef.child( commentKey ).removeValue();

        //menu_review안의 comment삭제
        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_review/" + reviewKey + "/comment" );
        mRef.child( commentKey ).removeValue();

    }









    public void connect(){

        String url = "http://kumchurk.ivyro.net/app/download_comment.php";

        //어플리케이션의 정보가 날아갔을 때 프레퍼런스에서 읽어와서 복구
        if(GlobalApplication.getUser_id()==null || GlobalApplication.getUser_id().equals("")){
            RestoreUserData restoreUserData = new RestoreUserData(this);
            restoreUserData.restoreApplication();
        }

        //서버로 메뉴이름, 식당이름, 아이디을 보내서 그에 해당하는 데이터를 받아온다
        Map<String, String> params = new HashMap<>();
        params.put("user_id", GlobalApplication.getUser_id() );
        params.put("review_num", reviewNum );


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

                //변화된 댓글개수 정보를 뉴스피드액티비티에 전해준다
                sendCommentCountToNewsFeedActivity();
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
        rCJ = gson.fromJson(jsonStr, ReviewComment_JSON.class);
        addComment();

    } //다운받은 Json데이터들을 객체화

    public void connect_comment(){

        String url = "http://kumchurk.ivyro.net/app/upload_comment2.php";

        //서버로 메뉴이름, 식당이름, 아이디을 보내서 그에 해당하는 데이터를 받아온다
        Map<String, String> params = new HashMap<>();
        params.put("review_num", reviewNum );
        params.put("writer_id", GlobalApplication.getUser_id() );
        params.put("writer_nickname", GlobalApplication.getUser_nickname() );
        params.put("comment", commentEt.getText().toString() );
        params.put("updated_at", SimpleFunction.getTodayDate() );


        //값을 받아올 리스너, Context, url, post로 보낼 것들의 key와 value들을 담은 해쉬맵
        volley = new VolleyConnect(mResultCallback, this, url, params);
    } //댓글업로드 volley

    public void connect_delete(String cm_num){

        String url = "http://kumchurk.ivyro.net/app/upload_comment_delete.php";

        //서버로 메뉴이름, 식당이름, 아이디을 보내서 그에 해당하는 데이터를 받아온다
        Map<String, String> params = new HashMap<>();
        params.put("review_num", reviewNum );
        params.put("comment_num", cm_num);

        //값을 받아올 리스너, Context, url, post로 보낼 것들의 key와 value들을 담은 해쉬맵
        volley = new VolleyConnect(mResultCallback, this, url, params);
    } //댓글삭제 volley

    //받은 리뷰데이터를 리스트에 추가
    public void addComment(){

//        Log.d("ALAL", "카운트: " + cmCount + "개수: " + rCJ.getReviewComment().size());

        //리스트뷰 리셋
        adapter = new CommentListViewAdapter();
        listview.setAdapter(adapter);

        if(rCJ.getReviewComment() != null && rCJ.getReviewComment().size() >0){

            //댓글은 아주 많지는 않을 것이므로 일단 주~욱 띄운다
            //나중에 이 부분이 문제가 된다면 뉴스피드나 소식 부분처럼 끊어서 불러오게 하면 됨

            //아이템추가
            for(int i=0; i<rCJ.getReviewComment().size(); i++){
                adapter.addItem( rCJ.getReviewComment().get(i));
            }
        }

        adapter.notifyDataSetChanged(); //리스트 새로고침
        listview.setSelection(adapter.getCount() - 1); //가장 아래쪽으로 스크롤다운

    }



    //댓글개수 정보를 뉴스피드 액티비티에 전해준다
    public void sendCommentCountToNewsFeedActivity(){
        //이 엑티비티가 만약 Newsfeed에서 실행된 경우, Newsfeed에게 업데이트된 댓글 수를 보내준다(리스트번호, 댓글수)
        NewsFeedActivity nfActivity = (NewsFeedActivity) NewsFeedActivity.NFActiviry;

        int commentCount;

        //댓글이 있으면 개수반환, 댓글이 없으면(list가 null)이면 0반환
        if(rCJ.getReviewComment() != null){
            commentCount = rCJ.getReviewComment().size();
        }else{
            commentCount = 0;
        }

        Log.d("NNII", "newsFeed에게 정보를 전달: " + commentCount);

        //newsFeed에게 정보를 제공한다
        if(nfActivity != null){
//            nfActivity.catchComment(listPosition, commentCount);
        }
    }

}







