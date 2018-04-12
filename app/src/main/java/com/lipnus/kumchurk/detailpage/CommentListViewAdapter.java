package com.lipnus.kumchurk.detailpage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.data.ReviewComment;
import com.lipnus.kumchurk.kum_class.SimpleFunction;
import com.lipnus.kumchurk.submenu.PersonalSpaceActivity;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Sunpil on 2016-07-13.
 */
public class CommentListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<CommentListViewItem> listViewItemList = new ArrayList<CommentListViewItem>() ;


    // ListViewAdapter의 생성자
    public CommentListViewAdapter() {

    }


    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }


    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // menulist.xml을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_comment, parent, false);
        }


        //------------------------------------------------------------------------------------------
        // 화면에 표시될 View의 구성요소
        //------------------------------------------------------------------------------------------
        ImageView profileIv = (ImageView) convertView.findViewById(R.id.cm_profile_iv);
        TextView nicknameTv = (TextView) convertView.findViewById(R.id.cm_nick_tv);
        TextView commentTv = (TextView) convertView.findViewById(R.id.cm_comment_tv);
        TextView timeTv = (TextView) convertView.findViewById(R.id.cm_time_tv);
        ImageView deleteIv = (ImageView) convertView.findViewById(R.id.cm_delete_iv);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        CommentListViewItem listViewItem = listViewItemList.get(position);



        //------------------------------------------------------------------------------------------
        // 데이터 정리
        //------------------------------------------------------------------------------------------
        ReviewComment rc = listViewItem.getReviewComment(); //여기서 선언하면 클릭리스너 안에 안먹혀서 밖에다가 선언

        //댓글쓴사람 정보
        String nickname = rc.getComment_user_nickname();
        String updateTime = SimpleFunction.timeGap( rc.getComment_updated_at().substring(0, 12) ); //현재시간과의 차이(초는 버림)

        //댓글내용
        String comment = rc.getComment_memo();

        //------------------------------------------------------------------------------------------
        // 터치 이벤트
        //------------------------------------------------------------------------------------------

        //프사클릭(개인페이지로 이동)
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CommentListViewItem listViewItem = listViewItemList.get(position);
                ReviewComment rc = listViewItem.getReviewComment();

                Intent iT = new Intent(context, PersonalSpaceActivity.class);
                iT.putExtra("uid", rc.comment_user_id );
                context.startActivity(iT);


            }

        });

        //삭제
        deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //삭제를 해주는 CommentActivity의 매소드를 호출한다
                                CommentActivity cmActivity = (CommentActivity) CommentActivity.CMActiviry;
                                CommentListViewItem listViewItem = listViewItemList.get(position);
                                ReviewComment rc = listViewItem.getReviewComment();

                                cmActivity.deleteComment( rc.getComment_key(), rc.getReview_key() );
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //무반응
                            }
                        });
                builder.show();





            }

        });


        //------------------------------------------------------------------------------------------
        // 아이템 내 각 위젯에 데이터 반영
        //------------------------------------------------------------------------------------------
        nicknameTv.setText(nickname); //닉네임
        commentTv.setText(comment); //댓글
        timeTv.setText(updateTime); //시간

        //자기가 쓴 글에만 삭제아이콘이 보인다
        if(rc.getComment_user_id().equals( GlobalApplication.getUser_id() )){
            deleteIv.setVisibility(View.VISIBLE);
        }

        //프사
        Glide.with(context)
                .load( rc.getComment_user_thumbnail() )
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(context))
                .into(profileIv);
        profileIv.setScaleType(ImageView.ScaleType.FIT_XY);

        //삭제
        Glide.with(context)
                .load( R.drawable.delete)
                .into(deleteIv);
        deleteIv.setScaleType(ImageView.ScaleType.FIT_XY);

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }


    // 아이템 데이터 추가
    public void addItem(ReviewComment rc) {

        CommentListViewItem item = new CommentListViewItem( rc );
        listViewItemList.add(item);
    }

    public void removeAllItem(){
        listViewItemList.clear();
    }
}