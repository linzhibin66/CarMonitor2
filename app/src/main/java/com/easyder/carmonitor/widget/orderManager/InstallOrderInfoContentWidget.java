package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.InstallChangeStatusListener;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;

/**
 * Created by ljn on 2017-12-05.
 */

public class InstallOrderInfoContentWidget {

    private Context context;

    private View rootView;

    private InstallOrderBaseInfo installOrderitem;

    private InstallChangeStatusListener statusListener;

    public InstallOrderInfoContentWidget(Context context, InstallOrderBaseInfo installOrderitem) {
        this.context = context;
        this.installOrderitem = installOrderitem;

        rootView = View.inflate(context, R.layout.install_order_info_content, null);

        initView();

    }

    public View getView(){
        return rootView;
    }

    public void setInstallChangeStatusListener(InstallChangeStatusListener statusListener){
        this.statusListener = statusListener;
    }

    private void initView(){
        TextView install_time = (TextView) rootView.findViewById(R.id.intall_time_title_value);
        install_time.setText(installOrderitem.getFinishTime());

        TextView install_location = (TextView) rootView.findViewById(R.id.install_location_value);
        install_location.setText(installOrderitem.getInstallLocation());

        TextView install_count = (TextView) rootView.findViewById(R.id.install_count_value);
        install_count.setText(""+installOrderitem.getInstallCount());

        TextView install_type = (TextView) rootView.findViewById(R.id.install_type_value);
        install_type.setText(installOrderitem.getInstallTypeString());

        TextView install_order_contact = (TextView) rootView.findViewById(R.id.install_order_contact_value);
        install_order_contact.setText(installOrderitem.getContacts());

        TextView install_order_contact_phone = (TextView) rootView.findViewById(R.id.install_order_contact_phone_value);
        install_order_contact_phone.setText("");

        TextView install_order_carlist = (TextView) rootView.findViewById(R.id.install_order_carlist);

        install_order_carlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusListener != null){
                    statusListener.showInstallCarList(installOrderitem);
                }
            }
        });
    }


}
