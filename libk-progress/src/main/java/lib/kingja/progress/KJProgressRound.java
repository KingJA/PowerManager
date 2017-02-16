package lib.kingja.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class KJProgressRound extends BaseKJProgress {
    private static final int DEFAULT_SIZE = 50;
    private float mMaxPaintWidth;
    private int mRadius;
    private Paint mUnreachPaint;
    private Paint mReachPaint;
    private Paint mTextPaint;
    private int mProgressStyle;

    public KJProgressRound(Context context) {
        super(context);
    }

    public KJProgressRound(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KJProgressRound(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.KJProgress);
        mRadius = (int) typedArray.getDimension(
                R.styleable.KJProgress_radius, dp2px(DEFAULT_SIZE));
        mProgressStyle = typedArray.getInteger(R.styleable.KJProgress_progressStyle, 0);
        typedArray.recycle();
    }

    @Override
    protected void onAttached() {

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMaxPaintWidth = Math.max(mReachWidth,mUnreachWidth);
        mProgressTextSize = Math.min(mRadius * 0.5f, mProgressTextSize);
        int expect = (int) (mRadius * 2 + mMaxPaintWidth) + getPaddingLeft() + getPaddingRight();
        int width = resolveSize(expect, widthMeasureSpec);
        int height = resolveSize(expect, heightMeasureSpec);
        int realWidth = Math.min(width, height);
        mRadius = (int) ((realWidth - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth) / 2);
        setMeasuredDimension(realWidth, realWidth);
    }

    @Override
    protected void setBetterSize(int width, int height) {
    }

    @Override
    protected void initVariable() {
        mUnreachPaint = new Paint();
        mUnreachPaint.setStyle(Paint.Style.STROKE);
        mUnreachPaint.setAntiAlias(true);
        mUnreachPaint.setColor(mUnreachColor);
        mUnreachPaint.setStrokeWidth(mUnreachWidth);

        mReachPaint = new Paint();
        mReachPaint.setStyle(mProgressStyle == 1 ? Paint.Style.FILL : Paint.Style.STROKE);
        mReachPaint.setStrokeCap(mSrokeCap == 0 ? Paint.Cap.SQUARE : Paint.Cap.ROUND);
        mReachPaint.setAntiAlias(true);
        mReachPaint.setDither(true);
        mReachPaint.setColor(mReachColor);
        mReachPaint.setStrokeWidth(mReachWidth);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(mProgressTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getPaddingLeft() + mMaxPaintWidth / 2, getPaddingTop()+ mMaxPaintWidth / 2);
        drawUnreachProgress(canvas);
        drawReachProgress(canvas, mProgressStyle == 1);
        drawProgressText(canvas, mProgressStyle == 1);
        canvas.restore();
    }

    private void drawUnreachProgress(Canvas canvas) {
        canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius * 2), -90, 360, false, mUnreachPaint);
    }

    private void drawReachProgress(Canvas canvas, boolean useCenter) {
        float paddingInside = useCenter ? mMaxPaintWidth : 0;
        float angle = getProgress() * 1.0f / mProgressMax * 360;
        canvas.drawArc(new RectF(paddingInside, paddingInside, mRadius * 2 - paddingInside, mRadius * 2 - paddingInside), -90, angle, useCenter, mReachPaint);
    }

    private void drawProgressText(Canvas canvas, boolean useCenter) {
        if (useCenter) {
            return;
        }
        String progressText = (int)(getProgress()*100f/mProgressMax) + "%";
        float textWidth = mTextPaint.measureText(progressText);
        float offsetY = -(mTextPaint.ascent() + mTextPaint.descent()) / 2;
        mTextPaint.setColor(mProgressTextColor);
        mTextPaint.setTextSize(mProgressTextSize);
        canvas.drawText(progressText, mRadius - textWidth * 0.5f, mRadius + offsetY, mTextPaint);
    }


}
