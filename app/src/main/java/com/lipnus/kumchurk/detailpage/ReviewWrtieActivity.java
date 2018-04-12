package com.lipnus.kumchurk.detailpage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.IVolleyResult;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.ScrollActivity;
import com.lipnus.kumchurk.VolleyConnect;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.kum_class.CustomDialog;
import com.lipnus.kumchurk.kum_class.SimpleFunction;
import com.lipnus.kumchurk.submenu.ReviewSearchActivity;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReviewWrtieActivity extends AppCompatActivity {

    //manifests에 android:windowSoftInputMode="adjustResize" 이걸 추가해줘야 키보드에 의해서 커서가 가려지지 않음

    IVolleyResult mResultCallback = null;
    IVolleyResult mResultCallback2 = null;
    VolleyConnect volley;

    private Bitmap bitmap = null;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int GALLERY_INTENT = 2;

    EditText reviewEt;
    ImageView iV;

    TextView menuNameTv;
    TextView resNameTv;

    //맛얼굴표정
    ImageView tasteIv1;
    ImageView tasteIv2;
    ImageView tasteIv3;

    int tasteEstimate; //0맛없다, 1맛있다, 2존맛 (기본값은 1)

    //이미지만 업로드 버튼
    Button imgUploadBt;

    //지금 화면에서 표시할 메뉴에름과 식당이름과 리뷰키(인텐트로 받아옴)
    private String nowResName;
    private String nowMenuName;
    private String nowMenuKey;

    //어디서 호출했는지
    private String callFrom;

    //그림 회전 다이얼로그
    CustomDialog customDialog;
    Context context;

    //파이어베이스
    Firebase mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        context = this;

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //페이지전환 애니매이션 설정
        this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        reviewEt = (EditText) findViewById(R.id.review_eT);
        iV = (ImageView) findViewById(R.id.imageview);
        menuNameTv = (TextView) findViewById(R.id.menuwrite_menuname_Tv);
        resNameTv = (TextView) findViewById(R.id.menuwrite_resName_Tv);
        imgUploadBt = (Button) findViewById(R.id.rw_img_uploaad_bt);

        //맛선택
        tasteIv1 = (ImageView) findViewById(R.id.taste1_iv);
        tasteIv2 = (ImageView) findViewById(R.id.taste2_iv);
        tasteIv3 = (ImageView) findViewById(R.id.taste3_iv);

        //이미지업로드 버튼
        Glide.with(this)
                .load( R.drawable.imageupload )
                .into(iV);
        iV.setScaleType(ImageView.ScaleType.FIT_XY);

        //앞의 엑티비티로부터 식당이름과 메뉴이름을 받는다
        Intent iT = getIntent();
        nowResName = iT.getExtras().getString("res_name");
        nowMenuName = iT.getExtras().getString("menu_name");
        nowMenuKey = iT.getExtras().getString("menu_key");
        callFrom = iT.getExtras().getString("callFrom");

        //인텐트로부터 받은 메뉴이름과 식당이름표시
        menuNameTv.setText(nowMenuName);
        resNameTv.setText(nowResName);
        Log.d("LSLS", "받은 식당이름: " + nowResName);

        //맛평가 표정 세팅
        initTasteEstimate();

    }

    @Override
    public void onBackPressed() {

        //이미지를 올렸거나 쓴 글이 있으면 한번 물어본다
        if( bitmap != null || !(reviewEt.getText().toString().equals("")) ) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage( "작성중인 리뷰가 있습니다. \n닫으시겠어요?" );
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        }
        else{
            super.onBackPressed();
        }




//
    }

    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    //맛평가 초기화
    public void initTasteEstimate(){

        //맛의 초기값은 맛있다
        tasteEstimate = 1;

        tasteIv1 = (ImageView) findViewById(R.id.taste1_iv);
        tasteIv2 = (ImageView) findViewById(R.id.taste2_iv);
        tasteIv3 = (ImageView) findViewById(R.id.taste3_iv);


        //존맛
        Glide.with(this)
                .load( R.drawable.taste1_gray )
                .into(tasteIv1);
        tasteIv1.setScaleType(ImageView.ScaleType.FIT_XY);

        //중간 표정을 선택한 상태로 둔다
        Glide.with(this)
                .load( R.drawable.taste2_black )
                .into(tasteIv2);
        tasteIv2.setScaleType(ImageView.ScaleType.FIT_XY);

        //쓰레기
        Glide.with(this)
                .load( R.drawable.taste3_gray )
                .into(tasteIv3);
        tasteIv3.setScaleType(ImageView.ScaleType.FIT_XY);

    }




    //체크터치
    public void onClick_reviewUpload(View v){

        if(bitmap == null){
            //이미지가 없으면 업로드 불가능
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage( "이미지를 업로드해주세요" );
            builder.setNeutralButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();

        }
        else{
            //이미지는 있고, 글이 없는경우
            if(reviewEt.getText().toString().equals("")){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage( "리뷰 글을 작성해주세요" );
                builder.setNeutralButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

            }else{
                //이미지와 글이 모두 있음

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage( "리뷰를 업로드 합니다" );
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                update();
                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

//                connect();
            }

        }

    }

    //사진업로드
    public void onClick_selectImage(View v){
        Intent iT = new Intent(Intent.ACTION_PICK);
        iT.setType("image/*");
        startActivityForResult(iT, GALLERY_INTENT);
    }

    //맛터치
    public void onClick_rw_taste(View v){

        //일단 회색으로 해놓고 다시 바꿔줌
//        tasteSetBasic();

        switch(v.getId()){

            case R.id.taste1_iv://존맛
                tasteEstimate=2;
                Glide.with(this)
                        .load( R.drawable.taste1_black )
                        .into(tasteIv1);
                tasteIv1.setScaleType(ImageView.ScaleType.FIT_XY);

                //맛있따
                Glide.with(this)
                        .load( R.drawable.taste2_gray )
                        .into(tasteIv2);
                tasteIv2.setScaleType(ImageView.ScaleType.FIT_XY);

                //쓰레기
                Glide.with(this)
                        .load( R.drawable.taste3_gray )
                        .into(tasteIv3);
                tasteIv3.setScaleType(ImageView.ScaleType.FIT_XY);
                break;

            case R.id.taste2_iv:
                tasteEstimate=1;
                Glide.with(this)
                        .load( R.drawable.taste2_black )
                        .into(tasteIv2);
                tasteIv2.setScaleType(ImageView.ScaleType.FIT_XY);

                //존맛
                Glide.with(this)
                        .load( R.drawable.taste1_gray )
                        .into(tasteIv1);
                tasteIv1.setScaleType(ImageView.ScaleType.FIT_XY);

                //쓰레기
                Glide.with(this)
                        .load( R.drawable.taste3_gray )
                        .into(tasteIv3);
                tasteIv3.setScaleType(ImageView.ScaleType.FIT_XY);
                break;

            case R.id.taste3_iv:
                tasteEstimate=0;
                Glide.with(this)
                        .load( R.drawable.taste3_black )
                        .into(tasteIv3);
                tasteIv3.setScaleType(ImageView.ScaleType.FIT_XY);

                //존맛
                Glide.with(this)
                        .load( R.drawable.taste1_gray )
                        .into(tasteIv1);
                tasteIv1.setScaleType(ImageView.ScaleType.FIT_XY);

                //맛있따
                Glide.with(this)
                        .load( R.drawable.taste2_gray )
                        .into(tasteIv2);
                tasteIv2.setScaleType(ImageView.ScaleType.FIT_XY);

                break;

        }

    }




    //사진첩 호출의 결과
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            try {

                //이미지뷰에 고른 사진을 보여줌
                Glide.with(this)
                        .load(data.getData())
                        .into(iV);

                //선택한 사진을 비트맵으로 전환(Glide 이용)
                Glide.with(this)
                        .load(data.getData())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                bitmap = resource;
                                Log.d("DDDD", "전" + bitmap.getWidth());
                                if (bitmap.getWidth() > 600) {
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

    //Firebase에 업로드
    public void update(){

        //다이얼로그 켜기
        customDialog = new CustomDialog(this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //다이얼로그 뒷배경투명처리
        customDialog.setCancelable(false);//끌 수 없다
        customDialog.show(); // 보여주기




        //이미지경로
        String imgName = nowMenuName + "_" + SimpleFunction.getTodayDate();

        //지금 년월일
        String todayDate = SimpleFunction.getTodayDate3();


        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child(imgName);

        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("menu_review").child( todayDate ).child( imgName );

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
                customDialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage( "네트워크 연결이 불안정하여 업로드가 정상적으로 진행되지 못했습니다. \n재시도 하시겠어요?" );
                builder.setPositiveButton("재시도",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                update();
                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();



            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                //이미지 업로드를 성공하고, 이미지의 경로를 받아온 부분

                @SuppressWarnings("VisibleForTests")
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                //Push키 생성
                mRef = new Firebase( GlobalApplication.fbPath + "/Korea");
                String reviewKey = mRef.child("menu_review").push().getKey(); //키 값을 얻는다

                //업로드할 리뷰객체
                MenuReview_fb menuReview_fb = new MenuReview_fb();

                menuReview_fb.uid = GlobalApplication.getUser_id();
                menuReview_fb.memo = reviewEt.getText().toString();
                menuReview_fb.menu_id = nowMenuKey;
                menuReview_fb.review_image = downloadUrl.toString();
                menuReview_fb.date = SimpleFunction.getTodayDate();
                menuReview_fb.taste = Integer.toString( tasteEstimate );


                //menuReview업로드
                mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_review" );
                mRef.child( reviewKey ).setValue( menuReview_fb );


                //리뷰키:true
                Map reviewMap = new HashMap();
                reviewMap.put(reviewKey, true);

                //menu_info의 review_id안에 업로드
                mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_info/" + nowMenuKey);
                mRef.child( "review_id" ).updateChildren( reviewMap );

                //re_user_review에 업로드
                mRef = new Firebase( GlobalApplication.fbPath + "/Korea/re_user_review/");
                mRef.child( GlobalApplication.getUser_id() ).updateChildren( reviewMap );

                //로딩 다이얼로그 종료
                customDialog.dismiss();


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage( "리뷰 업로드 완료!" );
                builder.setCancelable(false);
                builder.setNeutralButton("확인",
                       new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //앞에 것들을 다 끔 (ScrollAvtivity에서 호출했을때는 아무 의미없는 코드)
                                ReviewSearchActivity rvActivity = (ReviewSearchActivity) ReviewSearchActivity.RVActivity;
                                ReviewSelectActivity rsActivity = (ReviewSelectActivity) ReviewSelectActivity.RSActivity;
                                if(rvActivity != null){
                                    rvActivity.finish();
                                }
                                if(rsActivity != null){
                                    rsActivity.finish();
                                }

                                //자폭
                                finish();
                            }
                        });
                builder.show();
            }
        });







    }


    //비트맵크기조절(비율유지)
    public Bitmap resizeBitmap(Bitmap original) {

        //가로를 650으로 줄인다. 세로는 비율에 맞게 줄어듦
        int resizeWidth = 650;

        double aspectRatio = (double) original.getHeight() / (double) original.getWidth();
        int targetHeight = (int) (resizeWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(original, resizeWidth, targetHeight, false);
        if (result != original) {
            original.recycle();
        }
        return result;
    }




























    //리뷰성공 다이알로그
    public void uploadSuccess(){

        //삭제성공 메시지를 띄우고 액티비티 종료
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Complete")
                .setContentText("리뷰가 등록되었습니다")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();

                        //메뉴창 띄워줌
                        Intent iT = new Intent(getApplicationContext(), ScrollActivity.class);
                        iT.putExtra("res_name", nowResName);
                        iT.putExtra("menu_name", nowMenuName);
                        startActivity(iT);

                        if(callFrom.equals("ScrollAvtivity")){
                            //앞에 있떤 메뉴표시창은 닫음
                            ScrollActivity scActivity = (ScrollActivity)ScrollActivity.SCActivity;
                            scActivity.finish();

                        }

                        //현재 창 닫음
                        finish();
                    }
                })
                .show();
    }

    public void uploadFail(){

        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("sorry..")
                .setContentText("네트워크 접속 장애\n다시 시도할께요!")
                .setCustomImage(R.drawable.sorry2)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
//                        connect();

                    }
                })
                .show();

    }


}

