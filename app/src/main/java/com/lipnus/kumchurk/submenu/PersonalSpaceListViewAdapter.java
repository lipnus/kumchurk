package com.lipnus.kumchurk.submenu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.ScrollActivity;
import com.lipnus.kumchurk.firebaseModel.MenuInfo_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;
import com.lipnus.kumchurk.firebaseModel.User_fb;
import com.lipnus.kumchurk.kum_class.SimpleFunction;

import java.util.ArrayList;

/**
 * Created by Sunpil on 2016-07-13.
 */
public class PersonalSpaceListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<PersonalSpaceListViewItem> listViewItemList = new ArrayList<PersonalSpaceListViewItem>() ;


    // ListViewAdapter의 생성자
    public PersonalSpaceListViewAdapter() {

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
            convertView = inflater.inflate(R.layout.list_myreview, parent, false);
        }


        //------------------------------------------------------------------------------------------
        // 화면에 표시될 View의 구성요소
        //------------------------------------------------------------------------------------------
        LinearLayout rvLr = (LinearLayout) convertView.findViewById(R.id.rv_lr);
        ImageView reviewIv = (ImageView) convertView.findViewById(R.id.rv_review_iv);
        TextView menuResNameTv = (TextView) convertView.findViewById(R.id.rv_menu_res_name_tv);
        TextView infoTv = (TextView) convertView.findViewById(R.id.rv_info_tv);
        TextView dateTv = (TextView) convertView.findViewById(R.id.rv_date_tv);
        ImageView moreIv = (ImageView) convertView.findViewById(R.id.rv_more_iv);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        PersonalSpaceListViewItem pS = listViewItemList.get(position);



        //------------------------------------------------------------------------------------------
        // 데이터 정리
        //------------------------------------------------------------------------------------------

        //메뉴이름
        String menuName = pS.menuInfo_fb.menu_name;
        String resName = pS.resInfo_fb.res_name;
        String fuck = Integer.toString(pS.menuReview_fb.fuck.size());
        String heart= Integer.toString(pS.menuReview_fb.heart.size());
        String comment = Integer.toString(pS.menuReview_fb.comment.size());

        //시간
        String date = SimpleFunction.timeGap( pS.menuReview_fb.date );


        //------------------------------------------------------------------------------------------
        // 터치 이벤트
        //------------------------------------------------------------------------------------------

        //리뷰 클릭
        rvLr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("PRPR", "리뷰클릭");

                PersonalSpaceListViewItem pS = listViewItemList.get(position);

                Intent iT = new Intent(context, ScrollActivity.class);
                iT.putExtra("res_key", pS.resInfo_fb.key);
                iT.putExtra("menu_key", pS.menuInfo_fb.key);
                iT.putExtra("review_key", pS.menuReview_fb.key);
                context.startActivity(iT);

            }

        });

        //삭제
        moreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("리뷰를 삭제할까요?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //삭제를 해주는 CommentActivity의 매소드를 호출한다
                                PersonalSpaceActivity psActivity = (PersonalSpaceActivity) PersonalSpaceActivity.PSActiviry;
                                PersonalSpaceListViewItem pS = listViewItemList.get(position);

                                psActivity.connect_deleteReview( position );

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

        menuResNameTv.setText(resName + "의 " + menuName);
        infoTv.setText("like:"+heart + "  hate:"+fuck + "  Comment:" + comment);
        dateTv.setText(date);
//


        //자신의 정보가 아닐경우 설정버튼 없엠
        if(!pS.user_fb.uid.equals(GlobalApplication.getUser_id())){
            moreIv.setVisibility(View.GONE);
        }


        //리뷰사진
        Glide.with(context)
                .load( pS.menuReview_fb.review_image )
                .centerCrop()
                .into(reviewIv);
        reviewIv.setScaleType(ImageView.ScaleType.FIT_XY);

        //삭제하기
        Glide.with(context)
                .load( R.drawable.more2 )
                .into(moreIv);
        moreIv.setScaleType(ImageView.ScaleType.FIT_XY);

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
    public void addItem(ResInfo_fb resInfo_fb, MenuInfo_fb menuInfo_fb, MenuReview_fb menuReview_fb, User_fb user_fb) {

        PersonalSpaceListViewItem item = new PersonalSpaceListViewItem( resInfo_fb, menuInfo_fb, menuReview_fb, user_fb );
        listViewItemList.add(item);
    }

    public void removeItem(int index){

        listViewItemList.remove(index);

    }
}