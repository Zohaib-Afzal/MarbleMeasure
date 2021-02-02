package com.xoftedge_dev.granitemarblemeasurementsheet.Model;

public class SavedSheetModel {
    String id,partyName, date, serial, length, width, height, result, sheetNumber;

    public SavedSheetModel(String id, String partyName, String date, String serial, String length, String width, String height, String result, String sheetNumber) {
        this.id = id;
        this.partyName = partyName;
        this.date = date;
        this.serial = serial;
        this.length = length;
        this.width = width;
        this.height = height;
        this.result = result;
        this.sheetNumber = sheetNumber;
    }

    public SavedSheetModel(String id, String partyName, String date, String serial, String length, String width, String result, String sheetNumber) {
        this.id = id;
        this.partyName = partyName;
        this.date = date;
        this.serial = serial;
        this.length = length;
        this.width = width;
        this.result = result;
        this.sheetNumber = sheetNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(String sheetNumber) {
        this.sheetNumber = sheetNumber;
    }
}
