package com.lipnus.kumchurk.kum_class;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lipnus.kumchurk.firebaseModel.Comment_fb;

/**
 * Created by Sunpil on 2017-08-10.
 */

public class FirebaseControl {

    private Firebase mRef;


    //입력받은 주소의 개수를 반환
    public void getFBCount(){

        mRef = new Firebase("https://fireapp-9ef47.firebaseio.com/Korea/comment");
        mRef.orderByChild("uid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    Log.d("LLGG", "" + dataSnapshot.getKey());
                    Log.d("LLGG", "" + dataSnapshot.getChildren());
                    Log.d("LLGG", "" + dataSnapshot.getKey());

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Log.d("LLGG", "" + postSnapshot.getValue() );

                        Comment_fb comment_fb = postSnapshot.getValue( Comment_fb.class );
                    }

                }catch(Exception e){
                    Log.d("LLGG", "에러: " + e);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

}
