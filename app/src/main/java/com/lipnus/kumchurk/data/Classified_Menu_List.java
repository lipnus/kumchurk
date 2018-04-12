package com.lipnus.kumchurk.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunpil on 2017-03-27.
 * 소분류에 따라 몇개의 집단으로 나뉜 메뉴리스트
 */

public class Classified_Menu_List {

    public String category_name; //메뉴 소분류 이름(category2)

    public List<Menu_List> c_menu_list; //사진(리뷰)가 존재하는 메뉴들
    public List<Menu_List> c_menu_list_nopic; //사진(리뷰)가 존재하지 않는 메뉴들



    public Classified_Menu_List(){

        c_menu_list = new ArrayList<>();
        c_menu_list_nopic = new ArrayList<>();
    }


}
