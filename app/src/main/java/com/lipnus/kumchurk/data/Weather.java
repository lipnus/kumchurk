package com.lipnus.kumchurk.data;

import java.util.List;

/**
 * Created by Sunpil on 2017-03-23.
 * 날씨정보를 담고있는 DTO
 *
 * 귀찮으니 get set 안하고 다 public으로 함..
 *
 */

public class Weather {

    public List<W_weather> weather = null;
    public W_main main = null;

    public Weather(List<W_weather> weather, W_main main) {
        this.weather = weather;
        this.main = main;
    }

    public static class W_weather{
        public String main = "Clear";
        public String description = "clear sky";
        public String icon = null;
    }

    public static class W_main{
        public double temp = 10;
        public double presure = 1000;
        public int humidity = 30;
        public double temp_min = 15;
        public double temp_max = 15;
    }

}
