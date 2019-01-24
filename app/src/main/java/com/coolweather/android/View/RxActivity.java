package com.coolweather.android.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;
import com.coolweather.android.gson.MovieEntity;
import com.coolweather.android.utils.HttpWeatherMethod;
import com.coolweather.android.utils.ToastUtil;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_send)
    public void onViewClicked() {
        getMovie();
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
                ToastUtil.showToast(RxActivity.this, "成功获取Top,i=" + i++, Toast.LENGTH_SHORT);

            }
        };
        HttpWeatherMethod.getHttpWeatherMethod()
                         .getTopMovie(observer, 0, 10);

    }
}
