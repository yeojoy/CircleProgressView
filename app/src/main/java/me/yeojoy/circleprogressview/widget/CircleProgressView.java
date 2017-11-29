package me.yeojoy.circleprogressview.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by yeojoy on 2017. 11. 28..
 */

public class CircleProgressView extends View {
    private static final String TAG = CircleProgressView.class.getSimpleName();

    private float mLineWidth, mBgLineWidth;

    private int mPercent;
    private int mCircleColor, mBackgroundCircleColor;
    private Paint mPaint, mBackgroundPaint;

    private RectF mRectF, mLineRectF;

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initView(attrs);
    }

    private void initView(AttributeSet attributeSet) {
        if (attributeSet == null) {

        }

        mPercent = 0;

        mLineWidth = getContext().getResources().getDisplayMetrics().density * 3;
        mBgLineWidth = getContext().getResources().getDisplayMetrics().density * 5;
        mCircleColor = Color.RED;
        mBackgroundCircleColor = Color.BLACK;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mCircleColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundCircleColor);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(mBgLineWidth);
        mBackgroundPaint.setShadowLayer(3f, 10f, 10f, Color.parseColor("#80330000"));

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mPercent = 40;
//        animateCircleProgress(90);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw()");
        int cX = getWidth() / 2;
        int cY = getHeight() / 2;

        float sweepDegree;
        if (mPercent == 0) {
            sweepDegree = 0;
        } else {
            sweepDegree = (float) (mPercent * 360 * 0.01);
        }

//        canvas.drawCircle(cX, cY, cX, mBackgroundPaint);
        canvas.drawArc(mRectF, 270, 360, false, mBackgroundPaint);
        canvas.drawArc(mRectF, 270, sweepDegree, false, mPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "onMeasure()");
        int measuredWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int measuredHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        Log.d(TAG, "measuredWidth : " + measuredWidth + ", measuredHeight : " + measuredHeight);

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.i(TAG, "onLayout(), changed : " + changed + ", left : " + left + ", top : " + top + ", right : " + right + ", bottom : " + bottom);

        if (mRectF == null) {
            mRectF = new RectF();
        }

        if (mLineRectF == null) {
            mLineRectF = new RectF();
        }

        mRectF.left = 0 + (mBgLineWidth / 2) + 10;
//        mLineRectF.left = mRectF.left + ((mBgLineWidth - mLineWidth) / 2);

        mRectF.top = 0 + (mBgLineWidth / 2) + 10;
//        mLineRectF.top = mRectF.top + ((mBgLineWidth - mLineWidth) / 2);

        mRectF.right = right - left - mRectF.left - (mBgLineWidth / 2) - 10;
//        mLineRectF.right = mRectF.right - ((mBgLineWidth - mLineWidth) / 2);

        mRectF.bottom = bottom - top - (mBgLineWidth / 2) - 10;
//        mLineRectF.bottom = mRectF.bottom - ((mBgLineWidth - mLineWidth) / 2);

        Log.d(TAG, "onLayout() > mRectF >>> " + mRectF.toString());
//        Log.d(TAG, "onLayout() > mLineRectF >>> " + mLineRectF.toString());
    }

    public void animateCircleProgress(final int percent) {
        Log.i(TAG, "percent : " + percent);

        mPercent = 0;
        invalidate();

        final long time = (long) (800L * percent * 0.01);
        Log.d(TAG, "Animation Time : " + time);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run()");
                ValueAnimator valueAnimator = ValueAnimator.ofInt(0, percent);
                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                valueAnimator.setDuration(time);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int currentValue = ((int) animation.getAnimatedValue());
                        mPercent = currentValue;
                        invalidate();
                    }
                });
                valueAnimator.start();
            }
        }, 300L);
    }
}
