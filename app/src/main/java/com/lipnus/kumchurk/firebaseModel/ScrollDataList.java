package com.lipnus.kumchurk.firebaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunpil on 2017-08-08.
 * ScrollActivity에서 쓰는 것.
 *
 * 카테고리별로 분류한 것
 * ScrollData를 포함하는 더 큰 리스트다
 */

public class ScrollDataList {

    public String category;
    public Boolean haveReview; //이 카테고리 전체에서 리뷰를 가진 메뉴가 적어도 하나 있는 경우

    public List<ScrollData> scrollDatas;

    public ScrollDataList() {
        scrollDatas = new ArrayList<>();
        haveReview = false;
    }

    //call by value복사를 위한 생성자
    public ScrollDataList(ScrollDataList sdl) {

        scrollDatas = sdl.getScrollDatas();
        haveReview = sdl.getHaveReview();
        category = sdl.getCategory();
    }

    public String getCategory() {
        return category;
    }

    public Boolean getHaveReview() {
        return haveReview;
    }

    public List<ScrollData> getScrollDatas() {
        return scrollDatas;
    }
}
