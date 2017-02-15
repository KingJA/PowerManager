package com.kingja.power.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.junkchen.blelib.BleListener;
import com.junkchen.blelib.BleService;
import com.kingja.power.R;
import com.kingja.power.base.BackTitleActivity;
import com.kingja.power.util.ByteUtil;
import com.kingja.power.util.GoUtil;
import com.kingja.power.convert.HeartConvert;

/**
 * Description：蓄电池显示
 * Create Time：2017/2/10 13:51
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class PowerDisplayActivity extends BackTitleActivity implements BleListener.OnDataAvailableListener{

    private BleService mBleService;

    private TextView mTvPower;
    private TextView mTvKilometre;
    private TextView mTvVoltage;
    private TextView mTvTemperature;
    private TextView mTvLifecycle;
    private ListView mLv;
    private TextView mTvChargerStatus;
    private TextView mTvHealthStatus;
    private TextView mTvLastChargeTime;


    @Override
    protected void onResume() {
        super.onResume();
        mBleService.setOnDataAvailableListener(this);
    }

    @Override
    protected void initVariables() {
        Log.e(TAG, "PowerDisplayActivity: " );
        mBleService = BleService.getInstance();
    }

    @Override
    protected String setTitle() {
        return "蓄电池";
    }

    @Override
    protected void initContentView() {
        mTvPower = (TextView) findViewById(R.id.tv_power);
        mTvKilometre = (TextView) findViewById(R.id.tv_kilometre);
        mTvVoltage = (TextView) findViewById(R.id.tv_voltage);
        mTvTemperature = (TextView) findViewById(R.id.tv_temperature);
        mTvLifecycle = (TextView) findViewById(R.id.tv_lifecycle);
        mLv = (ListView) findViewById(R.id.gv);
        mTvChargerStatus = (TextView) findViewById(R.id.tv_charger_status);
        mTvHealthStatus = (TextView) findViewById(R.id.tv_health_status);
        mTvLastChargeTime = (TextView) findViewById(R.id.tv_last_charge_time);
    }

    @Override
    protected int getBackContentView() {
        return R.layout.activity_power_display;
    }

    @Override
    protected void initNet() {

    }

    @Override
    protected void initData() {
        mTvLifecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoUtil.goActivity(PowerDisplayActivity.this,PowerLifecycleActivity.class);
            }
        });

    }
//    运动状态（1Byte）+电池组状态(1Byte) +电压(6Byte)+平均温度(1Byte)+未充电放置天数（1Byte）+连续未充电满次数(1Byte)+本次充电时间（2Byte）+本次放电时间（2Byte）+续航公里数（1Byte）
    private String getOrderCode(String hexStr) {
        return  hexStr.substring(2,4);
    }

    @Override
    protected void setData() {
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                byte[] value = characteristic.getValue();
                Log.e(TAG, "onCharacteristicChanged: " + ByteUtil.byte2hex(value));
                String dateHex = ByteUtil.byte2hex(value).substring(4);

                if ("01".equals(getOrderCode(ByteUtil.byte2hex(value)))) {
                    mTvVoltage.setText(HeartConvert.getVoltage(dateHex)+"V");
                    mTvKilometre.setText(HeartConvert.getKilometre(dateHex)+"公里");
                    mTvTemperature.setText(HeartConvert.getTemperature(dateHex)+"℃");
                    mTvChargerStatus.setText(HeartConvert.getPowerStatus(dateHex));
//                            mTvHealthStatus.setText(HeartConvert.getMoveStatus(dateHex));
                }
            }
        });
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

    }
}
