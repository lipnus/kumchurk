package com.lipnus.kumchurk.map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.lipnus.kumchurk.R;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.tsengvn.typekit.TypekitContextWrapper;

public class MapActivity extends NMapActivity implements NMapView.OnMapStateChangeListener {

    private NMapView mMapView;// 지도 화면 View
    private final String CLIENT_ID = "982LkWj_x1NChbfsS7DJ";// 애플리케이션 클라이언트 아이디 값

    NMapController mMapController;
    NMapViewerResourceProvider mMapViewerResourceProvider;
    NMapOverlayManager mOverlayManager;

    TextView resNameTv;
    TextView resLocationTv;
    TextView resPhoneTv;

    double RES_LATITUDE = 127.0214863; //위도
    double RES_LONGITUDE = 37.5902947; //경도
    String RES_LOCATION = "주소";
    String RES_NAME = "식당이름"; //식당이름
    String RES_PHONE = "000-000-0000"; //식당전화번호

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        resNameTv = (TextView) findViewById(R.id.map_resNameTv);
        resLocationTv = (TextView) findViewById(R.id.map_resLocationTv);
        resPhoneTv = (TextView) findViewById(R.id.map_resPhoneTv);

        //인텐트를 통해 절달받은 위치정보를 반영
        Intent iT = getIntent();
        RES_LATITUDE = iT.getDoubleExtra("res_latitude", -1);
        RES_LONGITUDE = iT.getDoubleExtra("res_longitude", -1);
        RES_NAME =  iT.getExtras().getString("res_name");
        RES_LOCATION = iT.getExtras().getString("res_location");
        RES_PHONE = iT.getExtras().getString("res_phone");

        resNameTv.setText( RES_NAME );
        resLocationTv.setText( RES_LOCATION );

        //네이버지도 세팅
        mapSetting();

        //인텐트에서 받은 정보를 이용하여 각종 정보출력
        //intentSetting();
    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    } //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )

    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
        //지도의 초기위치를 설정
        mMapController.setMapCenter(new NGeoPoint(RES_LONGITUDE, RES_LATITUDE), 13); //인텐트를 통해 받아온 위도, 경도로 지도중심 설정
    }


    public void onClick_phoneCall(View v){
        String tel = "tel:"+RES_PHONE;
        startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
    } //위쪽의 탭을 눌러서 전화걸기

    public void onClick_mapEnd(View v){
        this.finish();
    } //아래쪽의 BACK을 눌렀을때 현재 액티비티 종료


    //이전 액티비티에서 받은 인텐트를 이용하여 화면에 정보들을 출력
    public void intentSetting(){

        //인텐트를 통해 절달받은 위치정보를 반영
        Intent iT = getIntent();
        RES_LATITUDE = iT.getDoubleExtra("res_latitude", -1);
        RES_LONGITUDE = iT.getDoubleExtra("res_longitude", -1);
        RES_NAME =  iT.getExtras().getString("res_name");
        RES_LOCATION = iT.getExtras().getString("res_location");
        RES_PHONE = iT.getExtras().getString("res_phone");

        resNameTv.setText( RES_NAME );
        resLocationTv.setText( RES_LOCATION );
        resPhoneTv.setText( RES_PHONE );
        //String으로 된 전화번호 사이에 하이픈을 삽입

    }

    //네이버 지도앱을 세팅한다
    public void mapSetting() {

        mMapView = (NMapView)findViewById(R.id.mapView);
        mMapView.setClientId(CLIENT_ID); // 클라이언트 아이디 값 설정
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();
        mMapView.setScalingFactor(2.0f); //확대가 많이 되도록 설정
        mMapView.setOnMapStateChangeListener(this); //리스너

        // use map controller to zoom in/out, pan and set map center, zoom level etc.
        mMapController = mMapView.getMapController();

        // create resource provider
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

        // create overlay manager
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);



        //==============================================================
        // 오버레이 마커표시
        //==============================================================

        int markerId = NMapPOIflagType.PIN;

        // set POI data
        NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
        poiData.beginPOIdata(0);
        poiData.addPOIitem( RES_LONGITUDE, RES_LATITUDE, RES_NAME, markerId, 0); //인텐트를 통해 받아온 위도, 경도에 식당마커 표시
        poiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

        // show all POI data
        poiDataOverlay.showAllPOIdata(0);
    }























    @Override
    public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

    }

    @Override
    public void onMapCenterChangeFine(NMapView nMapView) {

    }

    @Override
    public void onZoomLevelChange(NMapView nMapView, int i) {

    }

    @Override
    public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

    }
}
