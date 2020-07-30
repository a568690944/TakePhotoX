/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.camerax.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;


/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/29
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public final class ScannerView extends View {
    private static final long ANIMATION_DELAY = 16L;
    private ScannerFrameOption mOptions;
    private int SCAN_VELOCITY = 6;

    private @ScannerFrameOption.FrameMode.Mode
    int mFrameMode = ScannerFrameOption.FrameMode.MODE_FRAME_SQUARE;

    private Point mFrameOffset;
    private float mFrameRatio = 0.6f;
    private int mBorderColor = 0xFFDDDDDD;
    private int mCornerColor = 0xFF0F94ED;

    private Rect mFrameRect;
    private Rect mScanLineRect;
    private int mScanLineTop;

    private int mWidth;
    private int mHeight;

    private Context mContext;
    private Paint mPaint;
    private Bitmap mScanLight;


    public ScannerView(Context context) {
        this(context, null);
    }

    public ScannerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mPaint = new Paint();
        mFrameRect = new Rect();
        mScanLineRect = new Rect();
        if (attrs != null) {
            getDefaultOptions();
        }

        // 扫描控件
        mScanLight = BitmapFactory.decodeResource(getResources(), R.drawable.scan_light);
        //setBackgroundColor(Color.RED);
    }

    public void setOptions(@NonNull ScannerFrameOption options) {
        mOptions = options;

        mFrameOffset = options.getFrameOffset();
        mCornerColor = options.getCornerColor();
        mBorderColor = options.getFrameColor();
    }

    private void getDefaultOptions() {
        mOptions = new ScannerFrameOption
                .Builder(ScannerFrameOption.FrameMode.MODE_FRAME_SQUARE)
                .frameColor(mBorderColor)
                .cornerColor(mCornerColor)
                .build();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.i("aaa", "*W=" + getWidth() + "  h=" + getHeight());
        mWidth = getWidth();
        mHeight = getHeight();

        int frameWidth = 0;
        int frameHeight = 0;

        switch (mOptions.getFrameMode()) {
            case ScannerFrameOption.FrameMode.MODE_FRAME_SAME_RATIO:
                frameWidth = (int) (mFrameRatio * mWidth);
                frameHeight = (int) (mFrameRatio * mHeight);
                break;
            case ScannerFrameOption.FrameMode.MODE_FRAME_SQUARE:
                int size = Math.min(mWidth, mHeight);
                frameWidth = (int) (mFrameRatio * size);
                frameHeight = (int) (mFrameRatio * size);
                break;
            case ScannerFrameOption.FrameMode.MODE_FRAME_FREE:
                frameWidth = mOptions.getFrameWidth();
                frameHeight = mOptions.getFrameHeight();
                break;
        }

        if (mFrameOffset == null) {
            int x = (mWidth - frameWidth) / 2;
            int y = (mHeight - frameHeight) / 2;
            mFrameRect.set(x, y, x + frameWidth, y + frameHeight);
        } else {
            mFrameRect.set(mFrameOffset.x, mFrameOffset.y, mFrameOffset.x + frameWidth, mFrameOffset.y + frameHeight);
        }

        postInvalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();

        drawScanMask(canvas);
        drawFrameBorder(canvas);
        drawCorner(canvas);
        drawScanLight(canvas);

        canvas.restore();

        postInvalidateDelayed(ANIMATION_DELAY, mFrameRect.left, mFrameRect.top, mFrameRect.right, mFrameRect.bottom);
    }


    private void drawScanMask(Canvas canvas) {
        Paint paint = mPaint;
        paint.setColor(0x88000000);
        Rect bg = new Rect(0, 0, mWidth, mHeight);

        Region regionBg = new Region(bg);
        Region regionFrame = new Region(mFrameRect);

        regionBg.op(regionFrame, Region.Op.XOR);//交集
        paint.setStyle(Paint.Style.FILL);
        drawRegion(canvas, regionBg, paint);
    }

    private void drawRegion(Canvas canvas, Region region, Paint paint) {
        RegionIterator iterator = new RegionIterator(region);
        Rect r = new Rect();
        while (iterator.next(r)) {
            canvas.drawRect(r, paint);
        }
    }

    private void drawCorner(Canvas canvas) {
        Paint paint = mPaint;
        paint.setColor(mCornerColor);
        paint.setStyle(Paint.Style.FILL);

        Rect frame = mFrameRect;

        int corWidth = dip2px(mContext, 3);
        int corLength = dip2px(mContext, 20);;

        frame.inset(-corWidth, -corWidth);

        // 左上角
        canvas.drawRect(frame.left, frame.top, frame.left + corWidth, frame.top  + corLength, paint);
        canvas.drawRect(frame.left, frame.top, frame.left + corLength, frame.top + corWidth, paint);
        // 右上角
        canvas.drawRect(frame.right - corWidth, frame.top, frame.right, frame.top + corLength, paint);
        canvas.drawRect(frame.right - corLength, frame.top, frame.right, frame.top + corWidth, paint);
        // 左下角
        canvas.drawRect(frame.left, frame.bottom - corLength, frame.left + corWidth, frame.bottom, paint);
        canvas.drawRect(frame.left, frame.bottom - corWidth, frame.left + corLength, frame.bottom, paint);
        // 右下角
        canvas.drawRect(frame.right - corWidth, frame.bottom - corLength, frame.right, frame.bottom, paint);
        canvas.drawRect(frame.right - corLength, frame.bottom - corWidth, frame.right, frame.bottom, paint);

        //restore
        frame.inset(corWidth, corWidth);
    }

    private void drawFrameBorder(Canvas canvas) {
        Paint paint = mPaint;
        int width = dip2px(mContext, 1);

        Rect frame = mFrameRect;
        frame.inset(-width, -width);

        paint.setColor(mBorderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);

        canvas.drawRect(mFrameRect, paint);

        //restore
        frame.inset(width, width);
    }

    /**
     * 绘制移动扫描线
     *
     * @param canvas
     */
    private void drawScanLight(Canvas canvas) {
        Rect frame = mFrameRect;
        int lineHeight = dip2px(mContext, 10);

        if (mScanLineTop == 0) {
            mScanLineTop = frame.top;
        }

        if (mScanLineTop >= frame.bottom - lineHeight) {
            mScanLineTop = frame.top;
        } else {
            mScanLineTop += SCAN_VELOCITY;
        }

        mScanLineRect.set(mFrameRect.left, mScanLineTop, frame.right,
                mScanLineTop + lineHeight);
        canvas.drawBitmap(mScanLight, null, mScanLineRect, null);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取扫描区域
     */
    public Rect getScanRect() {
        return mFrameRect;
    }
}