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
public class BleInfo {
    @Id(autoincrement = true)
    private Long id;
    private String order;//命令字
    private String content;//内容
    private String dateTime;//接收日期
    public String getDateTime() {
        return this.dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getOrder() {
        return this.order;
    }
    public void setOrder(String order) {
        this.order = order;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 96528771)
    public BleInfo(Long id, String order, String content, String dateTime) {
        this.id = id;
        this.order = order;
        this.content = content;
        this.dateTime = dateTime;
    }
    @Generated(hash = 845620856)
    public BleInfo() {
    }

}
