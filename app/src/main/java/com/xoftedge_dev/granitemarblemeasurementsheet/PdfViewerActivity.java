package com.xoftedge_dev.granitemarblemeasurementsheet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PdfViewerActivity extends AppCompatActivity {

    private PDFView pdfView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        pdfView = (PDFView)findViewById(R.id.pdfView);

        Intent intent = getIntent();
        String partyName = intent.getExtras().getString("partyName");
        url = "/storage/emulated/0/Android/data/com.xoftedge_dev.granitemarblemeasurementsheet/files/MasterMarble/"+partyName+".pdf";
        File file = new File(url);
        pdfView.fromFile(file).load();
    }
}