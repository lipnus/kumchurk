package com.lipnus.kumchurk.firebaseModel;

/**
 * Created by Sunpil on 2017-08-18.
 */

public class Alarm_fb {



    //누가 썼나
    public User_fb user;

    //내가 쓴 리뷰정보
    public MenuReview_fb menuReview;
    public MenuInfo_fb menuInfo;

    //뭘 했나
    public Comment_fb comment = new Comment_fb();

    //댓글인지, 좋아요인지, (대댓글인지)
    public String type;

    //날짜를 여기다가 박아놓는다
    public String date;



    //댓글알람 생성자
    public Alarm_fb(User_fb user, MenuReview_fb menuReview, MenuInfo_fb menuInfo, Comment_fb comment,String type, String date) {
        this.user = user;
        this.menuReview = menuReview;
        this.menuInfo = menuInfo;
        this.comment = comment;
        this.type = type;
        this.date = date;
    }


    //좋아요알람 생성자
    public Alarm_fb(User_fb user, MenuReview_fb menuReview, MenuInfo_fb menuInfo, String type, String date) {
        this.user = user;
        this.menuReview = menuReview;
        this.menuInfo = menuInfo;

        this.comment = new Comment_fb();

        this.type = type;
        this.date = date;
    }
}
