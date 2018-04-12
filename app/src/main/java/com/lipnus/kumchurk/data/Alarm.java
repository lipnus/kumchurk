package com.lipnus.kumchurk.data;

/**
 * Created by Sunpil on 2017-02-26.
 * 본인에게 온 좋아요들을 모아서 보여주는곳에 쓰임
 */

public class Alarm {

    //투표한사람 정보
    private String giver_id;
    private String giver_nickname;
    private String giver_thumbnail;
    private String giver_image;
    private String giver_updated_at;

    private String giver_comment;

    //리뷰번호
    private String review_num;

    //내정보
    private String my_menu_name;
    private String my_review_image;


    public Alarm(String giver_id, String giver_nickname, String giver_thumbnail, String giver_image, String giver_updated_at, String giver_comment, String review_num, String my_menu_name, String my_review_image) {
        this.giver_id = giver_id;
        this.giver_nickname = giver_nickname;
        this.giver_thumbnail = giver_thumbnail;
        this.giver_image = giver_image;
        this.giver_updated_at = giver_updated_at;
        this.giver_comment = giver_comment;
        this.review_num = review_num;
        this.my_menu_name = my_menu_name;
        this.my_review_image = my_review_image;
    }

    public String getGiver_id() {
        return giver_id;
    }

    public String getGiver_nickname() {
        return giver_nickname;
    }

    public String getGiver_thumbnail() {
        return giver_thumbnail;
    }

    public String getGiver_image() {
        return giver_image;
    }

    public String getGiver_updated_at() {
        return giver_updated_at;
    }

    public String getGiver_comment() {
        return giver_comment;
    }

    public String getReview_num() {
        return review_num;
    }

    public String getMy_menu_name() {

        return my_menu_name;
    }

    public String getMy_review_image() {
        return my_review_image;
    }
}
