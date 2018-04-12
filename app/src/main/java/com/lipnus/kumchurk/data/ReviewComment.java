package com.lipnus.kumchurk.data;

/**
 * Created by Sunpil on 2017-05-16.
 */

public class ReviewComment {

    //댓글쓴사람 정보
    public String comment_user_id;
    public String comment_user_nickname;
    public String comment_user_image;
    public String comment_user_thumbnail;

    //댓글정보
    public String comment_key;
    public String comment_memo;
    public String comment_updated_at;

    //리뷰 키
    public String review_key;


    public String getComment_user_id() {
        return comment_user_id;
    }


    public String getComment_key() {
        return comment_key;
    }
    public String getComment_user_nickname() {
        return comment_user_nickname;
    }

    public String getComment_user_image() {
        return comment_user_image;
    }

    public String getComment_user_thumbnail() {
        return comment_user_thumbnail;
    }

    public String getComment_memo() {
        return comment_memo;
    }

    public String getComment_updated_at() {
        return comment_updated_at;
    }

    public String getReview_key() {
        return review_key;
    }
}
