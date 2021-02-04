package com.xoftedge_dev.granitemarblemeasurementsheet;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectionActivity extends AppCompatActivity {

    private CardView slabCardView, blockCardView;
    private Toolbar selectionToolbar;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        initFields();

        slabCardView.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
            intent.putExtra("name", "slab");
            startActivity(intent);
            finish();
        });

        blockCardView.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
            intent.putExtra("name", "block");
            startActivity(intent);
            finish();
        });

//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
//                intent.putExtra("name", "block");
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    private void initFields() {
        slabCardView = (CardView)findViewById(R.id.slab);
        blockCardView = (CardView)findViewById(R.id.block);
        selectionToolbar = (Toolbar)findViewById(R.id.custom_sel_toolbar);
        setSupportActionBar(selectionToolbar);
        selectionToolbar.setTitle("");
        selectionToolbar.setTitleTextColor(Color.WHITE);
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_selection_toolbar, null);
        actionBar.setCustomView(actionBarView);

       // backButton = (ImageView)findViewById(R.id.back_on_main);
    }
}