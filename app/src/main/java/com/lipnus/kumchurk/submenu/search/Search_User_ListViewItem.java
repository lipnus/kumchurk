package com.lipnus.kumchurk.submenu.search;

import com.lipnus.kumchurk.firebaseModel.User_fb;

/**
 * Created by Sunpil on 2016-07-13.
 *
 * 리스트뷰의 아이템데이터
 *
 */

public class Search_User_ListViewItem {

    public User_fb user;

    public Search_User_ListViewItem(User_fb user) {
        this.user = user;
    }
}