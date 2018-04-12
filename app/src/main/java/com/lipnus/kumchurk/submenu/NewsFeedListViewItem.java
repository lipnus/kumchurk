package com.lipnus.kumchurk.submenu;

import com.lipnus.kumchurk.firebaseModel.MenuInfo_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;
import com.lipnus.kumchurk.firebaseModel.User_fb;

/**
 * Created by Sunpil on 2016-07-13.
 *
 * 리스트뷰의 아이템데이터
 *
 */

public class NewsFeedListViewItem {

    public NewsFeedListViewItem(MenuInfo_fb menuInfo_fb, ResInfo_fb resInfo_fb, MenuReview_fb menuReview_fb, User_fb user_fb) {
        this.menuInfo_fb = menuInfo_fb;
        this.resInfo_fb = resInfo_fb;
        this.menuReview_fb = menuReview_fb;
        this.user_fb = user_fb;
    }

    public MenuInfo_fb menuInfo_fb;
    public ResInfo_fb resInfo_fb;
    public MenuReview_fb menuReview_fb;
    public User_fb user_fb;

}