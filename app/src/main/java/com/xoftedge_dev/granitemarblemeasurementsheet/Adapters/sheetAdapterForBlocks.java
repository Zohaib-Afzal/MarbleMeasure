package com.xoftedge_dev.granitemarblemeasurementsheet.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.xoftedge_dev.granitemarblemeasurementsheet.Database.DatabaseHelper;
import com.xoftedge_dev.granitemarblemeasurementsheet.MainSheetFragment;
import com.xoftedge_dev.granitemarblemeasurementsheet.Model.SheetModelForBlocks;
import com.xoftedge_dev.granitemarblemeasurementsheet.R;
import com.xoftedge_dev.granitemarblemeasurementsheet.util.UtilDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static com.xoftedge_dev.granitemarblemeasurementsheet.MainSheetFragment.choice1;
import static com.xoftedge_dev.granitemarblemeasurementsheet.MainSheetFragment.choice2;
import static com.xoftedge_dev.granitemarblemeasurementsheet.MainSheetFragment.spinnerPosition1;
import static com.xoftedge_dev.granitemarblemeasurementsheet.MainSheetFragment.spinnerPosition2;
import static com.xoftedge_dev.granitemarblemeasurementsheet.MainSheetFragment.subTotalTextView;

public class sheetAdapterForBlocks extends RecyclerView.Adapter<sheetAdapterForBlocks.ViewHolder> {
    Context context;
    List<SheetModelForBlocks> sheetListForBlocks;
    List<SheetModelForBlocks> getListForPdf = new ArrayList<>();
    String length, width, height, sheetNo;
    public static Double grandTotal = 0.0;
    private File pdfFile;
    public static Double total = 0.0;
    DatabaseHelper databaseHelper;
    private Double subTotal;

    public sheetAdapterForBlocks(Context context, List<SheetModelForBlocks> sheetListForBlocks, DatabaseHelper databaseHelper) {
        this.context = context;
        this.sheetListForBlocks = sheetListForBlocks;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout_for_block, parent, false);
        return new sheetAdapterForBlocks.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (sheetListForBlocks != null && sheetListForBlocks.size() > 0){

            SheetModelForBlocks sheet_list = sheetListForBlocks.get(position);
            holder.serial.setText(String.valueOf(position+1));
            holder.length.setText(sheet_list.getLength());
            holder.width.setText(sheet_list.getWidth());
            holder.height.setText(sheet_list.getHeight());
            holder.result.setText(sheet_list.getResult());

            holder.length.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (!holder.length.getText().toString().isEmpty() && !holder.width.getText().toString().isEmpty() && !holder.height.getText().toString().isEmpty()) {

                        performCalculations(holder, total, choice1, choice2);
                        grandTotal = 0.0;
                    }
                }
            });

            holder.width.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!holder.length.getText().toString().isEmpty() && !holder.width.getText().toString().isEmpty() && !holder.height.getText().toString().isEmpty()) {
                        performCalculations(holder, total, choice1, choice2);
                        grandTotal = 0.0;
                    }


                }
            });

            holder.height.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!holder.length.getText().toString().isEmpty() && !holder.width.getText().toString().isEmpty() && !holder.height.getText().toString().isEmpty()){
                        performCalculations(holder, total, choice1, choice2);
                        grandTotal = 0.0;
                    }
                }
            });
        }else{
            return;
        }

    }

    private void performCalculations(ViewHolder holder, Double total, String choice1, String choice2) {

        if (choice1.equals(choice2)){
            if (!holder.length.getText().toString().isEmpty() && !holder.width.getText().toString().isEmpty()) {

                length = holder.length.getText().toString();
                width = holder.width.getText().toString();
                height = holder.height.getText().toString();

                Double result = Double.parseDouble(length) * Double.parseDouble(width) * Double.parseDouble(height);


                Double subTotal = calculateSubtotal();

                getResults(holder, subTotal, result);
            }
        }else{
            if (choice1.equals("Inch(in)") && choice2.equals("Foot(ft)")){
                grandTotal = 0.0;
                length = holder.length.getText().toString();
                width = holder.width.getText().toString();
                height = holder.height.getText().toString();
                Double result = ((Double.parseDouble(length) / 12) * (Double.parseDouble(width) / 12) * (Double.parseDouble(height) / 12));
                grandTotal = grandTotal+result;
                getResults(holder, total, result);

            }else if (choice1.equals("Inch(in)") && choice2.equals("Meter(m)")){
                grandTotal = 0.0;
                length = holder.length.getText().toString();
                width = holder.width.getText().toString();
                height = holder.height.getText().toString();
                Double result = ((Double.parseDouble(length) / 39.37008) * (Double.parseDouble(width) / 39.37008) * (Double.parseDouble(height) / 39.37008));
                grandTotal = grandTotal+result;
                getResults(holder, total, result);

            }else if (choice1.equals("Inch(in)") && choice2.equals("Centimeter(cm)")){
                grandTotal = 0.0;
                length = holder.length.getText().toString();
                width = holder.width.getText().toString();
                height = holder.height.getText().toString();
                Double result = ((Double.parseDouble(length) * 2.54) * (Double.parseDouble(width) * 2.54) * (Double.parseDouble(height) * 2.54));
                grandTotal = grandTotal + result;
                getResults(holder, total, result);

            }else if (choice1.equals("Foot(ft)") && choice2.equals("Inch(in)")){
                grandTotal = 0.0;
                length = holder.length.getText().toString();
                width = holder.width.getText().toString();
                height = holder.height.getText().toString();
                Double result = ((Double.parseDouble(length) * 12) * (Double.parseDouble(width) * 12) * (Double.parseDouble(height) * 12));
                grandTotal = grandTotal+result;
                getResults(holder, total, result);

            }else if (choice1.equals("Foot(ft)") && choice2.equals("Meter(m)")){
                grandTotal = 0.0;
                length = holder.length.getText().toString();
                width = holder.width.getText().toString();
                height = holder.height.getText().toString();
                Double result = ((Double.parseDouble(length) / 3.280484) * (Double.parseDouble(width) / 3.280484) * (Double.parseDouble(height) / 3.280484));
                grandTotal = grandTotal + result;
                getResults(holder, total, result);

            }else if (choice1.equals("Foot(ft)") && choice2.equals("Centimeter(cm)")){
                grandTotal = 0.0;
                length = holder.length.getText().toString();
                width = holder.width.getText().toString();
                height = holder.height.getText().toString();
                Double result = ((Double.parseDouble(length) * 30.48) * (Double.parseDouble(width) * 30.48) * (Double.parseDouble(height) * 30.48));
                grandTotal = grandTotal + result;
                getResults(holder, total, result);

            }else if (choice1.equals("Meter(m)") && choice2.equals("Foot(ft)")){
                grandTotal = 0.0;
                length = holder.length.getText().toString();
                width = holder.width.getText().toString();
                height = holder.height.getText().toString();
                Double result = ((Double.parseDouble(length) * 3.280484) * (Double.parseDouble(width) * 3.280484) * (Double.parseDouble(height) * 3.280484));
                grandTotal = grandTotal + result;
                getResults(holder, total, result);

            }else if(choice1.equals("Meter(m)") && choice2.equals("Inch(in)")){
                grandTotal = 0.0;
                length = holder.length.getText().toString();
                width = holder.width.getText().toString();
                height = holder.height.getText().toString();
                Double result = ((Double.parseDouble(length) * 39.37008) * (Double.parseDouble(width) * 39.37008) * (Double.parseDouble(height) * 39.37008));
                grandTotal = grandTotal+result;
                getResults(holder, total, result);

            }else if (choice1.equals("Meter(m)") && choice2.equals("Centimeter(cm)")){
                grandTotal = 0.0;
                length = holder.length.getText().toString();
                width = holder.width.getText().toString();
                height = holder.height.getText().toString();
                Double result = ((Double.parseDouble(length) * 100) * (Double.parseDouble(width) * 100) * (Double.parseDouble(height) * 100));
                grandTotal = grandTotal+result;
                getResults(holder, total, result);

            }else if (choice1.equals("Centimeter(cm)") && choice2.equals("Foot(ft)")){
                grandTotal = 0.0;
                length = holder.length.getText().toString();
                width = holder.width.getText().toString();
                height = holder.height.getText().toString();
                Double result = ((Double.parseDouble(length) / 0.032808) * (Double.parseDouble(width) / 0.032808) * (Double.parseDouble(height) / 0.032808));
                grandTotal = grandTotal + result;
                getResults(holder, total, result);

            }else if (choice1.equals("Centimeter(cm)") && choice2.equals("Inch(in)")){
                grandTotal = 0.0;
                length = holder.length.getText().toString();
                width = holder.width.getText().toString();
                height = holder.height.getText().toString();
                Double result = ((Double.parseDouble(length) / 0.393701) * (Double.parseDouble(width) / 0.393701) * (Double.parseDouble(height) / 0.393701));
                grandTotal = grandTotal+result;
                getResults(holder, total, result);

            }else if(choice1.equals("Centimeter(cm)") && choice2.equals("Meter(m)")){
                grandTotal = 0.0;
                length = holder.length.getText().toString();
                width = holder.width.getText().toString();
                height = holder.height.getText().toString();
                Double result = ((Double.parseDouble(length) / 100) * (Double.parseDouble(width) / 100) * (Double.parseDouble(height) / 100));
                grandTotal = grandTotal + result;
                getResults(holder, total, result);

            }
        }
    }

    private void getResults(ViewHolder holder, Double total, Double result) {
        if (!width.isEmpty() && !length.isEmpty() && !height.isEmpty()) {
            grandTotal = 0.0;
            holder.result.setText(String.valueOf(result));
            int id = Integer.parseInt(holder.serial.getText().toString());
            sheetListForBlocks.get(id - 1).setLength(length);
            sheetListForBlocks.get(id - 1).setWidth(width);
            sheetListForBlocks.get(id - 1).setHeight(height);
            sheetListForBlocks.get(id - 1).setResult(String.valueOf(result));
            total = total + result;
            MainSheetFragment.result = total;
            Toast.makeText(context, String.valueOf(total), Toast.LENGTH_SHORT).show();
        }
    }

    public Double calculateSubtotal(){
        total = 0.0;
        for (int i =0; i<sheetListForBlocks.size(); i++){
            if (!sheetListForBlocks.get(i).getResult().isEmpty())
                total = Double.parseDouble(sheetListForBlocks.get(i).getResult()) + total;
            MainSheetFragment.subTotalTextView.setText("Sub Total: "+ total);

        }

        return total;
    }

    @Override
    public int getItemCount() {
        return sheetListForBlocks.size();
    }

    public void copyPreviousRow() {
        String length="", width ="", result = "", height = "";
        int index = 0;
        for (int i=0; i<sheetListForBlocks.size(); i++){
            result = sheetListForBlocks.get(i).getResult();
            if (!result.equals("")){
                index = i;
                length = sheetListForBlocks.get(i).getLength();
                width = sheetListForBlocks.get(i).getWidth();
                height = sheetListForBlocks.get(i).getHeight();
            }
        }

        sheetListForBlocks.get(index+1).setLength(length);
        sheetListForBlocks.get(index+1).setWidth(width);
        sheetListForBlocks.get(index+1).setHeight(height);
        notifyDataSetChanged();

        Toast.makeText(context, "Index: "+ index +"\nLength: "+length+"\nWidth: "+width, Toast.LENGTH_SHORT).show();
    }

    public void saveSheetToDatabase(String getSheetType, String date, String partyName) {

        long rows = databaseHelper.countTotalNumberOfSheetsBlock();
        Toast.makeText(context, String.valueOf(rows), Toast.LENGTH_SHORT).show();
        sheetNo = String.valueOf(rows+1);

        Boolean response = databaseHelper.addSheetBlock(sheetListForBlocks, getSheetType, rows+1, date, partyName, String.valueOf(spinnerPosition1), String.valueOf(spinnerPosition2));
        if (response){
            Toast.makeText(context, "Saved Successfully!", Toast.LENGTH_SHORT).show();
            List<SheetModelForBlocks> listForPdf = selectRecordsForPdf();
            saveAsPdf(listForPdf, getSheetType, date, partyName);
            sheetListForBlocks.clear();
        }else{
            Toast.makeText(context, "Error occured while saving sheet to database", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAsPdf(List<SheetModelForBlocks> listForPdf, String getSheetType, String date, String partyName) {


        File graniteMarbleFolder = new File(context.getExternalFilesDir("/") + "/MasterMarble");

        if (!graniteMarbleFolder.exists()){
            graniteMarbleFolder.mkdir();
            Toast.makeText(context, "directory created", Toast.LENGTH_SHORT).show();
        }

        String pdfName = partyName+".pdf";

        pdfFile = new File(graniteMarbleFolder.getAbsolutePath(), pdfName);
        try {

            Document document = new Document(PageSize.A4);
            PdfPTable table = new PdfPTable(new float[]{0.7f, 1.6f, 1.6f, 1.6f, 2, 0.7f, 1.6f, 1.6f, 1.6f, 2});
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setFixedHeight(50);
            table.setTotalWidth(PageSize.A4.getWidth());
            table.setWidthPercentage(100);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell("SL");
            table.addCell("Length\n"+choice1);
            table.addCell("Width\n"+choice1);
            table.addCell("Height\n"+choice1);
            table.addCell("SFT\nsq. "+choice2);
            table.addCell("SL");
            table.addCell("Length\n"+choice1);
            table.addCell("Width\n"+choice1);
            table.addCell("Height\n"+choice1);
            table.addCell("SFT\nsq. "+choice2);
            table.setHeaderRows(1);
            PdfPCell[] cells = table.getRow(0).getCells();
            for (int j=0; j < cells.length; j++){
                cells[j].setBackgroundColor(BaseColor.GRAY);
            }
            for (int i =0 ; i<listForPdf.size(); i++){

                String serial = String.valueOf(listForPdf.get(i).getId());
                String length = listForPdf.get(i).getLength();
                String width = listForPdf.get(i).getWidth();
                String height = listForPdf.get(i).getHeight();
                String result = listForPdf.get(i).getResult();
                table.addCell(serial);
                table.addCell(length);
                table.addCell(width);
                table.addCell(height);
                table.addCell(result);

            }
            OutputStream output = new FileOutputStream(pdfFile);
            PdfWriter.getInstance(document, output);
            document.open();
            InputStream inputStream = context.getResources().getAssets().open("logo.jpg");
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleToFit(150.0f, 150.0f);
            image.setAlignment(Element.ALIGN_LEFT);
            document.add(image);
            document.addCreationDate();
            document.addCreator("Master Measure");
            Font f = new Font(Font.FontFamily.TIMES_ROMAN, 30.0f, Font.BOLD, BaseColor.BLACK);
            Font g = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLACK);
            Paragraph paragraph = new Paragraph(partyName+"\n", f);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            Paragraph paragraph1 = new Paragraph(date+"\n\n", g);
            paragraph1.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph1);
            document.add(table);
            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private List<SheetModelForBlocks> selectRecordsForPdf(){
        Cursor cursor = databaseHelper.getDataToGeneratePdfBlock(sheetNo);
        while (cursor.moveToNext()){
            getListForPdf.add(new SheetModelForBlocks(cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(9), cursor.getString(4)));
        }
        return getListForPdf;
    }

    public void updateSheetNow(String selectionType, String selectedDate, String partyName, long sheetNumber) {

        databaseHelper.updateRecordsBlocks(sheetListForBlocks, selectionType,sheetNumber, selectedDate, partyName, spinnerPosition1,spinnerPosition2);

    }

    public List<String> setSpinnerItems(String sheetNo) {
        Cursor cursor = databaseHelper.getSelectedSpinnerItemsForBlocks(sheetNo);
        List<String> positions = new ArrayList<>();
        if (cursor.moveToNext()){
            String spinner1Position = cursor.getString(0);
            String spinner2Position = cursor.getString(1);
            positions.add(spinner1Position);
            positions.add(spinner2Position);
        }
        return positions;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView serial, result;
        EditText length, width, height;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serial = (TextView)itemView.findViewById(R.id.serialTextView);
            result = (TextView)itemView.findViewById(R.id.resultTextView);
            length = (EditText) itemView.findViewById(R.id.lengthEditText);
            width = (EditText)itemView.findViewById(R.id.widthEditText);
            height = (EditText)itemView.findViewById(R.id.heightEditText);
        }
    }
}
