package com.lipnus.kumchurk.data;

/**
 * Created by Sunpil on 2017-02-26.
 */

public class Menu_List {

    private String name;
    private String res_name;

    private int price;
    private int price2;
    private int price3;

    private String category1;
    private String category2;

    private int heart;
    private int fuck;
    private int comment;

    private String image;




    public Menu_List(String name, String res_name, int price, int price2, int price3, String category1, String category2, int heart, int fuck, int comment, String image) {
        this.name = name;
        this.res_name = res_name;
        this.price = price;
        this.price2 = price2;
        this.price3 = price3;
        this.category1 = category1;
        this.category2 = category2;
        this.heart = heart;
        this.fuck = fuck;
        this.comment = comment;
        this.image = image;


    }



    public String getName() {
        return name;
    }

    public String getRes_name() {
        return res_name;
    }

    public int getPrice() {
        return price;
    }

    public int getPrice2() {
        return price2;
    }

    public int getPrice3() {
        return price3;
    }

    public String getCategory1() {
        return category1;
    }

    public String getCategory2() {
        return category2;
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

    public String getImage() {
        return image;
    }
}
