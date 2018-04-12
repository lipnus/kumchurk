package com.lipnus.kumchurk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.lipnus.kumchurk.firebaseModel.ResInfo_fb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class FirebaseActivity extends AppCompatActivity {

    private ArrayList<String> mUsernames = new ArrayList<>(); //리스트 아이템
    private ListView mListView;
    private Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        //리스트뷰와 어댑터
        mListView = (ListView) findViewById(R.id.listview);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mUsernames);
        mListView.setAdapter(arrayAdapter);


        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/res_info");
        mRef.addChildEventListener(new ChildEventListener() {

            //데이터가 추가되었을때
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


//                Log.d("LLGG", "" + dataSnapshot);

                try{
                    ResInfo_fb resInfo = dataSnapshot.getValue( ResInfo_fb.class );
                    mUsernames.add( resInfo.res_name );

                    //==============================================================================
                    Map<String, Boolean> map = resInfo.res_theme;
                    Iterator<String> keySetIterator = map.keySet().iterator();

                    while (keySetIterator.hasNext()) {
                        String key = keySetIterator.next();
                        Log.d("LLGG", "key: " + key + " value: " + map.get(key));
                    }
                    //==============================================================================


                }catch(Exception e){
                    Log.d("LLGG", "에러: " + e);
                }



                arrayAdapter.notifyDataSetChanged();

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
