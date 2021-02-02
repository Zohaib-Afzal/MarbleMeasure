package com.xoftedge_dev.granitemarblemeasurementsheet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xoftedge_dev.granitemarblemeasurementsheet.Adapters.TutorialAdapter;

public class Tutorial extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout layoutDot;
    private TextView[] Dotstv;
    private int[] layouts;
    private Button btnSkip;
    private Button btnNext;
    private TutorialAdapter tutorialAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if (!isFirsTimeAppStart()){
            startSelectionActivity();
            finish();
        }

        setStatusBarTransparent();

        setContentView(R.layout.activity_tutorial);

//        getSupportActionBar().hide();

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        layoutDot = findViewById(R.id.dotlayout);
        btnNext = findViewById(R.id.btn_next);
        btnSkip = findViewById(R.id.btn_skip);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectionActivity();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentpage = viewPager.getCurrentItem()+1;
                if (currentpage < layouts.length){
                    viewPager.setCurrentItem(currentpage);
                }else{
                    startSelectionActivity();
                }
            }
        });

        layouts = new int[]{R.layout.view_page_1, R.layout.view_page_2};
        tutorialAdapter = new TutorialAdapter(layouts, getApplicationContext());
        viewPager.setAdapter(tutorialAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == layouts.length-1){
                    btnNext.setText("GOT IT");
                    btnSkip.setVisibility(View.GONE);
                }else{
                    btnNext.setText("NEXT");
                    btnSkip.setVisibility(View.VISIBLE);
                }
                setDotStatus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setDotStatus(0);
    }

    private Boolean isFirsTimeAppStart(){
        SharedPreferences ref = getApplicationContext().getSharedPreferences("GraniteApp", Context.MODE_PRIVATE);
        return ref.getBoolean("FirstTimeStartFlag", true);

    }

    private void setFirstTimeStartStatus(boolean stt){

        SharedPreferences ref = getApplicationContext().getSharedPreferences("GraniteApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ref.edit();
        editor.putBoolean("FirstTimeStartFlag", stt);
        editor.commit();


    }

    private void setDotStatus(int page){
        layoutDot.removeAllViews();
        Dotstv = new TextView[layouts.length];
        for (int i = 0; i < Dotstv.length; i++){
            Dotstv[i] = new TextView(this);
            Dotstv[i].setText(Html.fromHtml("&#8226;"));
            Dotstv[i].setTextSize(30);
            Dotstv[i].setTextColor(Color.parseColor("#a9b4bb"));
            layoutDot.addView(Dotstv[i]);
        }

        if (Dotstv.length > 0){
            Dotstv[page].setTextColor(Color.parseColor("#ffffff"));
        }

    }

    private void startSelectionActivity(){

        setFirstTimeStartStatus(false);

        startActivity(new Intent(Tutorial.this, SelectionActivity.class));
        finish();

    }

    private void setStatusBarTransparent(){
        if (Build.VERSION.SDK_INT >= 21){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}