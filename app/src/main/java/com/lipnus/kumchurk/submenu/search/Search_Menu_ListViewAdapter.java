package com.lipnus.kumchurk.submenu.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.ScrollActivity;
import com.lipnus.kumchurk.firebaseModel.MenuInfo_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;

import java.util.ArrayList;

import static com.lipnus.kumchurk.kum_class.SimpleFunction.distanceMinute;

/**
 * Created by Sunpil on 2016-07-13.
 */
public class Search_Menu_ListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<Search_Menu_ListViewItem> listViewItemList = new ArrayList<>() ;


    // ListViewAdapter의 생성자
    public Search_Menu_ListViewAdapter() {

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
            convertView = inflater.inflate(R.layout.list_menu_search, parent, false);
        }


        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        Search_Menu_ListViewItem mS = listViewItemList.get(position);

        //------------------------------------------------------------------------------------------
        // 화면에 표시될 View의 구성요소
        //------------------------------------------------------------------------------------------

        ImageView foodIv = (ImageView) convertView.findViewById(R.id.menuSearch_foodIv);
        TextView titleTv = (TextView) convertView.findViewById(R.id.menuSearch_Tv);
        TextView infoTv = (TextView) convertView.findViewById(R.id.menuSearch_smallTv);



        //------------------------------------------------------------------------------------------
        // 데이터 정리
        //------------------------------------------------------------------------------------------
        String price = Double.toString( Double.parseDouble( mS.menuInfo.price1 )/1000 );
        String menuName = mS.menuInfo.menu_name + " " + price;
        String disMin = distanceMinute(Double.parseDouble(mS.resInfo.latitude), Double.parseDouble(mS.resInfo.longitude));

        String info = mS.resInfo.res_name + " " + disMin;


        //------------------------------------------------------------------------------------------
        // 터치 이벤트
        //------------------------------------------------------------------------------------------
        foodIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //이 스코프 안에는 범위가 안먹혀서 제일 위의 전역변수를 사용해서 다시 객체를 만들어줌
                Search_Menu_ListViewItem mS = listViewItemList.get(position);

                Intent iT = new Intent(context, ScrollActivity.class);
                iT.putExtra("res_key", mS.resInfo.key);
                iT.putExtra("menu_key", mS.menuInfo.key);
                iT.putExtra("review_key", mS.menuReview.key);
                context.startActivity(iT);

            }
        });



        //------------------------------------------------------------------------------------------
        // 아이템 내 각 위젯에 데이터 반영
        //------------------------------------------------------------------------------------------

        titleTv.setText( menuName );
        infoTv.setText( info );

        //메뉴사진
        Glide.with(context)
                .load( mS.menuReview.review_image )
                .placeholder( R.drawable.empty_table2 )
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
    public void addItem(ResInfo_fb resInfo_fb, MenuInfo_fb menuInfo_fb, MenuReview_fb menuReview_fb) {

        Search_Menu_ListViewItem item = new Search_Menu_ListViewItem(resInfo_fb, menuInfo_fb, menuReview_fb);
        listViewItemList.add(item);
    }
}