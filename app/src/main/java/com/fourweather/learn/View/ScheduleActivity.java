package com.fourweather.learn.View;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourweather.learn.R;
import com.fourweather.learn.entity.ScheduleEntity;
import com.fourweather.learn.entity.WeatherEntity;
import com.fourweather.learn.utils.HttpUtil;
import com.fourweather.learn.utils.HttpWeatherEntity;
import com.fourweather.learn.utils.ToastUtil;
import com.suke.widget.SwitchButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.litepal.LitePal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ScheduleActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_mine)
    TextView tvSave;
    @BindView(R.id.et_loc_picker_start)
    EditText etLocPickerStart;
    @BindView(R.id.tv_time_picker_start)
    TextView tvTimePickerStart;
    @BindView(R.id.et_loc_picker_1)
    EditText etLocPicker1;
    @BindView(R.id.tv_time_picker_1)
    TextView tvTimePicker1;
    @BindView(R.id.schedule_m_1)
    LinearLayout scheduleM1;
    @BindView(R.id.et_loc_picker_2)
    EditText etLocPicker2;
    @BindView(R.id.tv_time_picker_2)
    TextView tvTimePicker2;
    @BindView(R.id.schedule_m_2)
    LinearLayout scheduleM2;
    @BindView(R.id.et_loc_picker_end)
    EditText etLocPickerEnd;
    @BindView(R.id.tv_time_picker_end)
    TextView tvTimePickerEnd;
    @BindView(R.id.indicator)
    View indicator;
    @BindView(R.id.add_city_item)
    RelativeLayout addCityItem;
    @BindView(R.id.schedule_container)
    LinearLayout scheduleContainer;
    @BindView(R.id.outer_container)
    LinearLayout outerContainer;
    @BindView(R.id.tv_see)
    TextView tvSee;
    @BindView(R.id.edt_trip_theme)
    EditText edtTripTheme;
    @BindView(R.id.switch_btn)
    SwitchButton switchBtn;
    @BindView(R.id.edt_note)
    EditText edtNote;

    private static final int REQUEST_ONE = 1;
    private static final String TAG = "ScheduleActivity";


    private int canAddCount = 2;
    private int curDatePicker = 0;
    private int inputCount = 0;
    private boolean isTimePickerStartOK;
    private boolean isTimePicker1OK;
    private boolean isTimePicker2OK;
    private boolean isTimePickerEndOK;
    private boolean isRequesting = false;
    private boolean willNotify = true;

    private ArrayList<String> allLocs = new ArrayList<>(4);
    private ArrayList<String> allTimes = new ArrayList<>(4);
    private HashMap<Integer, String> requestResult = new HashMap<>();
    private MHandler mHandler = new MHandler(this);

    public static final String CHANNEL_TRIP_ADVANCE_NOTIFY = "TRIP_NOTIFY";
    private NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        // 适配 Android 8.0 的通知渠道
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(CHANNEL_TRIP_ADVANCE_NOTIFY, "行程通知", NotificationManager.IMPORTANCE_HIGH);
            guideOpenTripNotifyChannel();
        }

        switchBtn.setOnCheckedChangeListener((view, isChecked) -> willNotify = isChecked);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.setmAc(this);
    }

    @OnClick({R.id.switch_btn, R.id.tv_see, R.id.img_back, R.id.tv_mine, R.id.tv_time_picker_start, R.id.tv_time_picker_1, R.id.tv_time_picker_2, R.id.tv_time_picker_end, R.id.add_city_item})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.switch_btn:
                break;
            case R.id.tv_mine:
                Intent i = new Intent(this, ScheduleManageActivity.class);
                startActivity(i);
                break;
            case R.id.tv_time_picker_start:
                curDatePicker = 0;
                DatePicker();
                break;
            case R.id.tv_time_picker_1:
                curDatePicker = 1;
                DatePicker();
                break;
            case R.id.tv_time_picker_2:
                curDatePicker = 2;
                DatePicker();
                break;
            case R.id.tv_time_picker_end:
                curDatePicker = 3;
                DatePicker();
                break;
            case R.id.add_city_item:
                switch (canAddCount) {
                    case 2:
                        scheduleM1.setVisibility(View.VISIBLE);
                        canAddCount--;
                        break;
                    case 1:
                        scheduleM2.setVisibility(View.VISIBLE);
                        canAddCount--;
                        addCityItem.setVisibility(View.GONE);
                        break;
                }
                break;

            case R.id.tv_see:
                if (edtTripTheme.getText().toString().trim().isEmpty()) {
                    ToastUtil.showToast(APP.getContext(), "主题不能为空", Toast.LENGTH_SHORT);
                    return;
                }
                if (isRequesting) {
                    return;
                }
                isRequesting = true;
                // 汇总用户输入，并网络请求
                getAllTripsInput();
                requestAllTrips();
                break;

        }
    }

    private void getAllTripsInput() {
        String startLoc = etLocPickerStart.getText().toString().trim();
        if (!startLoc.isEmpty() && isTimePickerStartOK) {
            allLocs.add(startLoc);
            allTimes.add(tvTimePickerStart.getText().toString());
        }
        String mLoc1 = etLocPicker1.getText().toString().trim();
        if (!mLoc1.isEmpty() && isTimePicker1OK) {
            allLocs.add(mLoc1);
            allTimes.add(tvTimePicker1.getText().toString());
        }
        String mLoc2 = etLocPicker2.getText().toString().trim();
        if (!mLoc2.isEmpty() && isTimePicker2OK) {
            allLocs.add(mLoc2);
            allTimes.add(tvTimePicker2.getText().toString());
        }
        String endLoc = etLocPickerEnd.getText().toString().trim();
        if (!endLoc.isEmpty() && isTimePickerEndOK) {
            allLocs.add(endLoc);
            allTimes.add(tvTimePickerEnd.getText().toString());
        }
    }

    private void requestAllTrips() {
        int allRequestCount = 0;
        for (String allLoc : allLocs) {
            if (allLoc != null) {
                allRequestCount++;
            }
        }
        mHandler.setWaitingRequestCount(allRequestCount);
        if (allRequestCount <= 0) {
            return;
        }

        int i = 0;
        for (; i < allLocs.size() - 1; i++) {
            if (allLocs.get(i) != null) {
                requestWeatherEntity(inputCount++, allLocs.get(i), allTimes.get(i), allTimes.get(i + 1));
            }
        }

        if (allLocs.get(i) != null) {
            requestWeatherEntity(inputCount, allLocs.get(i), allTimes.get(i), null);
        }
    }

    /* SeeOneTripActivity 中展示的一页信息,即为数据库中的一项数据，为了将数据划分开，我在不同信息的相应位置插入特殊符号（百分号% he 逗号, ）
     * 百分号以行程安排的各个城市节点为单位进行划分，然后每一个城市节点再用逗号划分该城市节点的天气信息，
     * ScheduleManageActivity解析的时候做反向处理，即疯狂split()
     * 南岸\n%
     * 05-02 小雨 18 ~ 23℃\n%，
     *
     * 成都\n%
     * 05-03 小雨 16~21℃\n
     * 05-04 阴   16~22℃\n%,
     *
     * 以此类推...
     *
     *
     *
     *
     * */
    private void requestWeatherEntity(int index, String cid, String startTime, String endTime) {
        if (HttpUtil.isNetworkEnable(APP.getContext())) {
            Observer<WeatherEntity> observer = new Observer<WeatherEntity>() {
                String start = startTime;
                String end = endTime;
                int pos = index;

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(WeatherEntity weatherEntity) {
                    Log.i(TAG, "onNext: --->>> start = " + start);
                    Log.i(TAG, "onNext: --->>> end = " + end);
                    if (weatherEntity.getHeWeather6().get(0).getStatus().toLowerCase().equals("ok")) {
                        StringBuilder sb = new StringBuilder();
                        for (WeatherEntity.HeWeather6Bean.DailyForecastBean forecast : weatherEntity.getHeWeather6().get(0).getDaily_forecast()) {
                            if (end == null && forecast.getDate().compareTo(start) >= 0) {
                                sb.append(forecast.getDate().substring(5) + "   " + forecast.getCond_txt_d() + "  ");
                                sb.append(forecast.getTmp_min() + " ~ " + forecast.getTmp_max() + "℃\n%");
                            } else if (forecast.getDate().compareTo(start) >= 0 && forecast.getDate().compareTo(end) < 0) {
                                sb.append(forecast.getDate().substring(5) + "   " + forecast.getCond_txt_d() + "  ");
                                sb.append(forecast.getTmp_min() + " ~ " + forecast.getTmp_max() + "℃\n%");
                            }
                        }
                        requestResult.put(pos, sb.toString());
                        mHandler.obtainMessage(REQUEST_ONE).sendToTarget();
                    } else {
                        failed("获取天气信息失败！");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    failed("获取天气信息失败！");
                }

                @Override
                public void onComplete() {
                }
            };
            HttpWeatherEntity.getInstance().getWeatherEntity(observer, cid);
        } else {
            failed("请检查您的网络！");
        }

    }

    private void failed(String msg) {
        ToastUtil.showToast(APP.getContext(), msg, Toast.LENGTH_SHORT);
        clearAllTripInput();
        isRequesting = false;
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Log.i(TAG, "--->>>onDateSet: " + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
        String selectDate = String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
        switch (curDatePicker) {
            case 0:
                isTimePickerStartOK = true;
                tvTimePickerStart.setText(selectDate);
                break;
            case 1:
                isTimePicker1OK = true;
                tvTimePicker1.setText(selectDate);
                break;
            case 2:
                isTimePicker2OK = true;
                tvTimePicker2.setText(selectDate);
                break;
            case 3:
                isTimePickerEndOK = true;
                tvTimePickerEnd.setText(selectDate);
                break;
        }
    }

    private void DatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(this::onDateSet, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.setSelectableDays(createSelectableDays());
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    private Calendar[] createSelectableDays() {
        Calendar[] calendars = new Calendar[7];
        Date date = new Date();
        for (int i = 0; i < 7; i++) {
            calendars[i] = Calendar.getInstance();
            calendars[i].setTime(date);
            calendars[i].add(Calendar.DATE, i);
        }
        return calendars;
    }

    private void startSeeOneTripAc() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, j = 0; i < 4; i++) {
            if (requestResult.get(i) != null) {
                sb.append(allLocs.get(j++) + "%");
                sb.append(requestResult.get(i));
                sb.append(",");
            }

        }
        SeeOneTripActivity.setmEntity(new ScheduleEntity(LitePal.findAll(ScheduleEntity.class).size(), edtTripTheme.getText().toString().trim(), sb.toString(), edtNote.getText().toString().trim(), willNotify));
        Intent i = new Intent(this, SeeOneTripActivity.class);
        i.putExtra(SeeOneTripActivity.FROM_SCHEDULE_AC, true);
        if (edtNote.getText().toString().trim().isEmpty()) {
            i.putExtra(SeeOneTripActivity.MY_NOTE, "无");
        } else {
            i.putExtra(SeeOneTripActivity.MY_NOTE, edtNote.getText().toString().trim());
        }
        startActivity(i);
    }

    private void clearAllTripInput() {
        allLocs.clear();
        allTimes.clear();
        requestResult.clear();
        inputCount = 0;
        isRequesting = false;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel tripChannel = new NotificationChannel(channelId, channelName, importance);
        // 显示未读角标
        tripChannel.setShowBadge(true);
        if (manager != null) {
            manager.createNotificationChannel(tripChannel);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void guideOpenTripNotifyChannel() {
        NotificationChannel tripChannel = manager.getNotificationChannel(CHANNEL_TRIP_ADVANCE_NOTIFY);
        if (tripChannel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
            Intent openChannel = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            openChannel.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            openChannel.putExtra(Settings.EXTRA_CHANNEL_ID, CHANNEL_TRIP_ADVANCE_NOTIFY);
            startActivity(openChannel);
            ToastUtil.showToast(APP.getContext(), "请您将通知功能打开", Toast.LENGTH_SHORT);
        }
    }

    static class MHandler extends Handler {

        void setmAc(ScheduleActivity scheduleActivity) {
            this.mAc = new WeakReference<>(scheduleActivity);
        }

        WeakReference<ScheduleActivity> mAc;
        int waitingRequestCount = 0;

        void setWaitingRequestCount(int waitingRequestCount) {
            this.waitingRequestCount = waitingRequestCount;
        }

        MHandler(ScheduleActivity scheduleActivity) {
            mAc = new WeakReference<>(scheduleActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_ONE:
                    if (--waitingRequestCount == 0 && mAc.get() != null) {
                        mAc.get().startSeeOneTripAc();
                        mAc.get().clearAllTripInput();
                    }
            }
        }
    }


}
