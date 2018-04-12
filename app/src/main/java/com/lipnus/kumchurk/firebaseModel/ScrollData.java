package com.lipnus.kumchurk.firebaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunpil on 2017-08-08.
 * ScrollActivity에서 쓰는 것.
 *
 * 메뉴 1개와 그에 딸린 리뷰들로 구성되어 있다
 */

public class ScrollData {

    public MenuInfo_fb menuInfo;
    public List<MenuReview_fb> menuReviews;
    public List<User_fb> users;

    public ScrollData() {
        menuReviews = new ArrayList<>();
        users = new ArrayList<>();
    }
}
