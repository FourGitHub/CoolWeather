package com.coolweather.android.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coolweather.android.R;
import com.coolweather.android.View.SearchActivity;
import com.coolweather.android.entity.SearchedCities;

import java.util.List;

/**
 * Create on 2019/01/23
 *
 * @author Four
 * @description 模糊搜索的时候，返回的实体
 */
public class SearchCityAdapter extends RecyclerView.Adapter<SearchCityAdapter.ViewHolder> {

    List<SearchedCities.HeWeather6Bean.BasicBean> mCities;
    private SearchActivity mContext;

    public SearchCityAdapter(SearchActivity context) {
        mContext = context;
    }

    public void setmCities(List<SearchedCities.HeWeather6Bean.BasicBean> mCities) {
        this.mCities = mCities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recycyer_city_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchedCities.HeWeather6Bean.BasicBean city = mCities.get(position);
        String cityName = city.getLocation() + ", " + city.getAdmin_area() + ", " + city.getCnty();
        holder.tvCityName.setText(cityName);
        holder.item.setOnClickListener(v -> mContext.updateWeatherActivity(city.getCid()));
    }

    @Override
    public int getItemCount() {
        return mCities != null ? mCities.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCityName;
        View item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = itemView;
            tvCityName = itemView.findViewById(R.id.tv_cityname);
        }
    }


}
