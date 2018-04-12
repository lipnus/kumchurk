package com.lipnus.kumchurk.submenu.search;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.IntroActivity;
import com.lipnus.kumchurk.R;

/**
 * Created by Sunpil on 2017-01-24.
 */

//프래그먼트
public class Search_ViewPager_Fragment {

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
                switch(viewNum){
                    case 1:
                        rootView = setFragment1(container, inflater);
                        break;
                    case 2:
                        rootView = setFragment2(container, inflater);
                        break;

                    default:
                        rootView = setFragment3(container, inflater);
                        break;
                }

            }catch (Exception e){

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

            rootView = inflater.inflate(R.layout.fragment_search, container, false);
            context = rootView.getContext();


            //리스트뷰와 어댑터
            ListView listview = (ListView)rootView.findViewById(R.id.search_listview);
            Search_Menu_ListViewAdapter adapter = new Search_Menu_ListViewAdapter();
            listview.setAdapter(adapter);

            //검색어를 포함하는 식당이름이 있을 경우
            if(GlobalApplication.search_fbList != null){

                for(int i=0; i<GlobalApplication.search_fbList.size(); i++){
                    adapter.addItem( GlobalApplication.search_fbList.get(i).resInfo,
                            GlobalApplication.search_fbList.get(i).menuInfo,
                            GlobalApplication.search_fbList.get(i).menuReview);
                }
            }//if

            adapter.notifyDataSetChanged(); //리스트 새로고침
            Log.d("SSCC", "입력완료");


            return rootView;
        }

        public View setFragment2(ViewGroup container, LayoutInflater inflater){

            View rootView;
            final Context context;


            rootView = inflater.inflate(R.layout.fragment_search, container, false);
            context = rootView.getContext();


            //리스트뷰와 어댑터
            ListView listview = (ListView)rootView.findViewById(R.id.search_listview);
            Search_Res_ListViewAdapter adapter = new Search_Res_ListViewAdapter();
            listview.setAdapter(adapter);


            //검색어를 포함하는 식당이름이 있을 경우
            if(GlobalApplication.search_resInfoList != null){

                for(int i = 0; i<GlobalApplication.search_resInfoList.size(); i++){
                    adapter.addItem( GlobalApplication.search_resInfoList.get(i) );
                }
            }//if
            adapter.notifyDataSetChanged(); //리스트 새로고침

            return rootView;
        }

        public View setFragment3(ViewGroup container, LayoutInflater inflater){

            View rootView;
            final Context context;


            rootView = inflater.inflate(R.layout.fragment_search, container, false);
            context = rootView.getContext();


            //리스트뷰와 어댑터
            ListView listview = (ListView)rootView.findViewById(R.id.search_listview);
            Search_User_ListViewAdapter adapter = new Search_User_ListViewAdapter();
            listview.setDividerHeight(0);
            listview.setAdapter(adapter);



            //검색어를 포함하는 식당이름이 있을 경우
            if(GlobalApplication.search_resInfoList != null){

                for(int i = 0; i<GlobalApplication.search_userList.size(); i++){
                    adapter.addItem( GlobalApplication.search_userList.get(i) );
                }
            }//if
            adapter.notifyDataSetChanged(); //리스트 새로고침

            return rootView;
        }

    }
}
