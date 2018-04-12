package com.lipnus.kumchurk.data;

/**
 * Created by Sunpil on 2017-02-26.
 */

public class Menu_Review {
    private int no;
    private String user_id; //글쓴사람 아이디( 유저의 아디랑 헷갈리니 바꿔야 할듯)

    //DB상으로는 user_info에 해당하지만 이곳에서 같이 처리한다(각 리뷰를 쓴 사람의 정보)
    private String user_nickname;
    private String user_profile;
    private String user_thumbnail;

    private int heart;
    private int fuck;
    private String memo;
    private int comment;
    private String res_name;
    private String menu_name;
    private String menu_image;

    private String created_at;
    private String updated_at;

    public Menu_Review(int no, String user_id, String user_nickname, String user_profile, String user_thumbnail, int heart, int fuck, String memo, int comment, String res_name, String menu_name, String menu_image, String created_at, String updated_at) {
        this.no = no;
        this.user_id = user_id;

        this.user_nickname = user_nickname;
        this.user_profile = user_profile;
        this.user_thumbnail = user_thumbnail;

        this.heart = heart;
        this.fuck = fuck;
        this.memo = memo;
        this.comment = comment;
        this.res_name = res_name;
        this.menu_name = menu_name;
        this.menu_image = menu_image;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public void setFuck(int fuck) {
        this.fuck = fuck;
    }

    public int getNo() {
        return no;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUser_profile() {
        return user_profile;
    }

    public void setUser_profile(String user_profile) {
        this.user_profile = user_profile;
    }

    public String getUser_thumbnail() {
        return user_thumbnail;
    }

    public void setUser_thumbnail(String user_thumbnail) {
        this.user_thumbnail = user_thumbnail;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public void setRes_name(String res_name) {
        this.res_name = res_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public void setMenu_image(String menu_image) {
        this.menu_image = menu_image;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
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
}
