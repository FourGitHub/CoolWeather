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

public class ShowLifestyleActivity extends AppCompatActivity {

    private TextView toolbarTitleText;
    private TextView brefText;
    private TextView infoText;
    private ImageView imageView;
    private Intent intent;
    private Toolbar toolbar;
    private static Context mContext;

    public static Intent sendInfo(Context context, int imageId, String title, String bref, String info) {
        Intent i = new Intent(context, ShowLifestyleActivity.class);
        i.putExtra("iamge_id", imageId);
        i.putExtra("bref_text", bref);
        i.putExtra("info_text", info);
        i.putExtra("toolbar_title", title);
        mContext = context;
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.toolbar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbarTitleText = (TextView) findViewById(R.id.toolbar_title);
        brefText = (TextView) findViewById(R.id.lifestyle_bref_text);
        infoText = (TextView) findViewById(R.id.lifestyle_info_text);
        imageView = (ImageView) findViewById(R.id.lifestyle_image);
        intent = getIntent();
        showLifestyle();
    }


    private void showLifestyle() {
        intent = getIntent();
        String bref = intent.getStringExtra("bref_text");
        String info = intent.getStringExtra("info_text");
        String title = intent.getStringExtra("toolbar_title");
        int imageId = intent.getIntExtra("iamge_id", -1);
        toolbarTitleText.setText(title);
        brefText.setText(bref);
        infoText.setText(info);
        Glide.with(mContext).load(imageId).into(imageView);
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
