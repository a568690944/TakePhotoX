package com.camerax.lib.analysis;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.camerax.lib.core.CameraOption;
import com.camerax.lib.core.CameraView;
import com.camerax.lib.core.ExAspectRatio;
import com.camerax.lib.core.OnFocusListener;
import com.camerax.lib.core.OnImgAnalysisListener;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：暂支持竖屏，横屏未适配
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/8/3
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class QRCodeView extends FrameLayout {
    private Context mContext;

    private CameraView mCameraView;
    private ScannerView mScannerViw;

    private CameraOption mCameraOption;
    private ScannerFrameOption mScannerFrameOption;

    public QRCodeView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public QRCodeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public QRCodeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public QRCodeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mCameraView = new CameraView(context, attrs);
        mScannerViw = new ScannerView(context, attrs);

        addView(mCameraView, params);
        addView(mScannerViw, params);
    }

    public void setCameraOptions(@NonNull CameraOption options) {
        mCameraOption = options;
    }

    public void setScannerFrameOption(@NonNull ScannerFrameOption options) {
        mScannerFrameOption = options;
    }

    public void startScan(LifecycleOwner lifecycle) {
        CameraOption option = mCameraOption != null ? mCameraOption :
                new CameraOption.Builder(ExAspectRatio.RATIO_16_9)
                .analysisImg(true)
                .build();
        mCameraView.initCamera(option, lifecycle);

        if (mScannerFrameOption != null) {
            mScannerViw.setOptions(mScannerFrameOption);
        }

        setCameraPreviewViewScale();
    }

    /**
     * 设置相机的缩放比例
     */
    protected void setCameraPreviewViewScale() {
        //默认相机分析图片是 4:3

        //屏幕比例
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float scrRatio = dm.widthPixels / (float)dm.heightPixels;
        float imgAnalysisRatio = 3.0f / 4;

        final float scale = imgAnalysisRatio / scrRatio;
        //TODO
    }

    public void setOnFocusListener(OnFocusListener listener) {
       mCameraView.setOnFocusListener(listener);
    }

    public void setOnImgAnalysisListener(OnImgAnalysisListener listener) {
        mCameraView.setOnImgAnalysisListener(listener);
    }

    /**
     * 获取预览view
     * @return
     */
    public Size getPreviewSize() {
        return mScannerViw.getPreviewSize();
    }

    /**
     * 获取扫描配置信息
     * @return
     */
    public ScannerFrameOption getOptions() {
        return mScannerViw.getOptions();
    }
}
