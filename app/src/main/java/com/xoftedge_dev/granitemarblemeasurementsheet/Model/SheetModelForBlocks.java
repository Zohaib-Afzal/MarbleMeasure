package com.xoftedge_dev.granitemarblemeasurementsheet.Model;

public class SheetModelForBlocks {
    public int id;
    public String length, width, height, result;

    public SheetModelForBlocks(int id, String length, String width, String height, String result) {
        this.id = id;
        this.length = length;
        this.width = width;
        this.height = height;
        this.result = result;
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
}
