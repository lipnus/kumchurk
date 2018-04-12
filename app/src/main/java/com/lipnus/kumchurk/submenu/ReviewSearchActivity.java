package com.lipnus.kumchurk.submenu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.lipnus.kumchurk.GlobalApplication;
import com.lipnus.kumchurk.R;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;
import com.lipnus.kumchurk.submenu.search.Search_Res_ListViewAdapter;
import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * SearchActivity를 거의 그대로 차용하였다
 */

public class ReviewSearchActivity extends AppCompatActivity {

    EditText searchEt;
    View searchV;



    //리스트뷰와 어댑터
    ListView listview;
    ReviewSearch_Res_ListViewAdapter adapter;

    //다른곳에서 이 액티비티를 종료할 수 있게
    public static Activity RVActivity;

    //Firebase 참조
    private Firebase mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_search);

        //툴바 없에기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //리스트뷰와 어댑터
        listview = (ListView) findViewById(R.id.sl_list);
        adapter = new ReviewSearch_Res_ListViewAdapter();
        listview.setAdapter(adapter);

        //아래의 공간. 검색결과나 필터가 표시
        searchV = findViewById(R.id.include_search);

        //EditText 조작
        searchEditText();

        //ScrollActivity가 Task에 쌓이지 않게 하기 위해
        RVActivity = ReviewSearchActivity.this;
    }

    //글꼴적용을 위해서 필요(참조 : http://gun0912.tistory.com/10 )
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.hide_to_right);
    }

    //왼쪽 상단의 뒤로가기
    public void onClick_BacktoMain(View v){
        finish();
        overridePendingTransition(R.anim.fadein, R.anim.hide_to_right);
    }






    //search EditText 조작
    public void searchEditText(){

        //포커스를 얻었을 때
        searchEt = (EditText) findViewById(R.id.search_et);

        searchEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b == true){
                    Log.d("FFCC", "포커스 얻음");
                    searchV.setVisibility(View.VISIBLE);
                }
            }
        });


        //텍스트에 변화가 있을 때
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchCode = searchEt.getText().toString();
                if(searchCode.equals("")){
                    adapter = new ReviewSearch_Res_ListViewAdapter();
                    listview.setAdapter(adapter);
                }else{
                    connect_fb_resInfo(searchCode); //검색요청
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });



        //검색버튼 눌렀을 때
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                //검색어가져오기, 키보드끄기
                String searchCode = searchEt.getText().toString();
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

                switch (i) {
                    case EditorInfo.IME_ACTION_SEARCH:

                        //검색버튼
                        if(searchCode.equals("")){
                            adapter = new ReviewSearch_Res_ListViewAdapter();
                            listview.setAdapter(adapter);
                        }else{
                            connect_fb_resInfo(searchCode); //검색요청
                        }
                        imm.hideSoftInputFromWindow( searchEt.getWindowToken(), 0); //키보드끄기
                        break;
                    default:

                        //엔터키
                        if(searchCode.equals("")){
                            adapter = new ReviewSearch_Res_ListViewAdapter();
                            listview.setAdapter(adapter);
                        }else{
                            connect_fb_resInfo(searchCode); //검색요청
                        }
                        imm.hideSoftInputFromWindow( searchEt.getWindowToken(), 0); //키보드끄기
                        return false;
                }
                return true;


            }
        });

    }

    //검색한 식당 다운받아서 리스트뷰에 띄우기(리스트 따로 안쓰고 리스트어댑터로 직빵)
    public void connect_fb_resInfo(final String searchText){

        //리스트뷰 리셋
        adapter = new ReviewSearch_Res_ListViewAdapter();
        listview.setAdapter(adapter);

        mRef = new Firebase( GlobalApplication.fbPath + "/Korea/res_info");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ResInfo_fb resInfo_fb = dataSnapshot.getValue( ResInfo_fb.class );
                resInfo_fb.key = dataSnapshot.getKey();


                //검색어를 포함하는 식당이름이 있을 경우
                if(resInfo_fb.res_name.contains(searchText)){
                    Log.d("SSHH", "식당: " + resInfo_fb.res_category);
                    adapter.addItem( resInfo_fb );
                    adapter.notifyDataSetChanged(); //리스트 새로고침
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}
