package com.lipnus.kumchurk.data;

/**
 * 메뉴와 그에 해당하는 식당정보를 Join을 통해 묶어서 서버에서 받아옴
 *
 *
 * 이방식이 훨씬 나은듯
 * 앞으로는 이 형식으로 수정할 예정
 */

public class MenuRes_Info {

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
    private String menu_image;

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
    private String res_image;
    private String res_image2;

    public String getMenu_name() {
        return menu_name;
    }

    public int getMenu_price() {
        return menu_price;
    }

    public int getMenu_price2() {
        return menu_price2;
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

    public String getMenu_image() {
        return menu_image;
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

    public String getRes_image() {
        return res_image;
    }

    public String getRes_image2() {
        return res_image2;
    }
}
