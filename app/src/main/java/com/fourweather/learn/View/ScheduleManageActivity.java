package com.fourweather.learn.View;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fourweather.learn.R;
import com.fourweather.learn.entity.ScheduleEntity;
import com.fourweather.learn.adapter.TripManageAdapter;

import org.litepal.LitePal;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScheduleManageActivity extends AppCompatActivity {

    @BindView(R.id.img_bg)
    ImageView imgBg;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.recycler_trip)
    RecyclerView recyclerTrip;
    @BindView(R.id.title_indicate)
    RelativeLayout titleIndicate;

    private TripManageAdapter mAdapter;
    private List<ScheduleEntity> mScheduleEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_manage);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mScheduleEntities = LitePal.order("pos asc").find(ScheduleEntity.class);

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(0, swipeFlag);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                LitePal.deleteAll(ScheduleEntity.class, "tripTheme like ?", mScheduleEntities.get(position).getTripTheme());
                mScheduleEntities.remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

        });
        mItemTouchHelper.attachToRecyclerView(recyclerTrip);
        mAdapter = new TripManageAdapter(this, mScheduleEntities, mItemTouchHelper);
        recyclerTrip.setAdapter(mAdapter);
        recyclerTrip.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mScheduleEntities = LitePal.order("pos asc").find(ScheduleEntity.class);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.img_back)
    public void onViewClicked() {
        finish();
    }


}
