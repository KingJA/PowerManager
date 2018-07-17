package com.kingja.power.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.junkchen.blelib.BleListener;
import com.junkchen.blelib.BleService;
import com.kingja.power.R;
import com.kingja.power.adapter.BatteryAdapter;
import com.kingja.power.base.BackTitleActivity;
import com.kingja.power.convert.HeartConvert;
import com.kingja.power.dao.CheckDeviceEvent;
import com.kingja.power.dao.DBManager;
import com.kingja.power.greenbean.Battery;
import com.kingja.power.util.ByteUtil;
import com.kingja.power.util.Constants;
import com.kingja.power.util.Crc16Util;
import com.kingja.power.util.DataManager;
import com.kingja.power.util.DialogUtil;
import com.kingja.power.util.GoUtil;
import com.kingja.power.util.TimeUtil;
import com.kingja.power.util.ToastUtil;
import com.kingja.zbar.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import lib.kingja.progress.KJProgressRound;

/**
 * Description：蓄电池显示
 * Create Time：2017/2/10 13:51
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class PowerDisplayActivity extends BackTitleActivity implements BleListener.OnDataAvailableListener {

    private BleService mBleService;

    private TextView mTvKilometre;
    private TextView mTvVoltage;
    private TextView mTvTemperature;
    private TextView mTvLifecycle;
    private ListView mLv;
    private TextView mTvChargerStatus;
    private TextView mTvHealthStatus;
    private TextView mTvLastChargeTime;
    private KJProgressRound mProgressCircle;
    private String deviceId;
    public static final int SERVICE_BIND = 1;
    private final int SCAN_CODE = 0X110;
    private boolean mIsBind;
    private List<Battery> mBatteryList = new ArrayList<>();
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBleService = ((BleService.LocalBinder) service).getService();
            if (mBleService != null) {
                mHandler.sendEmptyMessage(SERVICE_BIND);
            }
            mBleService.initialize();
            mIsBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBleService = null;
            mIsBind = false;
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SERVICE_BIND:
                    mBleService.setOnDataAvailableListener(PowerDisplayActivity.this);
                    mBleService.setOnConnectListener(new BleListener.OnConnectionStateChangeListener() {
                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                                Log.e(TAG, "服务断开: " + (mBleService == null));
                                doBindService();
                            } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                                //Ble正在连接
                            } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                                //Ble已连接
                            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                                //Ble正在断开连接
                            }
                        }
                    });
                    sendHeart();

                    break;
            }
        }
    };
    private String power;

    private void sendHeart() {
        String content = Constants.ORDER_HEART + Crc16Util.fixHex(Integer.toHexString(Integer.valueOf(DataManager.getDeviceId())), 8)
                + "000000000000000000000000";
        String crc16Code = Crc16Util.getCrc16Code(content);
        String sendMsg = Constants.FLAG + content + crc16Code;
        Log.e(TAG, "crc16Code: " + crc16Code);
        Log.e(TAG, "sendMsg: " + sendMsg);
        byte[] sendBytes = ByteUtil.hexStrToByte(sendMsg);
        //// TODO: 2017/7/25 暂停发送心跳
//        mBleService.writeCharacteristic(DataManager.getServiceUUID(), DataManager.getWriteUUID(), sendBytes);
//        mBleService.writeCharacteristic(DataManager.getServiceUUID(), DataManager.getWriteUUID(),
//                new byte[]{(byte) 0xaa, 0x0b, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, (byte) 0xC3});
    }

    private TextView mTvAddBattery;
    private NormalDialog bindDialog;
    private BatteryAdapter mBatteryAdapter;
    private NormalDialog unbindDialog;
    private String dateHex;


    @Override
    protected void onResume() {
        super.onResume();
        if (mBleService != null) {
            mBleService.setOnDataAvailableListener(this);
        }
    }

    @Override
    protected void initVariables() {
//        EventBus.getDefault().register(this);
        doBindService();
        mBatteryList = DBManager.getInstance(this).getBindedBatteries(DataManager.getMacAddress());
    }

    @Override
    protected String setTitle() {
        return "蓄电池";
    }

    @Override
    protected void initContentView() {
        mTvKilometre = (TextView) findViewById(R.id.tv_kilometre);
        mTvVoltage = (TextView) findViewById(R.id.tv_voltage);
        mTvTemperature = (TextView) findViewById(R.id.tv_temperature);
        mTvLifecycle = (TextView) findViewById(R.id.tv_lifecycle);
        mLv = (ListView) findViewById(R.id.gv);
        mTvChargerStatus = (TextView) findViewById(R.id.tv_charger_status);
        mTvHealthStatus = (TextView) findViewById(R.id.tv_health_status);
        mTvLastChargeTime = (TextView) findViewById(R.id.tv_last_charge_time);
        mProgressCircle = (KJProgressRound) findViewById(R.id.progress_circle);
        mTvAddBattery = (TextView) findViewById(R.id.tv_add_battery);
        mBatteryAdapter = new BatteryAdapter(this, mBatteryList);
        mLv.setAdapter(mBatteryAdapter);
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
        setBackButtonGone();
        mTvAddBattery.setOnClickListener(this);
        mTvLifecycle.setOnClickListener(this);
        mLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                if (position == 0) {
                    return true;
                }
                final Battery battery = (Battery) parent.getItemAtPosition(position);
                unbindDialog = DialogUtil.getDoubleDialog(PowerDisplayActivity.this, "您是否要解绑编号为" + battery.getDeviceId() + "的子电池", "取消", "确定");
                unbindDialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        unbindDialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        unbindDialog.dismiss();
//                        Battery battery = (Battery) parent.getItemAtPosition(position);
                        DBManager.getInstance(PowerDisplayActivity.this).deleteBattery(battery.getId());
                        mBatteryList = DBManager.getInstance(PowerDisplayActivity.this).getBindedBatteries(DataManager.getMacAddress());
                        mBatteryAdapter.setData(mBatteryList);
                    }
                });
                unbindDialog.show();
                return true;
            }
        });
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return ;
                }
                Battery battery = (Battery) parent.getItemAtPosition(position);
//                PowerChildDisplayActivity.goActivity(PowerDisplayActivity.this,power, HeartConvert.getPowerStatus(dateHex), "--", HeartConvert.getTemperature(dateHex) + "℃",
//                        battery.getDeviceId(), battery.getProduceTime(), battery.getCreateTime());
                GoUtil.goActivity(PowerDisplayActivity.this,PowerChildDisplayActivity.class);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_add_battery:
                GoUtil.goActivityForResult(PowerDisplayActivity.this, CaptureActivity.class, SCAN_CODE);
                break;
            case R.id.tv_lifecycle:
                GoUtil.goActivity(PowerDisplayActivity.this, PowerLifecycleActivity.class);
                break;

        }
    }

    //    运动状态（1Byte）+电池组状态(1Byte) +电压(6Byte)+平均温度(1Byte)+未充电放置天数（1Byte）+连续未充电满次数(1Byte)+本次充电时间（2Byte）+本次放电时间（2Byte）+续航公里数（1Byte）
    private String getOrderCode(String hexStr) {
        return hexStr.substring(2, 4);
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
                dateHex = ByteUtil.byte2hex(value).substring(4);
                if ("01".equals(getOrderCode(ByteUtil.byte2hex(value)))) {
                    Log.e(TAG, "展示信息: " + dateHex);
                    power = HeartConvert.getKilometre(dateHex);
                    mProgressCircle.setProgress(Integer.valueOf(power));
                    int mileage = (int) ((Integer.valueOf(HeartConvert.getKilometre(dateHex))* DataManager.getMileage())*0.01f);
                    mTvVoltage.setText(HeartConvert.getVoltage(dateHex) + "V");
                    mTvKilometre.setText(mileage + "公里");
                    mTvTemperature.setText(HeartConvert.getTemperature(dateHex) + "℃");
                    mTvChargerStatus.setText(HeartConvert.getPowerStatus(dateHex));
//                    mProgressCircle.setProgress();
//                            mTvHealthStatus.setText(HeartConvert.getMoveStatus(dateHex));
                }
            }
        });
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

    }

    /**
     * 绑定服务
     */
    private void doBindService() {
        Intent serviceIntent = new Intent(this, BleService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑服务
     */
    private void doUnBindService() {
        if (mIsBind) {
            unbindService(serviceConnection);
            mBleService = null;
            mIsBind = false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            final String result =  data.getStringExtra("result");
            final String deviceTypeHex = result.substring(0, 4);
            final String deviceIdHex = result.substring(4, 12);

            if (DBManager.getInstance(PowerDisplayActivity.this).hasBinded(ByteUtil.hexStr2Dec(result.substring(4, 12)) + "")) { //TODO 1.如果已经绑定则提示已经绑定，如果没有则进行绑定
                ToastUtil.showToast("该设备已经绑定");
            } else {

                bindDialog = DialogUtil.getDoubleDialog(this, "您是否要绑定编号为" + ByteUtil.hexStr2Dec(deviceIdHex) + "的子电池", "取消", "确定");
                bindDialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        bindDialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        bindDialog.dismiss();
                        saveData2Local(result);
                        mBatteryList = DBManager.getInstance(PowerDisplayActivity.this).getBindedBatteries(DataManager.getMacAddress());
                        mBatteryAdapter.setData(mBatteryList);
                        //// TODO: 2017/7/25 暂定发送子标签心跳
//                        sendChildDevice(deviceTypeHex,deviceIdHex,mBatteryAdapter.getCount()-1);

                    }
                });
                bindDialog.show();

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnBindService();
    }

    private void saveData2Local(String data) {
        String deviceType = ByteUtil.hexStr2Dec(data.substring(0, 4)) + "";
        String deviceId = ByteUtil.hexStr2Dec(data.substring(4, 12)) + "";
        String deviceProduceTime = ByteUtil.hexStr2Dec(data.substring(12, 20)) + "";
        String deviceProdueterNO = ByteUtil.hexStr2Dec(data.substring(20, 22)) + "";
        Log.e(TAG, "deviceType: " + deviceType);
        Log.e(TAG, "deviceId: " + deviceId);
        Log.e(TAG, "deviceProduceTime: " + deviceProduceTime);
        Log.e(TAG, "deviceProdueterNO: " + deviceProdueterNO);
        DBManager.getInstance(this).insertBattery(new Battery(null, DataManager.getMacAddress(), deviceType, deviceId, deviceProduceTime, deviceProdueterNO, TimeUtil.getYearDay()));
    }

    private long lastTime;

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime < 500) {
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            ToastUtil.showToast("连续点击两次退出");
            lastTime = currentTime;
        }
    }

    private void sendChildDevice(String deviceTypeHex, String deviceHexId,int number) {
        String content = Constants.ORDER_OD + deviceTypeHex+deviceHexId+"0"+number
                + "000000000000000000";
        String crc16Code = Crc16Util.getCrc16Code(content);
        String sendMsg = Constants.FLAG + content + crc16Code;
        Log.e(TAG, "crc16Code: " + crc16Code);
        Log.e(TAG, "sendMsg: " + sendMsg);
        byte[] sendBytes = ByteUtil.hexStrToByte(sendMsg);
        mBleService.writeCharacteristic(DataManager.getServiceUUID(), DataManager.getWriteUUID(), sendBytes);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onCheckDeviceEvent(CheckDeviceEvent event) {
//        if (event.isChecked()) {
//
//        }else{
//            ToastUtil.showToast("不是配对的标签");
//        }
//    }
}
