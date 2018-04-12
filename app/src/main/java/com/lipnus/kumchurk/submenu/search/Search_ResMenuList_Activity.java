package com.lipnus.kumchurk.submenu.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.IntroActivity;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.firebaseModel.MenuInfo_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;
import com.lipnus.kumchurk.firebaseModel.ScrollData;
import com.lipnus.kumchurk.firebaseModel.ScrollDataList;
import com.lipnus.kumchurk.kum_class.CustomDialog;
import com.lipnus.kumchurk.kum_class.SimpleFunction;
import com.lipnus.kumchurk.map.MapActivity;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.lipnus.kumchurk.kum_class.SimpleFunction.displayPrice;


/**
 * ReviewSelectActivity와 거의 같다
 */

/*==================================
//식당의 메뉴 목록들을 보여주는 액티비티
==================================== */

public class Search_ResMenuList_Activity extends AppCompatActivity {


     //해당 식당의 메뉴리스트 위에 텍스트뷰
    TextView menulistTitleTv;

    //해당 식당의 음식메뉴들을 출력하는 리스트뷰
    ListView listview;
    Search_ResMenuList_ListViewAdapter adapter;

    //Firebase 참조
    private Firebase mRef;

    //firebase에서 받은 메뉴정보를 저장
    List<ScrollData> sDList;
    ResInfo_fb resInfo_fb;

    //ScrollDataList는 카테고리가 같은 식당들의 묶음이다. 이놈은 그 묶음들을 담고있는 묶음
    List<ScrollDataList> sDLList;

    //다운로드가 끝난 것을 체크(review호출 수 = user호출 수 임을 이용)
    int reviewCount;
    int reviewCompareCount;
    int menuCount;
    int menuCompareCount;

    //그림 회전 다이얼로그
    CustomDialog customDialog;

    //식당키
    String nowResKey;

    //다른 곳에서 이 액티비티를 조종할 수 있게 한다
    public static Activity SRMLActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_resmenulist);

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


        //앞의 엑티비티로부터 식당이름과 메뉴이름을 받는다
        Intent iT = getIntent();
        nowResKey = iT.getExtras().getString("res_key");

        // Adapter 생성
        adapter = new Search_ResMenuList_ListViewAdapter();

        // 식당메뉴 리스트뷰와 어댑터
        listview = (ListView) findViewById(R.id.review_select_listview);
        listview.setAdapter(adapter);

        //메뉴타이틀
        menulistTitleTv = (TextView) findViewById(R.id.review_select_title_tv);

        //액티비티 선언
        SRMLActivity = Search_ResMenuList_Activity.this;

        //데이터를 받아온다
        connect_fb_resInfo();

    }//onCreate끝

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //애니매이션
        overridePendingTransition(R.anim.fadein, R.anim.translate_right);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    } //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )



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


    //===============================================================================================
    //Firebase에서 데이터 다운로드
    //===============================================================================================
    public void connect_fb_resInfo(){

        //다이얼로그 켜기
        customDialog = new CustomDialog(this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //다이얼로그 뒷배경투명처리
        customDialog.setCancelable(true);//끌 수 없다
        customDialog.show(); // 보여주기

        //리스트 초기화
        sDList = new ArrayList<>();

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/res_info/" + nowResKey);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{
                    reviewCount = 0;
                    reviewCompareCount = 0;
                    menuCount = 0;
                    menuCompareCount = 0;

                    //이 식당의 메뉴목록들을 가져온다

                    //이건 전역적으로 선언된 것
                    resInfo_fb = dataSnapshot.getValue( ResInfo_fb.class );
                    resInfo_fb.key = dataSnapshot.getKey();

                    Map map = resInfo_fb.menu_id;
                    Iterator<String> keySetIterator = map.keySet().iterator();

                    menuCount = map.size();

                    //메뉴들을 찾는다
                    while (keySetIterator.hasNext()) {
                        String menuKey = keySetIterator.next();
                        connect_fb_menuInfo(menuKey);
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

    public void connect_fb_menuInfo(String menuKey){
        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/menu_info/" + menuKey);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{
                    //식당의 메뉴들
                    MenuInfo_fb menuInfo_fb = dataSnapshot.getValue( MenuInfo_fb.class );
                    menuInfo_fb.key = dataSnapshot.getKey();

                    Log.d("SSFF", "메뉴: " + menuInfo_fb.menu_name);

                    //메뉴입력(하위의 리뷰가 없다면 메뉴정보만 입력, 리뷰가 있다면 리뷰와 유저 정보까지 찾아서 입력)
                    ScrollData scrollData = new ScrollData();
                    scrollData.menuInfo = menuInfo_fb;

                    Map map = menuInfo_fb.review_id;
                    Iterator<String> keySetIterator = map.keySet().iterator();

                    menuCompareCount++;

                    //이 메뉴가 가진 리뷰의 수 체크
                    reviewCount += map.size();

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



                    //이 식당에 사진이 하나도 없는경우 쫑을 내야함
                    if(menuCompareCount == menuCount && reviewCount==0){
                        Log.d("SSFF", "메뉴 끝이다");
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

                    reviewCompareCount++;
                    Log.d("SSFF", "리뷰: " + menuReview_fb.review_image + reviewCompareCount + "/" + reviewCount);

                    //이 메뉴가 등록되어 있으면 리뷰를 그 안에다가 집어넣는다(리뷰가 여러개일 경우 하나의 메뉴집단으로 묶음)
                    Boolean alreadyExist = false;
                    for(int i=0; i<sDList.size(); i++){
                        if( sDList.get(i).menuInfo.menu_name.equals( menuInfo_fb.menu_name ) ){

                            sDList.get(i).menuReviews.add( menuReview_fb );
                            alreadyExist = true;
                        }//if
                    }//for

                    //이 리뷰 메뉴이름으로 등록된 것이 없으면 새로운 메뉴를 추가
                    if(alreadyExist==false){
                        ScrollData scrollData = new ScrollData();
                        scrollData.menuInfo = menuInfo_fb;
                        scrollData.menuReviews.add( menuReview_fb );
                        sDList.add(scrollData);
                    }

                    //다운로드 정료
                    if(reviewCompareCount==reviewCount){
                        Log.d("SSFF", "리뷰 끝이다");
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

    //Firebase로부터 다운로드 완료 후 데이터들을 배치
    public void downloadFinish(){

        Log.d("SSFF", "downloadFinish()" );

        //다이얼로그 제거
        customDialog.dismiss();

        //메뉴들을 카테고리별로 묶는다
        menuListCategorized();

        //이미지가 있는 카테고리를 앞쪽으로 이동
        sortCategory();

        //메뉴들을 리스트뷰에 출력
        addMenuList();

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
                    resInfo_fb.res_name, resInfo_fb.key);

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
                            resInfo_fb.res_name, resInfo_fb.key);
                }
                else{//j가 마지막(j+1은 넘어감)

                    //리스에 하나(길쭉하게)집어넣음
                    adapter.addItem(true, left_name, l_price, l_img, path1.menuInfo.key,
                            false, "", "", "", "",
                            false, "",
                            resInfo_fb.res_name, resInfo_fb.key);

                    Log.d("LSLS", "왜식당이름 안나와: " + resInfo_fb.res_name);
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
                        resInfo_fb.res_name, resInfo_fb.key,
                        pos + haveImgCount +1);
            }
            else{//i가 마지막(i+1은 넘어감)

                //리스에 하나(길쭉하게)집어넣음
                adapter.addItem(true, left_name, l_price, l_img, path1.menuInfo.key,
                        false, "", "", "", "",
                        false, "",
                        resInfo_fb.res_name, resInfo_fb.key,
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

}//소스끝



