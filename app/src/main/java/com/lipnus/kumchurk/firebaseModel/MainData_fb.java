package com.lipnus.kumchurk.firebaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunpil on 2017-08-08.
 */

public class MainData_fb {

    public MainData_fb() {
        menuReview = new ArrayList<>();
        menuInfo = new ArrayList<>();
        resInfo = new ArrayList<>();
    }


    public List<MenuReview_fb> menuReview;
    public List<MenuInfo_fb> menuInfo;
    public List<ResInfo_fb> resInfo;
}
