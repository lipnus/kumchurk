package com.lipnus.kumchurk.submenu.search;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Sunpil on 2017-01-24.
 */

//어댑터
public class Search_ViewPager_Adapter extends FragmentPagerAdapter {

    //제목
    private String title1="메뉴";
    private String title2="식당";
    private String title3="유저";


    


    public Search_ViewPager_Adapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Log.d("FFF", "getItem(" + position +")");


        return Search_ViewPager_Fragment.PlaceholderFragment.newInstance(position + 1);
    }


    //전체 페이지수
    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return POSITION_NONE;
    }

    //탭을 사용할 경우 탭에 표시될 제목
    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return title1;
            case 1:
                return title2;
            case 2:
                return title3;
        }
        return null;
    }


    //탭뷰의 제목을 바꿈
    public void chageTabTitle(String title1, String title2, String title3){

        if(title1 != null){
            this.title1 = title1;
        }

        if(title2 != null){
            this.title2 = title2;
        }

        if(title3 != null){
            this.title3 = title3;
        }

    }

}
