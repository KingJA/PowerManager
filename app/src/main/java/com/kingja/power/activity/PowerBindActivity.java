package com.kingja.power.activity;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.junkchen.blelib.BleListener;
import com.junkchen.blelib.BleService;
import com.kingja.power.R;
import com.kingja.power.base.BackTitleActivity;
import com.kingja.power.base.App;
import com.kingja.power.dao.DBManager;
import com.kingja.power.greenbean.Battery;
import com.kingja.power.util.ByteUtil;
import com.kingja.power.util.DataManager;
import com.kingja.power.util.GoUtil;
import com.kingja.power.util.TimeUtil;

/**
 * Description：蓄电池绑定
 * Create Time：2017/2/10 13:51
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class PowerBindActivity extends BackTitleActivity {

    private String data;
    private TextView tv_confirm;
    private BleService mBleService;

    @Override
    protected void initVariables() {
        data = getIntent().getStringExtra("data");
        mBleService = BleService.getInstance();

    }

    @Override
    protected String setTitle() {
        return "蓄电池绑定";
    }

    @Override
    protected void initContentView() {
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
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
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData2Local(data);
                //下发电池信息0x0c
                mBleService.writeCharacteristic(DataManager.getServiceUUID(),DataManager.getWriteUUID(),
                        new byte[]{(byte) 0xaa, 0x0c, 0x01, 0x01, 0x01, 0x01, 0x01, 0x33, (byte) 0xc9, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x57, 0x76});

                PowerDisplayActivity.goActivity(PowerBindActivity.this,"");
            }


        });
        mBleService.setOnDataAvailableListener(new BleListener.OnDataAvailableListener() {
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                byte[] value = characteristic.getValue();
                Log.e(TAG, "onCharacteristicRead: " + ByteUtil.byte2hex(value));
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                byte[] value = characteristic.getValue();
                Log.e(TAG, "onCharacteristicChanged: " + ByteUtil.byte2hex(value));
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

            }
        });
    }
//设备类型2 设备ID 4 设备生产时间4 设备生产区编码1 预留4
    //0001 000003a8  0133c902         02         00000000
    // 1     936      20171010         2           0
    private void saveData2Local(String data) {
        String deviceType=ByteUtil.hexStr2Dec(data.substring(0,4))+"";
        String deviceId=ByteUtil.hexStr2Dec(data.substring(4,12))+"";
        String deviceProduceTime=ByteUtil.hexStr2Dec(data.substring(12,20))+"";
        String deviceProdueterNO=ByteUtil.hexStr2Dec(data.substring(20,22))+"";
        Log.e(TAG, "deviceType: "+deviceType );
        Log.e(TAG, "deviceId: "+deviceId );
        Log.e(TAG, "deviceProduceTime: "+deviceProduceTime );
        Log.e(TAG, "deviceProdueterNO: "+deviceProdueterNO );
        DBManager.getInstance(this).insertBattery(new Battery(null, DataManager.getMacAddress(),deviceType,deviceId,deviceProduceTime,deviceProdueterNO, TimeUtil.getYearDay()));
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
}
