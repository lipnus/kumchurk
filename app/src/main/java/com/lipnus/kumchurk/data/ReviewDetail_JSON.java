package com.lipnus.kumchurk.data;

import java.util.List;

/**
 * Created by Sunpil on 2017-05-16.
 * 리뷰글 번호를 통해 이것들을 얻을 수 있다. 리뷰 하나짜리 중에서 완성형
 * 식당정보, 메뉴정보, 리뷰글에 대한 모든 정보를 다 긁어오고, 그 리뷰글에 속한 댓글들도 리스트형태로 다 가져온다
 */

public class ReviewDetail_JSON {

    //메뉴정보
    private String menu_name;
    private int menu_price;
    private int menu_price2;
    private int menu_price3;
    private String menu_category1;
    private String menu_category2;
    private int menu_heart;
    private int menu_fuck;
    private String menu_comment;

    //식당정보
    private String res_name;
    private String res_category;
    private String res_theme;
    private String res_time;
    private String res_phone;
    private String res_heart;
    private String res_fuck;
    private String res_comment;
    private String res_location;
    private double res_longitude;
    private double res_latitude;
    private String res_star;
    private String res_star_num;

    //리뷰정보
    private String review_num;
    private String review_memo;
    private int review_taste;
    private String review_heart;
    private String review_fuck;
    private String review_image;
    private String review_comment;
    private String review_updated_at;

    //리뷰 쓴 사람정보
    private String writer_id;
    private String writer_nickname;
    private String writer_image;
    private String writer_thumbnail;

    //이 리뷰에 내가 한 투표정보
    private int vote_heart;
    private int vote_fuck;

    //이 리뷰에 달린 댓글들
    List<ReviewComment> reviewComment;


    public int getReview_taste() {
        return review_taste;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public int getMenu_price() {
        return menu_price;
    }

    public int getMenu_price2() {
        return menu_price2;
    }

    public String getReview_num() {
        return review_num;
    }

    public String getReview_image() {
        return review_image;
    }

    public String getReview_heart() {
        return review_heart;
    }

    public String getReview_fuck() {
        return review_fuck;
    }

    public String getReview_comment() {
        return review_comment;
    }

    public int getMenu_price3() {
        return menu_price3;
    }

    public String getMenu_category1() {
        return menu_category1;
    }

    public String getMenu_category2() {
        return menu_category2;
    }

    public int getMenu_heart() {
        return menu_heart;
    }

    public int getMenu_fuck() {
        return menu_fuck;
    }

    public String getMenu_comment() {
        return menu_comment;
    }

    public String getRes_name() {
        return res_name;
    }

    public String getRes_category() {
        return res_category;
    }

    public String getRes_theme() {
        return res_theme;
    }

    public String getRes_time() {
        return res_time;
    }

    public String getRes_phone() {
        return res_phone;
    }

    public String getRes_heart() {
        return res_heart;
    }

    public String getRes_fuck() {
        return res_fuck;
    }

    public String getRes_comment() {
        return res_comment;
    }

    public String getRes_location() {
        return res_location;
    }

    public double getRes_longitude() {
        return res_longitude;
    }

    public double getRes_latitude() {
        return res_latitude;
    }

    public String getRes_star() {
        return res_star;
    }

    public String getRes_star_num() {
        return res_star_num;
    }

    public String getReview_memo() {
        return review_memo;
    }

    public String getReview_updated_at() {
        return review_updated_at;
    }

    public String getWriter_id() {
        return writer_id;
    }

    public String getWriter_nickname() {
        return writer_nickname;
    }

    public String getWriter_image() {
        return writer_image;
    }

    public String getWriter_thumbnail() {
        return writer_thumbnail;
    }

    public int getVote_heart() {
        return vote_heart;
    }

    public int getVote_fuck() {
        return vote_fuck;
    }

    public List<ReviewComment> getReviewComment() {
        return reviewComment;
    }
}
