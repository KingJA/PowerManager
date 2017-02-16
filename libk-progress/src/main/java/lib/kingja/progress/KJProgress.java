package lib.kingja.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class KJProgress extends BaseKJProgress {
    private static final int PROGRESS_TEXT_MARGIN = 4;
    protected float mProgressTextMargin;


    private Paint mPaint;

    public KJProgress(Context context) {
        super(context);
    }

    public KJProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KJProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttached() {
        mPaint = new Paint();
        mPaint.setStrokeCap(mSrokeCap == 0 ? Paint.Cap.SQUARE : Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mProgressTextSize);
    }


    @Override
    protected void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.KJProgress);
        mProgressTextMargin = typedArray.getDimension(R.styleable.KJProgress_progressTextMargin, dp2px(PROGRESS_TEXT_MARGIN));
        typedArray.recycle();
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void setBetterSize(int width, int height) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            float textHeight = (mPaint.descent() - mPaint.ascent());
            int exceptHeight = (int) (getPaddingTop() + getPaddingBottom() + Math
                    .max(Math.max(mReachWidth,
                            mUnreachWidth), Math.abs(textHeight)));

            heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight,
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float offsetLeft = mSrokeCap == 1 ? mReachWidth * 0.5f : 0f;
        float offsetRight = mSrokeCap == 1 ? mReachWidth * 0.5f : 0f;
        float marginLeft = mProgressTextMargin;
        float marginRight = mProgressTextMargin;
        canvas.save();
        if (getProgress() == 0) {
            offsetLeft = 0;
        }
        canvas.translate(getPaddingLeft() + offsetLeft, mHeight * 0.5f);
        String progressText = (int)(getProgress()*100f/mProgressMax) + "%";
        float textWidth = mPaint.measureText(progressText);
        float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;

        mPaint.setColor(mReachColor);
        mPaint.setStrokeWidth(mReachWidth);


        float radio = getProgress() * 1.0f / mProgressMax;
        float progressX = radio * (mWidth - textWidth - mProgressTextMargin - offsetLeft * 0.5f - offsetRight);
        float reachX = progressX;
        if (reachX > 0) {
            canvas.drawLine(0, 0, reachX, 0, mPaint);
        }



        mPaint.setColor(mProgressTextColor);
        if (getProgress() == 0) {
            marginLeft = 0;
            offsetRight=offsetRight+marginLeft;
        }
        float textX = progressX + marginLeft;
        canvas.drawText(progressText, textX, -textHeight, mPaint);

        if (mProgress < mProgressMax) {
            mPaint.setColor(mUnreachColor);
            float unReachX = textX + textWidth + marginRight;
            canvas.drawLine(unReachX, 0, mWidth - 2.0f * offsetRight, 0, mPaint);
        }
        canvas.restore();
    }


}
