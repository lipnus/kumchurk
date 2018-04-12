package com.lipnus.kumchurk.submenu.search;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;

import java.util.ArrayList;

/**
 * Created by Sunpil on 2016-07-13.
 */
public class Search_Res_ListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<Search_Res_ListViewItem> listViewItemList = new ArrayList<>() ;


    // ListViewAdapter의 생성자
    public Search_Res_ListViewAdapter() {

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
            convertView = inflater.inflate(R.layout.list_res_search, parent, false);
        }


        //------------------------------------------------------------------------------------------
        // 화면에 표시될 View의 구성요소
        //------------------------------------------------------------------------------------------
        ImageView foodIv = (ImageView) convertView.findViewById(R.id.sl_foodIv);
        TextView resTv = (TextView) convertView.findViewById(R.id.sl_res_nameTv);
        TextView infoTv = (TextView) convertView.findViewById(R.id.sl_res_addressTv);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        Search_Res_ListViewItem listViewItem = listViewItemList.get(position);
        ResInfo_fb rI = listViewItem.resInfo;



        //------------------------------------------------------------------------------------------
        // 데이터 정리
        //------------------------------------------------------------------------------------------
        String resInfo = rI.location;
        String resName = rI.res_name + "(" + rI.res_category + ")";



        //------------------------------------------------------------------------------------------
        // 터치 이벤트
        //------------------------------------------------------------------------------------------
        foodIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //이 스코프 안에는 범위가 안먹혀서 제일 위의 전역변수를 사용해서 다시 객체를 만들어줌
                Search_Res_ListViewItem listViewItem = listViewItemList.get(position);
                ResInfo_fb rI = listViewItem.resInfo;

                Log.d("SSFF", "보내는 식당키: " + rI.key);

                Intent iT = new Intent(context, Search_ResMenuList_Activity.class); // 다음넘어갈 화면
                iT.putExtra("res_key", rI.key);
                context.startActivity(iT);

            }
        });



        //------------------------------------------------------------------------------------------
        // 아이템 내 각 위젯에 데이터 반영
        //------------------------------------------------------------------------------------------
        resTv.setText(resName);
        infoTv.setText(resInfo);

        //식당사진
        Glide.with(context)
                .load( rI.image.image1 )
                .placeholder( R.drawable.res_loading )
                .centerCrop()
                .into(foodIv);
        foodIv.setScaleType(ImageView.ScaleType.FIT_XY);

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
    public void addItem(ResInfo_fb resInfo_fb) {

        Search_Res_ListViewItem item = new Search_Res_ListViewItem(resInfo_fb);
        listViewItemList.add(item);
    }
}