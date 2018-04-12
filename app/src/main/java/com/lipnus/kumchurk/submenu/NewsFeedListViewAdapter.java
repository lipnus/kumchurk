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
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.ScrollActivity;
import com.lipnus.kumchurk.detailpage.CommentActivity;
import com.lipnus.kumchurk.firebaseModel.MenuInfo_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;
import com.lipnus.kumchurk.firebaseModel.User_fb;
import com.lipnus.kumchurk.kum_class.SimpleFunction;

import java.util.ArrayList;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Sunpil on 2016-07-13.
 */
public class NewsFeedListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<NewsFeedListViewItem> listViewItemList = new ArrayList<NewsFeedListViewItem>() ;


    // ListViewAdapter의 생성자
    public NewsFeedListViewAdapter() {

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
            convertView = inflater.inflate(R.layout.list_newsfeed, parent, false);
        }


        //------------------------------------------------------------------------------------------
        // 화면에 표시될 View의 구성요소
        //------------------------------------------------------------------------------------------
        LinearLayout topLr = (LinearLayout) convertView.findViewById(R.id.nf_topMarginLr);

        TextView menuNameTv = (TextView) convertView.findViewById(R.id.nf_menu_nameTv);
        TextView resNameTv = (TextView) convertView.findViewById(R.id.nf_res_nameTv);
        ImageView menuImgIv = (ImageView) convertView.findViewById(R.id.nf_menu_img_Iv);
        final ImageView tasteIv = (ImageView) convertView.findViewById(R.id.nf_taste_iv);
        final ImageView faceIv = (ImageView) convertView.findViewById(R.id.nf_faceIv);
        TextView nicknameTv = (TextView) convertView.findViewById(R.id.nf_nicknameTv);

        LinearLayout heartLr = (LinearLayout) convertView.findViewById(R.id.nf_heart_Lr);
        LinearLayout fuckLr = (LinearLayout) convertView.findViewById(R.id.nf_fuck_Lr);
        LinearLayout commentLr = (LinearLayout) convertView.findViewById(R.id.nf_comment_Lr);

        final ImageView heartIv = (ImageView) convertView.findViewById(R.id.nf_heart_iv);
        TextView heartTv = (TextView) convertView.findViewById(R.id.nf_heart_tv);
        final ImageView fuckIv = (ImageView) convertView.findViewById(R.id.nf_fuck_iv);
        TextView fuckTv = (TextView) convertView.findViewById(R.id.nf_fuck_tv);
        ImageView commentIv = (ImageView) convertView.findViewById(R.id.nf_comment_iv);
        TextView commentTv = (TextView) convertView.findViewById(R.id.nf_comment_tv);


        TextView dateTv = (TextView) convertView.findViewById(R.id.nf_date_Tv);
        TextView commentInduceTv = (TextView) convertView.findViewById(R.id.nf_comment_induce_tv);
        TextView reviewTv = (TextView) convertView.findViewById(R.id.nf_review_Tv);


        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        NewsFeedListViewItem nF = listViewItemList.get(position);



        //------------------------------------------------------------------------------------------
        // 데이터 정리
        //------------------------------------------------------------------------------------------
        String myUid = GlobalApplication.getUser_id();


        //가격
        String price = SimpleFunction.displayPrice( Double.parseDouble(nF.menuInfo_fb.price1), 0, 0);
        if(!nF.menuInfo_fb.price2.equals("0")){
            price = price + "~";
        }

        //거리
        String distance="";
        distance = SimpleFunction.distanceMinute(Double.parseDouble(nF.resInfo_fb.latitude), Double.parseDouble(nF.resInfo_fb.longitude));

        //날짜
        String date = nF.menuReview_fb.date;
        date = SimpleFunction.timeGap(date);

        //맛표정
        int tasteIconPath;
        if(nF.menuReview_fb.taste.equals("0")){
            tasteIconPath = R.drawable.taste3_black;
        }else if(nF.menuReview_fb.taste.equals("1")){
            tasteIconPath = R.drawable.taste2_black;
        }else{
            tasteIconPath = R.drawable.taste1_black;
        }


        //댓글유도멘트
        String commentInduce = "댓글달기";
        Log.d("CCTT", nF.menuInfo_fb.menu_name + "댓글수: " +nF.menuReview_fb.comment.size() );

        if(nF.menuReview_fb.comment.size() > 0){
            commentInduce = nF.menuReview_fb.comment.size() + "개의 댓글 모두보기";
            commentInduceTv.setVisibility(View.VISIBLE);
            Log.d("CCTT", "XX개의 댓글 모두보기" );
        }else{
            commentInduceTv.setVisibility(View.GONE);
        }

        //하트와 빠큐 상태설정
        int heartPath;
        int fuckPath;

        //내가 투표를 했는지 확인
        Map heartMap = nF.menuReview_fb.heart;
        Map fuckMap = nF.menuReview_fb.fuck;


        if( heartMap.get(myUid) != null ){
            heartPath = R.drawable.small_menu_heart2;
        }else{
            heartPath = R.drawable.small_menu_heart;
        }

        if( fuckMap.get(myUid) != null ){
            fuckPath = R.drawable.small_menu_fuck2;
        }else{
            fuckPath = R.drawable.small_menu_fuck;
        }

        Map commentMap = nF.menuReview_fb.comment;


        //------------------------------------------------------------------------------------------
        // 터치 이벤트
        //------------------------------------------------------------------------------------------


        //얼굴터치
        tasteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                YoYo.with(Techniques.Wobble)
                        .duration(700)
                        .playOn( tasteIv );
            }
        });


        //하트터치
        heartLr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //클릭리스너 안에는 범위가 안먹혀서 다시 선언
                NewsFeedListViewItem nF = listViewItemList.get(position);
                NewsFeedActivity nfActivity = (NewsFeedActivity) NewsFeedActivity.NFActiviry;

                //데이터처리 및 이미지변경
                nfActivity.heartTouch( position );

                //애니매이션
                YoYo.with(Techniques.RubberBand)
                        .duration(700)
                        .playOn( heartIv );

            }
        });


        //빠큐터치
        fuckLr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //클릭리스너 안에는 범위가 안먹혀서 다시 선언
                NewsFeedListViewItem nF = listViewItemList.get(position);
                NewsFeedActivity nfActivity = (NewsFeedActivity) NewsFeedActivity.NFActiviry;

                //데이터처리 및 이미지변경
                nfActivity.fuckTouch( position );

                //애니매이션
                YoYo.with(Techniques.Tada)
                        .duration(700)
                        .playOn( fuckIv );
            }
        });

        //댓글터치
        commentLr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //클릭리스너 안에는 범위가 안먹혀서 다시 선언
                NewsFeedListViewItem nF = listViewItemList.get(position);
                String review_key = nF.menuReview_fb.key;

                Intent iT = new Intent(context, CommentActivity.class);
                iT.putExtra("review_key", review_key);
                iT.putExtra("review_token", nF.user_fb.token ); //리뷰쓴사람 token
                iT.putExtra("newsfeed_position", position);
                iT.putExtra("callFrom", "NewsFeedListViewAdapter");
                context.startActivity(iT);
            }
        });

        //댓글유도터치
        commentInduceTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //클릭리스너 안에는 범위가 안먹혀서 다시 선언
                NewsFeedListViewItem nF = listViewItemList.get(position);
                String review_key = nF.menuReview_fb.key;

                Intent iT = new Intent(context, CommentActivity.class);
                iT.putExtra("review_key", review_key);
                iT.putExtra("review_token", nF.user_fb.token ); //리뷰쓴사람 token
                iT.putExtra("newsfeed_position", position);
                iT.putExtra("callFrom", "NewsFeedListViewAdapter");
                context.startActivity(iT);
            }
        });

        //리뷰터치
        reviewTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //클릭리스너 안에는 범위가 안먹혀서 다시 선언
                NewsFeedListViewItem nF = listViewItemList.get(position);

                Intent iT = new Intent(context, ScrollActivity.class);
                iT.putExtra("res_key", nF.resInfo_fb.key);
                iT.putExtra("menu_key", nF.menuInfo_fb.key);
                iT.putExtra("review_key", nF.menuReview_fb.key);
                context.startActivity(iT);
            }
        });

        //메뉴사진클릭
        menuImgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //클릭리스너 안에는 범위가 안먹혀서 다시 선언
                NewsFeedListViewItem nF = listViewItemList.get(position);

                Intent iT = new Intent(context, ScrollActivity.class);
                iT.putExtra("res_key", nF.resInfo_fb.key);
                iT.putExtra("menu_key", nF.menuInfo_fb.key);
                iT.putExtra("review_key", nF.menuReview_fb.key);
                context.startActivity(iT);
            }
        });


        //아이디터치
        faceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //클릭리스너 안에는 범위가 안먹혀서 다시 선언
            NewsFeedListViewItem nF = listViewItemList.get(position);

            Intent iT = new Intent(context, PersonalSpaceActivity.class);
            iT.putExtra("uid", nF.menuReview_fb.uid);
            context.startActivity(iT);
            }
        });

        //프사터치
        nicknameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //클릭리스너 안에는 범위가 안먹혀서 다시 선언
                NewsFeedListViewItem nF = listViewItemList.get(position);

                Intent iT = new Intent(context, PersonalSpaceActivity.class);
                iT.putExtra("uid", nF.menuReview_fb.uid);
                context.startActivity(iT);
            }
        });


        //------------------------------------------------------------------------------------------
        // 아이템 내 각 위젯에 데이터 반영
        //------------------------------------------------------------------------------------------
        if(position==0){
            //가장 첫페이지는 위쪽에 여백을 띄워줌
//            topLr.setVisibility(View.VISIBLE);
            Log.d("NNWW", "으잉");
        }
        Log.d("NNWW", "position: "+ position);

        menuNameTv.setText( nF.menuInfo_fb.menu_name + " " + price);
        resNameTv.setText( nF.resInfo_fb.res_name + ", " +distance );
        nicknameTv.setText( nF.user_fb.nickname );
        heartTv.setText( "" + heartMap.size() );
        fuckTv.setText( "" + fuckMap.size() );
        commentTv.setText( "" + commentMap.size() );
        dateTv.setText( date );
        reviewTv.setText( nF.menuReview_fb.memo );
        commentInduceTv.setText(commentInduce);

        //맛표정
        Glide.with(context)
                .load( tasteIconPath )
                .into(tasteIv);
        tasteIv.setScaleType(ImageView.ScaleType.FIT_XY);

        //메뉴이미지
        Glide.with(context)
                .load( nF.menuReview_fb.review_image )
                .centerCrop()
                .thumbnail(0.3f)
                .into(menuImgIv);
        menuImgIv.setScaleType(ImageView.ScaleType.FIT_XY);

        //프사
        Glide.with(context)
                .load( nF.user_fb.thumbnail )
                .placeholder(R.drawable.face_basic)
                .bitmapTransform(new CropCircleTransformation(context))
                .thumbnail(0.1f)
                .into(faceIv);
        faceIv.setScaleType(ImageView.ScaleType.FIT_XY);

        //하트
        Glide.with(context)
                .load( heartPath )
                .centerCrop()
                .into(heartIv);

        //빠큐
        Glide.with(context)
                .load( fuckPath )
                .centerCrop()
                .into(fuckIv);

        //댓글
        Glide.with(context)
                .load( R.drawable.small_menu_comment )
                .centerCrop()
                .into(commentIv);

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
    public void addItem(MenuInfo_fb mI, ResInfo_fb rI, MenuReview_fb mR, User_fb u) {

        NewsFeedListViewItem item = new NewsFeedListViewItem( mI, rI, mR, u );
        listViewItemList.add(item);
    }
}