package com.easyder.carmonitor.interfaces;

/**
 * Created by ljn on 2017-11-20.
 */

public interface CreatMaintenanceOrderListener {

    void showSelectPlateColorDialog();

    void showSelectAppointmentTimeDialog();

    void onSuccessCommitData(String orderNumber);

    void updateDialogFocusable(boolean b);

}
