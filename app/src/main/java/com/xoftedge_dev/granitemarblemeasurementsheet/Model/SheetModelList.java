package com.xoftedge_dev.granitemarblemeasurementsheet.Model;

public class SheetModelList {
    public int id;
    public String length, width;
    public String result;

    public SheetModelList(int id, String length, String width, String result) {
        this.id = id;
        this.length = length;
        this.width = width;
        this.result = result;
    }

    public SheetModelList() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() { return width; }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
