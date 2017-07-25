package com.kingja.power.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 项目名称：
 * 类描述：TODO
 * 创建人：KingJA
 * 创建时间：2016/6/15 9:54
 * 修改备注：
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler mCrashHandler;
    public static final String LOG_DIR = "PowerManager";
    public static final String LOG_FILENAME = "CrashLogs.txt";
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context context;
    private File mLogDir;

    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        if (mCrashHandler == null) {
            synchronized (CrashHandler.class) {
                if (mCrashHandler == null) {
                    mCrashHandler = new CrashHandler();
                }
            }
        }
        return mCrashHandler;
    }

    public void init(Context context) {
        this.context = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        Log.e("uncaughtException: ", "uncaughtException: ");
        ex.printStackTrace();
        savaToSdCard(ex);
        uploadToService(ex);
        ToastUtil.showToast("很抱歉，程序遭遇异常，即将重启");
        try {
            thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ActivityManager.getAppManager().finishAllActivity();//避免前台的其他APP被关闭
        android.os.Process.killProcess(android.os.Process.myPid());
    }



    /**
     * 上传异常信息到服务器
     *
     * @param ex
     */
    private void uploadToService(Throwable ex) {

    }

    /**
     * 保存异常信息到本地
     *
     * @param ex
     */
    private void savaToSdCard(Throwable ex) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mLogDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + LOG_DIR);
        } else {
            mLogDir = new File(context.getFilesDir().getAbsolutePath() + File.separator + LOG_DIR);
        }
        if (!mLogDir.exists()) {
            mLogDir.mkdirs();
        }
        File logFile = new File(mLogDir, LOG_FILENAME);
        PrintWriter pw;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
            pw.println(getFormaTime());
            pw.println("===================");
            pw.println("Crash Thread:     " + Thread.currentThread().getName());
            pw.println("Phone Info:       " + android.os.Build.MODEL + ","
                    + android.os.Build.VERSION.SDK_INT + ","
                    + android.os.Build.VERSION.RELEASE + ","
                    + android.os.Build.CPU_ABI);
            pw.println(getVersionInfo(context));
            ex.printStackTrace(pw);
            pw.close();
        } catch (IOException e) {
            Log.e("CrashHandler", "save file failed...  ", e.getCause());

        }
    }

    /**
     * 获取异常信息
     *
     * @param ex
     * @return
     */
    @NonNull
    private String getExceptionInfo(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(getFormaTime() + "\n");
        sb.append("===================" + "\n");
        sb.append("Phone Model: " + android.os.Build.MODEL + ","
                + android.os.Build.VERSION.SDK_INT + ","
                + android.os.Build.VERSION.RELEASE + ","
                + android.os.Build.CPU_ABI + "\n");
        sb.append("Thread:  " + Thread.currentThread().getName() + "\n");
        sb.append(getVersionInfo(context) + "\n");
        sb.append(ex.toString() + "\n");
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace != null) {
            for (int i = 0; i < stackTrace.length; i++) {
                sb.append("\tat  " + stackTrace[i].toString() + "\n");
            }
        }
        Throwable causeThrowable = ex.getCause();
        if (causeThrowable != null) {
            StackTraceElement[] causeElement = causeThrowable.getStackTrace();
            sb.append("\tCaused by: " + causeThrowable.toString() + "\n");
            for (int i = 0; i < causeElement.length; i++) {
                sb.append("\tat  " + causeElement[i].toString() + "\n");
            }
        }
        return sb.toString();
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    private String getVersionInfo(Context context) {
        PackageInfo pi;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return "Application Info: " + pi.packageName + ",VersionCode:" + pi.versionCode + ",VersionName:" + pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "版本号:未知\t版本号:未知";
    }

    /**
     * 格式化日期
     *
     * @return
     */
    public String getFormaTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

}
