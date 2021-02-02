package com.xoftedge_dev.granitemarblemeasurementsheet;

public interface savedSheetClickListener {
    void onDeleteIconClickListener(int position, String sheetNumber);
    void onUpdateIconClickListener(int position, String sheetNumber);
    void onViewIconClickListener(int position, String sheetNumber);
    void onShareIconClickListener(String sheetNumber);
}
