package com.kingja.power.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kingja.power.R;
import com.kingja.power.greenbean.Battery;

import java.util.List;

/**
 * Description：TODO
 * Create Time：2016/8/5 14:32
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class BatteryAdapter extends BaseSimpleAdapter<Battery> {

    public BatteryAdapter(Context context, List<Battery> list) {
        super(context, list);
    }

    @Override
    public View simpleGetView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View
                    .inflate(context, R.layout.item_accumulator, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvdeviceId.setText(list.get(position).getDeviceId());
        viewHolder.tvmain.setVisibility("1".equals(list.get(position).getDeviceType())?View.VISIBLE:View.GONE);

        return convertView;
    }


    public class ViewHolder {
        public final TextView tvdeviceId;
        public final TextView tvmain;
        public final TextView tvexception;
        public final View root;

        public ViewHolder(View root) {
            tvdeviceId = (TextView) root.findViewById(R.id.tv_deviceId);
            tvmain = (TextView) root.findViewById(R.id.tv_main);
            tvexception = (TextView) root.findViewById(R.id.tv_exception);
            this.root = root;
        }
    }
}
