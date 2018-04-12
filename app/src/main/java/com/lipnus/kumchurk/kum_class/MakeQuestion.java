package com.lipnus.kumchurk.kum_class;

import android.util.Log;

import com.lipnus.kumchurk.data.Weather;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sunpil on 2017-05-15.
 * 현재날씨에 맞게 질문을 만들어주는 곳
 */

public class MakeQuestion {

    Weather weather;

    //날짜

    public MakeQuestion(Weather weather) {
        this.weather = weather;
    }

    //날씨에 대한 언급을 해줌
    public static String weatherComment( String weather, double temp ){

        String wC=""; //weatherComment


        //특이한 날씨면 거기에 대한 언급을 해준다
        if(weather.equals("rain")){ wC = "오늘은 비가 추적추적 내리네요\n"; }
        else if(weather.equals("shower rain")){ wC = "부슬비가 내리네요\n"; }
        else if(weather.equals("snow")){ wC = "눈이 내려요!\n"; }
        else if(weather.equals("thunderstorm")){ wC = "천둥번개가 치는 무서운 날..\n"; }
        else if(temp > 30){ wC = "오늘은 정말 덥네요\n"; }
        else if(temp < 0){ wC = "너무 추워요 ~*.*~\n"; }



        Log.d("TTMM", " " + wC);

        //특별한 날씨이벤트가 없으면 null값
        return wC;
    }


    public static String whatTime(){

        //해당 시간의 속성
        String hS=null; //hourState

        //현재의 시간 설정
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("HH");
        String timeStr = dayTime.format(new Date(time));

        int nowHour = Integer.parseInt(timeStr);

        if(6<= nowHour && nowHour <10){
            hS = "아침";
        }
        else if(10<= nowHour && nowHour <12){
            hS = "아점";
        }
        else if(12<= nowHour && nowHour <14){
            hS = "점심";
        }
        else if(14<= nowHour && nowHour <17){
            hS = "점저";
        }
        else if(17<= nowHour && nowHour <20){
            hS = "저녁";
        }
        else if((20<= nowHour && nowHour <24) || 0 <= nowHour && nowHour <6 ){
            hS = "야식";
        }

        Log.d("TTMM", "상태: " + hS);
        return hS;

    }

}
