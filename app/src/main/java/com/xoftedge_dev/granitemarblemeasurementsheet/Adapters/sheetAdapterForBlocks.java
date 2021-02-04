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
import com.xoftedge_dev.granitemarblemeasurementsheet.Model.SheetModelList;
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

    public sheetAdapterForBlocks(Context context, List<SheetModelForBlocks> sheetListForBlocks) {
        this.context = context;
        this.sheetListForBlocks = sheetListForBlocks;
        this.databaseHelper =  new DatabaseHelper(context);
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

            SheetModelForBlocks item = sheetListForBlocks.get(position);
            holder.serial.setText(String.valueOf(position+1));
            holder.length.setText(item.getLength());
            holder.width.setText(item.getWidth());
            holder.height.setText(item.getHeight());
            holder.result.setText(item.getResult());

        }else{
            return;
        }

    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView serial, result;
        EditText length, width, height;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serial = (TextView)itemView.findViewById(R.id.serialTextView);
            result = (TextView)itemView.findViewById(R.id.resultTextView);
            length = (EditText) itemView.findViewById(R.id.lengthEditText);
            length.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    SheetModelForBlocks item = sheetListForBlocks.get((getAdapterPosition()));
                    item.setLength(editable.toString());
                    String currentResult = performCalculations(item);
                    result.setText(currentResult);
                    sheetListForBlocks.get(getAdapterPosition()).setResult(currentResult);
                    calculateSubtotal();
                }
            });
            width = (EditText)itemView.findViewById(R.id.widthEditText);
            width.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    SheetModelForBlocks item = sheetListForBlocks.get((getAdapterPosition()));
                    item.setWidth(editable.toString());
                    String currentResult = performCalculations(item);
                    result.setText(currentResult);
                    sheetListForBlocks.get(getAdapterPosition()).setResult(currentResult);
                    calculateSubtotal();
                }
            });
            height = (EditText)itemView.findViewById(R.id.heightEditText);
            height.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    SheetModelForBlocks item = sheetListForBlocks.get((getAdapterPosition()));
                    item.setHeight(editable.toString());
                    String currentResult = performCalculations(item);
                    result.setText(currentResult);
                    sheetListForBlocks.get(getAdapterPosition()).setResult(currentResult);
                    calculateSubtotal();
                }
            });
        }
    }

    public String performCalculations(SheetModelForBlocks value) {
        // Early Exit if you don't have the required data to perform the calculation
        if (value.width.isEmpty() || value.length.isEmpty() || value.height.isEmpty()) {
            return "";
        }
        double result = 0.0;

        if (choice1.equals(choice2)) {
            result = Double.parseDouble(value.getLength()) * Double.parseDouble(value.getWidth()) * Double.parseDouble(value.getHeight());
        } else {

            if (choice1.equals("Inch(in)") && choice2.equals("Foot(ft)")) {
                result = ((Double.parseDouble(value.getLength()) / 12) * (Double.parseDouble(value.getWidth()) / 12) * (Double.parseDouble(value.getHeight()) / 12));
            } else if (choice1.equals("Inch(in)") && choice2.equals("Meter(m)")) {
                result = ((Double.parseDouble(value.getLength()) / 39.37008) * (Double.parseDouble(value.getWidth()) / 39.37008) * (Double.parseDouble(value.getHeight()) / 39.37008));
            } else if (choice1.equals("Inch(in)") && choice2.equals("Centimeter(cm)")) {
                result = ((Double.parseDouble(value.getLength()) * 2.54) * (Double.parseDouble(value.getWidth()) * 2.54) * (Double.parseDouble(value.getHeight()) * 2.54));
            } else if (choice1.equals("Foot(ft)") && choice2.equals("Inch(in)")) {
                result = ((Double.parseDouble(value.getLength()) * 12) * (Double.parseDouble(value.getWidth()) * 12) * (Double.parseDouble(value.getHeight()) * 12));
            } else if (choice1.equals("Foot(ft)") && choice2.equals("Meter(m)")) {
                result = ((Double.parseDouble(value.getLength()) / 3.280484) * (Double.parseDouble(value.getWidth()) / 3.280484) * (Double.parseDouble(value.getHeight()) / 3.280484));
            } else if (choice1.equals("Foot(ft)") && choice2.equals("Centimeter(cm)")) {
                result = ((Double.parseDouble(value.getLength()) * 30.48) * (Double.parseDouble(value.getWidth()) * 30.48) * (Double.parseDouble(value.getHeight()) * 30.48));
            } else if (choice1.equals("Meter(m)") && choice2.equals("Foot(ft)")) {
                result = ((Double.parseDouble(value.getLength()) * 3.280484) * (Double.parseDouble(value.getWidth()) * 3.280484) * (Double.parseDouble(value.getHeight()) * 3.280484));
            } else if (choice1.equals("Meter(m)") && choice2.equals("Inch(in)")) {
                result = ((Double.parseDouble(value.getLength()) * 39.37008) * (Double.parseDouble(value.getWidth()) * 39.37008) * (Double.parseDouble(value.getHeight()) * 39.37008));
            } else if (choice1.equals("Meter(m)") && choice2.equals("Centimeter(cm)")) {
                result = ((Double.parseDouble(value.getLength()) * 100) * (Double.parseDouble(value.getWidth()) * 100) * (Double.parseDouble(value.getHeight()) * 100) );
            } else if (choice1.equals("Centimeter(cm)") && choice2.equals("Foot(ft)")) {
                result = ((Double.parseDouble(value.getLength()) / 0.032808) * (Double.parseDouble(value.getWidth()) / 0.032808) * (Double.parseDouble(value.getHeight()) / 0.032808));
            } else if (choice1.equals("Centimeter(cm)") && choice2.equals("Inch(in)")) {
                result = ((Double.parseDouble(value.getLength()) / 0.393701) * (Double.parseDouble(value.getWidth()) / 0.393701) * (Double.parseDouble(value.getHeight()) / 0.393701));
            } else if (choice1.equals("Centimeter(cm)") && choice2.equals("Meter(m)")) {
                result = ((Double.parseDouble(value.getLength()) / 100) * (Double.parseDouble(value.getWidth()) / 100) * (Double.parseDouble(value.getHeight()) / 100));
            }
        }
        //Must Updated the model class so that it has shows the
        //last calculated value when view is recycled
        result = roundTotal(result,4);

        value.setResult(String.valueOf(result));
        calculateSubtotal();
        return String.valueOf(result);
    }


    public Double calculateSubtotal() {
        total = 0.0;
        for (int i = 0; i < sheetListForBlocks.size(); i++) {
            if (!sheetListForBlocks.get(i).getResult().isEmpty())
                total = Double.parseDouble(sheetListForBlocks.get(i).getResult()) + total;
            MainSheetFragment.subTotalTextView.setText("Sub Total: " + roundTotal(total, 4));

        }
        MainSheetFragment.result = total;
        return total;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return sheetListForBlocks.size();
    }

    public void copyPreviousRow() {
        SheetModelForBlocks item = new SheetModelForBlocks();
        item.setHeight("");
        item.setWidth("");
        int index = 0;
            for (int i = 0; i < sheetListForBlocks.size(); i++) {
                item.setResult(sheetListForBlocks.get(i).getResult());
                if (!item.getResult().equals("")) {
                    index = i;
                    item.setLength(sheetListForBlocks.get(i).getLength());
                    item.setWidth(sheetListForBlocks.get(i).getWidth());
                    item.setHeight(sheetListForBlocks.get(i).getHeight());
                }
            }

            if (sheetListForBlocks.size() > index + 1) {
                if(sheetListForBlocks.get(index + 1).getResult().equals("") && sheetListForBlocks.get(index + 1).getLength().equals("") && sheetListForBlocks.get(index + 1).getWidth().equals("") && sheetListForBlocks.get(index + 1).getHeight().equals("")) {
                    sheetListForBlocks.get(index + 1).setLength(item.getLength());
                    sheetListForBlocks.get(index + 1).setWidth(item.getWidth());
                    sheetListForBlocks.get(index + 1).setHeight(item.getHeight());
                }

            } else {
                Toast.makeText(context, "Please add an empty row to duplicate previous one", Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();

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
    private double roundTotal(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

}
