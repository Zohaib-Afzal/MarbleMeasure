package com.xoftedge_dev.granitemarblemeasurementsheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.xoftedge_dev.granitemarblemeasurementsheet.Model.SavedSheetModel;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static List<SavedSheetModel> list;
    private DrawerLayout drawerLayout;
    private Button addLayerButton;
    
    private TextView selectAnotherType;
    public static TextView enterDate;
    private TextView imageText;

    public static EditText partNameEditText;

    private static TableLayout tableLayout;

    private Toolbar mainToolbar;

    private Uri imageUri;

    private static Button nextPageButton;
    
    private String formattedDate;

    private NavigationView navigationView;

    public static boolean dateAdded = false;

    public static String selectionType;
    public static String selectedDate;
    public static String partyName;

    private static String actionForNext = "normal";

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public static final String SHARED_PREF_NAME = "mainDataSharedPref";

    private DatePickerDialog.OnDateSetListener setListener;

    private Calendar calendar = Calendar.getInstance();
    final int year = calendar.get(Calendar.YEAR);
    final int Month = calendar.get(Calendar.MONTH);
    final int day = calendar.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFields();

        getIntentExtras();

        Toast.makeText(this, selectionType, Toast.LENGTH_SHORT).show();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mainToolbar, R.string.navigation_open_drawer,
        R.string.navigation_close_drawer);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validated()){
                    if (actionForNext.equals("normal")){
                        editor = sharedPreferences.edit();
                        editor.putString("party_name", partNameEditText.getText().toString());
                        editor.putString("date", enterDate.getText().toString());
                        editor.apply();
                        editor.commit();
                        switchVisibility(false);
                        selectedDate = enterDate.getText().toString();
                        partyName = partNameEditText.getText().toString();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.mainContainer, new MainSheetFragment()).commit();
                    }else{

                        switchVisibility(false);
                        MainSheetFragment.savedList = list;
                        MainSheetFragment.action = "update";
                        selectedDate = enterDate.getText().toString();
                        partyName = partNameEditText.getText().toString();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.mainContainer, new MainSheetFragment()).commit();
                        actionForNext = "normal";

//                        MainSheetFragment fragment = new MainSheetFragment();
//                        fragment.addListToUpdate(list);
                    }
                }
            }
        });
        
        selectAnotherType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        enterDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth,
                    setListener, year, Month, day);

            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            datePickerDialog.show();
        });

        setListener = (view, year, month, dayOfMonth) -> {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(year, month, dayOfMonth, 0, 0, 0);
                Date chosenDate = cal.getTime();

                DateFormat ukDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
                formattedDate = ukDateFormat.format(chosenDate);
                enterDate.setText(formattedDate);
                dateAdded = true;
                enterDate.setError(null);
        };


    }

    public static void updateSheet(String partyName, String date, List<SavedSheetModel> sheetList,String sheetNo, String action){
            partNameEditText.setText(partyName);
            enterDate.setText(date);
            list = sheetList;
            actionForNext = action;
    }

    public static void switchVisibility(boolean flag) {
        if (flag){
            tableLayout.setVisibility(View.VISIBLE);
            nextPageButton.setVisibility(View.VISIBLE);
        }else{
            tableLayout.setVisibility(View.GONE);
            nextPageButton.setVisibility(View.GONE);
        }
    }

    private boolean validated() {
        if (TextUtils.isEmpty(partNameEditText.getText().toString())){
            partNameEditText.setError("Party Name is required.");
            return false;
        }
        if (!dateAdded){
            enterDate.setError("Date is required.");
            return false;
        }
        return true;
    }


    private void initFields() {
        tableLayout = (TableLayout)findViewById(R.id.mainTableLayout);
        nextPageButton = (Button)findViewById(R.id.next_page_button_main);
        partNameEditText = (EditText)findViewById(R.id.party_name_ed_main);
        imageText = (TextView)findViewById(R.id.image);
        mainToolbar = (Toolbar)findViewById(R.id.custom_toolbar_main);
        setSupportActionBar(mainToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_toolbar, null);
        actionBar.setCustomView(actionBarView);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        selectAnotherType = (TextView)findViewById(R.id.selectAnotherType);
        enterDate = (TextView)findViewById(R.id.date_tv_main);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        selectionType = intent.getExtras().getString("name");
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_new_sheet:
                getSavedSheetsFragment fragment = new getSavedSheetsFragment();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                manager.getBackStackEntryCount();
                transaction.remove(fragment);
                transaction.commit();
                switchVisibility(true);
                break;
            case R.id.how_to_use:
                Toast.makeText(this, "How to use", Toast.LENGTH_SHORT).show();
                break;
            case R.id.saved_sheets:
                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.replace(R.id.mainContainer, new getSavedSheetsFragment()).commit();
                switchVisibility(false);
                break;
            case R.id.rate_us:
                Toast.makeText(this, "Rate Us", Toast.LENGTH_SHORT).show();
                break;
            case R.id.share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.contact_us:
                Toast.makeText(this, "Contact us", Toast.LENGTH_SHORT).show();
                break;
            case R.id.check_updates:
                Toast.makeText(this, "Check updates", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}