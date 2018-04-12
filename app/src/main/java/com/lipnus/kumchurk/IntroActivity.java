package com.lipnus.kumchurk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.lipnus.kumchurk.data.Weather;
import com.lipnus.kumchurk.fcm.MyFirebaseInstanceIDService;
import com.lipnus.kumchurk.join.JoinLastActivity;
import com.lipnus.kumchurk.join.JoinUserCheckActivity;
import com.lipnus.kumchurk.join.TermsAgreeActivity;
import com.lipnus.kumchurk.kum_class.GetLocation;
import com.lipnus.kumchurk.kum_class.SimpleFunction;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IntroActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    //동영상 재생에 관련된 것들
    //정보 : http://whiteduck.tistory.com/24
    //샘플 : KUM_movie
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    MediaPlayer mediaPlayer;

    //메인의 이미지 설정
    ImageView blackCoverIv;
    ImageView logoIv;

    //카카오버튼
    com.kakao.usermgmt.LoginButton kakaoBtn;
    ImageView kakaoBtn2;

    //질문
    LinearLayout TalkLr;
    TextView introTv1;
    public TextView introTv2;
    TextView introTv3;
    TextView introTv4;
    TextView skipTv;


    //Preference선언 (한번 로그인이 성공하면 자동으로 처리하는데 이용)
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    //카카오톡 세션 콜백함수
    SessionCallback callback;

    //volley, 리스너, 날씨정보를 담을 DTO
    IVolleyResult mResultCallback = null;
    VolleyConnect volley;
    Weather weatherData = null;

    //파이어베이스 참조
    Firebase mRef;

    //회원가입유무
    public boolean joined = false;

    //다른곳에서 조작
    public static Activity INActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        //버전체크
        connect_fb_version();

        //다른곳에서 조작(위치)
        INActivity = IntroActivity.this;

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //초기설정
        initSetting();
        initTalk();

        //비디오뷰 초기화
        videoSetting();

        //fcm토큰 초기화
        fcmSetting();

        //날씨정보를 받아온다(http://openweathermap.org)
        initVolleyCallback();
        connect();

        //사용자의 위치정보가 켜져있는지 확인
        checkGPS();

        //위치정보 업데이트(독립적인 클래스 형태로 구현해놓음)
        GetLocation gl = new GetLocation( getApplicationContext() );



        //==========================================================================================
        byte[] sha1 = {
            (byte)0xD8, (byte)0x0A, (byte)0xCF, (byte)0xDE, (byte)0x6D, (byte)0x51, (byte)0x15, (byte)0x08, (byte)0xBC, (byte)0x5E, (byte)0x69, (byte)0xA9, (byte)0x16, (byte)0x39, (byte)0x60, (byte)0x66, (byte)0xAE, (byte)0x2F, (byte)0xDC, (byte)0xE2
        };
        Logger.e("keyHash: " + Base64.encodeToString(sha1, Base64.NO_WRAP));
        //==========================================================================================


        //카카오톡 세션 콜백
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Log.d("KAKAKA", "세션상태: "+ Session.getCurrentSession().isClosed() );

        //가입되었는지 체크
        joinCheck();

        byte[] a = hexStringToByteArray("0A");
        Log.d("HHSS", Byte.toString(a[0]));

    }



    @Override
    protected void onPostResume() {
        super.onPostResume();

        //재시작했을때 동영상 시작(이거 없으면 검은화면만 나온다)

    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    //초기설정
    public void initSetting(){

        //카카오버튼
        kakaoBtn = (com.kakao.usermgmt.LoginButton)findViewById(R.id.com_kakao_login);
        kakaoBtn2 = (ImageView)findViewById(R.id.intro_kakaobtn_Iv);

        blackCoverIv = (ImageView) findViewById(R.id.intro_coverIv);
        logoIv = (ImageView) findViewById(R.id.intro_logoIv);

        Glide.with(this)
                .load( R.drawable.intro )
                .into(blackCoverIv);
        blackCoverIv.setScaleType(ImageView.ScaleType.FIT_XY);

        Glide.with(this)
                .load( R.drawable.logo_noodle )
                .into(logoIv);
        logoIv.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    //onCreate할때 대사준비
    public void initTalk(){

        //질문
        TalkLr = (LinearLayout) findViewById(R.id.intro_questionLr);

        //질문
        introTv1 = (TextView)findViewById(R.id.intro_tv_1);
        introTv2 = (TextView)findViewById(R.id.intro_tv_2);
        introTv3 = (TextView)findViewById(R.id.intro_tv_3);
        introTv4 = (TextView)findViewById(R.id.intro_tv_4);

        //넘어가기 버튼
        skipTv = (TextView) findViewById(R.id.intro_skip_tv);
    }

    //비디오뷰 초기화
    public void videoSetting(){

        // surfaceView 등록
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        // Activity로 Video Stream 전송 등록
        surfaceHolder.addCallback(this);
    }

    //gps가 켜져있는지 확인
    public boolean checkGPS() {

        //현재위치를 구하는데 필요한 지오코더
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPS) {
            Log.d("GPGP", "gps켜짐");
            return true;
        } else {
            Toast.makeText(getApplication(), "GPS를 켜주세요!", Toast.LENGTH_LONG).show();
            Log.d("GPGP", "gps꺼짐");

            //위치정보 설정창 띄우기
//            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        }
        return false;
    }

    //push를 위한 토큰 초기화
    public void fcmSetting(){
        MyFirebaseInstanceIDService service = new MyFirebaseInstanceIDService();
        service.onTokenRefresh();
    }

    //가입했는지 확인
    public void joinCheck(){

        //Prefrence설정(0:읽기,쓰기가능)
        setting = getSharedPreferences("USERDATA", 0);
        editor= setting.edit();

        //프레퍼런스에 아이디가 저장되어 있는지 확인
        String uid = setting.getString("user_id", "noId");

        //아이디가 있음
        if(!uid.equals("noId")){

            Log.d("URUR", "프레퍼런스 있음");
            joined = true;

            //프레퍼런스에서 정보를 읽어와서 Application에 저장(두번째 값은 없을때의 기본값)
            GlobalApplication.setUser_id(setting.getString("user_id", "uid_error"));
            GlobalApplication.setUser_nickname(setting.getString("user_nickname", "nickname_error"));
            GlobalApplication.setUser_image(setting.getString("user_image", "image_error"));
            GlobalApplication.setUser_thumbnail(setting.getString("user_thumbnail", "thumbnail_error"));
            GlobalApplication.setUser_email(setting.getString("user_email", "email_error"));
            GlobalApplication.setUser_token(setting.getString("user_token", "token_error"));

            //카톡버튼을 지우고
            kakaoBtn.setVisibility(View.GONE);
            kakaoBtn2.setVisibility(View.GONE);

            //질문창을 띄운다
            TalkLr.setVisibility(View.VISIBLE);

        }else{

            TalkLr.setVisibility(View.VISIBLE);
            skipTv.setVisibility(View.GONE);

            //카톡버튼이 반응할 수 있도록 로그아웃해줌
            logout();

            //회원가입 유도멘트
            startJoinTalk();
            Log.d("URUR", "프레퍼런스 비어있음");
        }


    }






    //대사 시작
    public void startTalk(){

        skipTv.setText("추천메뉴 보기");

        //쿰척이의 각 항목에 텍스트 입력
        makeIntroText(weatherData.weather.get(0).description, weatherData.weather.get(0).main, weatherData.main.temp-273);



        textFadeIn();

    }

    //회원가입 유도멘트
    public void startJoinTalk(){
        introTv1.setText("환영합니다");
        introTv2.setText("당신의 식사 선택 고민을 해결 해주는");
        introTv3.setText("인공지능 메뉴판 쿰척입니다");
        introTv4.setText("아래 버튼을 눌러 회원가입을 해주세요");

        textFadeIn();

        YoYo.with(Techniques.Shake)
                .delay(2000)
                .duration(500)
                .playOn( kakaoBtn );

        YoYo.with(Techniques.Shake)
                .delay(2000)
                .duration(800)
                .playOn( kakaoBtn2 );

    }

    //텍스트들이 위에서 최르륵 페이드인
    public void textFadeIn(){
        YoYo.with(Techniques.FadeInDown)
                .duration(500)
                .playOn( introTv1 );

        YoYo.with(Techniques.FadeInDown)
                .duration(1200)
                .playOn(findViewById(R.id.intro_tv_2));

        YoYo.with(Techniques.FadeInDown)
                .duration(1700)
                .playOn(findViewById(R.id.intro_tv_3));

        YoYo.with(Techniques.FadeInDown)
                .duration(3000)
                .playOn(findViewById(R.id.intro_tv_4));
    }



    //다음으로 버튼
    public void onClick_question_answer(View v){

        Intent iT = new Intent(getApplicationContext(), JoinLastActivity.class);
        startActivity(iT);
        finish(); //액티비티 종료

    }

    //로그아웃
    public void onClick_logout(View v){
        Log.d("URUR", "로고터치");

        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Log.d("URUR", "로그아웃");
            }
        });
    }

    //로그아웃(시작시에 자동호출)
    public void logout(){
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Log.d("URUR", "로그아웃");
            }
        });
    }







    //테스트용 버튼
    public void onClick_fcm(View v){

//        Intent iT = new Intent(getAppliationContext(), TermsServiceActivity.class);
//        Intent iT = new Intent(getApplicationContext(), TermsPersonalActivity.class);
        Intent iT = new Intent(getApplicationContext(), TermsAgreeActivity.class);
        startActivity(iT);

    }



    //파이어베이스에서 버전체크
    public void connect_fb_version(){

        mRef = new Firebase( GlobalApplication.fbPath + "/version");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    int fireVersion = Integer.parseInt( dataSnapshot.getValue().toString() );
                    int appVersion = versionCode();

                    Log.d("URUR", "Firebase Version: " + fireVersion );
                    Log.d("URUR", "App Version: " + appVersion );

                    //서버의 버전과 현재 버전을 비교
                    if(appVersion < fireVersion){

                        //강제 버전 업데이트
                        alertVersionUpdate();

                    }


                }catch(Exception e){
                    Log.d("URUR", "에러: " + e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    //강제적으로 업데이트하기
    public void alertVersionUpdate(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setCancelable(false);
        builder.setMessage("정상적인 서비스 이용을 위해서 새로운 버전으로의 업데이트가 필요합니다");
        builder.setPositiveButton("업데이트",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Uri uri = Uri.parse("market://details?id=" + "com.lipnus.kumchurk");
                        Intent iT = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(iT);
                        finish();

                    }
                });
        builder.setNegativeButton("종료",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
    }






    //volley 접속 - (1) 날씨
    public void connect(){
        Log.d("VOVO", "접속...");
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=37.56826&lon=126.977829&APPID=2aafb65d33ba4042d1b90190605c6829";

        //날씨를 받아온다
        Map<String, String> params = new HashMap<>();
        params.put("", "");

        //값을 받아올 리스너, Context, url, post로 보낼 것들의 key와 value들을 담은 해쉬맵
        volley = new VolleyConnect(mResultCallback, this, url, params);
    }

    //volley 콜백 - (2)
    void initVolleyCallback(){
        mResultCallback = new IVolleyResult() {
            @Override
            public void notifySuccess(String response) {
                //전송의 결과를 받는 부분
                jsonToJava(response);

            }

            @Override
            public void notifyError(VolleyError error) {
                //전송 시 에러가 생겼을 때 받는 부분
                reConnectDialog();

            }
        };
    }

    //다운받은 Json데이터들을 객체화 - (3)
    void jsonToJava(String jsonStr){
        Log.d("VOVO", "jsonToJava()");

        Gson gson = new Gson();

        weatherData = gson.fromJson(jsonStr, Weather.class);

        //어플리케이션 객체의 변수에 날씨정보를 업데이트(온도는 소수점까지는 필요없으니 정수값만)
        GlobalApplication.setTemp( (int)weatherData.main.temp - 273 );
        GlobalApplication.setPresure( weatherData.main.presure );
        GlobalApplication.setHumidity( weatherData.main.humidity );
        GlobalApplication.setTemp_max( (int)weatherData.main.temp_max - 273 );
        GlobalApplication.setTemp_min( (int)weatherData.main.temp_min - 273 );
        GlobalApplication.setMain( weatherData.weather.get(0).main );
        GlobalApplication.setDescription( weatherData.weather.get(0).description );


        Log.d( "VOVO", "온도: " + GlobalApplication.getTemp() + " 날씨: "+ GlobalApplication.getMain() +
                " 표현: " + GlobalApplication.getDescription() );

        if(joined){
            startTalk();
        }
    }


    //접속실패 다이얼로그
    void reConnectDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("네트워크 상태가 불안정합니다");
        builder.setPositiveButton("재시도",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        connect();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //무반응
                        if(joined){
                            startTalk();
                        }else{
                            finish();
                        }
                    }
                });
        builder.show();


    }


    //==============================================================================================
    // 동영상재생에 필요한 서퍼스뷰 Interface때문에 꼭 Override해줘야 하는 것들
    //==============================================================================================
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        /**
         * surface 생성
         */
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        try {
            // local resource : raw에 포함시켜 놓은 리소스 호출
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cham);
            mediaPlayer.setDataSource(this, uri);

            mediaPlayer.setDisplay(holder);                                    // 화면 호출
            mediaPlayer.prepare();                                             // 비디오 load 준비
            mediaPlayer.setOnCompletionListener(completionListener);        // 비디오 재생 완료 리스너
            mediaPlayer.start(); //준비가 완료되면 시작한다

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

        //비디오재생이 끝났을때
        @Override
        public void onCompletion(MediaPlayer mp) {
            //다시시작!
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    };


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }









    //==============================================================================================
    //카카오톡으로 로그인 리스너와 콜백(2개)
    //==============================================================================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("URUR", "onActivityResult");

        //간편로그인시 호출 ,없으면 간편로그인시 로그인 성공화면으로 넘어가지 않음
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.d("URUR", "세션 활성화");
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);

                    Log.d("URUR", "카톡 onFailure");

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.d("URUR", "카톡 onSessionClosed");
                }

                @Override
                public void onNotSignedUp() {
                    Log.d("URUR", "카톡 onNotSignedUp");
                }

                @Override
                public void onSuccess(UserProfile userProfile) {

                    Log.d("URUR", "카톡 onSuccess");

                    //[Application]에 할당
                    //최초가입인 경우 이 값이 초깃값이 된다
                    //재로그인인 경우 uid를 제외한 모든 값은 firebase에서 다운 받은 값들로 업데이트 될 것이다

                    GlobalApplication.setUser_id( Long.toString(userProfile.getId()) );
                    GlobalApplication.setUser_nickname( userProfile.getNickname() );
                    GlobalApplication.setUser_image( userProfile.getProfileImagePath() );
                    GlobalApplication.setUser_thumbnail( userProfile.getThumbnailImagePath() );
                    GlobalApplication.setUser_email( userProfile.getEmail() );


                    //FCM tocken은 MyFirebaseInstanceIDService안의 콜백함수에서 Application으로 할당되어 있다


                    Log.d("URUR", "IntroActivity \n"
                            + GlobalApplication.getUser_id() + "\n"
                            + GlobalApplication.getUser_nickname() + "\n"
                            + GlobalApplication.getUser_image() + "\n"
                            + GlobalApplication.getUser_thumbnail() + "\n"
                            + GlobalApplication.getUser_email() + "\n"
                            + GlobalApplication.getUser_token() + "\n");


                    //회원정보 업로드 페이지로 이동
                    Intent iT = new Intent(getApplicationContext(), JoinUserCheckActivity.class);
                    startActivity(iT);
                    finish(); //액티비티 종료

                }
            });

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
            Log.d("URUR", "씨발");
        }

    }








    /**
     * 클래스로 만드려고도 해봤지만 반환값이 여러가지라서 그냥 여기있는게 편리할 것 같다
     */

    //멘트생성
    public void makeIntroText(String description, String main, double temp ){


        String IntroStr = GlobalApplication.getUser_nickname() + "님 반갑습니다";
        String wC; //weatherComment
        String fC =""; //foodComment



        // 날씨
        if(description.equals("clear sky")){ wC = "맑은 날이에요"; }
        else if(description.equals("few clouds")){ wC = "하늘에 옅은 구름이 떠있어요"; }
        else if(description.equals("scattered clouds")){ wC = "하늘에는 구름이 흩어져 있어요"; }
        else if(description.equals("mist")){ wC = "안개가 짙은 날이네요"; }
        else if(description.equals("broken clouds")){ wC = "하늘엔 부서진 구름들이 떠있어요"; }
        else if(description.equals("rain")){ wC = "비가 추적추적 내리네요"; }
        else if(description.equals("shower rain")){ wC = "부슬비가 내리네요"; }
        else if(description.equals("snow")){ wC = "눈이 내려요!"; }
        else if(description.equals("thunderstorm")){ wC = "천둥번개가 치는 무서운 날.."; }
        else if(description.equals("light rain")){wC = "이슬비가 내리네요"; }
        else if(temp > 30){ wC = "정말 덥네요;;"; }
        else if(temp < 0){ wC = "너무 추워요*.*"; }
        else{

            if(main.equals("Rain")){
                wC = "비가 내리네요";
            }else if(main.equals("Clouds")){
                wC = "구름이 많아요";
            }else if(main.equals("Extream")){
                wC = "무서운 날씨네요";
            }else if(main.equals("Snow")){
                wC = "눈이 내려요";
            }else if(main.equals("Clear")){
                wC = "맑은 날이에요";
            }else{
                wC = "";
            }
        }





        // 시간
        if(whatTime()==1){ fC = "아침은 드셨나요?"; }
        else if(whatTime()==2){ fC = "이른 점심.. 아점.. 브런치?!"; }
        else if(whatTime()==3){ fC = "점심메뉴 선정을 도와드릴게요"; }
        else if(whatTime()==4){ fC = "살짝 출출하지 않나요?"; }
        else if(whatTime()==5){ fC = "저녁메뉴 선정을 도와드릴게요"; }
        else if(whatTime()==6){ fC = "밤이네요. 메뉴를 추천해드릴게요"; }


        //위치
        locationTextUpdate();


        //특별한 날씨이벤트가 없으면 null값
        introTv1.setText( IntroStr );
        introTv2.setText( wC );
        introTv4.setText( fC );

        Log.d("ITNT", "대화생성");
    }


    //GetLocation 클래스에서 원격조작 하기위해 따로 빼놓음(위의 것은 날씨기준이라 위치가 아직 도달하지 못한 경우에 위치종료하자마자 거기서 얘를 호출해서 업데이트)
    public void locationTextUpdate(){

        int userLocation = 0;
        String lC="";

        if(GlobalApplication.getUser_latitude()== 0 ){
            userLocation = 2;
        }else{

            //안암역까지 40분에 걸어갈 수 있으면 인안암, 아니면 탈안암
            if( SimpleFunction.distanceMinuteInt(37.586051, 127.0293623) < 40 ){
                userLocation = 0;
            }else{
                userLocation = 1;
            }


        }

        Log.d("GPGP", "업데이트: " + GlobalApplication.getUser_latitude());

        // 위치
        if(userLocation==0){
            lC = "지금 안암에 계시는군요";
        }else if(userLocation==1){
            lC = "탈안암 이시군요";
        }else if(userLocation==2){
            lC = "(현재 위치를 찾는중...)";
        }

        introTv3.setText( lC );
    }

    //현재시간의 속성을 반환
    public int whatTime(){

        //해당 시간의 속성
        int hS=1; //hourState

        //현재의 시간 설정
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("HH");
        String timeStr = dayTime.format(new Date(time));

        int nowHour = Integer.parseInt(timeStr);

        if(6<= nowHour && nowHour <10){
            hS = 1;
        }
        else if(10<= nowHour && nowHour <12){
            hS = 2;
        }
        else if(12<= nowHour && nowHour <14){
            hS = 3;
        }
        else if(14<= nowHour && nowHour <17){
            hS = 4;
        }
        else if(17<= nowHour && nowHour <20){
            hS = 5;
        }
        else if((20<= nowHour && nowHour <24) || 0 <= nowHour && nowHour <6 ){
            hS = 6;
        }

        return hS;

    }

    //버전체크
    public int versionCode(){
        PackageInfo pi = null;
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return pi.versionCode;
    }


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
