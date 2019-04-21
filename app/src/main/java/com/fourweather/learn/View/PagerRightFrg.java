package com.fourweather.learn.View;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fourweather.learn.R;
import com.fourweather.learn.entity.WeatherEntity;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Create on 2019/04/17
 *
 * @author Four
 * @description
 */
public class PagerRightFrg extends PagerFrag {

    @BindView(R.id.img_pager)
    ImageView imgPager;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_temperature)
    TextView tvTemperature;
    @BindView(R.id.tv_cityname)
    TextView tvCityname;
    @BindView(R.id.tv_1_1)
    TextView tv11;
    @BindView(R.id.tv_1_2)
    TextView tv12;
    @BindView(R.id.tv_1_3)
    TextView tv13;
    @BindView(R.id.tv_2_1)
    TextView tv21;
    @BindView(R.id.tv_2_2)
    TextView tv22;
    @BindView(R.id.tv_2_3)
    TextView tv23;
    @BindView(R.id.tv_3_1)
    TextView tv31;
    @BindView(R.id.tv_3_2)
    TextView tv32;
    @BindView(R.id.tv_3_3)
    TextView tv33;
    Unbinder unbinder;
    @BindView(R.id.tv_motto)
    EditText tvMotto;
    @BindView(R.id.card)
    CardView card;

    public CardView getCard() {
        return card;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_pager_1, container, false);
        unbinder = ButterKnife.bind(this, view);
        card = (CardView) view;

        // 背景图
        Glide.with(this).load(R.drawable.ic_right_pager).into(imgPager);

        // 日期
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String dateStr = format.format(date);
        SpannableString spannableDate = new SpannableString(dateStr);
        ForegroundColorSpan fColorSpan_0 = new ForegroundColorSpan(Color.parseColor("#fbe191"));
        spannableDate.setSpan(fColorSpan_0, 5, 7, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        ForegroundColorSpan fColorSpan_1 = new ForegroundColorSpan(Color.parseColor("#d89ca3"));
        spannableDate.setSpan(fColorSpan_1, 8, 10, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvDate.setText(spannableDate);

        // 获取Entity,后续用它解析出信息
        WeatherEntity weatherEntity = new Gson().fromJson(mCityWeaInfo.getJsonString(), WeatherEntity.class);

        // 温度
        String temperature = weatherEntity.getHeWeather6().get(0).getNow().getTmp();
        tvTemperature.setText(temperature);

        // 城市名 + 天气情况 （例如：南岸·晴）
        String weaInfo = weatherEntity.getHeWeather6().get(0).getNow().getCond_txt();
        String cityname = weatherEntity.getHeWeather6().get(0).getBasic().getLocation();
        String content = String.format("%s·%s", cityname, weaInfo);
        SpannableString spannableContent = new SpannableString(content);
        ForegroundColorSpan fColorSpan_2 = new ForegroundColorSpan(Color.parseColor("#fbf006"));
        spannableContent.setSpan(fColorSpan_2, 0, cityname.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        ForegroundColorSpan fColorSpan_3 = new ForegroundColorSpan(Color.parseColor("#ee95ae"));
        spannableContent.setSpan(fColorSpan_3, cityname.length() + 1, content.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvCityname.setText(spannableContent);

        // 3天预报
        WeatherEntity.HeWeather6Bean.DailyForecastBean tomorrow = weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(1);
        WeatherEntity.HeWeather6Bean.DailyForecastBean in2Days = weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(2);
        WeatherEntity.HeWeather6Bean.DailyForecastBean in3Days = weatherEntity.getHeWeather6().get(0).getDaily_forecast().get(3);

        // 设置未来三天的日期（星期几）
        format = new SimpleDateFormat("E");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        tv21.setText(format.format(calendar.getTime()));
        calendar.add(Calendar.DATE, 1);
        tv31.setText(format.format(calendar.getTime()));

        // 设置明天的天气情况
        weaInfo = tomorrow.getCond_txt_d();
        String tempMin = tomorrow.getTmp_min();
        String tempMax = tomorrow.getTmp_max();
        tv12.setText(weaInfo);
        tv13.setText(String.format("%s° ~ %s°", tempMin, tempMax));

        // 设置后天的天气情况
        weaInfo = in2Days.getCond_txt_d();
        tempMin = in2Days.getTmp_min();
        tempMax = in2Days.getTmp_max();
        tv22.setText(weaInfo);
        tv23.setText(String.format("%s° ~ %s°", tempMin, tempMax));

        // 设置大后天的天气情况
        weaInfo = in3Days.getCond_txt_d();
        tempMin = in3Days.getTmp_min();
        tempMax = in3Days.getTmp_max();
        tv32.setText(weaInfo);
        tv33.setText(String.format("%s° ~ %s°", tempMin, tempMax));

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
