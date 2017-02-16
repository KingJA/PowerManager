package com.kingja.power.greenbean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;


/**
 * Description：TODO
 * Create Time：2017/1/25 13:50
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
@Entity
public class Battery {
    @Id(autoincrement = true)
    private Long id;
    private String mac;//蓝牙设备mac地址
    private String deviceType;//设备类型
    private String deviceId;//设备ID
    private String produceTime;//生产日期
    private String producterNo;//设备产区编号
    private String createTime;//检测日期
    public String getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public String getProducterNo() {
        return this.producterNo;
    }
    public void setProducterNo(String producterNo) {
        this.producterNo = producterNo;
    }
    public String getProduceTime() {
        return this.produceTime;
    }
    public void setProduceTime(String produceTime) {
        this.produceTime = produceTime;
    }
    public String getDeviceId() {
        return this.deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getDeviceType() {
        return this.deviceType;
    }
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 376716360)
    public Battery(Long id, String mac, String deviceType, String deviceId,
            String produceTime, String producterNo, String createTime) {
        this.id = id;
        this.mac = mac;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.produceTime = produceTime;
        this.producterNo = producterNo;
        this.createTime = createTime;
    }
    @Generated(hash = 620155092)
    public Battery() {
    }
 

}
