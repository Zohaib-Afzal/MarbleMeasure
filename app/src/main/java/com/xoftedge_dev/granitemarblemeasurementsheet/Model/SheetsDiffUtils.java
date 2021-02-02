package com.xoftedge_dev.granitemarblemeasurementsheet.Model;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;
import java.util.List;

public class SheetsDiffUtils extends DiffUtil.Callback {
    private List<sheetModelList> oldSheetList;
    private List<sheetModelList> newSheetList;

    public SheetsDiffUtils(List<sheetModelList> oldSheetList, List<sheetModelList> newSheetList) {
        this.oldSheetList = oldSheetList;
        this.newSheetList = newSheetList;
    }

    @Override
    public int getOldListSize() {
        return oldSheetList.size();
    }

    @Override
    public int getNewListSize() {
        return newSheetList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldSheetList.get(oldItemPosition).getId() == newSheetList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldSheetList.get(oldItemPosition).getResult().equals(newSheetList.get(newItemPosition).getResult());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
