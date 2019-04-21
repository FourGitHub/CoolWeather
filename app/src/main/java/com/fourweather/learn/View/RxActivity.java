package com.fourweather.learn.View;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.fourweather.learn.R;
import com.fourweather.learn.entity.MovieEntity;
import com.fourweather.learn.utils.DensityUtil;
import com.fourweather.learn.utils.HttpMovieService;
import com.fourweather.learn.utils.ToastUtil;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RxActivity extends AppCompatActivity {
    private static final String TAG = "RxActivity";

    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.img_nav)
    ImageView imgNav;
    @BindView(R.id.title_city)
    TextView titleCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_send,R.id.img_nav})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                getMovie();
                break;
            case R.id.img_nav:
                showPopWindow();
                break;
        }
    }

    private void getMovie() {
        Observer<MovieEntity> observer = new Observer<MovieEntity>() {
            private Disposable mDisposable;
            private int i;

            @Override
            public void onSubscribe(Disposable d) {
                d = mDisposable;
                Log.i(TAG, "onSubscribe: --->> " + i++);
            }

            @Override
            public void onNext(MovieEntity movieEntity) {
                tvResult.setText(movieEntity.getSubjects()
                                            .get(0)
                                            .getTitle());
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                ToastUtil.showToast(APP.getContext(), "成功获取Top,i=" + i++, Toast.LENGTH_SHORT);

            }
        };
        getSupportFragmentManager();
        HttpMovieService.getInstance()
                        .getTopMovie(observer, 0, 10);

    }

    private void showPopWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_pop_window, null);
        PopupWindow popupWindow = new PopupWindow();
        popupWindow.setWidth(DensityUtil.dip2px(this, 120));
        popupWindow.setHeight(DensityUtil.dip2px(this, 150));
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.showAsDropDown(imgNav, -DensityUtil.dip2px(this, 110), DensityUtil.dip2px(this, 5));
    }

}
