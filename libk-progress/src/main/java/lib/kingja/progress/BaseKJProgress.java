package lib.kingja.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public abstract class BaseKJProgress extends View {
    protected String TAG =getClass().getSimpleName();
    private static final int PROGRESS_MAX = 100;
    private static final int REACH_WIDTH = 4;
    private static final int REACH_COLOR = 0XFF3F51B5;
    private static final int UNREACH_COLOR = 0XFFC6C6C6;
    private static final int UNREACH_WIDTH = 4;
    private static final int PROGRESS_TEXT_SIZE = 14;
    protected int mProgress;
    protected int mProgressMax;
    protected float mReachWidth;
    protected float mUnreachWidth;
    protected float mProgressTextSize;
    protected int mReachColor;
    protected int mUnreachColor;
    protected int mProgressTextColor;
    protected float mWidth;
    protected int mHeight;
    protected int mSrokeCap;
    private OnProgressFinsihedListener onProgressFinsihedListener;


    public BaseKJProgress(Context context) {
        this(context, null);
    }

    public BaseKJProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseKJProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.KJProgress);
        mProgress=typedArray.getInteger(R.styleable.KJProgress_progress, 0);
        mProgressMax=typedArray.getInteger(R.styleable.KJProgress_progressMax, PROGRESS_MAX);
        mReachWidth = typedArray.getDimension(R.styleable.KJProgress_reachWidth, dp2px(REACH_WIDTH));
        mUnreachWidth = typedArray.getDimension(R.styleable.KJProgress_unreachWidth, dp2px(UNREACH_WIDTH));
        mProgressTextSize = typedArray.getDimension(R.styleable.KJProgress_progressTextSize, sp2px(PROGRESS_TEXT_SIZE));
        mProgressTextColor = typedArray.getColor(R.styleable.KJProgress_progressTextColor, REACH_COLOR);
        mReachColor = typedArray.getColor(R.styleable.KJProgress_reachColor, REACH_COLOR);
        mUnreachColor = typedArray.getColor(R.styleable.KJProgress_unreachColor, UNREACH_COLOR);
        mSrokeCap = typedArray.getInteger(R.styleable.KJProgress_strokeCap, 0);
        typedArray.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onAttached();
    }

    protected abstract void onAttached();

    protected abstract void initAttrs(Context context, AttributeSet attrs);

    protected abstract void initVariable();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        setBetterSize(w,h);
        initVariable();
    }

    protected abstract void setBetterSize(int width, int height);

    protected int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    protected int sp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, getResources().getDisplayMetrics());
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("BASE", super.onSaveInstanceState());
        bundle.putInt("PROGRESS", mProgress);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mProgress = bundle.getInt("PROGRESS");
            super.onRestoreInstanceState(bundle.getParcelable("BASE"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int mProgress) {
        if (mProgress < 0) {
            mProgress = 0;
        }
        if (mProgress > mProgressMax) {
            mProgress = mProgressMax;
        }
        this.mProgress = mProgress;
        invalidate();
        if (mProgress == mProgressMax&&onProgressFinsihedListener!=null) {
            onProgressFinsihedListener.onFinished();
        }
    }


    public void setProgressMax(int mProgressMax) {
        if (mProgressMax > 0) {
            this.mProgressMax = mProgressMax;
            invalidate();
        }

    }

    public interface OnProgressFinsihedListener{
        void onFinished();
    }

    public void setOnProgressFinsihedListener(OnProgressFinsihedListener onProgressFinsihedListener) {
        this.onProgressFinsihedListener = onProgressFinsihedListener;
    }
}
