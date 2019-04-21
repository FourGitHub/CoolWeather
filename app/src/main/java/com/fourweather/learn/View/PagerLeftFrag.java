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
public class PagerLeftFrag extends PagerFrag {

    @BindView(R.id.img_pager)
    ImageView imgPager;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_temperature)
    TextView tvTemperature;
    @BindView(R.id.tv_cityname)
    TextView tvCityname;
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
        View view = inflater.inflate(R.layout.share_pager_0, container, false);
        unbinder = ButterKnife.bind(this, view);
        card = (CardView) view;

        Glide.with(this).load(R.drawable.ic_left_pager).into(imgPager);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        tvDate.setText(format.format(date));

        WeatherEntity weatherEntity = new Gson().fromJson(mCityWeaInfo.getJsonString(), WeatherEntity.class);

        String temperature = weatherEntity.getHeWeather6().get(0).getNow().getTmp();
        tvTemperature.setText(temperature + "°");

        String weaInfo = weatherEntity.getHeWeather6().get(0).getNow().getCond_txt();
        String cityname = weatherEntity.getHeWeather6().get(0).getBasic().getLocation();
        String content = cityname + "·" + weaInfo;
        SpannableString contentSpan = new SpannableString(content);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#fbf006"));
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#ee95ae"));
        contentSpan.setSpan(colorSpan, 0, cityname.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        contentSpan.setSpan(colorSpan1, cityname.length()+1, content.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvCityname.setText(contentSpan);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
