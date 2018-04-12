package com.lipnus.kumchurk.submenu;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.ScrollActivity;
import com.lipnus.kumchurk.firebaseModel.Alarm_fb;
import com.lipnus.kumchurk.kum_class.SimpleFunction;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Sunpil on 2016-07-13.
 */
public class AlarmListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<AlarmListViewItem> listViewItemList = new ArrayList<AlarmListViewItem>() ;


    // ListViewAdapter의 생성자
    public AlarmListViewAdapter() {

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
            convertView = inflater.inflate(R.layout.list_alarm, parent, false);
        }


        //------------------------------------------------------------------------------------------
        // 화면에 표시될 View의 구성요소
        //------------------------------------------------------------------------------------------
        ImageView profileIv = (ImageView) convertView.findViewById(R.id.al_profile_iv);
        TextView noticeTv = (TextView) convertView.findViewById(R.id.al_notice_tv);
        TextView timeTv = (TextView) convertView.findViewById(R.id.al_time_tv);
        ImageView myReviewIv = (ImageView) convertView.findViewById(R.id.al_myreview_iv);
        LinearLayout alarmLr = (LinearLayout) convertView.findViewById(R.id.al_lr);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        AlarmListViewItem listViewItem = listViewItemList.get(position);
        Alarm_fb aL = listViewItem.alarm_fb;


        //------------------------------------------------------------------------------------------
        // 데이터 정리
        //------------------------------------------------------------------------------------------

        //좋아요 누른사람 정보
        String nickname = aL.user.nickname;
        String updateTime = SimpleFunction.timeGap( aL.date ); //현재시간과의 차이


        //메뉴이름
        String menuName = aL.menuInfo.menu_name;

        String notice;

        if(aL.type.equals("heart")){
            notice = nickname + "님이 회원님의 " + menuName + " 리뷰를 좋아합니다";

        }else{
            notice = nickname + "님의 댓글: " + aL.comment.comment_text;
        }



        //------------------------------------------------------------------------------------------
        // 터치 이벤트
        //------------------------------------------------------------------------------------------

        //프사클릭
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlarmListViewItem listViewItem = listViewItemList.get(position);
                Alarm_fb aL = listViewItem.alarm_fb;

                Log.d("ALAL",  "보낸 UID: " + aL.user.uid);

                Intent iT = new Intent(context, PersonalSpaceActivity.class);
                iT.putExtra("uid", aL.user.uid);
                context.startActivity(iT);

            }

        });

//

        //한 칸을 클릭하면 내가 올린 리뷰로 이동
        alarmLr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlarmListViewItem listViewItem = listViewItemList.get(position);
                Alarm_fb aL = listViewItem.alarm_fb;

                Log.d("ALAL", aL.menuInfo.res_id + ", " + aL.menuInfo.key + ", " + aL.menuReview.key );

                Intent iT = new Intent(context, ScrollActivity.class);
                iT.putExtra("res_key", aL.menuInfo.res_id);
                iT.putExtra("menu_key", aL.menuInfo.key);
                iT.putExtra("review_key", aL.menuReview.key);
                context.startActivity(iT);
            }

        });



        //------------------------------------------------------------------------------------------
        // 아이템 내 각 위젯에 데이터 반영
        //------------------------------------------------------------------------------------------

        noticeTv.setText(notice);
        timeTv.setText(updateTime);

        //프사
        Glide.with(context)
                .load( aL.user.thumbnail )
                .placeholder(R.drawable.face_basic)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(context))
                .into(profileIv);
        profileIv.setScaleType(ImageView.ScaleType.FIT_XY);

        //내가 올린 리뷰
        Glide.with(context)
                .load( aL.menuReview.review_image )
                .centerCrop()
                .thumbnail(0.3f)
                .into(myReviewIv);
        myReviewIv.setScaleType(ImageView.ScaleType.FIT_XY);



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
    public void addItem(Alarm_fb alarm_fb) {

        AlarmListViewItem item = new AlarmListViewItem( alarm_fb );
        listViewItemList.add(item);
    }
}