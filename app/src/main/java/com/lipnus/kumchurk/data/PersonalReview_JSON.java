package com.lipnus.kumchurk.data;

import java.util.List;

/**
 * menu_review, user_info, res_info 3가지 테이블에서 필요한 정보들을 가져온다
 * 뉴스피드 리스트에서 아이템으로 사용된다
 */

public class PersonalReview_JSON {

    private String personal_nickname;
    private String personal_thumbnail;
    private String personal_image;

    public List<Review> review;


    public String getPersonal_nickname() {
        return personal_nickname;
    }

    public String getPersonal_thumbnail() {
        return personal_thumbnail;
    }

    public String getPersonal_image() {
        return personal_image;
    }
}
