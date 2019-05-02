package com.fourweather.learn.View;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourweather.learn.R;
import com.fourweather.learn.entity.ScheduleEntity;
import com.fourweather.learn.utils.AlarmReceiver;
import com.fourweather.learn.utils.ToastUtil;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.AlarmManagerCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SeeOneTripActivity extends AppCompatActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.title_indicate)
    RelativeLayout titleIndicate;
    @BindView(R.id.liner_trip_container)
    LinearLayout linerTripContainer;
    @BindView(R.id.img_bg)
    ImageView imgBg;
    @BindView(R.id.tv_theme)
    EditText tvTheme;
    @BindView(R.id.tv_save)
    Button tvSave;
    @BindView(R.id.tv_note)
    EditText tvNote;

    private static final String TAG = "SeeOneTripActivity";
    public static final String FROM_SCHEDULE_AC = "FROM_SCHEDULE_AC";
    public static final String MY_POSITION = "MY_POSITION";
    public static final String MY_NOTE = "MY_NOTE";


    private static ScheduleEntity mEntity;
    private boolean willNotify;
    private String[] notifyTime;
    private int curYear;

    public static void setmEntity(ScheduleEntity mEntity) {
        SeeOneTripActivity.mEntity = mEntity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_one_trip);
        ButterKnife.bind(this);
        init();
    }


    private void init() {
        // 由于信息中展示的日期没有年份，所以我得计算出年份，然后根据正确时间设置AlarmManager的提醒时间
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        curYear = c.get(Calendar.YEAR);
        notifyTime = new String[4];
        Log.i(TAG, "init: ---->>>> curYear = " + curYear);

        // 判断 ScheduelAc  OR  ScheduleManageAc  启动的这个Ac
        if (getIntent().getBooleanExtra(FROM_SCHEDULE_AC, false)) {
            tvSave.setVisibility(View.VISIBLE);
            if (!getIntent().getStringExtra(MY_NOTE).isEmpty()) {
                dispalyNote("备注：" + getIntent().getStringExtra(MY_NOTE));
            }

        } else {
            if (mEntity.getNote().isEmpty()) {
                dispalyNote("备注：无");
            } else {
                dispalyNote("备注：" + mEntity.getNote());
            }
        }
        willNotify = mEntity.isWillNotify();

        tvTheme.setText(mEntity.getTripTheme());
        String[] citys = mEntity.getTripInfo().split(",");
        View view = null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < citys.length; i++) {
            String[] cityInfo = citys[i].split("%");
            String tvName = cityInfo[0];
            String tvInfo;

            for (int j = 1; j < cityInfo.length; j++) {
                if (j == 1) {
                    addNotifyTime(cityInfo[j]);
                }
                sb.append(cityInfo[j]);
            }
            tvInfo = sb.toString();
            sb.setLength(0);
            view = getLayoutInflater().inflate(R.layout.schedule_start, linerTripContainer, false);
            TextView tvCityName = view.findViewById(R.id.tv_city_name);
            TextView tvCityInfo = view.findViewById(R.id.tv_city_info);
            if (i == citys.length - 1) {
                view = getLayoutInflater().inflate(R.layout.schedule_end, linerTripContainer, false);
                tvCityName = view.findViewById(R.id.tv_city_name);
                tvCityInfo = view.findViewById(R.id.tv_city_info);
            } else if (i == 1) {
                ImageView imgPoint = view.findViewById(R.id.img_point);
                ImageView imgVLine = view.findViewById(R.id.img_v_line);
                imgPoint.setImageResource(R.drawable.ic_point_1);
                imgVLine.setImageResource(R.drawable.line_1_2);
                tvCityName.setTextColor(Color.parseColor("#e0620d"));

            } else if (i == 2) {
                ImageView imgPoint = view.findViewById(R.id.img_point);
                ImageView imgVLine = view.findViewById(R.id.img_v_line);
                imgPoint.setImageResource(R.drawable.ic_point_2);
                imgVLine.setImageResource(R.drawable.line_2_3);
                tvCityName.setTextColor(Color.parseColor("#1296db"));

            }
            tvCityName.setText(tvName);
            tvCityInfo.setText(tvInfo);
            linerTripContainer.addView(view);
        }

        if (mEntity.getTripTheme().length() > 9) {
            tvTheme.setText(String.format("%s...", mEntity.getTripTheme().substring(0, 6)));
        }

        Log.i(TAG, "init: ---->>>>  willNotify = " + willNotify);
    }

    private void addNotifyTime(String s) {

    }

    private void dispalyNote(String content) {
        SpannableString spannableContent = new SpannableString(content);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#d6204b"));
        spannableContent.setSpan(colorSpan, 0, 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#1296db"));
        spannableContent.setSpan(colorSpan1, 3, content.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvNote.setText(spannableContent);
        tvNote.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.img_back, R.id.tv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_save:
                int pos = LitePal.findAll(ScheduleEntity.class).size();
                ScheduleEntity scheduleEntity = new ScheduleEntity(pos, mEntity.getTripTheme(), mEntity.getTripInfo(), mEntity.getNote(), willNotify);
                scheduleEntity.saveOrUpdate("tripTheme = ?", mEntity.getTripTheme());
                ToastUtil.showToast(APP.getContext(), "保存成功!", Toast.LENGTH_SHORT);
                configTripAdvanceNotify();
                break;
        }
    }

    private void configTripAdvanceNotify() {
        if (willNotify) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(APP.getContext(), AlarmReceiver.class);
            intent.setClassName(getPackageName(), "com.fourweather.learn.utils.AlarmReceiver");
            intent.setAction("com.fourweather.learn.TRIP_ADVANCE_NOTIFY");
            PendingIntent pi = PendingIntent.getBroadcast(APP.getContext(), 0, intent, 0);
            long triggerAtMills = System.currentTimeMillis() + 10 * 1000; // 10秒后触发
            AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP, triggerAtMills, pi);

        }
    }
}
