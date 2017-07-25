package com.kingja.power.dao;

/**
 * Description：TODO
 * Create Time：2017/2/28 16:58
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class CheckDeviceEvent {
    private boolean isChecked;

    public CheckDeviceEvent(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
