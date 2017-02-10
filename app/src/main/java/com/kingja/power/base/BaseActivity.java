package com.kingja.power.base;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.kingja.power.util.ActivityManager;
import com.kingja.power.util.ToastUtil;
import com.kingja.power.util.ZeusManager;


public abstract class BaseActivity extends FragmentActivity implements ZeusManager.OnPermissionCallback {
    protected String TAG = getClass().getSimpleName();
    protected ProgressDialog mProgressDialog;
    protected FragmentManager mSupportFragmentManager;
    protected ZeusManager mZeusManager;
    protected static final String[] permissionArr = {Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mSupportFragmentManager = getSupportFragmentManager();
        ActivityManager.getAppManager().addActivity(this);
        setContentView(getContentView());
        initConmonView();
        initVariables();
        initView();
        initNet();
        initData();
        setData();
    }


    private void initConmonView() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading");

        mZeusManager = new ZeusManager(this);
        mZeusManager.setOnPermissionCallback(this);
    }

    protected abstract void initVariables();

    protected abstract int getContentView();

    protected abstract void initView();

    protected abstract void initNet();

    protected abstract void initData();

    protected abstract void setData();


    protected void setProgressDialog(boolean show) {
        if (show) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mZeusManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClose() {
        ToastUtil.showToast("权限未允许，自动关闭页面");
        finish();
    }

    @Override
    public void onAllow() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getAppManager().finishActivity(this);
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
