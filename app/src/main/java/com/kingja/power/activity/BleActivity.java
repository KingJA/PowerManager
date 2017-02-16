package com.kingja.power.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.junkchen.blelib.BleListener;
import com.junkchen.blelib.BleService;
import com.kingja.power.R;
import com.kingja.power.base.BackTitleActivity;
import com.kingja.power.base.App;
import com.kingja.power.dao.DBManager;
import com.kingja.power.greenbean.BleInfo;
import com.kingja.power.util.ByteUtil;
import com.kingja.power.util.DataManager;
import com.kingja.power.util.DialogUtil;
import com.kingja.power.util.GoUtil;
import com.kingja.zbar.CaptureActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

/**
 * Description：蓝牙搜索
 * Create Time：2017/2/10 13:51
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class BleActivity extends BackTitleActivity {
    private final int SCAN_CODE = 0X110;
    //Debugging
    private static final String TAG = BleActivity.class.getSimpleName();

    //Constant
    public static final int SERVICE_BIND = 1;
    public static final int SERVICE_SHOW = 2;
    public static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    //Member fields
    private BleService mBleService;
    private boolean mIsBind;
    private CommonAdapter<Map<String, Object>> deviceAdapter;
    private ArrayAdapter<String> serviceAdapter;
    private List<Map<String, Object>> deviceList;
    private String connDeviceName;
    private String connDeviceAddress;
    private boolean hasBinded;

    //Layout view
    private ListView lstv_devList;
    //    private ListView lstv_showService;
    private final String SERVICE_UUID = "0000fff0-0000-1000-8000-00805f9b34fb";

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBleService = ((BleService.LocalBinder) service).getService();
            if (mBleService != null) mHandler.sendEmptyMessage(SERVICE_BIND);
            if (mBleService.initialize()) {
                if (mBleService.enableBluetooth(true)) {
                    verifyIfRequestPermission();
//                    Toast.makeText(BleActivity.this, "Bluetooth was opened", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(BleActivity.this, "not support Bluetooth", Toast.LENGTH_SHORT).show();
            }
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
                    setBleServiceListener();
                    break;
                case SERVICE_SHOW:
                    serviceAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private String writeUUID;
    private String readUUID;
    private TextView tv_scan_code;
    private NormalDialog bindDialog;

    @Override
    protected void initVariables() {
    }

    @Override
    protected String setTitle() {
        return "蓝牙连接";
    }

    @Override
    protected void initContentView() {
        lstv_devList = (ListView) findViewById(R.id.lstv_devList);
        tv_scan_code = (TextView) findViewById(R.id.tv_scan_code);
    }

    @Override
    protected int getBackContentView() {
        return R.layout.activity_ble_scan;
    }

    @Override
    protected void initNet() {

    }


    @Override
    protected void initData() {
        initAdapter();
        registerReceiver(bleReceiver, makeIntentFilter());
        doBindService();
        tv_scan_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoUtil.goActivityForResult(BleActivity.this, CaptureActivity.class, SCAN_CODE);
            }
        });
    }



    @Override
    protected void setData() {
        setOnMenuClickListener(new OnMenuClickListener() {
            @Override
            public void onMenuClick() {
                if (!mBleService.isScanning()) {
                    verifyIfRequestPermission();
                    deviceList.clear();
                    mBleService.scanLeDevice(true);
                }
            }
        }, R.drawable.scan);
    }


    private void verifyIfRequestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.i(TAG, "onCreate: checkSelfPermission");
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onCreate: Android 6.0 动态申请权限");

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    Log.i(TAG, "*********onCreate: shouldShowRequestPermissionRationale**********");
                    Toast.makeText(this, "只有允许访问位置才能搜索到蓝牙设备", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ACCESS_COARSE_LOCATION);
                }
            } else {
                showDialog(getResources().getString(R.string.scanning));
                mBleService.scanLeDevice(true);
            }
        } else {
            showDialog(getResources().getString(R.string.scanning));
            mBleService.scanLeDevice(true);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            Log.i(TAG, "onRequestPermissionsResult: permissions.length = " + permissions.length +
                    ", grantResults.length = " + grantResults.length);
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                showDialog(getResources().getString(R.string.scanning));
                mBleService.scanLeDevice(true);
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "位置访问权限被拒绝将无法搜索到ble设备", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void initAdapter() {
        deviceList = new ArrayList<>();
        deviceAdapter = new CommonAdapter<Map<String, Object>>(
                this, R.layout.item_device, deviceList) {
            @Override
            public void convert(ViewHolder holder, final Map<String, Object> deviceMap) {
                final Object name = deviceMap.get("name") == null ? "未命名" : deviceMap.get("name");
                holder.setText(R.id.txtv_name, name.toString());
                holder.setText(R.id.txtv_address, deviceMap.get("address").toString());
                holder.setText(R.id.txtv_connState, ((boolean) deviceMap.get("isConnect")) ?
                        getResources().getString(R.string.state_connected) :
                        getResources().getString(R.string.state_disconnected));
                holder.setText(R.id.btn_connect, ((boolean) deviceMap.get("isConnect")) ?
                        getResources().getString(R.string.disconnected) :
                        getResources().getString(R.string.connected));
                holder.getView(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((boolean) deviceMap.get("isConnect")) {
                            mBleService.disconnect();
                            showDialog(getString(R.string.disconnecting));
                            tv_scan_code.setVisibility(View.GONE);
                        } else {
                            connDeviceAddress = (String) deviceMap.get("address");
                            connDeviceName = (String) name;
                            if (hasBinded = DBManager.getInstance(BleActivity.this).getBindedBattery(connDeviceAddress).size() > 0) {
                                mBleService.connect(connDeviceAddress);
                                showDialog(getString(R.string.connecting));
//TODO
                            } else {
                                HashMap<String, Object> connDevMap = new HashMap<String, Object>();
                                connDevMap.put("name", connDeviceName);
                                connDevMap.put("address", connDeviceAddress);
                                connDevMap.put("isConnect", false);
                                deviceList.clear();
                                deviceList.add(connDevMap);
                                deviceAdapter.notifyDataSetChanged();
                                mBleService.connect(connDeviceAddress);
                                DataManager.putMacAddress(connDeviceAddress);
                                showDialog(getString(R.string.connecting));
                                tv_scan_code.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        };
        lstv_devList.setAdapter(deviceAdapter);
        serviceList = new ArrayList<>();
        characteristicList = new ArrayList<>();
        serviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, serviceList);
    }

    private List<BluetoothGattService> gattServiceList;
    private List<String> serviceList;
    private List<String[]> characteristicList;

    private void setBleServiceListener() {
        mBleService.setOnServicesDiscoveredListener(new BleService.OnServicesDiscoveredListener() {
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    gattServiceList = gatt.getServices();
                    serviceList.clear();
                    for (BluetoothGattService service :
                            gattServiceList) {
                        getReadAndWriteUUID(service);

                    }
                    mHandler.sendEmptyMessage(SERVICE_SHOW);
                }
            }
        });
    }

    private void getReadAndWriteUUID(BluetoothGattService service) {
        String serviceUuid = service.getUuid().toString();
        if (SERVICE_UUID.equals(serviceUuid)) {
            final List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            mBleService.setCharacteristicNotification(characteristics.get(3), true);
            writeUUID = characteristics.get(2).getUuid().toString();
            readUUID = characteristics.get(3).getUuid().toString();
            DataManager.putServiceUUID(SERVICE_UUID);
            DataManager.putReadUUID(readUUID);
            DataManager.putWriteUUID(writeUUID);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            final String result = data.getStringExtra("result");
             String deviceId = result.substring(4, 12);
            bindDialog = DialogUtil.getDoubleDialog(this, "您是否要绑定编号为" + ByteUtil.hexStr2Dec(deviceId) + "的设备", "取消", "确定");
            bindDialog.setOnBtnClickL(new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    bindDialog.dismiss();
                }
            }, new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    bindDialog.dismiss();
                    PowerBindActivity.goActivity(BleActivity.this, result);
                }
            });
            bindDialog.show();
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
            Log.e(TAG, "doUnBindService: " );
            unbindService(serviceConnection);
            mBleService = null;
            mIsBind = false;
        }
    }

    private BroadcastReceiver bleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BleService.ACTION_BLUETOOTH_DEVICE)) {
                String tmpDevName = intent.getStringExtra("name");
                String tmpDevAddress = intent.getStringExtra("address");
                Log.i(TAG, "name: " + tmpDevName + ", address: " + tmpDevAddress);
                HashMap<String, Object> deviceMap = new HashMap<>();
                deviceMap.put("name", tmpDevName);
                deviceMap.put("address", tmpDevAddress);
                deviceMap.put("isConnect", false);
                deviceList.add(deviceMap);
                deviceAdapter.notifyDataSetChanged();
            } else if (intent.getAction().equals(BleService.ACTION_GATT_CONNECTED)) {
                Log.e(TAG, "ACTION_GATT_CONNECTED: ");
                deviceList.get(0).put("isConnect", true);
                deviceAdapter.notifyDataSetChanged();
                dismissDialog();
                if (hasBinded) {
                    PowerDisplayActivity.goActivity(BleActivity.this,"00000001");
                }

            } else if (intent.getAction().equals(BleService.ACTION_GATT_DISCONNECTED)) {
                Log.e(TAG, "ACTION_GATT_DISCONNECTED: ");
                deviceList.get(0).put("isConnect", false);
                serviceList.clear();
                characteristicList.clear();
                deviceAdapter.notifyDataSetChanged();
                serviceAdapter.notifyDataSetChanged();
                dismissDialog();
            } else if (intent.getAction().equals(BleService.ACTION_SCAN_FINISHED)) {
                mRlTopMenu.setEnabled(true);
                dismissDialog();
            }
        }
    };

    private static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_BLUETOOTH_DEVICE);
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_SCAN_FINISHED);
        return intentFilter;
    }

    /**
     * Show dialog
     */
    private ProgressDialog progressDialog;

    private void showDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissDialog() {
        if (progressDialog == null) return;
        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnBindService();
        unregisterReceiver(bleReceiver);
    }

    @Override
    public void onBackPressed() {
        if (mBleService.isScanning()) {
            mBleService.scanLeDevice(false);
            return;
        }
        super.onBackPressed();
    }
}
