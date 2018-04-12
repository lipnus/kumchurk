package com.lipnus.kumchurk.data;

import java.util.List;

/**
 * Created by Sunpil on 2017-02-26.
 */

public class MenuInfo_JSON {
    public List<Menu_Review> menuReview;
    public List<Res_Info> resInfo;
    public List<Menu_List> menuList;
    public List<Menu_Recommend> menuRecommend;
    public List<Menu_Review_Vote> menuReviewVote;
    public List<Res_Vote> resVote;


    //생성자


    public MenuInfo_JSON(List<Menu_Review> menuReview, List<Res_Info> resInfo, List<Menu_List> menuList, List<Menu_Recommend> menuRecommend, List<Menu_Review_Vote> menuReviewVote, List<Res_Vote> resVote) {
        this.menuReview = menuReview;
        this.resInfo = resInfo;
        this.menuList = menuList;
        this.menuRecommend = menuRecommend;
        this.menuReviewVote = menuReviewVote;
        this.resVote = resVote;
    }
}
