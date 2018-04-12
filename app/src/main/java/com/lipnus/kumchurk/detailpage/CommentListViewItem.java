package com.lipnus.kumchurk.detailpage;

import com.lipnus.kumchurk.data.ReviewComment;

/**
 * Created by Sunpil on 2016-07-13.
 *
 * 리스트뷰의 아이템데이터
 *
 */

public class CommentListViewItem {

    private ReviewComment reviewComment;

    public CommentListViewItem(ReviewComment reviewComment) {
        this.reviewComment = reviewComment;
    }

    public ReviewComment getReviewComment() {
        return reviewComment;
    }

}