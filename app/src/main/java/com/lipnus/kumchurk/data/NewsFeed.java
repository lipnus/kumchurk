package com.lipnus.kumchurk.data;

/**
 * Created by Sunpil on 2017-02-26.
 * 뉴스피드에 쓰일 데이터들
 * menu_review, user_info, res_info 3가지 테이블에서 필요한 정보들을 가져온다
 * 뉴스피드 리스트에서 아이템으로 사용된다
 */

public class NewsFeed {

    //menu_reivew테이블의 정보
    private int no;
    private String user_id; //글쓴사람 아이디( 유저의 아디랑 헷갈리니 바꿔야 할듯)
    private int heart;
    private int fuck;
    private String memo;
    private int comment;
    private String res_name;
    private String menu_name;
    private int taste;
    private String menu_image;
    private String created_at;
    private String updated_at;

    //user_info테이블의 정보(글쓴이 정보)
    private String user_nickname;
    private String user_profile;
    private String user_thumbnail;

    //res_info테이블의 정보(식당정보)
    private int star;
    private int star_num;
    private String time;
    private double latitude;
    private double longitude;

    //menu_info테이블의 정보(가격)
    private double price;
    private double price2;
    private double price3;


    //내가 이 리뷰에 투표를 했는지 확인
    private int vote_heart;
    private int vote_fuck;




    public int getTaste() {
        return taste;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public void setFuck(int fuck) {
        this.fuck = fuck;
    }

    public void setVote_heart(int vote_heart) {
        this.vote_heart = vote_heart;
    }

    public void setVote_fuck(int vote_fuck) {
        this.vote_fuck = vote_fuck;
    }

    public double getPrice() {
        return price;
    }

    public double getPrice2() {
        return price2;
    }

    public double getPrice3() {
        return price3;
    }

    public int getNo() {
        return no;
    }

    public String getUser_id() {
        return user_id;
    }

    public int getHeart() {
        return heart;
    }

    public int getFuck() {
        return fuck;
    }

    public String getMemo() {
        return memo;
    }

    public int getComment() {
        return comment;
    }

    public String getRes_name() {
        return res_name;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public String getMenu_image() {
        return menu_image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getUser_profile() {
        return user_profile;
    }

    public String getUser_thumbnail() {
        return user_thumbnail;
    }

    public int getStar() {
        return star;
    }

    public int getStar_num() {
        return star_num;
    }

    public String getTime() {
        return time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getVote_heart() {
        return vote_heart;
    }

    public int getVote_fuck() {
        return vote_fuck;
    }


}
