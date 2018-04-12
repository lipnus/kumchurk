package com.lipnus.kumchurk.firebaseModel;

/**
 * Created by Sunpil on 2017-08-17.
 */

public class Profile_fb {

    public ResInfo_fb resInfo;
    public MenuInfo_fb menuInfo;
    public MenuReview_fb menuReview;

    public Profile_fb(ResInfo_fb resInfo, MenuInfo_fb menuInfo, MenuReview_fb menuReview) {
        this.resInfo = resInfo;
        this.menuInfo = menuInfo;
        this.menuReview = menuReview;
    }
}
