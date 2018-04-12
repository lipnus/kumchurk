package com.lipnus.kumchurk;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lipnus.kumchurk.kum_class.CustomDialog;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Sunpil on 2017-02-26.
 */

public class VolleyConnect {

    //resonse 리스너(즉각 결과가 나오는게 아니고, 접속이 끝나야 결과가 나오므로 호출하는 곳에서는 리스너로 받아야 한다)
    IVolleyResult mResultCallback = null;

    Context context; //이걸 호출한 곳의컨텍스트
    private String UPLOAD_URL; //접속주소
    private Map<String,String> params = new Hashtable<String, String>(); //post할 데이터들을 모아놓은 MAP
    private int dialongOption;

    //생성자
    public VolleyConnect(IVolleyResult resultCallback, Context context, String url, Map<String, String> parmas){
        mResultCallback = resultCallback;
        this.context = context;
        this.UPLOAD_URL = url;
        this.params = parmas;

        //다이얼로그는 보임
        this.dialongOption = 0;
        ConnectServer();
    }

    //Dialong에 대한 옵션을 지정하고 싶을 때 호출하는 생성자
    public VolleyConnect(IVolleyResult resultCallback, Context context, String url, Map<String, String> parmas, int dialogOption){
        mResultCallback = resultCallback;
        this.context = context;
        this.UPLOAD_URL = url;
        this.params = parmas;
        this.dialongOption = dialogOption;

        ConnectServer();


    }

    //volley를 이용하여 서버에 접속
    private void ConnectServer(){

        final CustomDialog customDialog = new CustomDialog(context); //커스터마이징 프로그래스다이얼로그
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //다이얼로그 뒷배경투명처리

        if(dialongOption==0){
            customDialog.setCancelable(false);//끌 수 없다
            customDialog.show(); // 보여주기

        }else if(dialongOption ==1) {
            customDialog.setCanceledOnTouchOutside(false); //바깥을 눌러서는 꺼지지 않음
            customDialog.setCancelable(true);//취소누르면 꺼짐
            customDialog.show(); // 보여주기

        }else if(dialongOption==2){

            //Dialong사용하지 않음

        }



        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        customDialog.dismiss();

                        //결과값은 s;

                        //결과값을 리스너를 통해 전달
                        if(mResultCallback != null){
                            mResultCallback.notifySuccess(s);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        customDialog.dismiss();


                        //결과값을 리스너를 통해 전달
                        if(mResultCallback != null){
                            mResultCallback.notifyError(volleyError);
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //보내는 부분
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

}
