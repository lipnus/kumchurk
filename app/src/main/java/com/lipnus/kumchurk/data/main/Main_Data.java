package com.lipnus.kumchurk.data.main;

import com.lipnus.kumchurk.data.MenuRes_Info;

import java.util.List;

/**
 * Created by Sunpil on 2017-04-22.
 */

public class Main_Data {
    public List<User_Info> userInfo;
    public List<MenuRes_Info> menuresInfo;

    public List<User_Info> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(List<User_Info> userInfo) {
        this.userInfo = userInfo;
    }

    public List<MenuRes_Info> getMenuresInfo() {
        return menuresInfo;
    }

    public void setMenuresInfo(List<MenuRes_Info> menuresInfo) {
        this.menuresInfo = menuresInfo;
    }
}
