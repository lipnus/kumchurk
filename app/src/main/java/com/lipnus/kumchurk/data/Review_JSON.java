package com.lipnus.kumchurk.data;

import java.util.List;

/**
 * Created by Sunpil on 2017-02-26.
 * 뉴스피드에 쓰일 데이터들
 * menu_review, user_info, res_info 3가지 테이블에서 필요한 정보들을 가져온다
 * 뉴스피드 리스트에서 아이템으로 사용된다
 */

public class Review_JSON {

    public List<Review> review;

    public List<Review> getReview() {
        return review;
    }
}
