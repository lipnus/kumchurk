package com.lipnus.kumchurk.kum_class;

import android.util.Log;

import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.data.MenuRes_Info;
import com.lipnus.kumchurk.data.main.Main_Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunpil on 2017-04-22.
 * 여기에선 Globalapplication.menuData에 저장된 식당과 메뉴데이터들 중에서
 * 호출한 요구조건(거리, 가격 등등)
 * 에 맞게 데이터들을 적당히 뽑아서 반환해준다
 */

public class Select_menu {

    private Main_Data mainData; //rowData
    private List<Main_Data> mainData2; //여기에 3개 단위로 포장


    //생성자
    public Select_menu(Main_Data mainData) {
        this.mainData = mainData;
        GlobalApplication.setMainData(mainData); //원본을 어플리케이션에 업로드
    }


    //생성자에서 받아놓은 mainData안의 menuresInfo를 3개씩 포장
    public void makeMainList() {

        mainData2 = new ArrayList<Main_Data>(); //반환데이터
        Main_Data temp_md; //임시데이터

        //첫번째 리스트는 8개짜리
        temp_md = new Main_Data();
        temp_md.setMenuresInfo( randomSelect(8) );
        mainData2.add(temp_md);


        //시간대별 메뉴추천
        if(SimpleFunction.whatTime() == 2){
            brunch();
        }
        if(SimpleFunction.whatTime() == 6){
            firenight();
        }
        else{
            basic();
        }

        //Application에 정렬된 메뉴리스트를 어플리케이션에 업로드
        GlobalApplication.setMainData2(mainData2);
    }


    //앱력받은 개수만큼의 menuresInfo 리스트를 반환
    public List<MenuRes_Info> randomSelect(int count) {

        int menuSize = mainData.menuresInfo.size(); //정보의 개수
        int ranNum; //랜덤넘버
        List<MenuRes_Info> mrI = new ArrayList<>(); //반환할 형식

        for (int i = 0; i < count; i++) {
            ranNum = SimpleFunction.getRandom(0, menuSize - 1); //난수생성
            mrI.add(mainData.menuresInfo.get(ranNum)); //반환될 리스트에 넣음
            mainData.menuresInfo.remove(ranNum); //넣었던 것은 다른곳에 또 넣어지지 않게 삭제
            menuSize--;
        }

        return mrI;
    }


    //특정 카테고리로만 구성된 menuresInfo리스트를 반환
    public List<MenuRes_Info> categorizedSelect(int count, String cat, String cat2, String cat3) {

        int menuSize = mainData.menuresInfo.size(); //정보의 개수
        int ranNum; //랜덤넘버
        List<MenuRes_Info> mrI = new ArrayList<>(); //반환할 형식

        String category = cat;
        String category2 = cat2;
        String category3 = cat3;

        int replayCount=0;

        for (int i = 0; i < count; i++) {
            ranNum = SimpleFunction.getRandom(0, menuSize - 1); //난수생성

            Log.d("SMSM", "" + mainData.menuresInfo.get(ranNum).getMenu_category1() + ", " + cat + ", " +
                    mainData.menuresInfo.get(ranNum).getMenu_category1().equals(category));

            if( mainData.menuresInfo.get(ranNum).getMenu_category1().equals(category) ||
                mainData.menuresInfo.get(ranNum).getMenu_category1().equals(category2) ||
                mainData.menuresInfo.get(ranNum).getMenu_category1().equals(category3)){

                Log.d("SMSM", "" + mainData.menuresInfo.get(ranNum).getMenu_category1());
                mrI.add(mainData.menuresInfo.get(ranNum)); //반환될 리스트에 넣음
                mainData.menuresInfo.remove(ranNum); //넣었던 것은 다른곳에 또 넣어지지 않게 삭제
                menuSize--;

            }else{


                replayCount++;

                //사진이 없는경우 그냥 랜덤으로(리뷰가 다시 많아지면 삭제)
                if(replayCount>100){
                    mrI.add(mainData.menuresInfo.get(ranNum)); //반환될 리스트에 넣음
                    mainData.menuresInfo.remove(ranNum);
                    menuSize--;
                }else{
                    i--; //적절하지 않으니 한번 더 해라
                }
            }




        }

        return mrI;
    }


    public void brunch(){
        Main_Data temp_md; //임시데이터

        //하나짜리
        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(1, "카페/제과", "기타", "없음") );
        mainData2.add(temp_md);

        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(3, "카페/제과", "기타", "없음") );
        mainData2.add(temp_md);

        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(3, "일식", "중식", "세계식당") );
        mainData2.add(temp_md);

        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(3, "양식", "패스트푸드", "치킨") );
        mainData2.add(temp_md);

        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(3, "한식", "중식", "분식") );
        mainData2.add(temp_md);

    }

    public void basic(){

        Main_Data temp_md; //임시데이터

        //하나짜리
        temp_md = new Main_Data();
        temp_md.setMenuresInfo( randomSelect(1) );
        mainData2.add(temp_md);

        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(3, "한식", "분식", "무한리필") );
        mainData2.add(temp_md);

        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(3, "양식", "패스트푸드", "치킨") );
        mainData2.add(temp_md);

        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(3, "일식", "중식", "세계식당") );
        mainData2.add(temp_md);

        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(3, "카페/제과", "없음", "기타") );
        mainData2.add(temp_md);
    }

    public void firenight(){
        Main_Data temp_md; //임시데이터

        //하나짜리
        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(1, "술", "치킨", "술") ) ;
        mainData2.add(temp_md);

        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(3, "술", "치킨", "치킨") );
        mainData2.add(temp_md);

        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(3, "술", "술", "술") );
        mainData2.add(temp_md);

        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(3, "술", "술", "술") );
        mainData2.add(temp_md);

        temp_md = new Main_Data();
        temp_md.setMenuresInfo( categorizedSelect(3, "술", "술", "술") );
        mainData2.add(temp_md);
    }

}


