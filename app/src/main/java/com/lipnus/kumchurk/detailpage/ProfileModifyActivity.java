package com.lipnus.kumchurk.detailpage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.IVolleyResult;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.VolleyConnect;
import com.lipnus.kumchurk.data.main.User_Info;
import com.lipnus.kumchurk.kum_class.CustomDialog;
import com.lipnus.kumchurk.kum_class.SimpleFunction;
import com.lipnus.kumchurk.submenu.SubMenuControl;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ProfileModifyActivity extends AppCompatActivity {

    //manifests에 android:windowSoftInputMode="adjustResize" 이걸 추가해줘야 키보드에 의해서 커서가 가려지지 않음

    IVolleyResult mResultCallback = null;
    VolleyConnect volley;

    private Bitmap bitmap = null;
    private int PICK_IMAGE_REQUEST = 1;



    //프사
    ImageView profileIv;
    EditText nicknameEt;

    //닉네임 등록실패
    TextView failTv;

    //다른 곳에서 여기를 finish()할 수 있도록 함.
    public static Activity PMActiviry;


    //파이어베이스
    Firebase mRef;

    private static final int GALLERY_INTENT = 2;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    //프레퍼런스
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    //회원정보
    User_Info user_info;

    //그림 회전 다이얼로그
    CustomDialog customDialog;

    //컨텍스트
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_modify);

        context = this;

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //페이지전환 애니매이션 설정
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        //프레퍼런스
        //Prefrence설정(0:읽기,쓰기가능)
        setting = getSharedPreferences("USERDATA", 0);
        editor= setting.edit();

        //다른 곳에서 이 액티비티를 조작할 수 있게 함
        PMActiviry = ProfileModifyActivity.this;

        //현재의 뷰(서브메뉴에 넘김)
        View thisView = this.getWindow().getDecorView();

        //서브메뉴 생성
        SubMenuControl smc = new SubMenuControl(this, thisView, "PROFILE", false);

        //Volley 콜백함수
        initVolleyCallback();

        profileIv = (ImageView)findViewById(R.id.pm_profile_iv);
        nicknameEt = (EditText) findViewById(R.id.pm_nickname_et);
        failTv = (TextView) findViewById(R.id.pm_overlap_tv);

        //닉네임
        nicknameEt.setText( GlobalApplication.getUser_nickname());

        //프사
        Glide.with(this)
                .load( GlobalApplication.getUser_image() )
                .placeholder(R.drawable.face_basic)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(this))
                .into(profileIv);
        profileIv.setScaleType(ImageView.ScaleType.FIT_XY);

        //저장소 위치 참조
        mStorage = FirebaseStorage.getInstance().getReference();


    }




    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }




    //사진 터치
    public void onClick_pm_selectImage(View v){
        Intent iT = new Intent(Intent.ACTION_PICK);
        iT.setType("image/*");
        startActivityForResult(iT, GALLERY_INTENT);
    }

    //체크터치
    public void onClick_pm_profileModify(View v){

        //아이디 중복 확인
        connect_fb_overlapId( nicknameEt.getText().toString() );

        //키보드닫기
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow( nicknameEt.getWindowToken(), 0);



    }





    //아이디 중복 확인
    public void connect_fb_overlapId(final String nickname){

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/user" );
        mRef.orderByChild("nickname").equalTo( nickname ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

                    //중복인가?
                    boolean isOverlap = false;
                    boolean itMe = false;

                    Log.d("PPFF", ""+ dataSnapshot);

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren() ) {

                        if(postSnapshot.getKey().equals(GlobalApplication.getUser_id()) ) {
                            //서버에 이미 이름이 있다. 그것은 나다
                            itMe = true;
                            Log.d("PPFF", "저건 나다");

                        }else{
                            //서버에 이미 이름이 존재하고, 그것은 내가 아님
                            isOverlap = true;
                            Log.d("PPFF", "닉네임중복");

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("동일한 닉네임이 존재합니다");
                            builder.setNeutralButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            builder.show();

                        }
                    }//for

                    //중복된 아이디가 없으면 업로드 시작
                    if(isOverlap == false){
                        if(bitmap==null){

                            if (itMe == false) {
                                //닉네임만 교체
                                update1( nickname );
                            }


                        }else{
                            //이미지와 함께 업로드
                            update2(nickname);
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

    //닉네임만 변경
    public void update1(String nickname){

        //파이어베이스에 업로드
        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/user/" + GlobalApplication.getUser_id());
        mRef.child("nickname").setValue(nickname);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("닉네임을 변경했습니다");
        builder.setNeutralButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();


        //클라이언트의 데이터도 바꿔준다
        GlobalApplication.setUser_nickname( nickname );

        //프레퍼런스 값도 업데이트 해준다
        updatePreferecnce();


    }

    //사진도 변경
    public void update2(final String nickname){

        //다이얼로그 켜기
        customDialog = new CustomDialog(this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //다이얼로그 뒷배경투명처리
        customDialog.setCancelable(false);//끌 수 없다
        customDialog.show(); // 보여주기


        Log.d("PPFF", "update()");

        //이미지경로
        String imgName = GlobalApplication.getUser_id() + "_" + SimpleFunction.getTodayDate();



        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child(imgName);

        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("user").child( imgName );

        // While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("PPFF", "실패: " + exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                @SuppressWarnings("VisibleForTests")
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                mRef = new Firebase( GlobalApplication.fbPath + "/Korea/user/" + GlobalApplication.getUser_id());
                mRef.child("nickname").setValue(nickname);
                mRef.child("profile_image").setValue( downloadUrl.toString() );
                mRef.child("thumbnail").setValue( downloadUrl.toString() );
                Log.d("PPFF", "경로 " + downloadUrl);

                //다이얼로그 제거
                customDialog.dismiss();


                //변경이 완료되었음을 표시해줌
                String noticeText = "프로필사진과 닉네임을 변경했습니다";
                if(nickname.equals( GlobalApplication.getUser_nickname() )){
                    noticeText = "프로필사진을 변경했습니다";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage( noticeText );
                builder.setCancelable(false);
                builder.setNeutralButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.show();

                //클라이언트의 데이터도 바꿔준다
                GlobalApplication.setUser_nickname( nickname );
                GlobalApplication.setUser_image(downloadUrl.toString());
                GlobalApplication.setUser_thumbnail(downloadUrl.toString());

                //프레퍼런스 값도 바꿔준다
                updatePreferecnce();
            }
        });







    }



    //서버에 사진이 올라가면 프레퍼런스도 바꿔준다
    public void updatePreferecnce(){

        //앱 내부의 프레퍼런스값을 업로드한다
        editor.putString("user_nickname", GlobalApplication.getUser_nickname() );
        editor.putString("user_image", GlobalApplication.getUser_image() );
        editor.putString("user_thumbnail", GlobalApplication.getUser_thumbnail() );
        editor.commit();


    }




    //인텐트로 갤러리를 호출한 것의결과를 받는 곳
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        mProgressDialog.setMessage("업르드중...");
//        mProgressDialog.show();

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            try{

                //이미지뷰에 고른 사진을 보여줌
                Glide.with(this)
                        .load( data.getData() )
                        .bitmapTransform(new CropCircleTransformation(this))
                        .into(profileIv);

                //선택한 사진을 비트맵으로 전환(Glide 이용)
                Glide.with(this)
                        .load( data.getData() )
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                bitmap = resource;
                                Log.d("DDDD", "전" + bitmap.getWidth());
                                if ( bitmap.getWidth() > 600 ){
                                    bitmap = resizeBitmap(bitmap);
                                }
                                Log.d("DDDD", "후:" + bitmap.getWidth());
                            }
                        });


            } catch (Exception e) {
                e.printStackTrace();
            }

        }//if
    }

    //비트맵크기조절(비율유지)
    public Bitmap resizeBitmap(Bitmap original) {

        //가로를 600으로 줄인다. 세로는 비율에 맞게 줄어듦
        int resizeWidth = 600;

        double aspectRatio = (double) original.getHeight() / (double) original.getWidth();
        int targetHeight = (int) (resizeWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(original, resizeWidth, targetHeight, false);
        if (result != original) {
            original.recycle();
        }
        return result;
    }
















    //volley 접속
    public void connect(){

        String url = "http://kumchurk.ivyro.net/app/upload_profile_modify.php";

        //이미지를 업로드 하지 않으면 none으로 해서 보냄
        String image;
        if(bitmap==null){
            image="none";
        }else{
            image = getStringImage(bitmap);
        }

        Log.d("IMIM", "" + image);

        Map<String, String> params = new HashMap<>();
        params.put("user_id", GlobalApplication.getUser_id() );
        params.put("user_nickname", nicknameEt.getText().toString() );
        params.put("user_image", image);

        //값을 받아올 리스너, Context, url, post로 보낼 것들의 key와 value들을 담은 해쉬맵
        volley = new VolleyConnect(mResultCallback, this, url, params);
    }

    //Volley 콜백
    void initVolleyCallback(){
        mResultCallback = new IVolleyResult() {

            @Override
            public void notifySuccess(String response) {

                Log.d("PMPM", ""+response);

                if(response.equals("overlap")){
                    failTv.setText("이미 존재하는 닉네임");
                }else{
                    //성공 다이얼로그
                    jsonToJava(response);
                    uploadSuccess();
                }


            }

            @Override
            public void notifyError(VolleyError error) {

                //실패 다이얼로그
                uploadFail();
            }
        };
    }


    //다운받은 Json데이터들을 객체화
    public void jsonToJava(String jsonStr){

        Gson gson = new Gson();
        user_info = gson.fromJson(jsonStr, User_Info.class);

        userInfoSetting();
    }

    //Application과 프레퍼런스에 업데이트 된 정보를 입력
    public void userInfoSetting(){

        //1. 어플리케이션에 업데이트
        //==========================================================================================
        GlobalApplication.setUser_nickname( user_info.getUser_nickname() );
        GlobalApplication.setUser_image( user_info.getUser_image() );
        GlobalApplication.setUser_thumbnail( user_info.getUser_thumbnail() );

        //2. 프레퍼런스에도 업데이트
        //==========================================================================================
        SharedPreferences setting;
        SharedPreferences.Editor editor;

        //Prefrence설정(0:읽기,쓰기가능)
        setting = getSharedPreferences("USERDATA", 0);
        editor= setting.edit();


        editor.putString("user_nickname", GlobalApplication.getUser_nickname() );
        editor.putString("user_image", GlobalApplication.getUser_image() );
        editor.putString("user_thumbnail", GlobalApplication.getUser_thumbnail() );
        editor.commit();
    }













    //이미지를 Base64(64비트?) 의 String으로 변환해줌
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }






















    //성공다이얼로그
    public void uploadSuccess(){
        //삭제성공 메시지를 띄우고 액티비티 종료
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Complete")
                .setContentText("프로필 변경 성공")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();


                        //현재 창 닫음
                        finish();
                    }
                })
                .show();
    }

    //실패다이얼로그
    public void uploadFail(){

        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("sorry..")
                .setContentText("네트워크 접속 장애\n다시 시도할께요!")
                .setCustomImage(R.drawable.sorry2)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        connect();

                    }
                })
                .show();

    }

    //이미지파일을 선택(사진첩을 호출하고 onActivityResult로 받음)
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

}

