package com.kingja.power.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.junkchen.blelib.BleListener;
import com.junkchen.blelib.BleService;
import com.kingja.power.R;
import com.kingja.power.base.BackTitleActivity;
import com.kingja.power.base.App;
import com.kingja.power.dao.DBManager;
import com.kingja.power.greenbean.Battery;
import com.kingja.power.receiver.BleReceiver;
import com.kingja.power.util.ByteUtil;
import com.kingja.power.util.DataManager;
import com.kingja.power.util.GoUtil;
import com.kingja.power.util.TimeUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Description：蓄电池绑定
 * Create Time：2017/2/10 13:51
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class PowerBindActivity extends BackTitleActivity implements View.OnClickListener, BleListener.OnDataAvailableListener {

    private String data;
    private BleService mBleService;
    private List<String> mTotlePower = Arrays.asList("20", "40", "60", "80");
    private List<String> mPowerCount = Arrays.asList("2", "4", "6", "8");
    private List<String> mChargerElectricity = Arrays.asList("50", "60", "70", "80");
    private List<String> mMileage = Arrays.asList("20", "50", "100", "200");

    private final int TOTLE_POWER = 0;
    private final int POWER_COUNT = 1;
    private final int CHARGER_ELECTRICITY = 2;
    private final int MILEAGE = 3;
    private TextView mTvTotlePower;
    private TextView mTvPowerCount;
    private TextView mTvChargerElectricity;
    private TextView mTvMileage;
    private TextView mTvDate;
    private TextView mTvConfirm;
    private int year;
    private int month;
    private int day;
    public static final int SERVICE_BIND = 1;
    private boolean mIsBind;
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
                    mBleService.setOnDataAvailableListener(PowerBindActivity.this);
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
//                    mBleService.writeCharacteristic(DataManager.getServiceUUID(), DataManager.getWriteUUID(),
//                            new byte[]{(byte) 0xaa, 0x0b, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, (byte) 0xC3});
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (mBleService != null) {
            mBleService.setOnDataAvailableListener(this);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        doUnBindService();
    }

    @Override
    protected void initVariables() {
        doBindService();
        data = getIntent().getStringExtra("data");

    }

    @Override
    protected String setTitle() {
        return "蓄电池绑定";

    }

    @Override
    protected void initContentView() {
        mTvTotlePower = (TextView) findViewById(R.id.tv_totlePower);
        mTvPowerCount = (TextView) findViewById(R.id.tv_powerCount);
        mTvChargerElectricity = (TextView) findViewById(R.id.tv_chargerElectricity);
        mTvMileage = (TextView) findViewById(R.id.tv_mileage);
        mTvDate = (TextView) findViewById(R.id.tv_date);
        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);
    }

    @Override
    protected int getBackContentView() {
        return R.layout.activity_power_bind;
    }

    @Override
    protected void initNet() {

    }

    @Override
    protected void initData() {
        intDate();
        mTvTotlePower.setOnClickListener(this);
        mTvPowerCount.setOnClickListener(this);
        mTvChargerElectricity.setOnClickListener(this);
        mTvMileage.setOnClickListener(this);
        mTvDate.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_totlePower:
                showPop(0);
                break;
            case R.id.tv_powerCount:
                showPop(1);
                break;
            case R.id.tv_chargerElectricity:
                showPop(2);
                break;
            case R.id.tv_mileage:
                showPop(3);
                break;
            case R.id.tv_date:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mTvDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        String date = year + "" + (monthOfYear + 1) + "" + dayOfMonth;
                        Log.e(TAG, "date: " + date);
                    }
                }, year, month, day).show();
                break;
            case R.id.tv_confirm:
                saveData2Local(data);
                GoUtil.goActivityAndFinish(this, PowerDisplayActivity.class);
                break;

        }

    }

   public int TwoByte_Reverse(int x){
       return ((x&0x00ff)<<8)|((x&0xff00)>>8);
   }


    public void intDate() {
        Calendar mycalendar = Calendar.getInstance();
        year = mycalendar.get(Calendar.YEAR);
        month = mycalendar.get(Calendar.MONTH);
        day = mycalendar.get(Calendar.DAY_OF_MONTH);
    }

    private void showPop(final int type) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter(
                PowerBindActivity.this,
                R.layout.item_pop, getPopData(type)));
        listPopupWindow.setAnchorView(getAnchorView(type));
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();
                String item = (String) parent.getItemAtPosition(position);
                switch (type) {
                    case TOTLE_POWER:
                        String totlePower = item;
                        mTvTotlePower.setText(totlePower + "V");
                        break;
                    case POWER_COUNT:
                        String powerCount = item;
                        mTvPowerCount.setText(powerCount + "组");
                        break;
                    case CHARGER_ELECTRICITY:
                        String chargerElectricity = item;
                        mTvChargerElectricity.setText(chargerElectricity + "A");
                        break;
                    case MILEAGE:
                        String mileage = item;
                        mTvMileage.setText(mileage + "公里");
                        DataManager.putMileage(Integer.valueOf(mileage));
                        break;
                }
            }
        });
        listPopupWindow.show();
    }

    private List<String> getPopData(int type) {
        List<String> list = new ArrayList<>();
        switch (type) {
            case TOTLE_POWER:
                list = mTotlePower;
                break;
            case POWER_COUNT:
                list = mPowerCount;
                break;
            case CHARGER_ELECTRICITY:
                list = mChargerElectricity;
                break;
            case MILEAGE:
                list = mMileage;
                break;
        }
        return list;
    }

    private View getAnchorView(int type) {
        TextView textView = new TextView(this);
        switch (type) {
            case TOTLE_POWER:
                textView = mTvTotlePower;
                break;
            case POWER_COUNT:
                textView = mTvPowerCount;
                break;
            case CHARGER_ELECTRICITY:
                textView = mTvChargerElectricity;
                break;
            case MILEAGE:
                textView = mTvMileage;
                break;
        }
        return textView;
    }

    //设备类型2 设备ID 4 设备生产时间4 设备生产区编码1 预留4
    //0001 000003a8  0133c902         02         00000000
    // 1     936      20171010         2           0
    private void saveData2Local(String data) {
        String deviceType = ByteUtil.hexStr2Dec(data.substring(0, 4)) + "";
        String deviceId = ByteUtil.hexStr2Dec(data.substring(4, 12)) + "";
        String deviceProduceTime = ByteUtil.hexStr2Dec(data.substring(12, 20)) + "";
        String deviceProdueterNO = ByteUtil.hexStr2Dec(data.substring(20, 22)) + "";
        Log.e(TAG, "deviceType: " + deviceType);
        Log.e(TAG, "deviceId: " + deviceId);
        Log.e(TAG, "deviceProduceTime: " + deviceProduceTime);
        Log.e(TAG, "deviceProdueterNO: " + deviceProdueterNO);
        DataManager.putDeviceId(deviceId);
        DBManager.getInstance(this).insertBattery(new Battery(null, DataManager.getMacAddress(), deviceType, deviceId, deviceProduceTime, deviceProdueterNO, TimeUtil.getYearDay()));
    }

    @Override
    protected void setData() {
    }

    public static void goActivity(Activity activity, String data) {
        Intent intent = new Intent(activity, PowerBindActivity.class);
        intent.putExtra("data", data);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        Log.e(TAG, "onCharacteristicChanged: " + ByteUtil.byte2hex(value));
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

    }
}
