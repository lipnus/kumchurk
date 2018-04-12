package com.lipnus.kumchurk.submenu.search;

import com.lipnus.kumchurk.firebaseModel.MenuInfo_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;

/**
 * Created by Sunpil on 2016-07-13.
 *
 * 리스트뷰의 아이템데이터
 *
 */

public class Search_Menu_ListViewItem {

    public ResInfo_fb resInfo;
    public MenuInfo_fb menuInfo;
    public MenuReview_fb menuReview;

    public Search_Menu_ListViewItem(ResInfo_fb resInfo, MenuInfo_fb menuInfo, MenuReview_fb menuReview) {
        this.resInfo = resInfo;
        this.menuInfo = menuInfo;
        this.menuReview = menuReview;
    }
}