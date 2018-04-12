package com.lipnus.kumchurk.kum_class;

/**
 * Created by Sunpil on 2017-04-16.
 */

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.lipnus.kumchurk.R;

public class CustomDialog extends Dialog {


    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 지저분한(?) 다이얼 로그 제목을 날림
        setContentView(R.layout.dialog_loading); // 다이얼로그에 박을 레이아웃
    }
}
