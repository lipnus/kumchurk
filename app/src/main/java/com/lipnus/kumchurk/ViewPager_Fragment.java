package com.lipnus.kumchurk;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.lipnus.kumchurk.firebaseModel.MenuInfo_fb;
import com.lipnus.kumchurk.firebaseModel.MenuReview_fb;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;
import com.lipnus.kumchurk.kum_class.SimpleFunction;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.MaskTransformation;

import static com.lipnus.kumchurk.kum_class.SimpleFunction.distanceMinute;

/**
 * Created by Sunpil on 2017-01-24.
 */
//프래그먼트
public class ViewPager_Fragment {

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            int viewNum = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView;
            Context context;

            Log.d("SSFF", "지금: " + viewNum);

            try{
                switch(viewNum%9){
                    case 1:
                        rootView = setFragment1(container, inflater);
                        break;
                    case 2:
                        rootView = setFragment2(container, inflater);
                        break;
                    case 3:
                        rootView = setFragment3(container, inflater);
                        break;
                    case 4:
                        rootView = setFragment4(container, inflater);
                        break;
                    case 5:
                        rootView = setFragment5(container, inflater);
                        break;
                    case 6:
                        rootView = setFragment6(container, inflater);
                        break;
                    case 7:
                        rootView = setFragment7(container, inflater);
                        break;
                    case 8:
                        rootView = setFragment8(container, inflater);
                        break;
                    case 0:
                        rootView = setFragment9(container, inflater);
                        break;

                    default:
                        rootView = setFragment1(container, inflater);
                        break;
                }

            }catch (Exception e){

                Log.d("SSFF", "Data refresh");
                rootView = inflater.inflate(R.layout.fragment_layout, container, false);
                context = rootView.getContext();
                applicationLiveCheck(context);
            }


            Log.d("FFF", viewNum + "번째뷰생성");
            return rootView;

        }//onCreateView


        //application의 객체가 재생성 되었는지 확인(작동되는지는 아직 확인안해봄)
        public void applicationLiveCheck(Context context){

            if(GlobalApplication.mainData_fbList==null){
                Log.d("SSFF", "ViewPager_Fragment에서 체크: 어플리케이션 객체 소멸");

                Toast toast = Toast.makeText(context, "새로고침", Toast.LENGTH_LONG);
                int offsetX = 0;
                int offsetY = 0;
                toast.setGravity(Gravity.CENTER, offsetX, offsetY);
                toast.show();

                Intent intent = new Intent(context,IntroActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        //각 프래그먼트들의 소스코드가 들어있다
        public View setFragment1(ViewGroup container, LayoutInflater inflater){

            View rootView;
            final Context context;

            rootView = inflater.inflate(R.layout.fragment_layout, container, false);
            context = rootView.getContext();

            try{
                int pIndex = 0; //몇번째 뷰페이저인지

                //리스트의 해당 경로
                final List<ResInfo_fb> resInfo = GlobalApplication.mainData_fbList.get(pIndex).resInfo;
                final List<MenuInfo_fb> menuInfo = GlobalApplication.mainData_fbList.get(pIndex).menuInfo;
                final List<MenuReview_fb> menuReview = GlobalApplication.mainData_fbList.get(pIndex).menuReview;


                //이미지경로
                String imagepath = menuReview.get(0).review_image;

                //가격
                String price = SimpleFunction.displayPrice(
                        Double.parseDouble(menuInfo.get(0).price1),
                        Double.parseDouble(menuInfo.get(0).price2),
                        Double.parseDouble(menuInfo.get(0).price3) );


                //이름과 가격표시
                String menuName = menuInfo.get(0).menu_name + "\n\n" + price;

                //거리
                String disMin = distanceMinute(
                        Double.parseDouble(resInfo.get(0).latitude),
                        Double.parseDouble(resInfo.get(0).longitude));
                String resName = "이곳으로부터 " + disMin + "의 " + resInfo.get(0).res_name ;

                if(disMin.equals("안암동")){
                    resName = "안암동에 있는 " + resInfo.get(0).res_name;
                }else if(disMin.equals("바로그곳!")){
                    resName = "지금 " + resInfo.get(0).res_name + "에 계시는군요!";
                }


                //--------------------------------------------------------------------------------------

                ImageView menuIv = (ImageView)rootView.findViewById(R.id.circle_menu_img_iv);
                ImageView circleIv = (ImageView) rootView.findViewById(R.id.circle_center_iv);

                TextView menuNameTv = (TextView) rootView.findViewById(R.id.circle_menuname_tv);
                TextView resNameTv = (TextView)rootView.findViewById(R.id.circle_resname_tv);

                //--------------------------------------------------------------------------------------



                circleIv.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO : click event
                        String res_key = resInfo.get(0).key;
                        String menu_key = menuInfo.get(0).key;
                        String review_key = menuReview.get(0).key;

                        Log.d("CLCL", "보낸키: " + res_key + ", " + menu_key + ", " + review_key);

                        Intent iT = new Intent(context, ScrollActivity.class);
                        iT.putExtra("res_key", res_key);
                        iT.putExtra("menu_key", menu_key);
                        iT.putExtra("review_key", review_key);
                        startActivity(iT);


                    }
                });



                //제목, 가격
                menuNameTv.setText(menuName);

                //거리, 식당이름
                resNameTv.setText(resName);


                //가운데 동그라미
                Glide.with( context )
                        .load( imagepath )
                        .bitmapTransform(new CropCircleTransformation(context), new CenterCrop(context))
                        .thumbnail(0.1f)
                        .dontAnimate()
                        .into( circleIv );
                circleIv.setScaleType(ImageView.ScaleType.FIT_XY);

                //전체이미지
                Glide.with( context )
                        .load( imagepath )
                        .placeholder(R.drawable.main_loading2)
                        .bitmapTransform(new BlurTransformation(context, 4),
                                new CenterCrop(context),new ColorFilterTransformation(context, Color.argb(150, 0, 0, 0)))
                        .thumbnail(0.1f)
                        .dontAnimate()
                        .into( menuIv );
                menuIv.setScaleType(ImageView.ScaleType.FIT_XY);



            }catch(Exception e){
                applicationLiveCheck(context);
            }

            return rootView;

        }

        public View setFragment2(ViewGroup container, LayoutInflater inflater){

            View rootView;
            final Context context;

            rootView = inflater.inflate(R.layout.fragment_layout2, container, false);
            context = rootView.getContext();


            int pIndex = 1; //몇번째 뷰페이저인지

            //리스트의 해당 경로
            final List<ResInfo_fb> resInfo = GlobalApplication.mainData_fbList.get(pIndex).resInfo;
            final List<MenuInfo_fb> menuInfo = GlobalApplication.mainData_fbList.get(pIndex).menuInfo;
            final List<MenuReview_fb> menuReview = GlobalApplication.mainData_fbList.get(pIndex).menuReview;

            //이미지경로
            String imagepath = menuReview.get(0).review_image;
            String imagepath2 = menuReview.get(1).review_image;
            String imagepath3 = menuReview.get(2).review_image;

            //가격
            String price = Double.toString( Double.parseDouble(menuInfo.get(0).price1)/1000 );
            String price2 = Double.toString( Double.parseDouble(menuInfo.get(1).price1)/1000 );
            String price3 = Double.toString( Double.parseDouble(menuInfo.get(2).price1)/1000 );

            //이름과 가격표시
            String menuName = menuInfo.get(0).menu_name + " " + price;
            String menuName2 = menuInfo.get(1).menu_name + " " + price2;
            String menuName3 = menuInfo.get(2).menu_name + " " + price3;

            //거리
            String disMin = distanceMinute(Double.parseDouble(resInfo.get(0).latitude), Double.parseDouble(resInfo.get(0).longitude));
            String disMin2 = distanceMinute(Double.parseDouble(resInfo.get(1).latitude), Double.parseDouble(resInfo.get(1).longitude));
            String disMin3 = distanceMinute(Double.parseDouble(resInfo.get(2).latitude), Double.parseDouble(resInfo.get(2).longitude));

            String resName = resInfo.get(0).res_name + " "+ disMin;
            String resName2 = resInfo.get(1).res_name + " "+ disMin2;
            String resName3 = resInfo.get(2).res_name + " "+ disMin3;

            //--------------------------------------------------------------------------------------

            ImageView topIv = (ImageView)rootView.findViewById(R.id.diagonal_top_iv);
            ImageView middleIv = (ImageView)rootView.findViewById(R.id.diagonal_middle_iv);
            ImageView bottomIv = (ImageView)rootView.findViewById(R.id.diagonal_bottom_iv);

            TextView topTv = (TextView) rootView.findViewById(R.id.diagonal_top_tv);
            TextView middleTv = (TextView)rootView.findViewById(R.id.diagonal_middle_tv);
            TextView bottomTv = (TextView)rootView.findViewById(R.id.diagonal_bottom_tv);

            TextView topTv2 = (TextView) rootView.findViewById(R.id.diagonal_top2_tv);
            TextView middleTv2 = (TextView)rootView.findViewById(R.id.diagonal_middle2_tv);
            TextView bottomTv2 = (TextView)rootView.findViewById(R.id.diagonal_bottom2_tv);
            //--------------------------------------------------------------------------------------

            topIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(0).key;
                    String menu_key = menuInfo.get(0).key;
                    String review_key = menuReview.get(0).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            middleIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(1).key;
                    String menu_key = menuInfo.get(1).key;
                    String review_key = menuReview.get(1).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            bottomIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(2).key;
                    String menu_key = menuInfo.get(2).key;
                    String review_key = menuReview.get(2).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });


            //제목, 가격
            topTv.setText(menuName);
            middleTv.setText(menuName2);
            bottomTv.setText(menuName3);


            //거리, 식당이름
            topTv2.setText(resName);
            middleTv2.setText(resName2);
            bottomTv2.setText(resName3);


//            new VignetteFilterTransformation(context, new PointF(0.5f, 0.7f),
//                    new float[] { 0.0f, 0.0f, 0.0f }, 0f, 0.9f),

            //이미지
            Glide.with( context )
                    .load( imagepath )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(
                            new CenterCrop(context),
                            new MaskTransformation(context, R.drawable.mask_top))
                    .into( topIv );
            topIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load( imagepath2 )
                    .placeholder(R.drawable.main_loading)
                    .centerCrop()
                    .thumbnail(0.3f)
                    .into( middleIv );
            middleIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load(imagepath3 )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(
                            new CenterCrop(context),
                            new MaskTransformation(context, R.drawable.mask_bottom))
                    .into( bottomIv );
            bottomIv.setScaleType(ImageView.ScaleType.FIT_XY);

            return rootView;
        }

        public View setFragment3(ViewGroup container, LayoutInflater inflater){

            View rootView;
            final Context context;

            rootView = inflater.inflate(R.layout.fragment_layout3, container, false);
            context = rootView.getContext();

            int pIndex = 2; //몇번째 뷰페이저인지(0부터시작)

            //리스트의 해당 경로
            final List<ResInfo_fb> resInfo = GlobalApplication.mainData_fbList.get(pIndex).resInfo;
            final List<MenuInfo_fb> menuInfo = GlobalApplication.mainData_fbList.get(pIndex).menuInfo;
            final List<MenuReview_fb> menuReview = GlobalApplication.mainData_fbList.get(pIndex).menuReview;

            //이미지경로
            String imagepath = menuReview.get(0).review_image;
            String imagepath2 = menuReview.get(1).review_image;
            String imagepath3 = menuReview.get(2).review_image;

            //가격
            String price = Double.toString( Double.parseDouble(menuInfo.get(0).price1)/1000 );
            String price2 = Double.toString( Double.parseDouble(menuInfo.get(1).price1)/1000 );
            String price3 = Double.toString( Double.parseDouble(menuInfo.get(2).price1)/1000 );

            //이름과 가격표시
            String menuName = menuInfo.get(0).menu_name + " " + price;
            String menuName2 = menuInfo.get(1).menu_name + " " + price2;
            String menuName3 = menuInfo.get(2).menu_name + " " + price3;

            //거리
            String disMin = distanceMinute(Double.parseDouble(resInfo.get(0).latitude), Double.parseDouble(resInfo.get(0).longitude));
            String disMin2 = distanceMinute(Double.parseDouble(resInfo.get(1).latitude), Double.parseDouble(resInfo.get(1).longitude));
            String disMin3 = distanceMinute(Double.parseDouble(resInfo.get(2).latitude), Double.parseDouble(resInfo.get(2).longitude));

            String resName = resInfo.get(0).res_name + " "+ disMin;
            String resName2 = resInfo.get(1).res_name + " "+ disMin2;
            String resName3 = resInfo.get(2).res_name + " "+ disMin3;

            //--------------------------------------------------------------------------------------

            ImageView topIv = (ImageView)rootView.findViewById(R.id.vertical_top_iv);
            ImageView middleIv = (ImageView)rootView.findViewById(R.id.vertical_middle_iv);
            ImageView bottomIv = (ImageView)rootView.findViewById(R.id.vertical_bottom_iv);
            TextView topTv = (TextView) rootView.findViewById(R.id.vertical_top_tv);
            TextView middleTv = (TextView)rootView.findViewById(R.id.vertical_middle_tv);
            TextView bottomTv = (TextView)rootView.findViewById(R.id.vertical_bottom_tv);
            TextView topTv2 = (TextView) rootView.findViewById(R.id.vertical_top2_tv);
            TextView middleTv2 = (TextView)rootView.findViewById(R.id.vertical_middle2_tv);
            TextView bottomTv2 = (TextView)rootView.findViewById(R.id.vertical_bottom2_tv);
            //--------------------------------------------------------------------------------------

            topIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(0).key;
                    String menu_key = menuInfo.get(0).key;
                    String review_key = menuReview.get(0).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            middleIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(1).key;
                    String menu_key = menuInfo.get(1).key;
                    String review_key = menuReview.get(1).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            bottomIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(2).key;
                    String menu_key = menuInfo.get(2).key;
                    String review_key = menuReview.get(2).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });


            //제목, 가격
            topTv.setText(menuName);
            middleTv.setText(menuName2);
            bottomTv.setText(menuName3);

            //거리, 식당이름
            topTv2.setText(resName);
            middleTv2.setText(resName2);
            bottomTv2.setText(resName3);


            //이미지
            Glide.with( context )
                    .load( imagepath )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(new CenterCrop(context))
                    .into( topIv );
            topIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load( imagepath2 )
                    .placeholder(R.drawable.main_loading)
                    .centerCrop()
                    .into( middleIv );
            middleIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load(imagepath3 )
                    .placeholder(R.drawable.main_loading)
                    .centerCrop()
                    .into( bottomIv );
            bottomIv.setScaleType(ImageView.ScaleType.FIT_XY);

            return rootView;
        }

        public View setFragment4(ViewGroup container, LayoutInflater inflater){

            View rootView;
            final Context context;

            rootView = inflater.inflate(R.layout.fragment_layout4, container, false);
            context = rootView.getContext();

            int pIndex = 3; //몇번째 뷰페이저인지

            //리스트의 해당 경로
            final List<ResInfo_fb> resInfo = GlobalApplication.mainData_fbList.get(pIndex).resInfo;
            final List<MenuInfo_fb> menuInfo = GlobalApplication.mainData_fbList.get(pIndex).menuInfo;
            final List<MenuReview_fb> menuReview = GlobalApplication.mainData_fbList.get(pIndex).menuReview;

            //이미지경로
            String imagepath = menuReview.get(0).review_image;
            String imagepath2 = menuReview.get(1).review_image;
            String imagepath3 = menuReview.get(2).review_image;

            //가격
            String price = Double.toString( Double.parseDouble(menuInfo.get(0).price1)/1000 );
            String price2 = Double.toString( Double.parseDouble(menuInfo.get(1).price1)/1000 );
            String price3 = Double.toString( Double.parseDouble(menuInfo.get(2).price1)/1000 );

            //이름과 가격표시
            String menuName = menuInfo.get(0).menu_name + " " + price;
            String menuName2 = menuInfo.get(1).menu_name + " " + price2;
            String menuName3 = menuInfo.get(2).menu_name + " " + price3;

            //거리
            String disMin = distanceMinute(Double.parseDouble(resInfo.get(0).latitude), Double.parseDouble(resInfo.get(0).longitude));
            String disMin2 = distanceMinute(Double.parseDouble(resInfo.get(1).latitude), Double.parseDouble(resInfo.get(1).longitude));
            String disMin3 = distanceMinute(Double.parseDouble(resInfo.get(2).latitude), Double.parseDouble(resInfo.get(2).longitude));

            String resName = resInfo.get(0).res_name + " "+ disMin;
            String resName2 = resInfo.get(1).res_name + " "+ disMin2;
            String resName3 = resInfo.get(2).res_name + " "+ disMin3;

            //--------------------------------------------------------------------------------------

            ImageView topIv = (ImageView)rootView.findViewById(R.id.diagonal_top_iv);
            ImageView middleIv = (ImageView)rootView.findViewById(R.id.diagonal_middle_iv);
            ImageView bottomIv = (ImageView)rootView.findViewById(R.id.diagonal_bottom_iv);

            TextView topTv = (TextView) rootView.findViewById(R.id.diagonal_top_tv);
            TextView middleTv = (TextView)rootView.findViewById(R.id.diagonal_middle_tv);
            TextView bottomTv = (TextView)rootView.findViewById(R.id.diagonal_bottom_tv);

            TextView topTv2 = (TextView) rootView.findViewById(R.id.diagonal_top2_tv);
            TextView middleTv2 = (TextView)rootView.findViewById(R.id.diagonal_middle2_tv);
            TextView bottomTv2 = (TextView)rootView.findViewById(R.id.diagonal_bottom2_tv);
            //--------------------------------------------------------------------------------------

            topIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(0).key;
                    String menu_key = menuInfo.get(0).key;
                    String review_key = menuReview.get(0).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            middleIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(1).key;
                    String menu_key = menuInfo.get(1).key;
                    String review_key = menuReview.get(1).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            bottomIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(2).key;
                    String menu_key = menuInfo.get(2).key;
                    String review_key = menuReview.get(2).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });


            //제목, 가격
            topTv.setText(menuName);
            middleTv.setText(menuName2);
            bottomTv.setText(menuName3);


            //거리, 식당이름
            topTv2.setText(resName);
            middleTv2.setText(resName2);
            bottomTv2.setText(resName3);


            //이미지
            Glide.with( context )
                    .load( imagepath )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(
                            new CenterCrop(context),
                            new MaskTransformation(context, R.drawable.mask_top_r))
                    .into( topIv );
            topIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load( imagepath2 )
                    .placeholder(R.drawable.main_loading)
                    .centerCrop()
                    .thumbnail(0.3f)
                    .into( middleIv );
            middleIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load(imagepath3 )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(
                            new CenterCrop(context),
                            new MaskTransformation(context, R.drawable.mask_bottom_r))
                    .into( bottomIv );
            bottomIv.setScaleType(ImageView.ScaleType.FIT_XY);

            return rootView;

        }

        public View setFragment5(ViewGroup container, LayoutInflater inflater){

            View rootView;
            final Context context;

            rootView = inflater.inflate(R.layout.fragment_layout2, container, false);
            context = rootView.getContext();

            int pIndex = 4; //몇번째 뷰페이저인지

            //리스트의 해당 경로
            final List<ResInfo_fb> resInfo = GlobalApplication.mainData_fbList.get(pIndex).resInfo;
            final List<MenuInfo_fb> menuInfo = GlobalApplication.mainData_fbList.get(pIndex).menuInfo;
            final List<MenuReview_fb> menuReview = GlobalApplication.mainData_fbList.get(pIndex).menuReview;

            //이미지경로
            String imagepath = menuReview.get(0).review_image;
            String imagepath2 = menuReview.get(1).review_image;
            String imagepath3 = menuReview.get(2).review_image;

            //가격
            String price = Double.toString( Double.parseDouble(menuInfo.get(0).price1)/1000 );
            String price2 = Double.toString( Double.parseDouble(menuInfo.get(1).price1)/1000 );
            String price3 = Double.toString( Double.parseDouble(menuInfo.get(2).price1)/1000 );

            //이름과 가격표시
            String menuName = menuInfo.get(0).menu_name + " " + price;
            String menuName2 = menuInfo.get(1).menu_name + " " + price2;
            String menuName3 = menuInfo.get(2).menu_name + " " + price3;

            //거리
            String disMin = distanceMinute(Double.parseDouble(resInfo.get(0).latitude), Double.parseDouble(resInfo.get(0).longitude));
            String disMin2 = distanceMinute(Double.parseDouble(resInfo.get(1).latitude), Double.parseDouble(resInfo.get(1).longitude));
            String disMin3 = distanceMinute(Double.parseDouble(resInfo.get(2).latitude), Double.parseDouble(resInfo.get(2).longitude));

            String resName = resInfo.get(0).res_name + " "+ disMin;
            String resName2 = resInfo.get(1).res_name + " "+ disMin2;
            String resName3 = resInfo.get(2).res_name + " "+ disMin3;

            //--------------------------------------------------------------------------------------

            ImageView topIv = (ImageView)rootView.findViewById(R.id.diagonal_top_iv);
            ImageView middleIv = (ImageView)rootView.findViewById(R.id.diagonal_middle_iv);
            ImageView bottomIv = (ImageView)rootView.findViewById(R.id.diagonal_bottom_iv);

            TextView topTv = (TextView) rootView.findViewById(R.id.diagonal_top_tv);
            TextView middleTv = (TextView)rootView.findViewById(R.id.diagonal_middle_tv);
            TextView bottomTv = (TextView)rootView.findViewById(R.id.diagonal_bottom_tv);

            TextView topTv2 = (TextView) rootView.findViewById(R.id.diagonal_top2_tv);
            TextView middleTv2 = (TextView)rootView.findViewById(R.id.diagonal_middle2_tv);
            TextView bottomTv2 = (TextView)rootView.findViewById(R.id.diagonal_bottom2_tv);
            //--------------------------------------------------------------------------------------

            topIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(0).key;
                    String menu_key = menuInfo.get(0).key;
                    String review_key = menuReview.get(0).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            middleIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(1).key;
                    String menu_key = menuInfo.get(1).key;
                    String review_key = menuReview.get(1).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            bottomIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(2).key;
                    String menu_key = menuInfo.get(2).key;
                    String review_key = menuReview.get(2).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });


            //제목, 가격
            topTv.setText(menuName);
            middleTv.setText(menuName2);
            bottomTv.setText(menuName3);


            //거리, 식당이름
            topTv2.setText(resName);
            middleTv2.setText(resName2);
            bottomTv2.setText(resName3);


            //이미지
            Glide.with( context )
                    .load( imagepath )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(
                            new CenterCrop(context),
                            new MaskTransformation(context, R.drawable.mask_top))
                    .into( topIv );
            topIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load( imagepath2 )
                    .placeholder(R.drawable.main_loading)
                    .centerCrop()
                    .thumbnail(0.3f)
                    .into( middleIv );
            middleIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load(imagepath3 )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(
                            new CenterCrop(context),
                            new MaskTransformation(context, R.drawable.mask_bottom))
                    .into( bottomIv );
            bottomIv.setScaleType(ImageView.ScaleType.FIT_XY);

            return rootView;
        }

        public View setFragment6(ViewGroup container, LayoutInflater inflater){

            View rootView;
            final Context context;

            rootView = inflater.inflate(R.layout.fragment_layout2, container, false);
            context = rootView.getContext();


            int pIndex = 5; //몇번째 뷰페이저인지

            //리스트의 해당 경로
            final List<ResInfo_fb> resInfo = GlobalApplication.mainData_fbList.get(pIndex).resInfo;
            final List<MenuInfo_fb> menuInfo = GlobalApplication.mainData_fbList.get(pIndex).menuInfo;
            final List<MenuReview_fb> menuReview = GlobalApplication.mainData_fbList.get(pIndex).menuReview;

            //이미지경로
            String imagepath = menuReview.get(0).review_image;
            String imagepath2 = menuReview.get(1).review_image;
            String imagepath3 = menuReview.get(2).review_image;

            //가격
            String price = Double.toString( Double.parseDouble(menuInfo.get(0).price1)/1000 );
            String price2 = Double.toString( Double.parseDouble(menuInfo.get(1).price1)/1000 );
            String price3 = Double.toString( Double.parseDouble(menuInfo.get(2).price1)/1000 );

            //이름과 가격표시
            String menuName = menuInfo.get(0).menu_name + " " + price;
            String menuName2 = menuInfo.get(1).menu_name + " " + price2;
            String menuName3 = menuInfo.get(2).menu_name + " " + price3;

            //거리
            String disMin = distanceMinute(Double.parseDouble(resInfo.get(0).latitude), Double.parseDouble(resInfo.get(0).longitude));
            String disMin2 = distanceMinute(Double.parseDouble(resInfo.get(1).latitude), Double.parseDouble(resInfo.get(1).longitude));
            String disMin3 = distanceMinute(Double.parseDouble(resInfo.get(2).latitude), Double.parseDouble(resInfo.get(2).longitude));

            String resName = resInfo.get(0).res_name + " "+ disMin;
            String resName2 = resInfo.get(1).res_name + " "+ disMin2;
            String resName3 = resInfo.get(2).res_name + " "+ disMin3;

            //--------------------------------------------------------------------------------------

            ImageView topIv = (ImageView)rootView.findViewById(R.id.diagonal_top_iv);
            ImageView middleIv = (ImageView)rootView.findViewById(R.id.diagonal_middle_iv);
            ImageView bottomIv = (ImageView)rootView.findViewById(R.id.diagonal_bottom_iv);

            TextView topTv = (TextView) rootView.findViewById(R.id.diagonal_top_tv);
            TextView middleTv = (TextView)rootView.findViewById(R.id.diagonal_middle_tv);
            TextView bottomTv = (TextView)rootView.findViewById(R.id.diagonal_bottom_tv);

            TextView topTv2 = (TextView) rootView.findViewById(R.id.diagonal_top2_tv);
            TextView middleTv2 = (TextView)rootView.findViewById(R.id.diagonal_middle2_tv);
            TextView bottomTv2 = (TextView)rootView.findViewById(R.id.diagonal_bottom2_tv);
            //--------------------------------------------------------------------------------------

            topIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(0).key;
                    String menu_key = menuInfo.get(0).key;
                    String review_key = menuReview.get(0).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            middleIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(1).key;
                    String menu_key = menuInfo.get(1).key;
                    String review_key = menuReview.get(1).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            bottomIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(2).key;
                    String menu_key = menuInfo.get(2).key;
                    String review_key = menuReview.get(2).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });


            //제목, 가격
            topTv.setText(menuName);
            middleTv.setText(menuName2);
            bottomTv.setText(menuName3);


            //거리, 식당이름
            topTv2.setText(resName);
            middleTv2.setText(resName2);
            bottomTv2.setText(resName3);


//            new VignetteFilterTransformation(context, new PointF(0.5f, 0.7f),
//                    new float[] { 0.0f, 0.0f, 0.0f }, 0f, 0.9f),

            //이미지
            Glide.with( context )
                    .load( imagepath )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(
                            new CenterCrop(context),
                            new MaskTransformation(context, R.drawable.mask_top))
                    .into( topIv );
            topIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load( imagepath2 )
                    .placeholder(R.drawable.main_loading)
                    .centerCrop()
                    .thumbnail(0.3f)
                    .into( middleIv );
            middleIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load(imagepath3 )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(
                            new CenterCrop(context),
                            new MaskTransformation(context, R.drawable.mask_bottom))
                    .into( bottomIv );
            bottomIv.setScaleType(ImageView.ScaleType.FIT_XY);

            return rootView;
        }

        public View setFragment7(ViewGroup container, LayoutInflater inflater){

            View rootView;
            final Context context;

            rootView = inflater.inflate(R.layout.fragment_layout3, container, false);
            context = rootView.getContext();

            int pIndex = 6; //몇번째 뷰페이저인지(0부터시작)

            //리스트의 해당 경로
            final List<ResInfo_fb> resInfo = GlobalApplication.mainData_fbList.get(pIndex).resInfo;
            final List<MenuInfo_fb> menuInfo = GlobalApplication.mainData_fbList.get(pIndex).menuInfo;
            final List<MenuReview_fb> menuReview = GlobalApplication.mainData_fbList.get(pIndex).menuReview;

            //이미지경로
            String imagepath = menuReview.get(0).review_image;
            String imagepath2 = menuReview.get(1).review_image;
            String imagepath3 = menuReview.get(2).review_image;

            //가격
            String price = Double.toString( Double.parseDouble(menuInfo.get(0).price1)/1000 );
            String price2 = Double.toString( Double.parseDouble(menuInfo.get(1).price1)/1000 );
            String price3 = Double.toString( Double.parseDouble(menuInfo.get(2).price1)/1000 );

            //이름과 가격표시
            String menuName = menuInfo.get(0).menu_name + " " + price;
            String menuName2 = menuInfo.get(1).menu_name + " " + price2;
            String menuName3 = menuInfo.get(2).menu_name + " " + price3;

            //거리
            String disMin = distanceMinute(Double.parseDouble(resInfo.get(0).latitude), Double.parseDouble(resInfo.get(0).longitude));
            String disMin2 = distanceMinute(Double.parseDouble(resInfo.get(1).latitude), Double.parseDouble(resInfo.get(1).longitude));
            String disMin3 = distanceMinute(Double.parseDouble(resInfo.get(2).latitude), Double.parseDouble(resInfo.get(2).longitude));

            String resName = resInfo.get(0).res_name + " "+ disMin;
            String resName2 = resInfo.get(1).res_name + " "+ disMin2;
            String resName3 = resInfo.get(2).res_name + " "+ disMin3;

            //--------------------------------------------------------------------------------------

            ImageView topIv = (ImageView)rootView.findViewById(R.id.vertical_top_iv);
            ImageView middleIv = (ImageView)rootView.findViewById(R.id.vertical_middle_iv);
            ImageView bottomIv = (ImageView)rootView.findViewById(R.id.vertical_bottom_iv);
            TextView topTv = (TextView) rootView.findViewById(R.id.vertical_top_tv);
            TextView middleTv = (TextView)rootView.findViewById(R.id.vertical_middle_tv);
            TextView bottomTv = (TextView)rootView.findViewById(R.id.vertical_bottom_tv);
            TextView topTv2 = (TextView) rootView.findViewById(R.id.vertical_top2_tv);
            TextView middleTv2 = (TextView)rootView.findViewById(R.id.vertical_middle2_tv);
            TextView bottomTv2 = (TextView)rootView.findViewById(R.id.vertical_bottom2_tv);
            //--------------------------------------------------------------------------------------

            topIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(0).key;
                    String menu_key = menuInfo.get(0).key;
                    String review_key = menuReview.get(0).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            middleIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(1).key;
                    String menu_key = menuInfo.get(1).key;
                    String review_key = menuReview.get(1).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            bottomIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(2).key;
                    String menu_key = menuInfo.get(2).key;
                    String review_key = menuReview.get(2).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });


            //제목, 가격
            topTv.setText(menuName);
            middleTv.setText(menuName2);
            bottomTv.setText(menuName3);

            //거리, 식당이름
            topTv2.setText(resName);
            middleTv2.setText(resName2);
            bottomTv2.setText(resName3);


            //이미지
            Glide.with( context )
                    .load( imagepath )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(new CenterCrop(context))
                    .into( topIv );
            topIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load( imagepath2 )
                    .placeholder(R.drawable.main_loading)
                    .centerCrop()
                    .into( middleIv );
            middleIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load(imagepath3 )
                    .placeholder(R.drawable.main_loading)
                    .centerCrop()
                    .into( bottomIv );
            bottomIv.setScaleType(ImageView.ScaleType.FIT_XY);

            return rootView;
        }

        public View setFragment8(ViewGroup container, LayoutInflater inflater){

            View rootView;
            final Context context;

            rootView = inflater.inflate(R.layout.fragment_layout4, container, false);
            context = rootView.getContext();

            int pIndex = 7; //몇번째 뷰페이저인지

            //리스트의 해당 경로
            final List<ResInfo_fb> resInfo = GlobalApplication.mainData_fbList.get(pIndex).resInfo;
            final List<MenuInfo_fb> menuInfo = GlobalApplication.mainData_fbList.get(pIndex).menuInfo;
            final List<MenuReview_fb> menuReview = GlobalApplication.mainData_fbList.get(pIndex).menuReview;

            //이미지경로
            String imagepath = menuReview.get(0).review_image;
            String imagepath2 = menuReview.get(1).review_image;
            String imagepath3 = menuReview.get(2).review_image;

            //가격
            String price = Double.toString( Double.parseDouble(menuInfo.get(0).price1)/1000 );
            String price2 = Double.toString( Double.parseDouble(menuInfo.get(1).price1)/1000 );
            String price3 = Double.toString( Double.parseDouble(menuInfo.get(2).price1)/1000 );

            //이름과 가격표시
            String menuName = menuInfo.get(0).menu_name + " " + price;
            String menuName2 = menuInfo.get(1).menu_name + " " + price2;
            String menuName3 = menuInfo.get(2).menu_name + " " + price3;

            //거리
            String disMin = distanceMinute(Double.parseDouble(resInfo.get(0).latitude), Double.parseDouble(resInfo.get(0).longitude));
            String disMin2 = distanceMinute(Double.parseDouble(resInfo.get(1).latitude), Double.parseDouble(resInfo.get(1).longitude));
            String disMin3 = distanceMinute(Double.parseDouble(resInfo.get(2).latitude), Double.parseDouble(resInfo.get(2).longitude));

            String resName = resInfo.get(0).res_name + " "+ disMin;
            String resName2 = resInfo.get(1).res_name + " "+ disMin2;
            String resName3 = resInfo.get(2).res_name + " "+ disMin3;

            //--------------------------------------------------------------------------------------

            ImageView topIv = (ImageView)rootView.findViewById(R.id.diagonal_top_iv);
            ImageView middleIv = (ImageView)rootView.findViewById(R.id.diagonal_middle_iv);
            ImageView bottomIv = (ImageView)rootView.findViewById(R.id.diagonal_bottom_iv);

            TextView topTv = (TextView) rootView.findViewById(R.id.diagonal_top_tv);
            TextView middleTv = (TextView)rootView.findViewById(R.id.diagonal_middle_tv);
            TextView bottomTv = (TextView)rootView.findViewById(R.id.diagonal_bottom_tv);

            TextView topTv2 = (TextView) rootView.findViewById(R.id.diagonal_top2_tv);
            TextView middleTv2 = (TextView)rootView.findViewById(R.id.diagonal_middle2_tv);
            TextView bottomTv2 = (TextView)rootView.findViewById(R.id.diagonal_bottom2_tv);
            //--------------------------------------------------------------------------------------

            topIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(0).key;
                    String menu_key = menuInfo.get(0).key;
                    String review_key = menuReview.get(0).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            middleIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(1).key;
                    String menu_key = menuInfo.get(1).key;
                    String review_key = menuReview.get(1).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            bottomIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(2).key;
                    String menu_key = menuInfo.get(2).key;
                    String review_key = menuReview.get(2).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });


            //제목, 가격
            topTv.setText(menuName);
            middleTv.setText(menuName2);
            bottomTv.setText(menuName3);


            //거리, 식당이름
            topTv2.setText(resName);
            middleTv2.setText(resName2);
            bottomTv2.setText(resName3);


            //이미지
            Glide.with( context )
                    .load( imagepath )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(
                            new CenterCrop(context),
                            new MaskTransformation(context, R.drawable.mask_top_r))
                    .into( topIv );
            topIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load( imagepath2 )
                    .placeholder(R.drawable.main_loading)
                    .centerCrop()
                    .thumbnail(0.3f)
                    .into( middleIv );
            middleIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load(imagepath3 )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(
                            new CenterCrop(context),
                            new MaskTransformation(context, R.drawable.mask_bottom_r))
                    .into( bottomIv );
            bottomIv.setScaleType(ImageView.ScaleType.FIT_XY);

            return rootView;

        }

        public View setFragment9(ViewGroup container, LayoutInflater inflater){

            View rootView;
            final Context context;

            rootView = inflater.inflate(R.layout.fragment_layout2, container, false);
            context = rootView.getContext();

            int pIndex = 8; //몇번째 뷰페이저인지

            //리스트의 해당 경로
            final List<ResInfo_fb> resInfo = GlobalApplication.mainData_fbList.get(pIndex).resInfo;
            final List<MenuInfo_fb> menuInfo = GlobalApplication.mainData_fbList.get(pIndex).menuInfo;
            final List<MenuReview_fb> menuReview = GlobalApplication.mainData_fbList.get(pIndex).menuReview;

            //이미지경로
            String imagepath = menuReview.get(0).review_image;
            String imagepath2 = menuReview.get(1).review_image;
            String imagepath3 = menuReview.get(2).review_image;

            //가격
            String price = Double.toString( Double.parseDouble(menuInfo.get(0).price1)/1000 );
            String price2 = Double.toString( Double.parseDouble(menuInfo.get(1).price1)/1000 );
            String price3 = Double.toString( Double.parseDouble(menuInfo.get(2).price1)/1000 );

            //이름과 가격표시
            String menuName = menuInfo.get(0).menu_name + " " + price;
            String menuName2 = menuInfo.get(1).menu_name + " " + price2;
            String menuName3 = menuInfo.get(2).menu_name + " " + price3;

            //거리
            String disMin = distanceMinute(Double.parseDouble(resInfo.get(0).latitude), Double.parseDouble(resInfo.get(0).longitude));
            String disMin2 = distanceMinute(Double.parseDouble(resInfo.get(1).latitude), Double.parseDouble(resInfo.get(1).longitude));
            String disMin3 = distanceMinute(Double.parseDouble(resInfo.get(2).latitude), Double.parseDouble(resInfo.get(2).longitude));

            String resName = resInfo.get(0).res_name + " "+ disMin;
            String resName2 = resInfo.get(1).res_name + " "+ disMin2;
            String resName3 = resInfo.get(2).res_name + " "+ disMin3;

            //--------------------------------------------------------------------------------------

            ImageView topIv = (ImageView)rootView.findViewById(R.id.diagonal_top_iv);
            ImageView middleIv = (ImageView)rootView.findViewById(R.id.diagonal_middle_iv);
            ImageView bottomIv = (ImageView)rootView.findViewById(R.id.diagonal_bottom_iv);

            TextView topTv = (TextView) rootView.findViewById(R.id.diagonal_top_tv);
            TextView middleTv = (TextView)rootView.findViewById(R.id.diagonal_middle_tv);
            TextView bottomTv = (TextView)rootView.findViewById(R.id.diagonal_bottom_tv);

            TextView topTv2 = (TextView) rootView.findViewById(R.id.diagonal_top2_tv);
            TextView middleTv2 = (TextView)rootView.findViewById(R.id.diagonal_middle2_tv);
            TextView bottomTv2 = (TextView)rootView.findViewById(R.id.diagonal_bottom2_tv);
            //--------------------------------------------------------------------------------------

            topIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(0).key;
                    String menu_key = menuInfo.get(0).key;
                    String review_key = menuReview.get(0).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            middleIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(1).key;
                    String menu_key = menuInfo.get(1).key;
                    String review_key = menuReview.get(1).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });

            bottomIv.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : click event
                    String res_key = resInfo.get(2).key;
                    String menu_key = menuInfo.get(2).key;
                    String review_key = menuReview.get(2).key;

                    Intent iT = new Intent(context, ScrollActivity.class);
                    iT.putExtra("res_key", res_key);
                    iT.putExtra("menu_key", menu_key);
                    iT.putExtra("review_key", review_key);
                    startActivity(iT);
                }
            });


            //제목, 가격
            topTv.setText(menuName);
            middleTv.setText(menuName2);
            bottomTv.setText(menuName3);


            //거리, 식당이름
            topTv2.setText(resName);
            middleTv2.setText(resName2);
            bottomTv2.setText(resName3);


            //이미지
            Glide.with( context )
                    .load( imagepath )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(
                            new CenterCrop(context),
                            new MaskTransformation(context, R.drawable.mask_top))
                    .into( topIv );
            topIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load( imagepath2 )
                    .placeholder(R.drawable.main_loading)
                    .centerCrop()
                    .thumbnail(0.3f)
                    .into( middleIv );
            middleIv.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with( context )
                    .load(imagepath3 )
                    .placeholder(R.drawable.main_loading)
                    .bitmapTransform(
                            new CenterCrop(context),
                            new MaskTransformation(context, R.drawable.mask_bottom))
                    .into( bottomIv );
            bottomIv.setScaleType(ImageView.ScaleType.FIT_XY);

            return rootView;
        }
    }
}
