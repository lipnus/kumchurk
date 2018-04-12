package com.lipnus.kumchurk.data;

import java.util.List;

/**
 * 메뉴와 그에 해당하는 식당정보를 Join을 통해 묶어서 서버에서 받아옴
 *
 * 이방식이 훨씬 나은듯
 * 앞으로는 이 형식으로 수정할 예정
 */

public class MenuRes_Info_JSON {

    private List<MenuRes_Info> menuresInfo;

    public MenuRes_Info_JSON(List<MenuRes_Info> menuresInfo) {
        this.menuresInfo = menuresInfo;
    }

    public List<MenuRes_Info> getMenuresInfo() {
        return menuresInfo;
    }
}
