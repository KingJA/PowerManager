package com.kingja.power.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.widget.TextView;

import com.junkchen.blelib.BleListener;
import com.junkchen.blelib.BleService;
import com.kingja.power.R;
import com.kingja.power.base.BackTitleActivity;
import com.kingja.power.convert.HistoryConvert;
import com.kingja.power.dao.DBManager;
import com.kingja.power.greenbean.BleInfo;
import com.kingja.power.util.ByteUtil;
import com.kingja.power.util.ToastUtil;

/**
 * Description：蓄电池生命周期
 * Create Time：2017/2/10 13:51
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class PowerLifecycleActivity extends BackTitleActivity implements BleListener.OnDataAvailableListener{
    private TextView mTvDischargerTime;
    private TextView mTvChargerTime;
    private TextView mTvChargerCount;
    private TextView mTvCreateTime;
    private TextView mTvTotleKilometre;
    private BleInfo lastBleInfo;
    private BleService mBleService;

    @Override
    protected void onResume() {
        super.onResume();
        mBleService.setOnDataAvailableListener(this);
    }
    @Override
    protected void initVariables() {
        lastBleInfo = DBManager.getInstance(this).getLastBleInfo("02");
        mBleService = BleService.getInstance();
    }

    @Override
    protected String setTitle() {
        return "蓄电池生命周期";
    }

    @Override
    protected void initContentView() {
        mTvDischargerTime = (TextView) findViewById(R.id.tv_discharger_time);
        mTvChargerTime = (TextView) findViewById(R.id.tv_charger_time);
        mTvChargerCount = (TextView) findViewById(R.id.tv_charger_count);
        mTvCreateTime = (TextView) findViewById(R.id.tv_createTime);
        mTvTotleKilometre = (TextView) findViewById(R.id.tv_totle_kilometre);
    }

    @Override
    protected int getBackContentView() {
        return R.layout.activity_power_lifecycle;
    }

    @Override
    protected void initNet() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setData() {
        if (lastBleInfo != null) {
            fillData(lastBleInfo.getContent());
        } else {
            ToastUtil.showToast("抱歉，暂时没有相关数据");
        }
    }

    private void fillData(String data) {
        mTvDischargerTime.setText(HistoryConvert.getDischargerTime(data));
        mTvChargerTime.setText(HistoryConvert.getChargerTime(data));
        mTvChargerCount.setText(HistoryConvert.getChargerCount(data));
//        mTvCreateTime.setText(HistoryConvert.getDischargerCount(data));
        mTvTotleKilometre.setText(HistoryConvert.getTotleKilometre(data));
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if ("02".equals(ByteUtil.byte2hex(characteristic.getValue()).substring(2, 4))) {
            fillData(ByteUtil.byte2hex(characteristic.getValue()).substring(4));
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

    }
}
