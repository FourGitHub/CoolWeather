package com.coolweather.android.View;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.coolweather.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowLifestyleActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.lifestyle_bref_text)
    TextView lifestyleBrefText;
    @BindView(R.id.lifestyle_info_text)
    TextView lifestyleInfoText;
    @BindView(R.id.lifestyle_image)
    ImageView lifestyleImage;

    public static Intent sendInfo(Context context, int imageId, String title, String bref, String info) {
        Intent i = new Intent(context, ShowLifestyleActivity.class);
        i.putExtra("iamge_id", imageId);
        i.putExtra("bref_text", bref);
        i.putExtra("info_text", info);
        i.putExtra("toolbar_title", title);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_show_lifestyle);
        ButterKnife.bind(this);
        toolbar.setNavigationIcon(R.drawable.toolbar_back);
        toolbar.setNavigationOnClickListener(v -> finish());
        showLifestyle();
    }

    private void showLifestyle() {
        Intent intent = getIntent();
        String bref = intent.getStringExtra("bref_text");
        String info = intent.getStringExtra("info_text");
        String title = intent.getStringExtra("toolbar_title");
        int imageId = intent.getIntExtra("iamge_id", -1);
        toolbarTitle.setText(title);
        lifestyleBrefText.setText(bref);
        lifestyleInfoText.setText(info);
        Glide.with(this).load(imageId).into(lifestyleImage);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}
