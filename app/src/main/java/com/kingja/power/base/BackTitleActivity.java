package com.kingja.power.base;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kingja.power.R;
import com.kingja.power.util.NoDoubleClickListener;

/**
 * Description：TODO
 * Create Time：2016/8/5 15:57
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public abstract class BackTitleActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout mRlTopRoot;
    private RelativeLayout mRlTopBack;
    private ImageView mIvTopBack;
    protected RelativeLayout mRlTopMenu;
    private TextView mTvTopTitle;
    private FrameLayout mFlContent;
    private OnMenuClickListener onMenuClickListener;
    private OnRightClickListener onRightClickListener;
    protected RelativeLayout mRlTopRight;
    private TextView mTvTopRight;
    private ImageView mIvTop_menu;


    @Override
    protected abstract void initVariables();

    @Override
    protected void initView() {
        mRlTopRoot = (RelativeLayout) findViewById(R.id.rl_top_root);
        mRlTopBack = (RelativeLayout) findViewById(R.id.rl_top_back);
        mIvTopBack = (ImageView) findViewById(R.id.iv_top_back);
        mRlTopMenu = (RelativeLayout) findViewById(R.id.rl_top_menu);
        mRlTopRight = (RelativeLayout) findViewById(R.id.rl_top_right);
        mTvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        mTvTopRight = (TextView) findViewById(R.id.tv_top_right);
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);
        mIvTop_menu = (ImageView) findViewById(R.id.iv_top_menu);

        mRlTopBack.setOnClickListener(this);
        mRlTopMenu.setOnClickListener(this);
        mRlTopRight.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (onRightClickListener != null) {
                    onRightClickListener.onRightClick();
                }
            }
        });
        View content = View.inflate(this, getBackContentView(), null);
        if (content != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            mFlContent.addView(content, params);
        }
        initContentView();

    }

    protected abstract void initContentView();


    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        mTvTopTitle.setText(title);
        mTvTopTitle.setVisibility(View.VISIBLE);
    }

    public void setLeftImg(int res) {
        if (res == -1) {
            mRlTopBack.setVisibility(View.GONE);
        }else{
            mIvTopBack.setBackgroundResource(res);
        }

    }



    /**
     * 设置菜单图标点击事件
     * @param onMenuClickListener 菜单点击监听器
     */
    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener,int res) {
        mIvTop_menu.setBackgroundResource(res);
        mRlTopMenu.setVisibility(View.VISIBLE);
        this.onMenuClickListener = onMenuClickListener;
    }

    /**
     * 设置右侧文字点击事件
     * @param onRightClickListener 右侧文字点击监听器
     */

    public void setOnRightClickListener(OnRightClickListener onRightClickListener,String rightText) {
        mRlTopRight.setVisibility(View.VISIBLE);
        mTvTopRight.setText(rightText);
        this.onRightClickListener = onRightClickListener;
    }

    public void setOnRightClickGone() {
        mRlTopRight.setVisibility(View.GONE);
    }




    /**
     * 菜单点击接口
     */
    public interface OnMenuClickListener {
        void onMenuClick();
    }

    /**
     * 右侧文字点击接口
     */
    public interface OnRightClickListener {
        void onRightClick();
    }

    /**
     * 获取内容页布局
     *
     * @return 布局文件ID
     */
    protected abstract int getBackContentView();

    /**
     * 初始化网络访问
     */
    @Override
    protected abstract void initNet();

    /**
     * 初始化数据，如设置事件
     */
    @Override
    protected abstract void initData();

    /**
     * 设置数据
     */
    @Override
    protected abstract void setData();


    @Override
    protected int getContentView() {
        return R.layout.activity_back;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_top_back:
                onClickBack();
                break;
            case R.id.rl_top_menu:
                if (onMenuClickListener != null) {
                    onMenuClickListener.onMenuClick();
                }
                break;
            default:
                break;
        }
    }

    protected void onClickBack() {
        finish();
    }
}
