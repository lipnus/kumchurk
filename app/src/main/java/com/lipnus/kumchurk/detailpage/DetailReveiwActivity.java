package com.lipnus.kumchurk.detailpage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kcode.bottomlib.BottomDialog;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.IVolleyResult;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.VolleyConnect;
import com.lipnus.kumchurk.data.ReviewDetail_JSON;
import com.lipnus.kumchurk.kum_class.SimpleFunction;
import com.lipnus.kumchurk.submenu.PersonalSpaceActivity;
import com.lipnus.kumchurk.submenu.SubMenuControl;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DetailReveiwActivity extends AppCompatActivity {

    IVolleyResult mResultCallback = null;
    IVolleyResult mResultCallback2 = null;
    IVolleyResult mResultCallback3 = null;
    VolleyConnect volley;

    String reviewNum;

    //자료들을 받는 객체
    ReviewDetail_JSON rvJ;

    //댓글의 리스트뷰 어댑터
    ListView listview;
    CommentListViewAdapter adapter;

    //상단, 서브메뉴 컨트롤
    SubMenuControl smc;

    //객체들
    TextView menuNameTv;
    TextView resNameTv;
    ImageView menuIv;
    ImageView profileIv;
    TextView profileTv;
    ImageView heartIv;
    ImageView fuckIv;
    ImageView commentIv;
    TextView heartNumTv;
    TextView fuckNumTv;
    TextView commentNumTv;
    TextView reviewTv;
    TextView timeTv;
    ImageView moreIv;
    ImageView tasteIv;

    Context context;

    //리스트뷰가 바닥에 닿는지 체크
    boolean lastItemVisibleFlag = false;

    //다른 곳에서 여기를 finish()할 수 있도록 함.
    public static Activity DRActiviry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reveiw);

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //컨텍스트
        context = this;

        //앞의 엑티비티로부터 식당이름과 메뉴이름을 받는다
        Intent iT = getIntent();
        reviewNum = iT.getExtras().getString("review_num");

        Log.d("TTMM", "받은리뷰번호: " + reviewNum);

        //다른 곳에서 이 액티비티를 조작할 수 있게 함
        DRActiviry = DetailReveiwActivity.this;

        //현재의 뷰(서브메뉴에 넘김)
        View thisView = this.getWindow().getDecorView();

        //서브메뉴 생성
        smc = new SubMenuControl(this, thisView, "REVIEW", false);

        //초기화
        initSetting();

        //리스트뷰 초기화
        initList();

        //Volley 콜백함수
        initVolleyCallback();
        initVolleyCallback2();
        initVolleyCallback3();

    }

    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        try{
            //서버에서 데이터를 받아온다
            connect();
        }catch(Exception e){
            finish();
        }
    }

    //초기설정
    public void initSetting(){
        menuNameTv = (TextView) findViewById(R.id.dr_menu_nameTv);
        resNameTv = (TextView) findViewById(R.id.dr_res_nameTv);
        menuIv=(ImageView)findViewById(R.id.dr_menu_img_Iv);
        profileIv=(ImageView)findViewById(R.id.dr_faceIv);
        profileTv =(TextView)findViewById(R.id.dr_nicknameTv);
        heartIv=(ImageView)findViewById(R.id.dr_heart_iv);
        fuckIv=(ImageView)findViewById(R.id.dr_fuck_iv);
        commentIv=(ImageView)findViewById(R.id.dr_comment_iv);
        heartNumTv= (TextView) findViewById(R.id.dr_heart_tv);
        fuckNumTv= (TextView) findViewById(R.id.dr_fuck_tv);
        commentNumTv= (TextView) findViewById(R.id.dr_comment_tv);
        reviewTv= (TextView) findViewById(R.id.dr_review_Tv);
        timeTv= (TextView) findViewById(R.id.dr_date_Tv);
        moreIv= (ImageView)findViewById(R.id.dr_more_iv);
        tasteIv = (ImageView)findViewById(R.id.dr_taste_iv);

        //댓글
        Glide.with(this)
                .load( R.drawable.small_menu_comment )
                .into(commentIv);
        commentIv.setScaleType(ImageView.ScaleType.FIT_XY);


        //옵션
        Glide.with(this)
                .load( R.drawable.more )
                .into(moreIv);

    }

    //리스트 초기화
    public void initList(){

        //리스트뷰 Adapter 생성
        adapter = new CommentListViewAdapter();

        //알람를 표시할 리스트뷰와 컨트롤을 위한 어댑터
        listview = (ListView) findViewById(R.id.dr_listview);
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



    //댓글나와라
    public void onClick_dr_comment_Lr(View v){
        Intent iT = new Intent(this, CommentActivity.class);
        iT.putExtra("review_num", rvJ.getReview_num());
        startActivity(iT);
    }

    //하트클릭
    public void onClick_dr_heart(View v){
        //하트 있을경우 -1
        if(rvJ.getVote_heart() == 1){
            connect_vote(rvJ.getWriter_id(), rvJ.getMenu_name(), rvJ.getRes_name(), rvJ.getReview_num(), -1, 0);
        }

        //하트만 +1
        else if(rvJ.getVote_heart() == 0 && rvJ.getVote_fuck() == 0){
            connect_vote(rvJ.getWriter_id(),rvJ.getMenu_name(), rvJ.getRes_name(), rvJ.getReview_num(), 1, 0);
        }

        //빠큐 했던건 없에고, 하트 추가
        else if(rvJ.getVote_heart() == 0 && rvJ.getVote_fuck() == 1){
            connect_vote(rvJ.getWriter_id(), rvJ.getMenu_name(), rvJ.getRes_name(), rvJ.getReview_num(), 1, -1);
        }

        //오류가 난 경우 0,0으로 세팅
        else{
            connect_vote(rvJ.getWriter_id(), rvJ.getMenu_name(), rvJ.getRes_name(), rvJ.getReview_num(), 0, 0);
        }
    }

    //뻑큐클릭
    public void onClick_dr_fuck(View v){
        //빠큐만 -1
        if(rvJ.getVote_fuck() == 1){
            connect_vote(rvJ.getWriter_id(),rvJ.getMenu_name(), rvJ.getRes_name(), rvJ.getReview_num(),   0, -1);
        }

        //빠큐만 +1
        else if(rvJ.getVote_heart() == 0 && rvJ.getVote_fuck() == 0 ){
            connect_vote(rvJ.getWriter_id(),rvJ.getMenu_name(), rvJ.getRes_name(), rvJ.getReview_num(),   0, 1);
        }

        //하트 없에고 빠큐만 +1
        else if(rvJ.getVote_heart() == 1 && rvJ.getVote_fuck() == 0){
            connect_vote(rvJ.getWriter_id(),rvJ.getMenu_name(), rvJ.getRes_name(), rvJ.getReview_num(),  -1, 1);
        }

        //오류가 난 경우 0,0으로 세팅
        else{
            connect_vote(rvJ.getWriter_id(), rvJ.getMenu_name(), rvJ.getRes_name(), rvJ.getReview_num(), 0, 0);
        }
    }

    //옵션클릭
    public void onClick_dr_option(View v){

        BottomDialog dialog = BottomDialog.newInstance(" ", "취소",new String[]{"삭제하기"});

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
                                connect_delete();

                            }
                        })
                        .show();

            }
        });
    }//끝

    //프사클릭
    public void onClick_dr_profile(View v){

        //개인공간 -> 디테일리뷰 -> 다시 개인공간
        //이런 경우 처음 개인공간은 닫아준다
        PersonalSpaceActivity rvActivity = (PersonalSpaceActivity) PersonalSpaceActivity.PSActiviry;

        if(rvActivity != null) {
            Log.d("NMNM", "켜져있다");
            rvActivity.finish();
        }else{
            Log.d("NMNM", "꺼져있다");
        }

        Intent iT = new Intent(this, PersonalSpaceActivity.class);
        iT.putExtra("user_id", rvJ.getWriter_id());
        startActivity(iT);
    }




    //데이터 다운로드 volley
    public void connect(){

        String url = "http://kumchurk.ivyro.net/app/download_review2.php";

        //서버로 메뉴이름, 식당이름, 아이디을 보내서 그에 해당하는 데이터를 받아온다
        Map<String, String> params = new HashMap<>();
        params.put("user_id", GlobalApplication.getUser_id() );
        params.put("review_num", reviewNum );


        //값을 받아올 리스너, Context, url, post로 보낼 것들의 key와 value들을 담은 해쉬맵
        volley = new VolleyConnect(mResultCallback, this, url, params);
    }

    //데이터 다운로드 callback
    void initVolleyCallback(){
        mResultCallback = new IVolleyResult() {
            @Override
            public void notifySuccess(String response) {
                //전송의 결과를 받는 부분
                Log.d("DRDR", response);
                jsonToJava(response);
            }

            @Override
            public void notifyError(VolleyError error) {
                //전송 시 에러가 생겼을 때 받는 부분
                Log.d("VOVO", "(기본)에러: "+ error);
            }
        };
    }

    //다운받은 Json데이터들을 객체화
    void jsonToJava(String jsonStr){
        Log.d("VOVO", "jsonToJava()");

        Gson gson = new Gson();
        rvJ = gson.fromJson(jsonStr, ReviewDetail_JSON.class);

        addReview();
//        addComment();
    }

    //하트,빠큐 투표
    public void connect_vote(String voted_id, String menuName, String resName, String review_num, int heart, int fuck){

        String url = "http://kumchurk.ivyro.net/app/upload_menureview_vote2.php";

        //아이디, 리뷰쓴사람 id, 리뷰글번호, 메뉴이름, 식당이름, 하트, 뻐큐(시간은 웹에서 처리)
        Map<String, String> params = new HashMap<>();
        params.put("user_id", GlobalApplication.getUser_id() );
        params.put("user_nickname", GlobalApplication.getUser_nickname() );
        params.put("user_thumbnail", GlobalApplication.getUser_thumbnail() );

        params.put("voted_id", voted_id );
        params.put("review_num", review_num );
        params.put("menu_name", menuName );
        params.put("res_name", resName );
        params.put("heart", Integer.toString(heart) );
        params.put("fuck", Integer.toString(fuck) );


        //값을 받아올 리스너, Context, url, post로 보낼 것들의 key와 value들을 담은 해쉬맵
        volley = new VolleyConnect(mResultCallback2, this, url, params);
    } //좋아요 싫어요 투표 volley

    //하트빠큐투표 callback
    void initVolleyCallback2(){
        mResultCallback2 = new IVolleyResult() {
            @Override
            public void notifySuccess(String response) {
                //전송의 결과를 받는 부분
                Log.d("PUSHTEST", "" + response);
                //이걸 다시 호출해서 페이지를 새로고침
                connect();
            }

            @Override
            public void notifyError(VolleyError error) {
                //전송 시 에러가 생겼을 때 받는 부분
                Log.d("VOVO", "(기본)에러: "+ error);
            }
        };
    }

    //삭제 volley
    public void connect_delete(){

        String url = "http://kumchurk.ivyro.net/app/upload_review_delete.php";

        Map<String, String> params = new HashMap<>();
        params.put("review_num", reviewNum );
        params.put("menu_name", rvJ.getMenu_name() );
        params.put("res_name", rvJ.getRes_name() );



        //값을 받아올 리스너, Context, url, post로 보낼 것들의 key와 value들을 담은 해쉬맵
        volley = new VolleyConnect(mResultCallback3, this, url, params);
    }

    //삭제 callback
    void initVolleyCallback3(){
        mResultCallback3 = new IVolleyResult() {
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
                                finish();
                            }
                        })
                        .show();

            }

            @Override
            public void notifyError(VolleyError error) {
                //전송 시 에러가 생겼을 때 받는 부분
                Log.d("VOVO", "(기본)에러: "+ error);
            }
        };
    }


    //다운받은 정보들을 가지고 페이지를 채워넣는다
    public void addReview(){

        int heartPath;
        int fuckPath;

        if( rvJ.getVote_heart() == 1){
            heartPath = R.drawable.small_menu_heart2;
        }else{
            heartPath = R.drawable.small_menu_heart;
        }

        if( rvJ.getVote_fuck() == 1){
            fuckPath = R.drawable.small_menu_fuck2;
        }else{
            fuckPath = R.drawable.small_menu_fuck;
        }

        //맛 표정
        int tastePath;
        if(rvJ.getReview_taste() == 2){
            tastePath = R.drawable.taste1_black;
        }else if(rvJ.getReview_taste() == 1){
            tastePath = R.drawable.taste2_black;
        }else{
            tastePath = R.drawable.taste3_black;
        }
        Glide.with(this)
                .load( tastePath )
                .into(tasteIv);
        tasteIv.setScaleType(ImageView.ScaleType.FIT_XY);


        //하트
        Glide.with(this)
                .load( heartPath )
                .into(heartIv);
        heartIv.setScaleType(ImageView.ScaleType.FIT_XY);

        //빠큐
        Glide.with(this)
                .load( fuckPath )
                .into(fuckIv);
        heartIv.setScaleType(ImageView.ScaleType.FIT_XY);





        int dis = SimpleFunction.distance(rvJ.getRes_latitude(), rvJ.getRes_longitude(), "meter");
        String disMin = SimpleFunction.distanceMinute(rvJ.getRes_latitude(), rvJ.getRes_longitude());
        String distance = "걸어서 " + disMin + " (" + Integer.toString(dis) + "m)";

        String price = SimpleFunction.displayPrice(rvJ.getMenu_price(), rvJ.getMenu_price2(), rvJ.getMenu_price3());

        menuNameTv.setText(rvJ.getMenu_name());
        resNameTv.setText(price);
        // + "\n" + rvJ.getRes_name() + ", " + distance
        profileTv.setText(rvJ.getWriter_nickname());
        fuckNumTv.setText("" + rvJ.getReview_fuck());
        heartNumTv.setText("" + rvJ.getReview_heart());
        commentNumTv.setText("" + rvJ.getReview_comment());
        reviewTv.setText(rvJ.getReview_memo());

        String date = SimpleFunction.timeGap( rvJ.getReview_updated_at() );
        timeTv.setText(date);

        Log.d("DTTD", "사진경로: " + rvJ.getReview_image());
        Log.d("DTTD", "번호: " + rvJ.getReview_num());

        //메뉴사진
        Glide.with(this)
                .load( rvJ.getReview_image() )
                .centerCrop()
                .placeholder(R.drawable.empty_table2)
                .thumbnail(0.2f)
                .into(menuIv);
        menuIv.setScaleType(ImageView.ScaleType.FIT_XY);

        //프사
        Glide.with(this)
                .load( rvJ.getWriter_image() )
                .placeholder(R.drawable.face_basic)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(this))
                .thumbnail(0.2f)
                .into(profileIv);
        profileIv.setScaleType(ImageView.ScaleType.FIT_XY);


        //이 글이 내가 쓴 글이면 옵션버튼 생김
        if(rvJ.getWriter_id().equals(GlobalApplication.getUser_id()) ){
            moreIv.setVisibility(View.VISIBLE);
        }

    }

    public void addComment(){

//        Log.d("ALAL", "카운트: " + cmCount + "개수: " + rCJ.getReviewComment().size());

        //리스트뷰 리셋
        adapter = new CommentListViewAdapter();
        listview.setAdapter(adapter);

        if(rvJ.getReviewComment() != null && rvJ.getReviewComment().size() >0){

            //댓글은 아주 많지는 않을 것이므로 일단 주~욱 띄운다
            //나중에 이 부분이 문제가 된다면 뉴스피드나 소식 부분처럼 끊어서 불러오게 하면 됨

            //아이템추가
            for(int i=0; i<rvJ.getReviewComment().size(); i++){
                adapter.addItem( rvJ.getReviewComment().get(i));
            }
        }

        adapter.notifyDataSetChanged(); //리스트 새로고침
        listview.setSelection(adapter.getCount() - 1); //가장 아래쪽으로 스크롤다운


    }
}
