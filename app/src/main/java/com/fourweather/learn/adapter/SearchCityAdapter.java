package com.fourweather.learn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fourweather.learn.R;
import com.fourweather.learn.View.SearchLocActivity;
import com.fourweather.learn.entity.SearchedCities;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Create on 2019/01/23
 *
 * @author Four
 * @description 模糊搜索的时候，返回的实体
 */
public class SearchCityAdapter extends RecyclerView.Adapter<SearchCityAdapter.ViewHolder> {

    protected List<SearchedCities.HeWeather6Bean.BasicBean> mCities;
    private SearchLocActivity mContext;

    protected SearchCityAdapter() {
    }

    public SearchCityAdapter(SearchLocActivity context) {
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
                .inflate(R.layout.recycyer_city_list_item_search_ac, parent, false);
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

        ViewHolder(View itemView) {
            super(itemView);
            item = itemView;
            tvCityName = itemView.findViewById(R.id.tv_cityname);
        }
    }


}
