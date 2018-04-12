package com.lipnus.kumchurk.detailpage;

/**
 * Created by Sunpil on 2016-07-13.
 *
 * 리스트뷰의 아이템데이터
 *
 */

public class ReviewSelect_ListViewItem {

    public boolean left_menu_visible;
    public String left_menu_name;
    public String left_menu_cost;
    public String left_menu_image;
    public String left_menuKey;

    public boolean right_menu_visible;
    public String right_menu_name;
    public String right_menu_cost;
    public String right_menu_image;
    public String right_menuKey;

    public boolean category_visible;
    public String category_name;

    //ScrollAvtivity에서는 key인데 여기서는 name이다
    public String res_name;

    public boolean isCategoryOpen; //해당 아이템이 메뉴소분류(category2)표시일 경우, 메뉴리스팅이 열려있는지(true) 닫혀있는지(false)


    //생성자
    public ReviewSelect_ListViewItem(){
        this.isCategoryOpen = false; //초기설정은 메뉴펼침이 닫혀있는 것
    }

    public boolean isLeft_menu_visible() {
        return left_menu_visible;
    }

    public String getLeft_menu_name() {
        return left_menu_name;
    }

    public String getLeft_menu_cost() {
        return left_menu_cost;
    }

    public String getLeft_menu_image() {
        return left_menu_image;
    }

    public String getLeft_menuKey() {
        return left_menuKey;
    }

    public boolean isRight_menu_visible() {
        return right_menu_visible;
    }

    public String getRight_menu_name() {
        return right_menu_name;
    }

    public String getRight_menu_cost() {
        return right_menu_cost;
    }

    public String getRight_menu_image() {
        return right_menu_image;
    }

    public String getRight_menuKey() {
        return right_menuKey;
    }

    public boolean isCategory_visible() {
        return category_visible;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getRes_name() {
        return res_name;
    }

    public boolean isCategoryOpen() {
        return isCategoryOpen;
    }

    public void setLeft_menu_visible(boolean left_menu_visible) {
        this.left_menu_visible = left_menu_visible;
    }

    public void setLeft_menu_name(String left_menu_name) {
        this.left_menu_name = left_menu_name;
    }

    public void setLeft_menu_cost(String left_menu_cost) {
        this.left_menu_cost = left_menu_cost;
    }

    public void setLeft_menu_image(String left_menu_image) {
        this.left_menu_image = left_menu_image;
    }

    public void setLeft_menuKey(String left_menuKey) {
        this.left_menuKey = left_menuKey;
    }

    public void setRight_menu_visible(boolean right_menu_visible) {
        this.right_menu_visible = right_menu_visible;
    }

    public void setRight_menu_name(String right_menu_name) {
        this.right_menu_name = right_menu_name;
    }

    public void setRight_menu_cost(String right_menu_cost) {
        this.right_menu_cost = right_menu_cost;
    }

    public void setRight_menu_image(String right_menu_image) {
        this.right_menu_image = right_menu_image;
    }

    public void setRight_menuKey(String right_menuKey) {
        this.right_menuKey = right_menuKey;
    }

    public void setCategory_visible(boolean category_visible) {
        this.category_visible = category_visible;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public void setRes_name(String res_name) {
        this.res_name = res_name;
    }

    public void setCategoryOpen(boolean categoryOpen) {
        isCategoryOpen = categoryOpen;
    }
}