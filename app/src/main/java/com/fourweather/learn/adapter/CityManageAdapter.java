package com.fourweather.learn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourweather.learn.R;
import com.fourweather.learn.entity.WeatherEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create on 2019/02/01
 *
 * @author Four
 * @description
 */
public class CityManageAdapter extends RecyclerView.Adapter<CityManageAdapter.ViewHolder> {

    private List<WeatherEntity> mWeatherEntityList;
    private Context mContext;
    private ItemTouchHelper mItemTouchHelper;

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.mItemTouchHelper = itemTouchHelper;
    }

    public CityManageAdapter(Context context, List<WeatherEntity> weatherEntityList) {
        this.mContext = context;
        this.mWeatherEntityList = weatherEntityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_city_manage_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherEntity weatherEntity = mWeatherEntityList.get(position);
        String cityName = weatherEntity.getHeWeather6().get(0).getBasic().getLocation();
        String cityInfo = weatherEntity.getHeWeather6().get(0).getNow().getCond_txt();
        String tmpMax = weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(0).getTmp_max();
        String temMin = weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(0).getTmp_min();

        holder.tvManageName.setText(cityName);
        holder.tvManageWinfo.setText(String.format("%s  %s ~ %s℃", cityInfo, temMin, tmpMax));
        // 拖拽监听
        holder.imgDrag.setOnTouchListener((v, event) -> {
            mItemTouchHelper.startDrag(holder);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mWeatherEntityList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.img_drag)
        ImageView imgDrag;
        @BindView(R.id.tv_manage_name)
        TextView tvManageName;
        @BindView(R.id.tv_manage_winfo)
        TextView tvManageWinfo;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
