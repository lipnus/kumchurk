package com.lipnus.kumchurk.detailpage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lipnus.kumchurk.R;

import java.util.ArrayList;

/**
 * Created by Sunpil on 2016-07-13.
 */
public class ReviewSelect_ListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ReviewSelect_ListViewItem> listViewItemList = new ArrayList<ReviewSelect_ListViewItem>() ;

    // ListViewAdapter의 생성자
    public ReviewSelect_ListViewAdapter() {

    }


    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }


    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // menulist.xml을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.menulist, parent, false);
        }

        //------------------------------------------------------------------------------------------
        // 화면에 표시될 View의 구성요소
        //------------------------------------------------------------------------------------------
        FrameLayout left_menu_fL = (FrameLayout) convertView.findViewById(R.id.left_menu_FrameLayout);
        ImageView left_menu_Iv = (ImageView) convertView.findViewById(R.id.left_menu_Iv);
        TextView left_menu_name_Tv = (TextView) convertView.findViewById(R.id.left_menu_nameTv);
        TextView left_menu_cost_Tv = (TextView) convertView.findViewById(R.id.left_menu_costTv);

        FrameLayout right_menu_fL = (FrameLayout) convertView.findViewById(R.id.right_menu_FrameLayout);
        ImageView right_menu_Iv = (ImageView) convertView.findViewById(R.id.right_menu_Iv);
        TextView right_menu_name_Tv = (TextView) convertView.findViewById(R.id.right_menu_nameTv);
        TextView right_menu_cost_Tv = (TextView) convertView.findViewById(R.id.right_menu_costTv);

        FrameLayout category_FL = (FrameLayout) convertView.findViewById(R.id.category_menu_LinearLayout);
        final TextView category_Tv = (TextView) convertView.findViewById(R.id.category_menuTv);
        final ImageView category_arrow_Iv = (ImageView) convertView.findViewById(R.id.category_arrowIv); //이거때문에 에러나면 어떡하지...

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ReviewSelect_ListViewItem listViewItem = listViewItemList.get(position);

        //------------------------------------------------------------------------------------------
        // 터치 이벤트
        //------------------------------------------------------------------------------------------

        //카테고리를 누르면 메뉴 목록 펼치고 닫기
        category_FL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ReviewSelectActivity rsActivity = (ReviewSelectActivity) ReviewSelectActivity.RSActivity;
                ReviewSelect_ListViewItem listViewItem = listViewItemList.get(pos);
                boolean isOpen = listViewItem.isCategoryOpen();

                //위아래 화살표 방향 바꾸기
                if(isOpen){//닫기
                    Glide.with(context)
                            .load( R.drawable.down_arrow )
                            .into(category_arrow_Iv);
                    listViewItem.setCategoryOpen(false);
                    category_Tv.setTextSize( 18 );
                    rsActivity.closeMenuList(listViewItem.getCategory_name(), pos);
                }else{//열기
                    Glide.with(context)
                            .load( R.drawable.up_arrow )
                            .into(category_arrow_Iv);
                    category_Tv.setTextSize( 21 );
                    listViewItem.setCategoryOpen(true);
                    rsActivity.openMenuList(listViewItem.getCategory_name(), pos);
                    //얘 때문에 화면이 새로 그려짐.
                    //어자피 새로 그려지지만 그려지는게 하나도 없을 경우는 업데이트가 안되므로 위의 glide가 필요한 것.
                }

                //메뉴리스트를 펼치거나 닫음



            }
        });

        left_menu_fL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ReviewSelect_ListViewItem  rS = listViewItemList.get(pos);

                Intent iT = new Intent(context, ReviewWrtieActivity.class); // 다음넘어갈 화면
                iT.putExtra("res_name", rS.getRes_name());
                iT.putExtra("menu_name", rS.getLeft_menu_name());
                iT.putExtra("menu_key", rS.getLeft_menuKey());
                iT.putExtra("callFrom", "ReviewSelectActivity");
                context.startActivity(iT);

                Log.d("LSLS", "왜식당이름 안나와2: " + rS.getRes_name());
            }
        });

        right_menu_fL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ReviewSelect_ListViewItem  rS = listViewItemList.get(pos);

                Intent iT = new Intent(context, ReviewWrtieActivity.class); // 다음넘어갈 화면
                iT.putExtra("res_name", rS.getRes_name());
                iT.putExtra("menu_name", rS.getRight_menu_name());
                iT.putExtra("menu_key", rS.getRight_menuKey());
                iT.putExtra("callFrom", "ReviewSelectActivity");
                context.startActivity(iT);

                Log.d("LSLS", "왜식당이름 안나와2: " + rS.getRes_name());
            }
        });

        //------------------------------------------------------------------------------------------
        // 아이템 내 각 위젯에 데이터 반영
        //------------------------------------------------------------------------------------------

        //좌측부분
        if( listViewItem.isLeft_menu_visible() ){ left_menu_fL.setVisibility(View.VISIBLE); }
        else{ left_menu_fL.setVisibility(View.GONE); }
        left_menu_name_Tv.setText( listViewItem.getLeft_menu_name() );
        left_menu_cost_Tv.setText( listViewItem.getLeft_menu_cost() );
        Glide.with(context)
                .load( listViewItem.getLeft_menu_image() )
                .placeholder(R.drawable.loading)
                .centerCrop()
                .into(left_menu_Iv);
        left_menu_Iv.setScaleType(ImageView.ScaleType.FIT_XY);


        //우측부분
        if( listViewItem.isRight_menu_visible() ){ right_menu_fL.setVisibility(View.VISIBLE); }
        else{ right_menu_fL.setVisibility(View.GONE); }
        right_menu_name_Tv.setText( listViewItem.getRight_menu_name() );
        right_menu_cost_Tv.setText( listViewItem.getRight_menu_cost() );
        Glide.with(context)
                .load( listViewItem.getRight_menu_image() )
                .placeholder(R.drawable.loading)
                .centerCrop()
                .into(right_menu_Iv);
        right_menu_Iv.setScaleType(ImageView.ScaleType.FIT_XY);


        //카테고리 부분
        if( listViewItem.isCategory_visible() ){
            category_FL.setVisibility(View.VISIBLE);

            //화살표 표시
            //가장 초기 & 리스트 내용이 변화되어서 새로 그려질때
            boolean isOpen = listViewItem.isCategoryOpen();

            if(isOpen){//열려있을때(화살표는 위로)
                Glide.with(context)
                        .load( R.drawable.up_arrow )
                        .into(category_arrow_Iv);
            }else{//닫혀있을때(화살표는 아래로)
                Glide.with(context)
                        .load( R.drawable.down_arrow )
                        .into(category_arrow_Iv);
            }

            Log.d("JJJJ", "리셋되나: " + listViewItem.isCategoryOpen() );

        }
        else{ category_FL.setVisibility(View.GONE); }
        category_Tv.setText( listViewItem.getCategory_name() );

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
    public void addItem(boolean lvisible, String lname, String lcost, String lPic, String lMenuKey,
                        boolean rvisible, String rname, String rcost, String rPic, String rMenuKey,
                        boolean category_visible, String category_name,
                        String res_name) {

        ReviewSelect_ListViewItem item = new ReviewSelect_ListViewItem();

        item.setLeft_menu_visible(lvisible);
        item.setLeft_menu_name(lname);
        item.setLeft_menu_cost(lcost);
        item.setLeft_menu_image(lPic);
        item.setLeft_menuKey(lMenuKey);
        Log.d("IMAGE_CONTROL", "addItem: "+lPic);

        item.setRight_menu_visible(rvisible);
        item.setRight_menu_name(rname);
        item.setRight_menu_cost(rcost);
        item.setRight_menu_image(rPic);
        item.setRight_menuKey(rMenuKey);

        item.setCategory_visible(category_visible);
        item.setCategory_name(category_name);

        item.setRes_name(res_name);

        listViewItemList.add(item);


    }


    //아이템 데이터 추가(가장 마지막에 숫자를 하나 더 넣으면 해당 위치에 넣는것)
    public void addItem(boolean lvisible, String lname, String lcost, String lPic, String lMenuLey,
                              boolean rvisible, String rname, String rcost, String rPic, String rMenukey,
                              boolean category_visible, String category_name,
                              String res_name, int index){

        ReviewSelect_ListViewItem item = new ReviewSelect_ListViewItem();

        item.setLeft_menu_visible(lvisible);
        item.setLeft_menu_name(lname);
        item.setLeft_menu_cost(lcost);
        item.setLeft_menu_image(lPic);
        item.setLeft_menuKey(lMenuLey);
        Log.d("IMAGE_CONTROL", "addItem: "+lPic);

        item.setRight_menu_visible(rvisible);
        item.setRight_menu_name(rname);
        item.setRight_menu_cost(rcost);
        item.setRight_menu_image(rPic);
        item.setRight_menuKey(rMenukey);

        item.setCategory_visible(category_visible);
        item.setCategory_name(category_name);

        item.setRes_name(res_name);

        listViewItemList.add(index, item);
    }


    // 아이템 데이터 삭제
    public void removeItem(int position){
        listViewItemList.remove(position);
    }


    public void removeAllItem(){
        listViewItemList.clear();
    }


}