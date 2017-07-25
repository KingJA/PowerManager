package com.kingja.power.activity;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.kingja.power.R;
import com.kingja.power.base.BackTitleActivity;

/**
 * Description：子蓄电池显示
 * Create Time：2017/2/10 13:51
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class PowerChildDisplayActivity extends BackTitleActivity {
    private TextView mTvCurrentPower;
    private TextView mTvChargerStatus;
    private TextView mTvHealthStatus;
    private TextView mTvDeviceId;
    private TextView mTvProduceTime;
    private TextView mTvTestTime;
    private TextView mTvTemperature;
    private String chargerStatus;
    private String healthStatus;
    private String temperature;
    private String deviceId;
    private String produceTime;
    private String testTime;
    private String power;

    @Override
    protected void initVariables() {
//        chargerStatus = getIntent().getStringExtra("chargerStatus");
//        healthStatus = getIntent().getStringExtra("healthStatus");
//        temperature = getIntent().getStringExtra("temperature");
//        deviceId = getIntent().getStringExtra("deviceId");
//        produceTime = getIntent().getStringExtra("produceTime");
//        testTime = getIntent().getStringExtra("testTime");
//        power = getIntent().getStringExtra("power");
    }

    @Override
    protected String setTitle() {
        return "子蓄电池";
    }

    @Override
    protected void initContentView() {
        mTvCurrentPower = (TextView) findViewById(R.id.tv_currentPower);
        mTvChargerStatus = (TextView) findViewById(R.id.tv_charger_status);
        mTvHealthStatus = (TextView) findViewById(R.id.tv_health_status);
        mTvDeviceId = (TextView) findViewById(R.id.tv_deviceId);
        mTvProduceTime = (TextView) findViewById(R.id.tv_produceTime);
        mTvTestTime = (TextView) findViewById(R.id.tv_testTime);
        mTvTemperature = (TextView) findViewById(R.id.tv_temperature);
    }

    @Override
    protected int getBackContentView() {
        return R.layout.activity_child_display;
    }

    @Override
    protected void initNet() {

    }

    @Override
    protected void initData() {
//        mTvCurrentPower.setText(power+"%");
//        mTvChargerStatus.setText(chargerStatus);
//        mTvHealthStatus.setText(healthStatus);
//        mTvDeviceId.setText(deviceId);
//        mTvProduceTime.setText(produceTime);
//        mTvTestTime.setText(testTime);
//        mTvTemperature.setText(temperature);

        mTvCurrentPower.setText(75+"%");
        mTvChargerStatus.setText("未充满");
        mTvHealthStatus.setText("正常");
        mTvDeviceId.setText("100256");
        mTvProduceTime.setText("2017-07-20");
        mTvTestTime.setText("2017-07-22");
        mTvTemperature.setText("55");
    }

    @Override
    protected void setData() {
    }

    public static void goActivity(Activity activity, String power, String chargerStatus, String healthStatus, String temperature, String deviceId, String produceTime, String testTime) {
        Intent intent = new Intent(activity, PowerChildDisplayActivity.class);
        intent.putExtra("power", power);
        intent.putExtra("chargerStatus", chargerStatus);
        intent.putExtra("healthStatus", healthStatus);
        intent.putExtra("temperature", temperature);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("produceTime", produceTime);
        intent.putExtra("testTime", testTime);
        activity.startActivity(intent);
    }
}
