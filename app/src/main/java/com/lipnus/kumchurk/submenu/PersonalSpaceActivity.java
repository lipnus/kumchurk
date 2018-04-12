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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.kcode.bottomlib.BottomDialog;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.IVolleyResult;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.VolleyConnect;
import com.lipnus.kumchurk.data.PersonalReview_JSON;
import com.lipnus.kumchurk.detailpage.ProfileImgActivity;
import com.lipnus.kumchurk.detailpage.ProfileModifyActivity;
import com.lipnus.kumchurk.firebaseModel.MenuInfo_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.Profile_fb;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;
import com.lipnus.kumchurk.firebaseModel.User_fb;
import com.lipnus.kumchurk.kum_class.CustomDialog;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class PersonalSpaceActivity extends AppCompatActivity {

    //알람의 리스트뷰 어댑터
    ListView listview;
    PersonalSpaceListViewAdapter adapter;


    //volley와 리스너 (volly는 독립적인 클래스로 구현)
    IVolleyResult mResultCallback = null;
    IVolleyResult mResultCallback2 = null;
    VolleyConnect volley;

    //프로필부분
    ImageView faceIv;
    TextView nicknameTv;
    TextView userinfoTv;

    TextView modifyProfileTv;

    //내 리뷰 데이터
    PersonalReview_JSON rvJ;

    //현재 표시된 알람의 개수
    int rvCount = 0;

    //상단, 서브메뉴 컨트롤
    SubMenuControl smc;

    //다른 곳에서 여기를 finish()할 수 있도록 함.
    public static Activity PSActiviry;

    //컨텍스트
    Context context;

    //새로고침(삭제 후에 true로 바뀌며, 새로 connect함)
    boolean refreshList = false;

    //리스트바닥닿기체크
    boolean lastItemVisibleFlag = false;


    //Firebase 참조
    private Firebase mRef;

    //리뷰가 몇개인지(콜백의 끝나는 지점을 알기위해 필요)
    int reviewCount;
    int reviewCountCompare;
    int testcount;

    //그림 회전 다이얼로그
    CustomDialog customDialog;

    //파이어베이스에서 받은 데이터
    User_fb user_fb;

    //누구의 정보인지 앞의 인텐트로부터 받아옴
    String uid;

    List<Profile_fb> profile_fbList_temp; //정렬전
    List<Profile_fb> profile_fbList;

    //재실행 횟수
    int refreshCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myreivew);

        //이 창이 이미 떠있다면 꺼준다
        PersonalSpaceActivity psActivity = (PersonalSpaceActivity) PersonalSpaceActivity.PSActiviry;
        if(psActivity!=null){
            Log.d("PRPR", "중복 엑티비티 종료");
            psActivity.finish();
        }

        //다른 곳에서 이 액티비티를 조작할 수 있게 함
        PSActiviry = PersonalSpaceActivity.this;


        //앞의 엑티비티로부터 값을 받아온다
        Intent iT = getIntent();
        uid = iT.getExtras().getString("uid");

        Log.d("PRPR", "받은 uid:" + uid);

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context= this;

        //현재의 뷰(서브메뉴에 넘김)
        View thisView = this.getWindow().getDecorView();

        //서브메뉴 생성(내꺼면 석삼메뉴쓰고, 아닌경우는 안쓴다)
        if(uid.equals(GlobalApplication.getUser_id())){
            smc = new SubMenuControl(this, thisView, "PERSONAL SPACE");
        }else{
            smc = new SubMenuControl(this, thisView, "PERSONAL SPACE", false);
        }




        //Volley 콜백함수
        initVolleyCallback();
        initVolleyCallback2();

        //리스트뷰 설정
        initList();

        //서버에서 데이터를 받아온다
        connect_fb_user();


    }

    //초기세팅
    public void initList() {

        //리스트뷰 Adapter 생성
        adapter = new PersonalSpaceListViewAdapter();

        //리뷰를 표시할 리스트와 어댑터
        listview = (ListView) findViewById(R.id.rv_listview);

        //프로필부분을 헤더로 추가
        View header = getLayoutInflater().inflate(R.layout.header_myreview, null, false);
        listview.addHeaderView(header);

        //어댑터랑 연결
        listview.setAdapter(adapter);

        //스크롤체크
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
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
                    //TODO 화면이 바닦에 닿을때 처리
                    Log.d("LILI", "인생바닥침");

                    addReview();//추가
                }
            }

        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(refreshCount > 0){
            Log.d("PRPR", "프로필 새로고침");
            connect_fb_refresh();
        }
        refreshCount++;
    }

    //글씨체
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    } //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )

    //취소버튼
    @Override
    public void onBackPressed() {
        smc.backPress();
    }

    //상단바의 중간부분 타이틀을 터치하면 스크롤 위로 끌어올림
    public void onClick_topmenu_title(View v) {
        listview.smoothScrollToPosition(0);
    }


    //프사를 터치
    public void onClick_mrf_profile(View v){

        Intent iT = new Intent(this, ProfileImgActivity.class);
        iT.putExtra("nickname", user_fb.nickname );
        iT.putExtra("image_path", user_fb.profile_image );
        startActivity(iT);
    }

    //프로필수정
    public void onClick_mrf_modify_profile(View v){
        Intent iT = new Intent(this, ProfileModifyActivity.class);
        startActivity(iT);
    }






    //Firebase에서 데이터를 받아온다
    public void connect_fb_user(){

        //다이얼로그 켜기
        customDialog = new CustomDialog(this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //다이얼로그 뒷배경투명처리
        customDialog.setCancelable(true);//끌 수 없다
        customDialog.show(); // 보여주기

        //입력공간 초기화
        user_fb = new User_fb();
        profile_fbList_temp = new LinkedList<>();


        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/user/" + uid );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    //유저정보를 받아옴
                    user_fb = dataSnapshot.getValue( User_fb.class );
                    user_fb.uid = uid;

                    Log.d("PRPR", "유저: "+ user_fb.nickname);

                    //이 유저의 리뷰를 다 받아온다
                    connect_fb_reUserReview();

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

    public void connect_fb_reUserReview(){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/re_user_review/" + uid );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String catchErrorKey="?";
                try{
                    Log.d("PRPR", "성공");

                    //리뷰갯수 초기화
                    reviewCount = 0;
                    reviewCountCompare = 0;
                    testcount = 0;

                    //리뷰 하나도 없음
                    if(dataSnapshot.getValue(Map.class)==null){
                        Log.d("PRPR", "비었다");
                        customDialog.dismiss();
                        profileSetting();

                    //리뷰 있음
                    }else{
                        Map reviewKeyMap = dataSnapshot.getValue(Map.class);
                        Iterator<String> keySetIterator = reviewKeyMap.keySet().iterator();
                        Log.d("PRPR", "사이즈!: " + reviewKeyMap.size());

                        while (keySetIterator.hasNext()) {
                            reviewCount++;
                            String reviewKey = keySetIterator.next();
                            catchErrorKey = reviewKey;

                            Log.d("PRPR", "리뷰키: " + reviewKey + " / " + reviewCount);
                            connect_fb_menuReview(reviewKey, reviewCount);
                        }//while
                    }
                }catch(Exception e){
                    Log.d("PRPR", "에러reuserReview: " + e + " 에러리뷰키: " + catchErrorKey);
                    reviewCount--; //전체개수를 줄여버린다
                    customDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                customDialog.dismiss();
            }
        });
    }

    public void connect_fb_menuReview(final String reviewKey , final int tt){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_review/" + reviewKey );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    MenuReview_fb menuReview_fb = dataSnapshot.getValue( MenuReview_fb.class );
                    menuReview_fb.key = dataSnapshot.getKey();
                    connect_fb_menuInfo(menuReview_fb, tt);

                }catch(Exception e){
                    Log.d("PRPR", "에러review: " + e + " 에러키: " + reviewKey);
                    reviewCount--; //전체개수를 줄여버린다
                    customDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                customDialog.dismiss();
            }
        });
    }

    public void connect_fb_menuInfo(final MenuReview_fb menuReview_fb ,final int tt){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_info/" + menuReview_fb.menu_id );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    MenuInfo_fb menuInfo_fb = dataSnapshot.getValue(MenuInfo_fb.class);
                    menuInfo_fb.key = dataSnapshot.getKey();

                    testcount++;
                    Log.d("PRPR", "메뉴명: " + menuInfo_fb.menu_name + " / " + tt + " / " + menuReview_fb.key);

                    //리뷰를 찾는다
                    connect_fb_resInfo(menuReview_fb, menuInfo_fb, tt);

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

    public void connect_fb_resInfo(final MenuReview_fb menuReview_fb, final MenuInfo_fb menuInfo_fb, final int tt){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/res_info/" + menuInfo_fb.res_id );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    reviewCountCompare++;
                    ResInfo_fb resInfo_fb = dataSnapshot.getValue( ResInfo_fb.class );
                    resInfo_fb.key = dataSnapshot.getKey();


                    Log.d("PRPR", "식당명: " + resInfo_fb.res_name);
                    Log.d("PRPR", "날짜: " + menuReview_fb.date);
                    Log.d("PRPR", "카운트: " + reviewCountCompare + "/" + reviewCount + " #" + tt);

                    Profile_fb profile = new Profile_fb( resInfo_fb, menuInfo_fb, menuReview_fb );
                    profile_fbList_temp.add( profile ); //정렬안된걸로 채워넣음

                if(reviewCount == reviewCountCompare){
                    Log.d("PRPR", "끝");
                    customDialog.dismiss();


                    sortProfile();
                    profileSetting();
                }
                }catch(Exception e){
                    Log.d("PRPR", "Error: " + e);
                    customDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                customDialog.dismiss();
            }
        });
    }


    //개인정보만 새로고침(수정되었을 수도 있으니)
    public void connect_fb_refresh(){



        //입력공간 초기화
        user_fb = new User_fb();
        profile_fbList_temp = new LinkedList<>();


        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/user" );
        mRef.orderByKey().equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren() ) {

                        //유저정보를 받아옴
                        user_fb = postSnapshot.getValue( User_fb.class );
                        user_fb.uid = uid;

                        Log.d("PRPR", "유저: "+ user_fb.nickname);
                        profileSetting();

                    }
                }catch(Exception e){
                    Log.d("NNWW", "에러: " + e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                customDialog.dismiss();
            }
        });

    }


    //리뷰를 삭제한다(관련된 6개의 테이블 모두 삭제)
    public void connect_deleteReview(int reviewIndex){

        Log.d("PRPR", "삭제시작");

        String reviewKey = profile_fbList.get( reviewIndex ).menuReview.key;
        String menuKey = profile_fbList.get( reviewIndex ).menuInfo.key;
        String uid = GlobalApplication.getUser_id(); //아이디는 내 아이디

        //1. menu_review삭제
        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_review" );
        mRef.child( reviewKey ).removeValue();

        //2. menu_info안에 review_id삭제
        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_info/" + menuKey + "/review_id" );
        mRef.child( reviewKey ).removeValue();

        //3. comment삭제
        for(int i=0; i<profile_fbList.get(reviewIndex).menuReview.comment.size(); i++ ){

            Map commentMap = profile_fbList.get(reviewIndex).menuReview.comment;
            Iterator<String> keySetIterator = commentMap.keySet().iterator();

            //Map안의 commentKey를 이용하여 comment제거
            while (keySetIterator.hasNext()) {
                String commentKey = keySetIterator.next();
                mRef = new Firebase( GlobalApplication.fbPath + "/Korea/comment" );
                mRef.child( commentKey ).removeValue();
            }//while
        }//for(comment삭제)


        //4. re_user_reivew삭제( 내가 쓴 것 )
        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/re_user_review/" + uid );
        mRef.child( reviewKey ).removeValue();


        //5. re_user_heart( 남이 나에게 heart ) - 사용자마다 찾아서 다 지움
        for(int i=0; i<profile_fbList.get(reviewIndex).menuReview.heart.size(); i++ ){

            Map heartMap = profile_fbList.get(reviewIndex).menuReview.heart;
            Iterator<String> keySetIterator = heartMap.keySet().iterator();

            //Map안의 uid(하트보낸자)를 이용하여 re_user_heart제거
            while (keySetIterator.hasNext()) {
                String heartUid = keySetIterator.next();

                Log.d("PRPR", "삭제되는 하트의 유저: " + heartUid);

                Map deleteHeart = new HashMap();
                deleteHeart.put(reviewKey, null);
                mRef = new Firebase( GlobalApplication.fbPath + "/Korea/re_user_heart/" + heartUid );
                mRef.child( reviewKey ).removeValue();
            }//while
        }//for(comment삭제)


        //6. re_user_fuck( 남이 나에게 fuck ) - 사용자마다 찾아서 다 지움
        for(int i=0; i<profile_fbList.get(reviewIndex).menuReview.fuck.size(); i++ ){

            Map fuckMap = profile_fbList.get(reviewIndex).menuReview.fuck;
            Iterator<String> keySetIterator = fuckMap.keySet().iterator();

            //Map안의 uid(하트보낸자)를 이용하여 re_user_heart제거
            while (keySetIterator.hasNext()) {
                String fuckUid = keySetIterator.next();

                Log.d("PRPR", "삭제되는 하트의 유저: " + fuckUid);

                Map deleteFuck = new HashMap();
                deleteFuck.put(reviewKey, null);
                mRef = new Firebase( GlobalApplication.fbPath + "/Korea/re_user_fuck/" + fuckUid );
                mRef.child( reviewKey ).removeValue();
            }//while
        }//for(comment삭제)


        //7. 클라이언트 삭제
        profile_fbList.remove(reviewIndex);
        adapter.removeItem( reviewIndex );

        //리스트 새로고침
        adapter.notifyDataSetChanged(); //리스트 새로고침
        Log.d("PRPR", "삭제 후 리스트뷰 새로고침");


    }



    //받은 것들을 날짜순으로 정렬
    public void sortProfile(){

        //집어넣을 공간 초기화
        profile_fbList = new LinkedList<>();

        //순서대로 집어넣는다
        for(int i=0; i<profile_fbList_temp.size(); i++){

            //temp는 정렬안된상태
            Profile_fb tempProfile = profile_fbList_temp.get(i);

            //넣을 것의 날짜 날짜
            Long inputDate = Long.parseLong( profile_fbList_temp.get(i).menuReview.date );

            //처음 2개 세팅
            if(i==0){
                profile_fbList.add( tempProfile );
                continue;
            }else if(i==1){
                Long listDate = Long.parseLong( profile_fbList.get(0).menuReview.date );

                if(inputDate <= listDate) {
                    profile_fbList.add(0, tempProfile );
                    continue;
                }else{
                    profile_fbList.add( tempProfile );
                    continue;
                }
            }//처음 2개 세팅


            //날짜를 정렬하면서 입력(리스트 앞쪽이 오래된 것, 날짜크기는 작음)
            Boolean inputOk = false;
            for(int j=0; j<profile_fbList.size() -1; j++){

                //제일 앞에 끼워넣기
                if(inputDate < Long.parseLong( profile_fbList.get(j).menuReview.date )){
                    profile_fbList.add(0, tempProfile);
                    inputOk = true;
                    break;
                }
                else if( Long.parseLong( profile_fbList.get(j).menuReview.date ) <= inputDate  ){

                    //사이에 끼워넣기
                    if(inputDate < Long.parseLong( profile_fbList.get(j+1).menuReview.date )){

                        profile_fbList.add(j+1, tempProfile);
                        inputOk = true;
                        break;
                    }
                }
            }//for

            //위의 조건이 충족되지 않은 경우 이걸로 넣음(가장 최신, 가장 큰 날짜숫자)
            if(inputOk == false){
                profile_fbList.add(tempProfile);
            }
        }//for(i)


        for(int i=0; i<profile_fbList.size(); i++){
            Log.d("PRPR", "정렬후날짜: " + profile_fbList.get(i).menuReview.date);
        }

        //날짜가 오름차순이니 내림차순으로 변경해준다
        Collections.reverse(profile_fbList);

        //출력!
        addReview();


    }

    //무한스크롤을 이용하여 유저가 쓴 리뷰를 15개씩 보여준다
    public void addReview() {

        //리스트뷰 새로고침
        if(refreshList==true){
            rvCount=0;
            refreshList=false;

            adapter = new PersonalSpaceListViewAdapter();
            listview.setAdapter(adapter);
        }

        //리뷰가 1개이상 존재하는 경우
        if (profile_fbList != null && profile_fbList.size() > 0) {

            //더이상 추가할 것이 없음
            if (rvCount == profile_fbList.size()) {
                Toast.makeText(getApplicationContext(), "아쉽지만 마지막이예요", Toast.LENGTH_SHORT).show();
            }

            //15개가 되지 않을때 남은 것들 추가
            else if ((profile_fbList.size() - rvCount) < 15) {
                for (int i = rvCount; i < profile_fbList.size(); i++) {
                    adapter.addItem(
                            profile_fbList.get(i).resInfo,
                            profile_fbList.get(i).menuInfo,
                            profile_fbList.get(i).menuReview,
                            user_fb);
                }
                rvCount = profile_fbList.size();
            }

            //15개씩 추가
            else {
                for (int i = rvCount; i < rvCount + 15; i++) {
                    adapter.addItem(
                            profile_fbList.get(i).resInfo,
                            profile_fbList.get(i).menuInfo,
                            profile_fbList.get(i).menuReview,
                            user_fb);
                }
                rvCount = rvCount + 15;
            }
        }

        adapter.notifyDataSetChanged(); //리스트 새로고침

    }


    //프로필부분 세팅
    public void profileSetting(){

        Log.d("PRPR", "프로필세팅");

        faceIv = (ImageView) findViewById(R.id.mrf_face_iv);
        nicknameTv = (TextView) findViewById(R.id.mrf_nickanme_tv);
        userinfoTv = (TextView) findViewById(R.id.mrf_userinfo_iv);
        modifyProfileTv = (TextView) findViewById(R.id.mrf_modify_profile_tv);

        String userinfoStr;

        //내 공간일 경우 프로필수정
        if(user_fb.uid.equals(GlobalApplication.getUser_id())){
            modifyProfileTv.setVisibility(View.VISIBLE);
            modifyProfileTv.setText("프로필수정하기");
        }


        if (profile_fbList != null && profile_fbList.size() > 0){
            userinfoStr = profile_fbList.size() + "개의 리뷰";
        }else{
            userinfoStr = "리뷰가 단 한개도 없습니다!!";
        }

        //프사
        Glide.with(this)
                .load( user_fb.profile_image )
                .placeholder(R.drawable.face_basic)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(this))
                .into(faceIv);
        faceIv.setScaleType(ImageView.ScaleType.FIT_XY);

        nicknameTv.setText( user_fb.nickname );
        userinfoTv.setText( userinfoStr );


    }


















    public void connect() {

        String url = "http://kumchurk.ivyro.net/app/download_myreview2.php";

        //서버로 메뉴이름, 식당이름, 아이디을 보내서 그에 해당하는 데이터를 받아온다
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);


        //값을 받아올 리스너, Context, url, post로 보낼 것들의 key와 value들을 담은 해쉬맵
        volley = new VolleyConnect(mResultCallback, this, url, params);
    } //데이터 다운로드 volley

    void initVolleyCallback() {
        mResultCallback = new IVolleyResult() {
            @Override
            public void notifySuccess(String response) {
                //전송의 결과를 받는 부분
                jsonToJava(response);
            }

            @Override
            public void notifyError(VolleyError error) {
                //전송 시 에러가 생겼을 때 받는 부분
                Log.d("VOVO", "(기본)에러: " + error);
            }
        };
    } //데이터 다운로드 callback

    void jsonToJava(String jsonStr) {

        Log.d("MRMR", "" + jsonStr);
        Gson gson = new Gson();
        rvJ = gson.fromJson(jsonStr, PersonalReview_JSON.class);

        addReview();
        profileSetting();

    } //다운받은 Json데이터들을 객체화

    //삭제 volley
    public void connect_delete(String reviewNum, String menuName, String resName) {

        String url = "http://kumchurk.ivyro.net/app/upload_review_delete.php";

        Map<String, String> params = new HashMap<>();
        params.put("review_num", reviewNum);
        params.put("menu_name", menuName);
        params.put("res_name", resName);


        //값을 받아올 리스너, Context, url, post로 보낼 것들의 key와 value들을 담은 해쉬맵
        volley = new VolleyConnect(mResultCallback2, this, url, params);
    }

    //삭제 callback
    void initVolleyCallback2() {
        mResultCallback2 = new IVolleyResult() {
            @Override
            public void notifySuccess(String response) {

                //삭제성공 메시지를 띄우고 액티비티 종료
                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Complete")
                        .setContentText("삭제가 완료되었습니다")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();

                                //다시 다운로드
                                refreshList = true;
                                connect();
                            }
                        })
                        .show();

            }

            @Override
            public void notifyError(VolleyError error) {
                //전송 시 에러가 생겼을 때 받는 부분
                Log.d("VOVO", "(기본)에러: " + error);
            }
        };
    }






    //삭제 다이얼로그(어댑터에서 호출한다)
    public void deleteReview(final String reviewNum, final String menuName, final String resName) {

        BottomDialog dialog = BottomDialog.newInstance(" ","취소",new String[]{"삭제하기"});
        dialog.show(getSupportFragmentManager(),"dialog");
        //add item click listener
        dialog.setListener(new BottomDialog.OnClickListener() {
            @Override
            public void click(int position) {

                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("삭제하시겠습니까?")
                        .setCancelText("취소")
                        .setConfirmText("삭제")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                                connect_delete(reviewNum, menuName, resName);
                            }
                        })
                        .show();
            }
        });

    }




}

