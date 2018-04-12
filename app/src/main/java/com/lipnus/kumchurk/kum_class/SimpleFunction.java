package com.lipnus.kumchurk.kum_class;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.lipnus.kumchurk.GlobalApplication;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sunpil on 2017-03-13.
 */

//자주 쓰이는 간단한 함수들을 모아놓음
public class SimpleFunction {

    /**
     * 두 지점간의 거리 계산
     *
     * @param lat2 목표지점 위도
     * @param lon2 목표지점 경도
     * @param unit 거리 표출단위
     * @return
     */
    public static int distance(double lat2, double lon2, String unit) {

        //GlobalApplication에 저장된 사용자의 위치값(어플이 실행될 때 받아옴)
        double lat1 = GlobalApplication.getUser_latitude();
        double lon1 = GlobalApplication.getUser_longitude();

        //위도경도를 받지 못할경우 초깃값인 0,0으로 유지되며, 이럴 경우 -1을 반환
       if (lat1 == 0){
            return -1;
        }

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (int)(dist);
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    //지금 위치에서 해당 목표까지 걸어서(35m/분) 몇분거리인지 표시
    public static String distanceMinute(double lat, double lon){

        String rV;

        int distance = distance(lat, lon, "meter");
        double needTime = (double)distance / 35;

        //소수 첫째자리까지만 표시
        DecimalFormat df =new DecimalFormat("#");

        if(distance != -1){
            rV = df.format(needTime) + "분 거리";
        }else{
            rV = "";
        }

        //해당 위치에 있을때 표시
        if(df.format(needTime).equals("0")){
            rV = "바로그곳!";
        }

        if( needTime > 80){
            rV = "안암동";
        }

        return rV;
    }


    //지금 위치에서 해당 목표까지 걸어서(35m/분) 몇분거리인지 표시(정수형)
    public static int distanceMinuteInt(double lat, double lon){

        String rV;

        int distance = distance(lat, lon, "meter");
        double needTime = (double)distance / 35;

        //소수 첫째자리까지만 표시
        DecimalFormat df =new DecimalFormat("#");

        if(distance != -1){
            rV = df.format(needTime);
        }else{
            rV = "0";
        }

        int returnValue = Integer.parseInt(rV);
        return returnValue;
    }



    // 지정된 범위의 정수 1개를 램덤하게 반환하는 메서드
    // n1 은 "하한값", n2 는 상한값
    public static int randomInt(int n1, int n2) {
        return (int) (Math.random() * (n2 - n1 + 1)) + n1;
    }


    //가격정보 표시
    public static String displayPrice(double p1, double p2, double p3){

        double price1 = p1 / 1000.0;
        double price2 = p2 / 1000.0;
        double price3 = p3 / 1000.0;
        String result = null;

        if (price3 != 0){
            result = ("S: " + price1 + "  M: " + price2 + "  L: "+ price3);
        }else if(price2 != 0){
            result = ("S: " + price1 + "  L: " + price2);
        }else{
            result = Double.toString(price1);

            //M부터 가격이 있는 경우
            if(price1 == 0){
                if(price2 != 0){
                    result = ("M: " + price2);
                }
            }
        }

        return result;
    }



    //두 정수 사이의 랜덤값을 반환해준다
    public static int getRandom(int n1, int n2) {
        return (int) (Math.random() * (n2 - n1 + 1)) + n1;
    }


    //서버에서 숫자로 된 시간을 받으면 현재 시간과 얼마나 차이가 나는지를 문자로 반환
    public static String timeGap(String tt) {

        String timeGap = "☆"; //남은 시간을 그때그때 상황에 맞는 형식으로 표시(25분전, 15시간전 2일전 등)


        Log.d("TTMM", "?: "+tt);

        //날짜표현형식
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");


        //비교할 시간 설정
        String targetTime = tt;
        Date startday = sdf.parse(targetTime, new ParsePosition(0));
        long startTime = startday.getTime();

        //현재의 시간 설정
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        long endTime = endDate.getTime();

        //날짜 차이
        long diffMin = (endTime - startTime)/(1000*60);
        long diffHour = diffMin/60;
        long diffDay = diffHour/24;

        Log.d("TITI", "분차이: " + diffMin);
        Log.d("TITI", "시간차이: " + diffHour);
        Log.d("TITI", "날차이: " + diffDay);

        if(diffMin < 60){
            timeGap = diffMin + "분 전";
        }else if(diffHour < 24){
            timeGap = diffHour + "시간 전";
        }else if(diffDay < 365){
            timeGap = diffDay + "일 전";
        }else{

            //1년 이전이면 날짜를 그대로 표시
            String showDate = tt;
            String year = showDate.substring(0,4);
            String month = showDate.substring(4,6);
            String day = showDate.substring(6,8);
            showDate = year + "년 " + month + "월 " + day + "일";

            timeGap = showDate;
        }

        Log.d("TITI", "timeGap: " + timeGap);

        return timeGap;
    }

    //지금 년월일시간분
    public static String getTodayDate(){

        //오늘
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyyMMddHHmm", Locale.KOREA );
        java.util.Date currentTime = new java.util.Date ( );
        String todayDate = mSimpleDateFormat.format(currentTime);

        return todayDate;
    }

    //지금 년월일시간분초
    public static String getTodayDate2(){

        //오늘
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyyMMddHHmmss", Locale.KOREA );
        java.util.Date currentTime = new java.util.Date ( );
        String todayDate = mSimpleDateFormat.format(currentTime);

        return todayDate;
    }

    //지금 년월일
    public static String getTodayDate3(){

        //오늘
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyyMMdd", Locale.KOREA );
        java.util.Date currentTime = new java.util.Date ( );
        String todayDate = mSimpleDateFormat.format(currentTime);

        return todayDate;
    }

    //리스트뷰 안의 내용을 토대로 세로크기를 정해준다
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    //시간을 몇가지의 경우로 분류하고 그 값을 반환
    public static int whatTime(){

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




    //어플리케이션의 유저 정보를 프레퍼런스에 저장
    public static void applicationToPreference(){

        SharedPreferences setting;
        SharedPreferences.Editor editor;

        //Prefrence설정(0:읽기,쓰기가능)
//        setting = getSharedPreferences("USERDATA", 0);
//        editor= setting.edit();

        //2.
//        editor.putString("user_nickname", GlobalApplication.getUser_nickname() );
//        editor.putString("user_sex", GlobalApplication.getUser_sex() );
//        editor.putString("user_grade", GlobalApplication.getUser_grade() );
//        editor.putString("user_image", GlobalApplication.getUser_image() );
//        editor.putString("user_thumbnail", GlobalApplication.getUser_thumbnail() );
//        editor.commit();
    }


}

