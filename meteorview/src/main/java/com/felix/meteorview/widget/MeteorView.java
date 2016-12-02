package com.felix.meteorview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.util.Random;

/**
 * @author Felix
 */
public class MeteorView extends ImageView {

    private static final float C = 0.551915024494f;
    private float mRadius;
    private float mMaxDistance;
    private int mNumberOfMeteors;
    private int mColorMeteors;
    private long mSleepTime;
    private Paint mPaint;
    private float[] mX;
    private float[] mY;
    private float[] mOffsetX;
    private float[] mOffsetY;
    private float[] mOffset;
    private float mAngle;
    private PointF[][] mDataPoints;
    private PointF[][] mCtrlPoints;
    private float mCtrl;
    private UpdateMeteorThread[] mThreads;
    private Handler mHandler = new Handler();
    private int mWidth;
    private int mHeight;
    private Path mPath;
    private Random mRandom = new Random();
    private int mMeteorT;
    private int mMeteorR;
    private int mMeteorB;
    private int mMeteorL;

    public MeteorView(Context context) {
        super(context);
    }

    public MeteorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MeteorView);
        mRadius = array.getDimension(R.styleable.MeteorView_meteorRadius, 3);
        mMaxDistance = array.getDimension(R.styleable.MeteorView_maxDistance, 400);
        mNumberOfMeteors = array.getInteger(R.styleable.MeteorView_numberOfMeteors, 20);
        mColorMeteors = array.getColor(R.styleable.MeteorView_meteorColor, 0x990772A1);
        mAngle = array.getFloat(R.styleable.MeteorView_meteorAngle, 40);
        mSleepTime = array.getInteger(R.styleable.MeteorView_speed, 20);
        mMeteorT = array.getDimensionPixelSize(R.styleable.MeteorView_meteorOffsetTop, 0);
        mMeteorR = array.getDimensionPixelSize(R.styleable.MeteorView_meteorOffsetRight, 0);
        mMeteorB = array.getDimensionPixelSize(R.styleable.MeteorView_meteorOffsetBottom, 0);
        mMeteorL = array.getDimensionPixelSize(R.styleable.MeteorView_meteorOffsetLeft, 0);
        array.recycle();
        setClickable(true);
        initFields();
        initPaints();
        initCoordinates();
    }

    private void initFields() {
        mX = new float[mNumberOfMeteors];
        mY = new float[mNumberOfMeteors];
        for (int i = 0; i < mNumberOfMeteors; i++) {
            mX[i] = -999;
            mY[i] = -999;
        }
        mOffsetX = new float[mNumberOfMeteors];
        mOffsetY = new float[mNumberOfMeteors];
        mOffset = new float[mNumberOfMeteors];
        mDataPoints = new PointF[mNumberOfMeteors][4];
        mCtrlPoints = new PointF[mNumberOfMeteors][8];
        mThreads = new UpdateMeteorThread[mNumberOfMeteors];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        startThreads();
    }

    private void startThreads() {
        for (int i = 0; i < mThreads.length; i++) {
            if (mThreads[i] == null) {
                mThreads[i] = new UpdateMeteorThread(i);
                final int finalI = i;
                long delayTime = finalI == 0 ? 0 : mRandom.nextInt(1000);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mThreads[finalI].start();
                    }
                }, delayTime);
            }
        }
    }

    private void initCoordinates() {
        for (int i = 0; i < mNumberOfMeteors; i++) {
            mDataPoints[i] = new PointF[4];
            mDataPoints[i][0] = new PointF(0, -mRadius);
            mDataPoints[i][1] = new PointF(mRadius, 0);
            mDataPoints[i][2] = new PointF(0, mRadius);
            mDataPoints[i][3] = new PointF(-mRadius, 0);
            mCtrlPoints[i] = new PointF[8];
            mCtrl = C * mRadius;
            mCtrlPoints[i][0] = new PointF(mCtrl, -mRadius);
            mCtrlPoints[i][1] = new PointF(mRadius, -mCtrl);
            mCtrlPoints[i][2] = new PointF(mRadius, mCtrl);
            mCtrlPoints[i][3] = new PointF(mCtrl, mRadius);
            mCtrlPoints[i][4] = new PointF(-mCtrl, mRadius);
            mCtrlPoints[i][5] = new PointF(-mRadius, mCtrl);
            mCtrlPoints[i][6] = new PointF(-mRadius, -mCtrl);
            mCtrlPoints[i][7] = new PointF(-mCtrl, -mRadius);
        }
    }

    private void setPoints() {
        for (int i = 0; i < mNumberOfMeteors; i++) {
            float cOffsetX = mOffset[i] * 24 / 25;
            float cOffsetY = mOffset[i] / 200;
            mDataPoints[i][0].x = 0 + cOffsetX;
            mDataPoints[i][0].y = -mRadius;
            mCtrlPoints[i][0].x = mCtrl + cOffsetX;
            mCtrlPoints[i][0].y = -mRadius;
            mCtrlPoints[i][1].x = mRadius + mOffset[i];
            mCtrlPoints[i][1].y = -mCtrl;
            mDataPoints[i][1].x = mRadius + mOffset[i];
            mDataPoints[i][1].y = 0;
            mCtrlPoints[i][2].x = mRadius + mOffset[i];
            mCtrlPoints[i][2].y = mCtrl;
            mCtrlPoints[i][3].x = mCtrl + cOffsetX;
            mCtrlPoints[i][3].y = mRadius;
            mDataPoints[i][2].x = 0 + cOffsetX;
            mDataPoints[i][2].y = mRadius;
            mCtrlPoints[i][4].x = -mCtrl + cOffsetX;
            mCtrlPoints[i][4].y = mRadius;
            mCtrlPoints[i][5].x = -mRadius;
            mCtrlPoints[i][5].y = mCtrl - cOffsetY;
            mDataPoints[i][3].x = -mRadius;
            mDataPoints[i][3].y = 0;
            mCtrlPoints[i][6].x = -mRadius;
            mCtrlPoints[i][6].y = -mCtrl + cOffsetY;
            mCtrlPoints[i][7].x = -mCtrl + cOffsetX;
            mCtrlPoints[i][7].y = -mRadius;
        }
    }

    @SuppressWarnings("deprecation")
    private void initPaints() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColorMeteors);
        mPaint.setStyle(Paint.Style.FILL);
    }

    private void reset(int index) {
        mX[index] = -999;
        mY[index] = -999;
        mOffsetX[index] = 0;
        mOffsetY[index] = 0;
        mOffset[index] = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mNumberOfMeteors; i++) {
            if (mY[i] == -999) return;
            canvas.save();
            canvas.translate(mX[i], mY[i]);
            canvas.rotate(mAngle);
            canvas.drawPath(getPath(i), mPaint);
            canvas.restore();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopThread();
        super.onDetachedFromWindow();
    }

    private void stopThread() {
        for (int i = 0; i < mThreads.length; i++) {
            if (mThreads[i] != null) {
                mThreads[i].interrupt();
                mThreads[i] = null;
            }
        }
    }

    private Path getPath(int index) {
        setPoints();
        if (mPath == null)
            mPath = new Path();
        else mPath.reset();
        mPath.moveTo(mDataPoints[index][0].x, mDataPoints[index][0].y);
        mPath.cubicTo(mCtrlPoints[index][0].x, mCtrlPoints[index][0].y, mCtrlPoints[index][1].x, mCtrlPoints[index][1].y, mDataPoints[index][1].x, mDataPoints[index][1].y);
        mPath.cubicTo(mCtrlPoints[index][2].x, mCtrlPoints[index][2].y, mCtrlPoints[index][3].x, mCtrlPoints[index][3].y, mDataPoints[index][2].x, mDataPoints[index][2].y);
        mPath.cubicTo(mCtrlPoints[index][4].x, mCtrlPoints[index][4].y, mCtrlPoints[index][5].x, mCtrlPoints[index][5].y, mDataPoints[index][3].x, mDataPoints[index][3].y);
        mPath.cubicTo(mCtrlPoints[index][6].x, mCtrlPoints[index][6].y, mCtrlPoints[index][7].x, mCtrlPoints[index][7].y, mDataPoints[index][0].x, mDataPoints[index][0].y);
        mPath.close();
        return mPath;
    }

    private class UpdateMeteorThread extends Thread {

        private final int mIndex;

        UpdateMeteorThread(int index) {
            mIndex = index;
        }

        @Override
        public void run() {
            outer:
            while (true) {
                mX[mIndex] = (float) (mRandom.nextInt(mWidth - mMeteorL + mMeteorR)
                        + mMeteorL);
                mY[mIndex] = (float) (mRandom.nextInt(mHeight + mMeteorT - mMeteorB)
                        - mMeteorT);
                mOffsetX[mIndex] = 0;
                mOffsetY[mIndex] = 0;
                Log.d("test", "x=" + mX[mIndex] + ", y=" + mY[mIndex]);
                while (mOffset[mIndex] <= mMaxDistance) {
                    try {
                        mOffsetX[mIndex] += 10;
                        mOffsetY[mIndex] += 10;
                        mOffset[mIndex] = (float) Math.hypot(mOffsetX[mIndex], mOffsetY[mIndex]);
                        postInvalidate();
                        Thread.sleep(mSleepTime);
                    } catch (InterruptedException e) {
                        break outer;
                    }
                }
                reset(mIndex);
                postInvalidate();
            }
        }
    }
}
