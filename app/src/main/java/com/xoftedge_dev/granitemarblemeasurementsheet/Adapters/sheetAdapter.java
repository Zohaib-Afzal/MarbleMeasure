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
import com.xoftedge_dev.granitemarblemeasurementsheet.Model.sheetModelList;
import com.xoftedge_dev.granitemarblemeasurementsheet.R;
import com.xoftedge_dev.granitemarblemeasurementsheet.util.UtilDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.xoftedge_dev.granitemarblemeasurementsheet.MainSheetFragment.choice1;
import static com.xoftedge_dev.granitemarblemeasurementsheet.MainSheetFragment.choice2;
import static com.xoftedge_dev.granitemarblemeasurementsheet.MainSheetFragment.spinnerPosition1;
import static com.xoftedge_dev.granitemarblemeasurementsheet.MainSheetFragment.spinnerPosition2;

public class sheetAdapter extends RecyclerView.Adapter<sheetAdapter.ViewHolder> {

    Context context;
    List<sheetModelList> sheetList;
    List<sheetModelList> sheetListForPdf = new ArrayList<>();
    Double resultGot;
    String sheetNo;
    private File pdfFile;
    long rows;
    public static Double total = 0.0;
    DatabaseHelper databaseHelper;

    public sheetAdapter(Context context, List<sheetModelList> sheetList) {
        this.context = context;
        this.sheetList = sheetList;
        this.databaseHelper = new DatabaseHelper(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sheet_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (sheetList != null && sheetList.size() > 0) {
            sheetModelList item = sheetList.get((position));
            //holder.setIsRecyclable(false);
            holder.serial.setText(String.valueOf(position + 1));
            holder.length.setText(item.getLength());
            holder.width.setText(item.getWidth());
            holder.result.setText(item.getResult());
          //  performCalculations(holder, total, choice1, choice2, position);

            holder.length.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (!holder.length.getText().toString().isEmpty() && !holder.width.getText().toString().isEmpty()) {
                        item.setLength(s.toString());
                        item.setWidth(holder.width.getText().toString());
                        performCalculations(holder, total, choice1, choice2, holder.getAdapterPosition(), item.getWidth(), item.getLength());
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

                    if (!holder.length.getText().toString().isEmpty() && !holder.width.getText().toString().isEmpty()) {
                        item.setWidth(s.toString());
                        item.setLength(holder.length.getText().toString());
                        performCalculations(holder, total, choice1, choice2, holder.getAdapterPosition(), item.getWidth(), item.getLength());
                    }

                }
            });

        } else {
            return;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void performCalculations(ViewHolder holder, Double total, String choice1, String choice2, int position, String width, String length) {
        sheetModelList value = sheetList.get(position);
       // holder.itemView.;
       // if(!value.getWidth().equals(holder.width.getText().toString()) || !value.getLength().equals(holder.length.getText().toString())){
//            String newWidth = holder.width.getText().toString();
//            String newLength = holder.length.getText().toString();
        if(width.isEmpty() || length.isEmpty()){
            return;
        }
        String newWidth = width;
        String newLength = length;
            value.setLength(newLength);
            value.setWidth(newWidth);
       // }

        double result = 0.0;

        if (choice1.equals(choice2)) {
            if (!holder.length.getText().toString().isEmpty() && !holder.width.getText().toString().isEmpty()) {
                result = Double.parseDouble(value.getLength()) * Double.parseDouble(value.getWidth());
            }
        } else {
            if (!holder.length.getText().toString().isEmpty() && !holder.width.getText().toString().isEmpty()) {

                if (choice1.equals("Inch(in)") && choice2.equals("Foot(ft)")) {
                    result = ((Double.parseDouble(value.getLength()) / 12) * (Double.parseDouble(value.getWidth()) / 12));
                } else if (choice1.equals("Inch(in)") && choice2.equals("Meter(m)")) {
                    result = ((Double.parseDouble(value.getLength()) / 39.37008) * (Double.parseDouble(value.getWidth()) / 39.37008));
                } else if (choice1.equals("Inch(in)") && choice2.equals("Centimeter(cm)")) {
                    result = ((Double.parseDouble(value.getLength()) * 2.54) * (Double.parseDouble(value.getWidth()) * 2.54));
                } else if (choice1.equals("Foot(ft)") && choice2.equals("Inch(in)")) {
                    result = ((Double.parseDouble(value.getLength()) * 12) * (Double.parseDouble(value.getWidth()) * 12));
                } else if (choice1.equals("Foot(ft)") && choice2.equals("Meter(m)")) {
                    result = ((Double.parseDouble(value.getLength()) / 3.280484) * (Double.parseDouble(value.getWidth()) / 3.280484));
                } else if (choice1.equals("Foot(ft)") && choice2.equals("Centimeter(cm)")) {
                    result = ((Double.parseDouble(value.getLength()) * 30.48) * (Double.parseDouble(value.getWidth()) * 30.48));
                } else if (choice1.equals("Meter(m)") && choice2.equals("Foot(ft)")) {
                    result = ((Double.parseDouble(value.getLength()) * 3.280484) * (Double.parseDouble(value.getWidth()) * 3.280484));
                } else if (choice1.equals("Meter(m)") && choice2.equals("Inch(in)")) {
                    result = ((Double.parseDouble(value.getLength()) * 39.37008) * (Double.parseDouble(value.getWidth()) * 39.37008));
                } else if (choice1.equals("Meter(m)") && choice2.equals("Centimeter(cm)")) {
                    result = ((Double.parseDouble(value.getLength()) * 100) * (Double.parseDouble(value.getWidth()) * 100));
                } else if (choice1.equals("Centimeter(cm)") && choice2.equals("Foot(ft)")) {
                    result = ((Double.parseDouble(value.getLength()) / 0.032808) * (Double.parseDouble(value.getWidth()) / 0.032808));
                } else if (choice1.equals("Centimeter(cm)") && choice2.equals("Inch(in)")) {
                    result = ((Double.parseDouble(value.getLength()) / 0.393701) * (Double.parseDouble(value.getWidth()) / 0.393701));
                } else if (choice1.equals("Centimeter(cm)") && choice2.equals("Meter(m)")) {
                    result = ((Double.parseDouble(value.getLength()) / 100) * (Double.parseDouble(value.getWidth()) / 100));
                }
            }
        }

        value.setResult(String.valueOf(result));
        holder.result.setText(String.valueOf(result));
        calculateSubtotal();
    }

//    private void getResults(ViewHolder holder, Double total, Double result, int position) {
//        if (!sheetList.get(position).getWidth().isEmpty() && !sheetList.get(position).getLength().isEmpty()) {
//            holder.result.setText(String.valueOf(result));
//            int id = Integer.parseInt(holder.serial.getText().toString());
//            sheetModelList val = sheetList.get(id-1);
//            val.setLength(length);
//            val.setWidth(width);
//            val.setResult(String.valueOf(result));
//            total = total + result;
//            MainSheetFragment.result = total;
//            Toast.makeText(context, String.valueOf(total), Toast.LENGTH_SHORT).show();
//        }
//    }

    public Double calculateSubtotal() {
        total = 0.0;
        for (int i = 0; i < sheetList.size(); i++) {
            if (!sheetList.get(i).getResult().isEmpty())
                total = Double.parseDouble(sheetList.get(i).getResult()) + total;
            MainSheetFragment.subTotalTextView.setText("Sub Total: " + roundTotal(total, 4));

        }
        MainSheetFragment.result = total;
        return total;
    }

    @Override
    public int getItemCount() {
        return sheetList.size();
    }

    public void copyPreviousRow() {
        String length = "", width = "", result = "";
        int index = 0;
        for (int i = 0; i < sheetList.size(); i++) {
            result = sheetList.get(i).getResult();
            if (!result.equals("")) {
                index = i;
                length = sheetList.get(i).getLength();
                width = sheetList.get(i).getWidth();
            }
        }

        sheetList.get(index + 1).setLength(length);
        sheetList.get(index + 1).setWidth(width);
        notifyDataSetChanged();

        Toast.makeText(context, "Index: " + index + "\nLength: " + length + "\nWidth: " + width, Toast.LENGTH_SHORT).show();
    }

    public void saveSheetToDatabase(String getSheetType, String date, String partyName) {

        rows = databaseHelper.countTotalNumberOfSheetsSlab();
        Toast.makeText(context, String.valueOf(rows), Toast.LENGTH_SHORT).show();
        sheetNo = String.valueOf(rows + 1);

        Boolean response = databaseHelper.addSheetSlab(sheetList, getSheetType, rows + 1, date, partyName, String.valueOf(spinnerPosition1), String.valueOf(spinnerPosition2));
        if (response) {
            Toast.makeText(context, "Saved Successfully!", Toast.LENGTH_SHORT).show();
            List<sheetModelList> listForPdf = selectRecordToGenerateSlabPdf();
            saveAsPdf(listForPdf, getSheetType, date, partyName);
            sheetList.clear();
        } else {
            Toast.makeText(context, "Error occured while saving sheet to database", Toast.LENGTH_SHORT).show();
        }
    }

    public List<String> setSpinnerItems(String sheetNo) {
        Cursor cursor = databaseHelper.getSelectedSpinnerItemsForSlabs(sheetNo);
        List<String> positions = new ArrayList<>();
        if (cursor.moveToNext()) {
            String spinner1Position = cursor.getString(0);
            String spinner2Position = cursor.getString(1);
            positions.add(spinner1Position);
            positions.add(spinner2Position);
        }
        return positions;
    }

    private void saveAsPdf(List<sheetModelList> listForPdf, String getType, String date, String partyName) {
        File graniteMarbleFolder = new File(context.getExternalFilesDir("/") + "/MasterMarble");
        if (!graniteMarbleFolder.exists()) {
            graniteMarbleFolder.mkdir();
            Toast.makeText(context, "directory created", Toast.LENGTH_SHORT).show();
        }
        String pdfName = partyName + ".pdf";
        pdfFile = new File(graniteMarbleFolder.getAbsolutePath(), pdfName);
        try {
            Document document = new Document(PageSize.A4);
            PdfPTable table = new PdfPTable(new float[]{1, 2, 2, 3, 1, 2, 2, 3});
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setFixedHeight(50);
            table.setTotalWidth(PageSize.A4.getWidth());
            table.setWidthPercentage(100);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell("SL");
            table.addCell("Length\n" + choice1);
            table.addCell("Width\n" + choice1);
            table.addCell("SFT\nsq. " + choice2);
            table.addCell("SL");
            table.addCell("Length\n" + choice1);
            table.addCell("Width\n" + choice1);
            table.addCell("SFT\nsq. " + choice2);
            table.setHeaderRows(1);
            PdfPCell[] cells = table.getRow(0).getCells();
            for (int j = 0; j < cells.length; j++) {
                cells[j].setBackgroundColor(BaseColor.GRAY);
            }
            for (int i = 0; i < listForPdf.size(); i = i + 1) {

                String serial = String.valueOf(listForPdf.get(i).getId());
                String length = listForPdf.get(i).getLength();
                String width = listForPdf.get(i).getWidth();
                String result = listForPdf.get(i).getResult();
                table.addCell(serial);
                table.addCell(length);
                table.addCell(width);
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
            Paragraph paragraph = new Paragraph(partyName + "\n", f);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            Paragraph paragraph1 = new Paragraph(date + "\n\n", g);
            paragraph1.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph1);
            document.add(table);
            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<sheetModelList> selectRecordToGenerateSlabPdf() {
        Cursor cursor = databaseHelper.getDataToGeneratePdfSlab(sheetNo);
        while (cursor.moveToNext()) {
            sheetListForPdf.add(new sheetModelList(cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
        }
        return sheetListForPdf;
    }

    public void updateSheetNow(String selectionType, String selectedDate, String partyName, long sheetNumber) {

        databaseHelper.updateRecordsSlab(sheetList, selectionType, sheetNumber, selectedDate, partyName, spinnerPosition1, spinnerPosition2);
        sheetList.clear();
        sheetListForPdf.clear();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serial, result;
        EditText length, width;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serial = itemView.findViewById(R.id.serialTextView);
            result = itemView.findViewById(R.id.resultTextView);
            length = itemView.findViewById(R.id.lengthEditText);
            width = itemView.findViewById(R.id.widthEditText);
        }
    }

    private double roundTotal(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    private String round(double value, int scale) {
        return String.valueOf(Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale));
    }
}
