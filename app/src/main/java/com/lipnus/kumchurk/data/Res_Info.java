package com.lipnus.kumchurk.data;

/**
 * Created by Sunpil on 2017-02-26.
 */

public class Res_Info {
    private String name;
    private String category;
    private String theme;
    private String time;
    private String phone;
    private int heart;
    private int fuck;
    private int comment;
    private String location;
    private double longitude;
    private double latitude;
    private int star;
    private int star_num;
    private String res_image;
    private String res_image2;


    public Res_Info(String name, String category, String theme, String time, String phone, int heart, int fuck, int comment, String location, double longitude, double latitude, int star, int star_num, String res_image, String res_image2) {
        this.name = name;
        this.category = category;
        this.theme = theme;
        this.time = time;
        this.phone = phone;
        this.heart = heart;
        this.fuck = fuck;
        this.comment = comment;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.star = star;
        this.star_num = star_num;
        this.res_image = res_image;
        this.res_image2 = res_image2;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public void setFuck(int fuck) {
        this.fuck = fuck;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRes_image() {
        return res_image;
    }

    public void setRes_image(String res_image) {
        this.res_image = res_image;
    }

    public String getRes_image2() {
        return res_image2;
    }

    public void setRes_image2(String res_image2) {
        this.res_image2 = res_image2;
    }

    public String getName() {
        return name;
    }

    public String getTheme() {
        return theme;
    }

    public String getCategory() {
        return category;
    }

    public String getTime() {
        return time;
    }

    public String getPhone() {
        return phone;
    }

    public int getHeart() {
        return heart;
    }

    public int getFuck() {
        return fuck;
    }

    public int getComment() {
        return comment;
    }

    public String getLocation() {
        return location;
    }

    public int getStar() {
        return star;
    }

    public int getStar_num() {
        return star_num;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public void setStar_num(int star_num) {
        this.star_num = star_num;
    }
}
