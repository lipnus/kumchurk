package com.lipnus.kumchurk.kum_class;

import android.util.Log;

import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.firebaseModel.MainData_fb;

/**
 * Created by Sunpil on 2017-08-11.
 * ScrollActivity하단에 올 메뉴 8개를 뽑아서 반환
 *
 * 동일한 카테고리
 */

public class ScrollMenuRecommend {


    //서버에서 받은 전체 리스트의 복사본(최상단만 call by value로 복사)
    private MainData_fb mainData_fb_raw;


    //생성자
    public ScrollMenuRecommend() {

        //원본을 복사

        //최상단 리스트를 call by value방식으로 복사하여서 중복방지를 위해 삭제했을 때 원본이 손상되지 않도록 한다
        mainData_fb_raw = new MainData_fb();
        mainData_fb_raw.menuReview.addAll(GlobalApplication.mainData_fb_raw.menuReview);
        mainData_fb_raw.menuInfo.addAll( GlobalApplication.mainData_fb_raw.menuInfo );
        mainData_fb_raw.resInfo.addAll( GlobalApplication.mainData_fb_raw.resInfo );
    }



    //카테고리로 걸러진 num개를 반환
    public MainData_fb categorizedSelect(int num, String[] category, String resName){

        //호출한 메뉴와 동일한 식당의 메뉴는 거른다

        Log.d("SSFF", "categorizedSelect() 제외식당: " + resName);

        int menuSize; //전체 자료개수
        int ranNum; //랜덤넘버
        int inputCount = 0; // 한번 돌때마다 1씩 증가
        int inputSuccessCount = 0; //하나 넣을때마다 1씩 증가

        MainData_fb mD_fb = new MainData_fb(); //여기에 추천목록을 넣어서 반환한다


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

            //카테고리와 일치&동일한 식당의 메뉴는 표시하지 않음
            for(String str : category){
                String res_category = mainData_fb_raw.resInfo.get(ranNum).res_category;

                if(str.equals(res_category) && !mainData_fb_raw.resInfo.get(ranNum).res_name.equals(resName)){
                    pickOk = true;
                    break;
                }
            }//조건확인(for)


            //조건을 만족
            if( pickOk ) {

                inputSuccessCount++;

                Log.d("SSFF", "뽑자: " + mainData_fb_raw.menuInfo.get(ranNum).menu_name + ", 카테고리: " +
                        mainData_fb_raw.resInfo.get(ranNum).res_category);

                //반환될 것에 넣고
                mD_fb.resInfo.add(mainData_fb_raw.resInfo.get(ranNum));
                mD_fb.menuInfo.add(mainData_fb_raw.menuInfo.get(ranNum));
                mD_fb.menuReview.add(mainData_fb_raw.menuReview.get(ranNum));

                //또 뽑히지 않도록 기존 리스트에서 삭제 하지 않는다
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
