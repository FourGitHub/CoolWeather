package com.fourweather.learn.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fourweather.learn.R;
import com.fourweather.learn.entity.SearchedCities;

import androidx.annotation.NonNull;

/**
 * Create on 2019/04/25
 *
 * @author Four
 * @description
 */
public class ScheduleCityAdapter extends SearchCityAdapter {
    public ScheduleCityAdapter() {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recycyer_city_list_item_schedule_ac, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchedCities.HeWeather6Bean.BasicBean city = mCities.get(position);
        String cityName = city.getLocation() + ", " + city.getAdmin_area() + ", " + city.getCnty();
        holder.tvCityName.setText(cityName);
    }
}
