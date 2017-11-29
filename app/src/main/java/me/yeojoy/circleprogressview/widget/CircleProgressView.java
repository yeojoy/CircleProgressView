package me.yeojoy.circleprogressview.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import me.yeojoy.circleprogressview.R;

/**
 * Created by yeojoy on 2017. 11. 28..
 */

public class CircleProgressView extends View {
    private static final String TAG = CircleProgressView.class.getSimpleName();

    private static final float DEFAULT_SHADOW_SIZE = 10f;
    private static final int DEFAULT_STYLE = 0; /* Default style >> STROKE */
    private static final long DEFAULT_ANIMTION_DURATION = 1000L; /* Default style >> STROKE */

    private float mLineWidth, mBgLineWidth;

    private int mPercent;
    private int mCircleColor, mBackgroundCircleColor;
    private Paint mPaint, mBackgroundPaint;

    private RectF mRectF;

    private boolean mHasShadow;
    private int mStyle;

    private long mAnimationDuration;
    private boolean mHasAnimation;

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initView(attrs);
    }

    private void initView(AttributeSet attributeSet) {
        if (attributeSet == null) {
            return;
        }

        Context context = getContext();
        Resources resources = context.getResources();
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CircleProgressView);

        mHasShadow = typedArray.getBoolean(R.styleable.CircleProgressView_has_shadow, false);
        mLineWidth = typedArray.getDimensionPixelSize(R.styleable.CircleProgressView_line_width,
                resources.getDimensionPixelSize(R.dimen.default_line_width));
        mBgLineWidth = typedArray.getDimensionPixelSize(R.styleable.CircleProgressView_background_line_width,
                resources.getDimensionPixelSize(R.dimen.default_background_line_width));

        mCircleColor = typedArray.getColor(R.styleable.CircleProgressView_line_color,
                ContextCompat.getColor(context, R.color.default_line_color));
        mBackgroundCircleColor = typedArray.getColor(R.styleable.CircleProgressView_background_line_color,
                ContextCompat.getColor(context, R.color.default_background_line_color));

        mStyle = typedArray.getInt(R.styleable.CircleProgressView_style, DEFAULT_STYLE);

        mHasAnimation = typedArray.getBoolean(R.styleable.CircleProgressView_has_animation, true);
        mAnimationDuration = typedArray.getInt(R.styleable.CircleProgressView_animation_duration, (int) DEFAULT_ANIMTION_DURATION);

        // Background line width가 크다면 line width로 변경
        if (mLineWidth > mBgLineWidth) {
            mLineWidth = mBgLineWidth;
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mCircleColor);
        mPaint.setStyle(mStyle == DEFAULT_STYLE ? Paint.Style.STROKE : Paint.Style.FILL);
        mPaint.setStrokeWidth(mLineWidth);
        // 선 끝을 둥글게 만듬
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundCircleColor);
        mBackgroundPaint.setStyle(mStyle == DEFAULT_STYLE ? Paint.Style.STROKE : Paint.Style.FILL);
        mBackgroundPaint.setStrokeWidth(mBgLineWidth);
        mBackgroundPaint.setShadowLayer(3f, DEFAULT_SHADOW_SIZE, DEFAULT_SHADOW_SIZE,
                Color.parseColor("#80330000"));

        mPercent = typedArray.getInt(R.styleable.CircleProgressView_value, 0);

        // Shadow가 있을 때 추가 함.
        setLayerType(mHasShadow ? View.LAYER_TYPE_SOFTWARE : View.LAYER_TYPE_NONE, null);
        animateCircleProgress(mPercent);
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
        canvas.drawArc(mRectF, 270, 360, mStyle != DEFAULT_STYLE, mBackgroundPaint);
        canvas.drawArc(mRectF, 270, sweepDegree, mStyle != DEFAULT_STYLE, mPaint);

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

        mRectF.left = 0 + (mBgLineWidth / 2) + (mHasShadow ? DEFAULT_SHADOW_SIZE : 0);

        mRectF.top = 0 + (mBgLineWidth / 2) + (mHasShadow ? DEFAULT_SHADOW_SIZE : 0);

        mRectF.right = right - left - mRectF.left - (mBgLineWidth / 2) - (mHasShadow ? DEFAULT_SHADOW_SIZE : 0);

        mRectF.bottom = bottom - top - (mBgLineWidth / 2) - (mHasShadow ? DEFAULT_SHADOW_SIZE : 0);

        Log.d(TAG, "onLayout() > mRectF >>> " + mRectF.toString());
    }

    public void animateCircleProgress(final int percent) {
        Log.i(TAG, "percent : " + percent);

        mPercent = 0;
        invalidate();

        postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run()");
                if (mHasAnimation) {
                    final long time = (long) (mAnimationDuration * percent * 0.01);
                    Log.d(TAG, "Animation Time : " + time);
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
                } else {
                    mPercent = percent;
                    invalidate();
                }
            }
        }, 300L);
    }
}
