package com.lipnus.kumchurk;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Sunpil on 2017-01-21.
 */

public class ViewPager_DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;
    private static final float MIN_ALPHA = 0.5f;

    //메인에서 쓰는 것

//    스크린의 중앙과 연관하여 해당 page의 위치를 표현한다. user가 page를 scroll하는 것에 따라 동적으로 변경된다.
//    해당 페이지가 화면에 온전히 그려지는 경우 position = 0
//    스크롤이 될때 위쪽페이지는  0>-1, 아래쪽페이지는 0->1

    public void transformPage(View view, float position) {
        int pageHeight = view.getHeight();

        //중간에서 멀어질수록 1->0 이 되어감(절댓값을 이용)
        final float normalizedposition = Math.abs(Math.abs(position) - 1);

        if (position < -1) { // [-Infinity,-1)
            // 위쪽 바깥으로 나가리 되었을때의 상태
            view.setAlpha(0);

        } else if (position <= 0) { // [-1,0]
            // 위쪽에 있는거 애니매이션

            //점점 흐려짐
            view.setAlpha(normalizedposition);

            //살짝 아래로 내려옴
            view.setTranslationY(pageHeight * -position);

            //크기 작아짐
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

        } else if (position <= 1) { // (0,1]
            // 아래쪽에 있는거 애니매이션
            view.setAlpha(1);
            view.setScaleX(1);
            view.setScaleY(1);

        } else { // (1,+Infinity]
            // 아래쪽 바깥으로 나가리 되었을때의 상태
            view.setAlpha(0);
        }

    }
}