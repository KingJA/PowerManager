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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.junkchen.blelib.BleService;
import com.kingja.power.R;
import com.kingja.power.base.BackTitleActivity;
import com.kingja.power.base.BaseApplication;
import com.kingja.power.dao.DBManager;
import com.kingja.power.greenbean.BleInfo;
import com.kingja.power.util.ByteUtil;
import com.kingja.power.util.DialogUtil;
import com.kingja.power.util.GoUtil;
import com.kingja.power.util.ToastUtil;
import com.kingja.zbar.CaptureActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        BleInfo lastBleInfo = DBManager.getInstance(this).getLastBleInfo("01");
        Log.e(TAG, "lastBleInfo: "+lastBleInfo.getContent() );
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String result = data.getStringExtra("result");
            final String deviceId = result.substring(4, 12);
            Log.e(TAG, "deviceId: " + deviceId);
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
                    PowerBindActivity.goActivity(BleActivity.this, deviceId);
                }
            });
            bindDialog.show();
        }
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
        }, R.drawable.bluetooth);
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
                        } else {
                            connDeviceAddress = (String) deviceMap.get("address");
                            connDeviceName = (String) name;
                            HashMap<String, Object> connDevMap = new HashMap<String, Object>();
                            connDevMap.put("name", connDeviceName);
                            connDevMap.put("address", connDeviceAddress);
                            connDevMap.put("isConnect", false);
                            deviceList.clear();
                            deviceList.add(connDevMap);
                            deviceAdapter.notifyDataSetChanged();
                            mBleService.connect(connDeviceAddress);
                            showDialog(getString(R.string.connecting));
                            tv_scan_code.setVisibility(View.VISIBLE);
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
//        //Ble扫描回调
//        mBleService.setOnLeScanListener(new BleService.OnLeScanListener() {
//            @Override
//            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//                //每当扫描到一个Ble设备时就会返回，（扫描结果重复的库中已处理）
//            }
//        });
//        //Ble连接回调
//        mBleService.setOnConnectListener(new BleService.OnConnectListener() {
//            @Override
//            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                    //Ble连接已断开
//                } else if (newState == BluetoothProfile.STATE_CONNECTING) {
//                    //Ble正在连接
//                } else if (newState == BluetoothProfile.STATE_CONNECTED) {
//                    //Ble已连接
//                } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
//                    //Ble正在断开连接
//                }
//            }
//        });
//        //Ble服务发现回调
//        mBleService.setOnServicesDiscoveredListener(new BleService.OnServicesDiscoveredListener() {
//            @Override
//            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//
//            }
//        });
//        //Ble数据回调
//        mBleService.setOnDataAvailableListener(new BleService.OnDataAvailableListener() {
//            @Override
//            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//                //处理特性读取返回的数据
//            }
//
//            @Override
//            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//                //处理通知返回的数据
//            }
//        @Override
//        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//
//        }
//        });
        mBleService.setOnReadRemoteRssiListener(new BleService.OnReadRemoteRssiListener() {
            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                Log.i(TAG, "onReadRemoteRssi: rssi = " + rssi);
            }
        });
    }

    private void getReadAndWriteUUID(BluetoothGattService service) {
        String serviceUuid = service.getUuid().toString();
        if (SERVICE_UUID.equals(serviceUuid)) {
            final List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            Log.e(TAG, "onClick: " + characteristics.get(3).getUuid());
            mBleService.setCharacteristicNotification(characteristics.get(3), true);
            mBleService.setOnDataAvailableListener(new BleService.OnDataAvailableListener() {
                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    byte[] value = characteristic.getValue();
                    Log.e(TAG, "onCharacteristicRead: " + byte2hex(value));
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    byte[] value = characteristic.getValue();
                    Log.e(TAG, "onCharacteristicChanged: " + byte2hex(value));
                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    Log.e(TAG, "onDescriptorRead: ");
                }
            });
//            findViewById(R.id.btn_read).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mBleService.readCharacteristic(characteristics.get(3));
//                }
//            });
//            findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mBleService.writeCharacteristic(characteristics.get(2), new byte[]{(byte) 0xaa, 0x0b, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, (byte) 0xC3});
//                    mBleService.readCharacteristic(characteristics.get(3));
//                }
//            });


            writeUUID = characteristics.get(2).getUuid().toString();
            readUUID = characteristics.get(3).getUuid().toString();
            BaseApplication.write_uuid=writeUUID;
            BaseApplication.read_uuid=readUUID;
            Log.e(TAG, "writeUUID: " + writeUUID);
            Log.e(TAG, "readUUID: " + readUUID);
        }
    }

    private void doOperation() {
//        mBleService.initialize();//Ble初始化操作
//        mBleService.enableBluetooth(boolean enable);//打开或关闭蓝牙
//        mBleService.scanLeDevice(boolean enable, long scanPeriod);//启动或停止扫描Ble设备
//        mBleService.connect(String address);//连接Ble
//        mBleService.disconnect();//取消连接
//        mBleService.getSupportedGattServices();//获取服务
//        mBleService.setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
//        boolean enabled);//设置通知
//        mBleService.readCharacteristic(BluetoothGattCharacteristic characteristic);//读取数据
//        mBleService.writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] value);//写入数据
//        mBleService.close();//关闭客户端
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
                Log.e(TAG, "ACTION_GATT_CONNECTED: " );
                deviceList.get(0).put("isConnect", true);
                deviceAdapter.notifyDataSetChanged();
                dismissDialog();
            } else if (intent.getAction().equals(BleService.ACTION_GATT_DISCONNECTED)) {
                Log.e(TAG, "ACTION_GATT_DISCONNECTED: " );
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

    public static String byte2hex(byte[] buffer) {
        String h = "";
        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + temp;
        }
        return h;
    }

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
