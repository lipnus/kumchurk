package com.lipnus.kumchurk.kum_class;

import android.util.Log;

import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.firebaseModel.MainData_fb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunpil on 2017-08-11.
 */

public class MenuRecommend {


    //서버에서 받은 그대로
    private MainData_fb mainData_fb_raw;


    //위에놈을 3개단위로 넣는 리스트
    private List<MainData_fb> mainData_fbList;


    //생성자
    public MenuRecommend(MainData_fb mainData_fb_raw) {

        Log.d("SSFF", "MenuRecommend 생성자");

        this.mainData_fb_raw = mainData_fb_raw;

        //원본을 GlobalApplication에 업로드 해놓는다
        GlobalApplication.mainData_fb_raw = mainData_fb_raw;
        mainData_fbList = new ArrayList<MainData_fb>();
    }


    //메인의 추천 리스트를 생성해준다
    public void makeMainList(){

        Log.d("SSFF", "MakeMainList()");


        //아래의 과정이 진행되면 mainData_fbList에 값이 저장된다
        //그것을 GlobalApplication에 업로드한다.

        //정렬테마 선정


        //시간대별 메뉴추천
        if(SimpleFunction.whatTime() == 2){
            brunch();
        }
        if(SimpleFunction.whatTime() == 6){
            basic();

        }
        else{
            basic();
        }


    }

    private void basic(){

        Log.d("SSFF", "basic()");
        MainData_fb temp_md ; //임시데이터
        String[] categoryArr;

        //메인페이지
        categoryArr = new String[] {"한식", "분식", "무한리필", "고기", "양식", "치킨", "일식", "중식", "세계식당", "횟집"};
        temp_md = categorizedSelect(1, categoryArr);
        mainData_fbList.add(temp_md);

        //3개씩 포장
        categoryArr = new String[] {"한식", "분식", "무한리필", "고기", "학식"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"양식", "치킨"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"일식", "중식", "세계식당", "횟집"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"카페"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"한식", "분식", "무한리필", "고기", "학식"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"양식", "치킨"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"일식", "중식", "세계식당", "횟집"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"카페"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        //GlobalApplication에 업로드
        GlobalApplication.mainData_fbList = mainData_fbList;

    }

    private void brunch(){

        Log.d("SSFF", "brunch");
        MainData_fb temp_md; //임시데이터
        String[] categoryArr;

        //메인페이지
        categoryArr = new String[] {"카페"};
        temp_md = categorizedSelect(1, categoryArr);
        mainData_fbList.add(temp_md);

        //3개씩 포장
        categoryArr = new String[] {"카페"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"카페"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"한식", "분식", "무한리필", "고기", "학식"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"양식", "치킨"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"일식", "중식", "세계식당", "횟집"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"카페"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"한식", "분식", "무한리필", "고기", "학식"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);

        categoryArr = new String[] {"카페"};
        temp_md = categorizedSelect(3, categoryArr);
        mainData_fbList.add(temp_md);


        //GlobalApplication에 업로드
        GlobalApplication.mainData_fbList = mainData_fbList;

    }

    private MainData_fb categorizedSelect(int num, String[] category){

        Log.d("SSFF", "categorizedSelect()");

        int menuSize; //전체 자료개수
        int ranNum; //랜덤넘버
        int inputCount = 0; // 한번 돌때마다 1씩 증가
        int inputSuccessCount = 0; //하나 넣을때마다 1씩 증가

        MainData_fb mD_fb = new MainData_fb(); //여기에 3개를 넣어서 반환한다


        //입력받은 카테고리
        for(String str : category){
            Log.d("SSFF", "카테고리: " + str);
        }//카테고리 일치확인(for)


        //뽑아보자!
        while (inputSuccessCount < num) {

            inputCount++; //한바퀴 돌때마다 +1
            menuSize = mainData_fb_raw.resInfo.size(); //정보의 개수
            ranNum = SimpleFunction.getRandom(0, menuSize - 1); //난수생성

            Boolean pickOk = false;

            //카테고리와 일치하는지 확인
            for(String str : category){
                String res_category = mainData_fb_raw.resInfo.get(ranNum).res_category;
                if(str.equals(res_category)){ pickOk = true; break; }
            }//카테고리 일치확인(for)


            //조건을 만족
            if( pickOk ) {

                inputSuccessCount++;

                Log.d("SSFF", "뽑자: " + mainData_fb_raw.menuInfo.get(ranNum).menu_name + ", 카테고리: " +
                        mainData_fb_raw.resInfo.get(ranNum).res_category);

                //반환될 것에 넣고
                mD_fb.resInfo.add(mainData_fb_raw.resInfo.get(ranNum));
                mD_fb.menuInfo.add(mainData_fb_raw.menuInfo.get(ranNum));
                mD_fb.menuReview.add(mainData_fb_raw.menuReview.get(ranNum));

                //또 뽑히지 않도록 기존 리스트에서 삭제
                mainData_fb_raw.resInfo.remove(ranNum);
                mainData_fb_raw.menuInfo.remove(ranNum);
                mainData_fb_raw.menuReview.remove(ranNum);
            }

            //조건을 만족하지 않는경우
            else{

                Log.d("SSFF", "조건에 안맞음: " + mainData_fb_raw.resInfo.get(ranNum).res_category);

                //100번 이상 돌았으면 각이 더이상 안나오는 것임. 걍 확인안하고 넣자
                if(inputCount > 100){

                    inputSuccessCount++;

                    Log.d("SSFF", "어쩔 수 없이 뽑자: " + mainData_fb_raw.menuInfo.get(ranNum).menu_name);

                    //반환될 것에 넣고
                    mD_fb.resInfo.add(mainData_fb_raw.resInfo.get(ranNum));
                    mD_fb.menuInfo.add(mainData_fb_raw.menuInfo.get(ranNum));
                    mD_fb.menuReview.add(mainData_fb_raw.menuReview.get(ranNum));

                    //또 뽑히지 않도록 기존 리스트에서 삭제
                    mainData_fb_raw.resInfo.remove(ranNum);
                    mainData_fb_raw.menuInfo.remove(ranNum);
                    mainData_fb_raw.menuReview.remove(ranNum);

                }

            }//조건을 만족하지 않는경우(else)
        }//while문

        return mD_fb; //3개씩 포장해서 반환

    }



}
