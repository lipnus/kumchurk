package com.lipnus.kumchurk.data;

/**
 * Created by Sunpil on 2017-03-17.
 * 메뉴리뷰에 대한 투표정보
 */


public class Menu_Review_Vote {


    private String voted_id; //누구의 글에 투표?
    private int review_num; //투표한 글번호
    private int heart;
    private int fuck;
    private String updated_at;

    public Menu_Review_Vote(int review_num, String voted_id, int heart, int fuck, String updated_at) {
        this.review_num = review_num;
        this.voted_id = voted_id;
        this.heart = heart;
        this.fuck = fuck;
        this.updated_at = updated_at;
    }

    //생성자 오버로드(앱 내에서만 쓸때는 이것만 필요. 어자피 내가 올린 것들만 다운받았을테니..)
    public Menu_Review_Vote(int review_num, int heart, int fuck) {
        this.review_num = review_num;
        this.heart = heart;
        this.fuck = fuck;
    }

    //생성자 오버로드(앱 내에서만 쓸때는 이것만 필요. 어자피 내가 올린 것들만 다운받았을테니..)
    public Menu_Review_Vote(String voted_id, int review_num, int heart, int fuck) {
        this.voted_id = voted_id;
        this.review_num = review_num;
        this.heart = heart;
        this.fuck = fuck;
        this.updated_at = "";
    }

    public int getReview_num() {
        return review_num;
    }

    public String getVoted_id() {
        return voted_id;
    }

    public int getHeart() {
        return heart;
    }

    public int getFuck() {
        return fuck;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public void setFuck(int fuck) {
        this.fuck = fuck;
    }
}
