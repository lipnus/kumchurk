package com.lipnus.kumchurk;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Sunpil on 2017-01-24.
 */

//어댑터
public class ViewPager_Adapter extends FragmentPagerAdapter {

    public ViewPager_Adapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Log.d("FFF", "getItem(" + position +")");


        return ViewPager_Fragment.PlaceholderFragment.newInstance(position + 1);
    }


    //전체 페이지수
    @Override
    public int getCount() {
        // Show 6 total pages.
        return 1000;
    }

    //이걸 어떻게 잘 써야 할텐데...
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
                return "PAGE 1";
            case 1:
                return "PAGE 2";
            case 2:
                return "PAGE 3";
        }
        return null;
    }

}
