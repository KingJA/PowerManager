package com.kingja.power.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.junkchen.blelib.BleService;
import com.kingja.power.dao.CheckDeviceEvent;
import com.kingja.power.dao.DBManager;
import com.kingja.power.greenbean.BleInfo;
import com.kingja.power.util.ByteUtil;
import com.kingja.power.util.DataManager;
import com.kingja.power.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;


/**
 * Description：TODO
 * Create Time：2017/2/15 14:50
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class BleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BleService.ACTION_BLUETOOTH_DEVICE)) {
        } else if (intent.getAction().equals(BleService.ACTION_GATT_CONNECTED)) {
        } else if (intent.getAction().equals(BleService.ACTION_GATT_DISCONNECTED)) {
        } else if (intent.getAction().equals(BleService.ACTION_SCAN_FINISHED)) {
        } else if (intent.getAction().equals(BleService.ACTION_DATA_CHANGE)) {
            byte[] data = intent.getByteArrayExtra("data");
            Log.e("BleReceiver", "广播收到数据: " + ByteUtil.byte2hex(data));
            DBManager.getInstance(context).insertBleInfo(new BleInfo(null, ByteUtil.byte2hex(data).substring(2, 4), ByteUtil.byte2hex(data).substring(4), TimeUtil.getCurrentTime()));
//            if ("0d".equals(ByteUtil.byte2hex(data).substring(2, 4))) {
//                if (ByteUtil.byte2hex(data).substring(4, 12).equals(DataManager.getDeviceId())) {
//                    EventBus.getDefault().post(new CheckDeviceEvent(true));
//                } else {
//                    //主标签ID错误
//                    EventBus.getDefault().post(new CheckDeviceEvent(false));
//                }
//            }
        }
    }
}
