package com.lipnus.kumchurk;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.lipnus.kumchurk.firebaseModel.ScrollData;

public class FoodPagerFragment {


    public static class PlaceholderFragment extends Fragment {

        //==========================================================================================

        //addMenu메소드를 통해, 리뷰 정보들이 이곳에 저장된다
        private static ScrollData scrollData = null;


        //이 메소드를 통해 값을 입력
        public static void addMenu(ScrollData sD){
            scrollData = sD;
        }
        //==========================================================================================

        //현재 어느페이지인지
        private static final String ARG_SECTION_NUMBER = "section_number";


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            Log.d("VPVP", "PlaceholderFragment newInstance " + sectionNumber);
            return fragment;
        }

        public PlaceholderFragment() {
            Log.d("VPVP", "PlaceholderFragment()");
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            Log.d("VPVP", "onCreateView()");

            int viewNum = getArguments().getInt(ARG_SECTION_NUMBER); //뷰 번호
            View rootView;
            ImageView iV;
            final Context context;

            rootView = inflater.inflate(R.layout.fragment_menu_image_inside, container, false);
            iV = (ImageView)rootView.findViewById(R.id.imageView);
            context = rootView.getContext();

            //올려진 리뷰가 1개 이상일때
            if(scrollData.menuReviews.size() != 0){

                Glide.with(context)
                        .load( scrollData.menuReviews.get(viewNum-1).review_image )
                        .placeholder(R.drawable.res_loading)
                        .priority(Priority.HIGH)
                        .centerCrop()
                        .into(iV);
                iV.setScaleType(ImageView.ScaleType.FIT_XY);

//                //사진을 터치하면 상세페이지로 이동
//                iV.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        Intent iT = new Intent(context, DetailReveiwActivity.class);
//                        iT.putExtra("review_num", Integer.toString(menu_review.get(index).getNo()));
//                        startActivity(iT);
//
//                    }
//                });
            }

            //올라온 리뷰가 0개일때(기본사진을 표시)
            else{
                //이미지만 올라가 있는지 확인하고 띄워줌
                Glide.with(context)
                        .load(R.drawable.empty_table2)
                        .centerCrop()
                        .into(iV);
                iV.setScaleType(ImageView.ScaleType.FIT_XY);
            }



            Log.d("FFF", viewNum + "번째뷰");
            return rootView;
        }


    }
}