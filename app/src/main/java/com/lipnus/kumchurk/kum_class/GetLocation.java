package com.lipnus.kumchurk.kum_class;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.IntroActivity;

/**
 * Created by Sunpil on 2017-03-23.
 *
 * 사용자의 위치를 받아오는 클래스
 *
 */

public class GetLocation {

    //현재위치를 구하는데 필요한 지오코더
    LocationManager lm;

    //생성자
    public GetLocation(Context ct){
        //현재위치를 구하는데 필요한 지오코더
        lm = (LocationManager) ct.getSystemService(Context.LOCATION_SERVICE); //LocationManager 객체를 얻어온다

        //연재위치 구하기
        WhereAreyou();
    }


    //현재위치좌표를 구한다
    public void WhereAreyou(){
        try{

            Log.d("GPGP", "위치정보 수신중..");

            // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);

        }catch(SecurityException ex){}

    }

    //위치 리스너
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("GPGP", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.

            Log.d("GPGP", "위치정보 : " + provider + "\n위도 : " + latitude + "\n경도 : " + longitude
                    + "\n고도 : " + altitude + "\n정확도 : "  + accuracy);

            //GlobalApplication에 현재의 위치 저장
            GlobalApplication.setLocation(latitude, longitude);

            //IntroActivity의 날씨텍스트를 수정한다
            IntroActivity inActivity = (IntroActivity) IntroActivity.INActivity;
            if(inActivity != null && inActivity.joined==true){
                inActivity.locationTextUpdate();
            }


            lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };



}
