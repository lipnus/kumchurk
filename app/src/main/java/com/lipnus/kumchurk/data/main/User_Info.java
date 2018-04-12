package com.lipnus.kumchurk.data.main;

/**
 * Created by Sunpil on 2017-05-08.
 */

public class User_Info {
    private String user_nickname;
    private String user_sex;
    private String user_grade;
    private String user_image;
    private String user_thumbnail;
    private int user_certificate;


    public User_Info(String user_nickname, String user_sex, String user_grade, String user_image, String user_thumbnail, int user_certificate) {
        this.user_nickname = user_nickname;
        this.user_sex = user_sex;
        this.user_grade = user_grade;
        this.user_image = user_image;
        this.user_thumbnail = user_thumbnail;
        this.user_certificate = user_certificate;
    }

    public int getUser_certificate() {
        return user_certificate;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUser_sex() {
        return user_sex;
    }

    public void setUser_sex(String user_sex) {
        this.user_sex = user_sex;
    }

    public String getUser_grade() {
        return user_grade;
    }

    public void setUser_grade(String user_grade) {
        this.user_grade = user_grade;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_thumbnail() {
        return user_thumbnail;
    }

    public void setUser_thumbnail(String user_thumbnail) {
        this.user_thumbnail = user_thumbnail;
    }
}
