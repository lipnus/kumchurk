package com.lipnus.kumchurk.data;

/**
 * Created by Sunpil on 2017-05-16.
 * 리뷰를 리스트로 쭈욱 받아올 때 쓴다
 * 나만의 리뷰에 쓰임
 */

public class Review {

    //리뷰정보
    private String review_num;
    private String review_memo;
    private String res_name;
    private String menu_name;

    private String writer_nickname;
    private String writer_thumbnail;
    private String writer_id;

    private String review_heart;
    private String review_fuck;
    private String review_image;
    private String review_comment;
    private String review_updated_at;

    public String getReview_num() {
        return review_num;
    }

    public String getReview_memo() {
        return review_memo;
    }

    public String getRes_name() {
        return res_name;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public String getWriter_nickname() {
        return writer_nickname;
    }

    public String getWriter_thumbnail() {
        return writer_thumbnail;
    }

    public String getWriter_id() {
        return writer_id;
    }

    public String getReview_heart() {
        return review_heart;
    }

    public String getReview_fuck() {
        return review_fuck;
    }

    public String getReview_image() {
        return review_image;
    }

    public String getReview_comment() {
        return review_comment;
    }

    public String getReview_updated_at() {
        return review_updated_at;
    }
}
