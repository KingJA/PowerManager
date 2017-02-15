package com.kingja.power.activity;

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
import com.kingja.power.base.BaseApplication;
import com.kingja.power.util.ByteUtil;
import com.kingja.power.util.GoUtil;

/**
 * Description：蓄电池绑定
 * Create Time：2017/2/10 13:51
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class PowerBindActivity extends BackTitleActivity {

    private String deviceId;
    private TextView tv_confirm;
    private BleService mBleService;

    @Override
    protected void initVariables() {
        deviceId = getIntent().getStringExtra("DeviceId");
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
                String sendContent = "aa0b" + deviceId + "000000000000000000000000";
//                mBleService.writeCharacteristic(BaseApplication.service_uuid,BaseApplication.write_uuid, ByteUtil.hexStrToByte(sendContent));
                //握手0x0b
                mBleService.writeCharacteristic(BaseApplication.service_uuid, BaseApplication.write_uuid,
                        new byte[]{(byte) 0xaa, 0x0b, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, (byte) 0xC3});
                //下发电池信息0x0c
                mBleService.writeCharacteristic(BaseApplication.service_uuid, BaseApplication.write_uuid,
                        new byte[]{(byte) 0xaa, 0x0c, 0x01, 0x01, 0x01, 0x01, 0x01, 0x33, (byte) 0xc9, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x57, 0x76});

                GoUtil.goActivity(PowerBindActivity.this,PowerDisplayActivity.class);
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

    @Override
    protected void setData() {
    }

    public static void goActivity(Context context, String deviceId) {
        Intent intent = new Intent(context, PowerBindActivity.class);
        intent.putExtra("DeviceId", deviceId);
        context.startActivity(intent);

    }
}
