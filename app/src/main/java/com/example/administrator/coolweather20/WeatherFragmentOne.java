package com.example.administrator.coolweather20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.coolweather20.gson.Forecast;
import com.example.administrator.coolweather20.gson.Weather;
import com.example.administrator.coolweather20.util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/2/4.
 */

public class WeatherFragmentOne extends Fragment{
    private View view;
    private Button btn_nav;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView pm25Text;
    private TextView aqiText;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private View view_statusBar;
    private ImageView bingPicImg;
    private FloatingActionButton calButton;
    private SunView mSunView;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Fragment","onCreateView");
        view=inflater.inflate(R.layout.fragment_weather,container,false);
        //让view_statusBar充当stateBar
        view_statusBar=(View)view.findViewById(R.id.view_statusBar);
        view_statusBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                WindowAttr.getStateBarHeight(getActivity())));

        weatherLayout=(ScrollView)view.findViewById(R.id.weather_layout);
        titleCity=(TextView)view.findViewById(R.id.title_city);
        titleUpdateTime=(TextView)view.findViewById(R.id.title_update_time);
        degreeText=(TextView)view.findViewById(R.id.degree_text);
        weatherInfoText=(TextView)view.findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)view.findViewById(R.id.forecast_layout);
        aqiText=(TextView)view.findViewById(R.id.aqi_text);
        pm25Text=(TextView)view.findViewById(R.id.pm25_text);
        comfortText=(TextView)view.findViewById(R.id.comfort_text);
        carWashText=(TextView)view.findViewById(R.id.car_wash_text);
        sportText=(TextView)view.findViewById(R.id.sport_text);
        bingPicImg =(ImageView)view.findViewById(R.id.bing_pic_img);
        btn_nav=(Button)view.findViewById(R.id.btn_nav);
        calButton = view.findViewById(R.id.calButton);
        mSunView = view.findViewById(R.id.sun);



        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),WeatherAddActivity.class);
                startActivity(intent);
            }
        });

        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),CalendarActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
    public void showWeatherInfo(Weather weather){
        Log.d("Fragment","ONE");
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        loadBingPic();
        for(Forecast forecast:weather.forecastList){//将forecastLayout的子项forecast_item加载进去
            View view= LayoutInflater.from(getActivity()).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max+"℃");
            minText.setText(forecast.temperature.min+"℃");
            forecastLayout.addView(view);
        }
        if(weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度："+weather.suggestion.comfort.info;
        String carWash="洗车指数："+weather.suggestion.carWash.info;
        String sport="运动建议："+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        mSunView.setTime("16:30");
    }
    /*判断fragment是否被隐藏*/
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            /*延时500毫秒*/
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    weatherLayout.smoothScrollTo(0,0);//将fragment置顶
                }
            }, 500);
        }else {

        }
    }

    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherFragmentOne.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });

    }

}
