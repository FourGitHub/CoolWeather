package com.fourweather.learn.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fourweather.learn.R;
import com.fourweather.learn.View.SeeOneTripActivity;
import com.fourweather.learn.entity.ScheduleEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create on 2019/04/29
 *
 * @author Four
 * @description
 */
public class TripManageAdapter extends RecyclerView.Adapter<TripManageAdapter.ViewHolder> {

    private List<ScheduleEntity> mTrips;
    private ItemTouchHelper mItemTouchHelper;
    private Context mContext;

    public TripManageAdapter(Context mContext, List<ScheduleEntity> mTrips, ItemTouchHelper mItemTouchHelper) {
        this.mTrips = mTrips;
        this.mItemTouchHelper = mItemTouchHelper;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_manage_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleEntity scheduleEntity = mTrips.get(position);
        holder.imgDispaly.setOnClickListener(v -> {
            SeeOneTripActivity.setmEntity(ScheduleEntity.getCopyInstance(scheduleEntity));
//            SeeOneTripActivity.theme = scheduleEntity.getTripTheme();
//            SeeOneTripActivity.totalInfo = scheduleEntity.getTripInfo();
            Intent i = new Intent(mContext, SeeOneTripActivity.class);
            i.putExtra(SeeOneTripActivity.MY_POSITION, position);
            mContext.startActivity(i);
        });
        holder.tvTripTheme.setText(scheduleEntity.getTripTheme());

        holder.tripItem.setOnTouchListener((v, event) -> {
            mItemTouchHelper.startSwipe(holder);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_trip_theme)
        TextView tvTripTheme;
        @BindView(R.id.trip_item)
        RelativeLayout tripItem;
        @BindView(R.id.img_dispaly)
        ImageView imgDispaly;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
