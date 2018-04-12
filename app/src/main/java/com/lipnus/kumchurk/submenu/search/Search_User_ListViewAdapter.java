package com.lipnus.kumchurk.submenu.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.detailpage.ProfileImgActivity;
import com.lipnus.kumchurk.firebaseModel.User_fb;
import com.lipnus.kumchurk.submenu.PersonalSpaceActivity;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Sunpil on 2016-07-13.
 */
public class Search_User_ListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<Search_User_ListViewItem> listViewItemList = new ArrayList<>() ;


    // ListViewAdapter의 생성자
    public Search_User_ListViewAdapter() {

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
            convertView = inflater.inflate(R.layout.list_user_search, parent, false);
        }


        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        Search_User_ListViewItem listViewItem = listViewItemList.get(position);
        User_fb user = listViewItem.user;

        //------------------------------------------------------------------------------------------
        // 화면에 표시될 View의 구성요소
        //------------------------------------------------------------------------------------------
        ImageView profileIv = (ImageView) convertView.findViewById(R.id.search_profile_iv);
        TextView nickNameTv = (TextView) convertView.findViewById(R.id.search_nickname_tv);
        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.search_layout);





        //------------------------------------------------------------------------------------------
        // 데이터 정리
        //------------------------------------------------------------------------------------------



        //------------------------------------------------------------------------------------------
        // 터치 이벤트
        //------------------------------------------------------------------------------------------
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //프사

                //이 스코프 안에는 범위가 안먹혀서 제일 위의 전역변수를 사용해서 다시 객체를 만들어줌
                Search_User_ListViewItem listViewItem = listViewItemList.get(position);
                User_fb user = listViewItem.user;

                Intent iT = new Intent(context, ProfileImgActivity.class); // 다음넘어갈 화면
                iT.putExtra("nickname", user.nickname);
                iT.putExtra("image_path", user.profile_image);
                context.startActivity(iT);
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //칸 터치

                //이 스코프 안에는 범위가 안먹혀서 제일 위의 전역변수를 사용해서 다시 객체를 만들어줌
                Search_User_ListViewItem listViewItem = listViewItemList.get(position);
                User_fb user = listViewItem.user;

                Intent iT = new Intent(context, PersonalSpaceActivity.class); // 다음넘어갈 화면
                iT.putExtra("uid", user.uid);
                context.startActivity(iT);
            }
        });



        //------------------------------------------------------------------------------------------
        // 아이템 내 각 위젯에 데이터 반영
        //------------------------------------------------------------------------------------------
        nickNameTv.setText( user.nickname );

        //얼굴
        Glide.with(context)
                .load( user.thumbnail )
                .placeholder( R.drawable.face_basic )
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(context))
                .into(profileIv);
        profileIv.setScaleType(ImageView.ScaleType.FIT_XY);

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
    public void addItem(User_fb user_fb) {

        Search_User_ListViewItem item = new Search_User_ListViewItem(user_fb);
        listViewItemList.add(0, item);
    }
}