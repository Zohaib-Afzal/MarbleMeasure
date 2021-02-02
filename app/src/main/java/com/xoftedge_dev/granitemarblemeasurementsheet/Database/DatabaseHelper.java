package com.xoftedge_dev.granitemarblemeasurementsheet.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.xoftedge_dev.granitemarblemeasurementsheet.Adapters.savedSheetsAdapter;
import com.xoftedge_dev.granitemarblemeasurementsheet.MainSheetFragment;
import com.xoftedge_dev.granitemarblemeasurementsheet.Model.SheetModelForBlocks;
import com.xoftedge_dev.granitemarblemeasurementsheet.Model.sheetModelList;
import com.xoftedge_dev.granitemarblemeasurementsheet.util.UtilDatabase;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private savedSheetsAdapter adapter;
    public DatabaseHelper(Context context) {
        super(context,UtilDatabase.DATABASE_NAME, null, UtilDatabase.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("Creating Database Table", UtilDatabase.TABLE_NAME_SLAB);
        String SHEET_TABLE_SLAB = "CREATE TABLE "+UtilDatabase.TABLE_NAME_SLAB+" ( "+
                UtilDatabase.COL_ID+" INTEGER PRIMARY KEY, "+
                UtilDatabase.COL_SL+" INTEGER, "+
                UtilDatabase.COL_LENGTH+" TEXT, "+
                UtilDatabase.COL_WIDTH+" TEXT, "+
                UtilDatabase.COL_RESULT+" TEXT, "+
                UtilDatabase.COL_TYPE+" TEXT, "+
                UtilDatabase.COL_SHEET_NUMBER+" TEXT, "+
                UtilDatabase.COL_DATE+" TEXT, "+
                UtilDatabase.COL_PARTY_NAME+" TEXT, "+
                UtilDatabase.COL_CHOICE_1+" TEXT, "+
                UtilDatabase.COL_CHOICE_2+" TEXT)";

        String SHEET_TABLE_BLOCK = "CREATE TABLE "+UtilDatabase.TABLE_NAME_BLOCK+" ( "+
                UtilDatabase.COL_ID+" INTEGER PRIMARY KEY, "+
                UtilDatabase.COL_SL+" INTEGER, "+
                UtilDatabase.COL_LENGTH+" TEXT, "+
                UtilDatabase.COL_WIDTH+" TEXT, "+
                UtilDatabase.COL_RESULT+" TEXT, "+
                UtilDatabase.COL_TYPE+" TEXT, "+
                UtilDatabase.COL_SHEET_NUMBER+" TEXT, "+
                UtilDatabase.COL_DATE+" TEXT, "+
                UtilDatabase.COL_PARTY_NAME+" TEXT, "+
                UtilDatabase.COL_HEIGHT+" TEXT, "+
                UtilDatabase.COL_CHOICE_1+" TEXT, "+
                UtilDatabase.COL_CHOICE_2+" TEXT)";

        try{
            db.execSQL(SHEET_TABLE_SLAB);
            db.execSQL(SHEET_TABLE_BLOCK);
            Log.e("Database Creation", "Database Created Successfully");
        }catch (SQLException e){
            e.printStackTrace();
            Log.e("Database Creation", "Error occured while creating Database");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+UtilDatabase.TABLE_NAME_SLAB);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+UtilDatabase.TABLE_NAME_BLOCK);
    }

    public Cursor getSelectedSpinnerItemsForSlabs(String sheetNo){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+UtilDatabase.COL_CHOICE_1+ ", "+UtilDatabase.COL_CHOICE_2+" FROM "+
                UtilDatabase.TABLE_NAME_SLAB+" WHERE "+UtilDatabase.COL_SHEET_NUMBER+"='"+sheetNo+"'" , null);
        return cursor;
    }

    public Cursor getSelectedSpinnerItemsForBlocks(String sheetNo){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+UtilDatabase.COL_CHOICE_1+ ", "+UtilDatabase.COL_CHOICE_2+" FROM "+
                UtilDatabase.TABLE_NAME_BLOCK+" WHERE "+UtilDatabase.COL_SHEET_NUMBER+"='"+sheetNo+"'" , null);
        return cursor;
    }

//    public boolean addOrUpdateSheet(int id, int serial, String length, String width, String result){
//
//        SQLiteDatabase db = getWritableDatabase();
//
//        String QUERY = "INSERT OR REPLACE INTO "+TABLE_NAME+"(id, serial, length, width, result)VALUES("
//                +id+", "+serial+", "+length+", "+width+", "+result+")";
//
//        try {
//            db.execSQL(QUERY);
//        }catch (SQLException e){
//            return false;
//        }
//
//        return true;
//    }

    public boolean addSheetBlock(List<SheetModelForBlocks> providedSheet, String type, long sheetNumber, String date, String partyName, String choice1, String choice2){
        SQLiteDatabase db = getWritableDatabase();
        String serial, length, width, result, height;
        long response = 0;

        for (SheetModelForBlocks sheet:providedSheet
        ) {

            serial = String.valueOf(sheet.getId());
            length = sheet.getLength();
            width = sheet.getWidth();
            result = sheet.getResult();
            height = sheet.getHeight();
            Log.d("Adding Data", serial + ", " + length + ", " + width + ", " + result);

            if (!serial.isEmpty() && !length.isEmpty() && !width.isEmpty() && !result.isEmpty()){
                ContentValues contentValues = new ContentValues();
                contentValues.put(UtilDatabase.COL_SL, serial);
                contentValues.put(UtilDatabase.COL_LENGTH, length);
                contentValues.put(UtilDatabase.COL_WIDTH, width);
                contentValues.put(UtilDatabase.COL_HEIGHT, height);
                contentValues.put(UtilDatabase.COL_RESULT, result);
                contentValues.put(UtilDatabase.COL_TYPE, type);
                contentValues.put(UtilDatabase.COL_SHEET_NUMBER, sheetNumber);
                contentValues.put(UtilDatabase.COL_DATE, date);
                contentValues.put(UtilDatabase.COL_PARTY_NAME, partyName);
                contentValues.put(UtilDatabase.COL_CHOICE_1, String.valueOf(choice1));
                contentValues.put(UtilDatabase.COL_CHOICE_2, String.valueOf(choice2));
                response = db.insert(UtilDatabase.TABLE_NAME_BLOCK, null, contentValues);
            }
        }
        if (response>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean addSheetSlab(List<sheetModelList> providedSheet, String type, long sheetNumber, String date, String partyName, String choice1, String choice2){
        SQLiteDatabase db = getWritableDatabase();
        String serial, length, width, result;
        long response = 0;

        for (sheetModelList sheet:providedSheet
             ) {

            serial = String.valueOf(sheet.getId());
            length = sheet.getLength();
            width = sheet.getWidth();
            result = sheet.getResult();
            Log.d("Adding Data", serial + ", " + length + ", " + width + ", " + result);

            if (!serial.isEmpty() && !length.isEmpty() && !width.isEmpty() && !result.isEmpty()){
                ContentValues contentValues = new ContentValues();
                contentValues.put(UtilDatabase.COL_SL, serial);
                contentValues.put(UtilDatabase.COL_LENGTH, length);
                contentValues.put(UtilDatabase.COL_WIDTH, width);
                contentValues.put(UtilDatabase.COL_RESULT, result);
                contentValues.put(UtilDatabase.COL_TYPE, type);
                contentValues.put(UtilDatabase.COL_SHEET_NUMBER, sheetNumber);
                contentValues.put(UtilDatabase.COL_DATE, date);
                contentValues.put(UtilDatabase.COL_PARTY_NAME, partyName);
                contentValues.put(UtilDatabase.COL_CHOICE_1, String.valueOf(choice1));
                contentValues.put(UtilDatabase.COL_CHOICE_2, String.valueOf(choice2));
                response = db.insert(UtilDatabase.TABLE_NAME_SLAB, null, contentValues);
            }
        }
        if (response>0){
            return true;
        }else{
            return false;
        }

    }

    public long countTotalNumberOfSheetsSlab(){

        SQLiteDatabase db = getReadableDatabase();

        long count = DatabaseUtils.queryNumEntries(db, UtilDatabase.TABLE_NAME_SLAB);

        return count;
    }

    public long countTotalNumberOfSheetsBlock(){

        SQLiteDatabase db = getReadableDatabase();

        long count = DatabaseUtils.queryNumEntries(db, UtilDatabase.TABLE_NAME_BLOCK);

        return count;
    }

    public Cursor getAllDataSlab(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+UtilDatabase.TABLE_NAME_SLAB+" GROUP BY "+UtilDatabase.COL_SHEET_NUMBER, null);
        return cursor;
    }

    public Cursor getAllDataBlock(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+UtilDatabase.TABLE_NAME_BLOCK+" GROUP BY "+UtilDatabase.COL_SHEET_NUMBER, null);
        return cursor;
    }

    public void deleteRecordSlab(String sheetNumber) {
        SQLiteDatabase db = getReadableDatabase();
        int s_no = Integer.valueOf(sheetNumber);
        String QUERY = "DELETE FROM "+UtilDatabase.TABLE_NAME_SLAB+" WHERE "+UtilDatabase.COL_SHEET_NUMBER+" = "+s_no;
        db.execSQL(QUERY);
    }

    public void deleteRecordBlock(String sheetNumber) {
        SQLiteDatabase db = getReadableDatabase();
        int s_no = Integer.valueOf(sheetNumber);
        String QUERY = "DELETE FROM "+UtilDatabase.TABLE_NAME_BLOCK+" WHERE "+UtilDatabase.COL_SHEET_NUMBER+" = "+s_no;
        db.execSQL(QUERY);
    }

    public Cursor getRecordToUpdateSlab(String sheetNumber) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UtilDatabase.TABLE_NAME_SLAB + " WHERE "+UtilDatabase.COL_SHEET_NUMBER+" = "+sheetNumber, null);

        return cursor;
    }

    public Cursor getRecordToUpdateBlock(String sheetNumber) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UtilDatabase.TABLE_NAME_BLOCK + " WHERE "+UtilDatabase.COL_SHEET_NUMBER+" = "+sheetNumber, null);

        return cursor;
    }

    public void updateRecordsBlocks(List<SheetModelForBlocks> providedSheet, String sheetType, long sheetNumber, String date, String partyName, int choice1, int choice2){
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        String serial, length, width, result, height;
        long response = 0;
        for (SheetModelForBlocks sheet:providedSheet) {

            length = sheet.getLength();
            width = sheet.getWidth();
            result = sheet.getResult();
            serial = String.valueOf(sheet.getId());
            height = sheet.getHeight();

            if (!serial.isEmpty() && !length.isEmpty() && !width.isEmpty() && !result.isEmpty()){
                values.put(UtilDatabase.COL_SL, serial);
                values.put(UtilDatabase.COL_LENGTH, length);
                values.put(UtilDatabase.COL_WIDTH, width);
                values.put(UtilDatabase.COL_HEIGHT, height);
                values.put(UtilDatabase.COL_RESULT, result);
                values.put(UtilDatabase.COL_TYPE, sheetType);
                values.put(UtilDatabase.COL_SHEET_NUMBER, sheetNumber);
                values.put(UtilDatabase.COL_DATE, date);
                values.put(UtilDatabase.COL_PARTY_NAME, partyName);
                values.put(UtilDatabase.COL_CHOICE_1,choice1);
                values.put(UtilDatabase.COL_CHOICE_2,choice2);
                db.update(UtilDatabase.TABLE_NAME_BLOCK, values, UtilDatabase.COL_SHEET_NUMBER + " = ? AND " + UtilDatabase.COL_SL + " = ?",
                        new String[]{String.valueOf(sheetNumber), serial});
            }
        }
    }

    public void updateRecordsSlab(List<sheetModelList> providedSheet, String sheetType, long sheetNumber, String date, String partyName, int choice1, int choice2){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        String serial, length, width, result;
        long response = 0;
        for (sheetModelList sheet:providedSheet) {
            length = sheet.getLength();
            width = sheet.getWidth();
            result = sheet.getResult();
            serial = String.valueOf(sheet.getId());

            if (!serial.isEmpty() && !length.isEmpty() && !width.isEmpty() && !result.isEmpty()){
                values.put(UtilDatabase.COL_SL, serial);
                values.put(UtilDatabase.COL_LENGTH, length);
                values.put(UtilDatabase.COL_WIDTH, width);
                values.put(UtilDatabase.COL_RESULT, result);
                values.put(UtilDatabase.COL_TYPE, sheetType);
                values.put(UtilDatabase.COL_SHEET_NUMBER, sheetNumber);
                values.put(UtilDatabase.COL_DATE, date);
                values.put(UtilDatabase.COL_PARTY_NAME, partyName);
                values.put(UtilDatabase.COL_CHOICE_1, choice1);
                values.put(UtilDatabase.COL_CHOICE_2, choice2);
                db.update(UtilDatabase.TABLE_NAME_SLAB, values, UtilDatabase.COL_SHEET_NUMBER + " = ? AND " + UtilDatabase.COL_SL + " = ?",
                        new String[]{String.valueOf(sheetNumber), serial});
            }
        }



    }

    private String getSerialNumberSlab(String length, String width) {
        SQLiteDatabase db = getReadableDatabase();
        String serial = "";
        Cursor cursor = db.rawQuery("SELECT "+UtilDatabase.COL_SL+" FROM "+UtilDatabase.TABLE_NAME_SLAB+" WHERE "+UtilDatabase.COL_LENGTH+"='"+length+"' AND "+UtilDatabase.COL_WIDTH+"='"+width+"'", null);
        if (cursor.moveToNext()){
            serial = cursor.getString(0);
        }
        return serial;
    }

    private String getSerialNumberBlock(String length, String width) {
        SQLiteDatabase db = getReadableDatabase();
        String serial = "";
        Cursor cursor = db.rawQuery("SELECT "+UtilDatabase.COL_SL+" FROM "+UtilDatabase.TABLE_NAME_BLOCK+" WHERE "+UtilDatabase.COL_LENGTH+"='"+length+"' AND "+UtilDatabase.COL_WIDTH+"='"+width+"'", null);
        if (cursor.moveToNext()){
            serial = cursor.getString(0);
        }
        return serial;
    }

    public Cursor getDataToGeneratePdfSlab(String sheetNo) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+UtilDatabase.TABLE_NAME_SLAB+" WHERE "+UtilDatabase.COL_SHEET_NUMBER+"="+sheetNo, null);

        return cursor;
    }

    public Cursor getDataToGeneratePdfBlock(String sheetNo) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+UtilDatabase.TABLE_NAME_BLOCK+" WHERE "+UtilDatabase.COL_SHEET_NUMBER+"="+sheetNo, null);

        return cursor;
    }

    public Cursor getPartyNameForSlab(String sheetNumber) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+UtilDatabase.TABLE_NAME_SLAB+" WHERE "+UtilDatabase.COL_SHEET_NUMBER+"="+sheetNumber, null);
        return  cursor;
    }

    public Cursor getPartyNameForBlock(String sheetNumber) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+UtilDatabase.TABLE_NAME_BLOCK+" WHERE "+UtilDatabase.COL_SHEET_NUMBER+"="+sheetNumber, null);
        return  cursor;
    }
}
