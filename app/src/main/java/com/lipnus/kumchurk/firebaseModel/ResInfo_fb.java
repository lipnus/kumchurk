package com.lipnus.kumchurk.firebaseModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sunpil on 2017-07-28.
 */

@IgnoreExtraProperties
public class ResInfo_fb {


    public ResInfo_fb(){
        this.res_theme = new HashMap<>();
        this.menu_id = new HashMap<>();
        this.res_time = new ResTime();
        this.image = new Image();
    }

    public String res_name;
    public String phone;
    public String location;
    public String latitude;
    public String longitude;
    public String res_category;
    public String extra_info;

    public String key;

    public ResTime res_time;
    public Image image;

    public Map<String, Boolean> res_theme = new HashMap<>();
    public Map<String, Boolean> menu_id = new HashMap<>();


    public class Image{
        public String image1;
        public String image2;
    }


    public class ResTime {
        public String restime_sat;
        public String restime_sun;
        public String restime_weekday;
    }

}


