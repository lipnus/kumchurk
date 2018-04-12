package com.lipnus.kumchurk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;
import com.lipnus.kumchurk.map.MapActivity;

public class ResPagerFragment {


    public static class PlaceholderFragment extends Fragment {

        //==========================================================================================

        //addRes메소드를 통해, 식당사진이 이곳에 저장된다
        private static ResInfo_fb rI = null;

        //이 메소드를 통해 값을 입력
        public static void addRes(ResInfo_fb resInfo_fb){
            rI = resInfo_fb;
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

            int viewNum = getArguments().getInt(ARG_SECTION_NUMBER); //뷰 번호(1부터 시작)
            View rootView;
            ImageView resIv;
            final Context context;

            rootView = inflater.inflate(R.layout.fragment_res_image_inside, container, false);
            resIv = (ImageView)rootView.findViewById(R.id.sc_res_iv);
            context = rootView.getContext();


            //첫페이지는 외부, 두번째페이지는 내부
            String imgPath="";

            if(viewNum==1){
                imgPath = rI.image.image1;
            }else if(viewNum == 2){
                imgPath = rI.image.image2;
            }


            Glide.with(context)
                    .load( imgPath )
                    .placeholder(R.drawable.res_loading)
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .into(resIv);
            resIv.setScaleType(ImageView.ScaleType.FIT_XY);




            //식당사진 터치
            resIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //ScrollActivity의 resInfo객체를 참조한다
                    ScrollActivity scActivity = (ScrollActivity) ScrollActivity.SCActivity;

                    Intent iT = new Intent(context, MapActivity.class);
                    iT.putExtra("res_latitude", Double.parseDouble(scActivity.resInfo_fb.latitude) );
                    iT.putExtra("res_longitude", Double.parseDouble(scActivity.resInfo_fb.longitude) );
                    iT.putExtra("res_location", scActivity.resInfo_fb.location );
                    iT.putExtra("res_name", scActivity.resInfo_fb.res_name );
                    iT.putExtra("res_phone", scActivity.resInfo_fb.phone );
                    startActivity(iT);

                }
            });

            return rootView;
        }


    }
}