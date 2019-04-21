package com.fourweather.learn.View;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fourweather.learn.R;
import com.fourweather.learn.entity.CityWeaInfo;
import com.fourweather.learn.utils.DensityUtil;
import com.fourweather.learn.utils.SharePageTransformer;
import com.fourweather.learn.utils.SharePagerAdapter;
import com.fourweather.learn.utils.ToastUtil;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ShareActivity extends AppCompatActivity {

    @BindView(R.id.tv_pager_indicator)
    TextView tvPagerIndicator;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.share_pager)
    ViewPager sharePager;
    @BindView(R.id.tv_share)
    TextView tvShare;
    @BindView(R.id.img_loading)
    ImageView imgLoading;

    private static final String TAG = "ShareActivity";
    public static final String SHARE_CID = "share_cid";
    private static final int SHARE_REQUEST_CODE = 1;

    // 分享图片缓存文件夹，之前使用的是 support库中的FileProvider,并且图片目录是 ExternalStorage下的Pictures系统目录
    // 切换到andoridx库之后，发现以前那个目录老是不行，所以就换了 InternalStorage下的自定义目录
    public static final String SHARED_CARD_FILEPATH = APP.getContext().getCacheDir().getAbsolutePath() + "/SharedPictures";
    public static final int PAGER_COUNT = 2;
    private List<PagerFrag> mPagerFragList = new ArrayList<>();
    SharePagerAdapter mAdapter;
    String mCid;
    private int curPos = 0;
    private boolean isSharing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(imgLoading, "rotation", 360);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.setDuration(1000);
        rotateAnim.start();

        mCid = getIntent().getStringExtra(SHARE_CID);
        Log.i(TAG, "init: mCid = " + mCid);
        List<CityWeaInfo> cityWeaInfos = LitePal.where("cid like ?", mCid).find(CityWeaInfo.class);
        if (cityWeaInfos.size() != 0) {
            PagerFrag.mCityWeaInfo = cityWeaInfos.get(0);
        }

        initPagerFragList();
        mAdapter = new SharePagerAdapter(getSupportFragmentManager(), mPagerFragList);
        sharePager.setAdapter(mAdapter);
        sharePager.setPageTransformer(true, new SharePageTransformer());
        sharePager.setOffscreenPageLimit(1);
        sharePager.setPageMargin(DensityUtil.dip2px(APP.getContext(), 10));
        sharePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curPos = position;
                int userPos = position + 1;
                tvPagerIndicator.setText(String.format("%d/2", userPos));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initPagerFragList() {
        for (int pos = 0; pos < PAGER_COUNT; pos++) {
            mPagerFragList.add(PagerFrag.getInstance(pos));
        }
    }

    @OnClick(R.id.tv_share)
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onViewClicked() {
        // 同时执行一个分享任务
        if (isSharing) {
            return;
        }

        isSharing = true;
        imgLoading.setVisibility(View.VISIBLE);
        final String filename = "four_weather_share.png";

        PagerFrag.saveBitmapToSDCard(new PagerFrag.onSavedToSDCardListener() {
            @Override
            public void onSuccess() {
                isSharing = false;
                File file = new File(SHARED_CARD_FILEPATH + "/" + filename);
                final Uri shareUri;
                if (!file.exists()) {
                    return;
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    shareUri = Uri.fromFile(file);
                } else {
                    shareUri = FileProvider.getUriForFile(APP.getContext(), "com.fourweather.learn.fileprovider", file);
                }
                Log.i(TAG, "onViewClicked: file = " + file.getAbsolutePath());
                Log.i(TAG, "onViewClicked: shareUri = " + shareUri);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                intent.putExtra(Intent.EXTRA_STREAM, shareUri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, "分享至"), SHARE_REQUEST_CODE);

            }

            @Override
            public void onFailed() {
                isSharing = false;
                ToastUtil.showToast(APP.getContext(), "分享失败！", Toast.LENGTH_SHORT);
            }
        }, PagerFrag.getViewBitmap(mPagerFragList.get(curPos).getView()), SHARED_CARD_FILEPATH, filename);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        imgLoading.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SHARE_REQUEST_CODE:
                File shareFile = new File(SHARED_CARD_FILEPATH + "/four_weather_share.png");
                if (shareFile.exists()) {
                    shareFile.delete();
                }
                break;
        }
    }
}
