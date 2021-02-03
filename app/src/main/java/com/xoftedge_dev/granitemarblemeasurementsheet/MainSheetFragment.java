package com.xoftedge_dev.granitemarblemeasurementsheet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.xoftedge_dev.granitemarblemeasurementsheet.Adapters.sheetAdapter;
import com.xoftedge_dev.granitemarblemeasurementsheet.Adapters.sheetAdapterForBlocks;
import com.xoftedge_dev.granitemarblemeasurementsheet.Database.DatabaseHelper;
import com.xoftedge_dev.granitemarblemeasurementsheet.Model.SavedSheetModel;
import com.xoftedge_dev.granitemarblemeasurementsheet.Model.SheetModelForBlocks;
import com.xoftedge_dev.granitemarblemeasurementsheet.Model.SheetModelList;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.xoftedge_dev.granitemarblemeasurementsheet.MainActivity.enterDate;
import static com.xoftedge_dev.granitemarblemeasurementsheet.MainActivity.list;
import static com.xoftedge_dev.granitemarblemeasurementsheet.MainActivity.partNameEditText;
import static com.xoftedge_dev.granitemarblemeasurementsheet.MainActivity.partyName;
import static com.xoftedge_dev.granitemarblemeasurementsheet.MainActivity.selectionType;
import static com.xoftedge_dev.granitemarblemeasurementsheet.R.*;

public class MainSheetFragment extends Fragment {

    private LinearLayout spinnerLayout, moreButtons;
    private RecyclerView sheetRecyclerView;
    private long sheetNo;
    public static List<SheetModelList> sheetListMain = new ArrayList<>();
    public static List<SheetModelList> newSheetList = new ArrayList<>();
    private ProgressDialog progressDialog;
    public static List<SheetModelForBlocks> sheetListMainForBlock = new ArrayList<>();
    public static List<SheetModelForBlocks> newSheetListForBlock = new ArrayList<>();
    private EditText lengthEditText, widthEditText, numRowsEditText;
    private TextView serialTextView, resultTextView;
    public static TextView subTotalTextView;
    private Button addLayerButton, okButton, backButton, copyPreviousRowButton, addMoreRowsButton, saveButton;
    private RelativeLayout dialog_container, addLayerLayout;
    private AlertDialog dialog;
    private DatabaseHelper databaseHelper;
    public static int spinnerPosition1, spinnerPosition2;
    public static List<SavedSheetModel> savedList;
    private ImageView shareSheetButton;
    private sheetAdapter adapter;
    private sheetAdapterForBlocks adapterBlock;
    public static String action = "normal";
    public static boolean flag = false;
    public static Double result = 0.0;
    private Spinner spinner1, spinner2;
    private TableRow blockRow, slabRow;
    public static String choice1, choice2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(layout.fragment_main_sheet, container, false);

        initFields(v);
        if (selectionType.equals("slab")) {
            slabRow.setVisibility(View.VISIBLE);
            blockRow.setVisibility(GONE);
            if (action.equals("update")) {
                addListToUpdate(savedList);
                sheetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                sheetRecyclerView.setHasFixedSize(true);

                adapter = new sheetAdapter(getContext(), newSheetList);
                List<String> pos = adapter.setSpinnerItems(savedList.get(0).getSheetNumber());
                  spinner1.setSelection(Integer.parseInt(pos.get(0)), true);
                  spinner2.setSelection(Integer.parseInt(pos.get(1)), true);
                sheetRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                saveButton.setText("Update");
                sheetNo = Long.parseLong(savedList.get(0).getSheetNumber());

            }

        } else {
            slabRow.setVisibility(View.GONE);
            blockRow.setVisibility(View.VISIBLE);
            if (action.equals("update")) {
                addListToUpdate(savedList);
                sheetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                sheetRecyclerView.setHasFixedSize(true);

                adapterBlock = new sheetAdapterForBlocks(getContext(), newSheetListForBlock);
                List<String> pos = adapterBlock.setSpinnerItems(savedList.get(0).getSheetNumber());
                spinner1.setSelection(Integer.parseInt(pos.get(0)), true);
                spinner2.setSelection(Integer.parseInt(pos.get(1)), true);
                sheetRecyclerView.setAdapter(adapterBlock);
                adapterBlock.notifyDataSetChanged();
                saveButton.setText("Update");
                sheetNo = Long.parseLong(savedList.get(0).getSheetNumber());
            }
        }
        choice2 = spinner2.getSelectedItem().toString();
        choice1 = spinner1.getSelectedItem().toString();
        subTotalTextView.setText("Sub Total: " + result);
        copyPreviousRowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectionType.equals("slab")) {
                    adapter.copyPreviousRow();
                } else {
                    adapterBlock.copyPreviousRow();
                }
            }
        });
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                spinnerPosition1 = position;
                choice1 = item.toString();
                if (selectionType.equals("slab")) {
                    adapter.notifyDataSetChanged();
                   // adapter.notifyAll();
                } else {
                    adapterBlock.notifyDataSetChanged();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                choice1 = "Foot(ft)";
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                spinnerPosition2 = position;
                choice2 = item.toString();
                if (selectionType.equals("slab")) {
                    adapter.notifyDataSetChanged();
                } else {
                    adapterBlock.notifyDataSetChanged();
                }

                //adapter.performCalculations();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                choice2 = "Foot(ft)";
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action.equals("normal")) {
                    if (selectionType.equals("slab")) {
                        progressDialog.setMessage("Saving");
                        progressDialog.setCancelable(false);
                        Toast.makeText(getContext(), MainActivity.selectionType, Toast.LENGTH_SHORT).show();
                        adapter.saveSheetToDatabase(MainActivity.selectionType, MainActivity.selectedDate, partyName);
                        returnUserToMainActivity();
                        enterDate.setText("");
                        partNameEditText.setText("");
                        progressDialog.dismiss();
                        createPdfForSlab();

                    } else {
                        progressDialog.setMessage("Saving");
                        progressDialog.setCancelable(false);
                        Toast.makeText(getContext(), MainActivity.selectionType, Toast.LENGTH_SHORT).show();
                        adapterBlock.saveSheetToDatabase(MainActivity.selectionType, MainActivity.selectedDate, partyName);
                        returnUserToMainActivity();
                        enterDate.setText("");
                        partNameEditText.setText("");
                        progressDialog.dismiss();
                    }

                    saveSheetAsPDF();
                } else {
                    if (selectionType.equals("slab")) {
                        Toast.makeText(getContext(), "Updating", Toast.LENGTH_SHORT).show();
                        adapter.updateSheetNow(MainActivity.selectionType, MainActivity.selectedDate, partyName, sheetNo);
                        returnUserToMainActivity();
                        enterDate.setText("");
                        partNameEditText.setText("");
                        newSheetList.clear();
                        savedList.clear();
                        action = "normal";
                    } else {
                        Toast.makeText(getContext(), "Updating", Toast.LENGTH_SHORT).show();
                        adapterBlock.updateSheetNow(MainActivity.selectionType, MainActivity.selectedDate, partyName, sheetNo);
                        returnUserToMainActivity();
                        enterDate.setText("");
                        partNameEditText.setText("");
                        newSheetListForBlock.clear();
                        savedList.clear();
                        action = "normal";
                    }

                    //saveSheetAsPDF();
                }

            }
        });

        shareSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSavedSheets();
            }
        });
        addLayerButton.setOnClickListener(v12 -> openAddRowsDialog());

        backButton.setOnClickListener(v1 -> {
            action = "normal";
            enterDate.setText("");
            partNameEditText.setText("");
            sheetListMain.clear();
            sheetListMainForBlock.clear();
            returnUserToMainActivity();
        });

        addMoreRowsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddRowsDialog();
            }
        });

        if (action.equals("normal")) {
            if (selectionType.equals("slab")) {
                sheetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                sheetRecyclerView.setHasFixedSize(true);
                adapter = new sheetAdapter(getContext(), sheetListMain);
                sheetRecyclerView.setAdapter(adapter);
            } else {
                sheetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                sheetRecyclerView.setHasFixedSize(true);
                adapterBlock = new sheetAdapterForBlocks(getContext(), sheetListMainForBlock);
                sheetRecyclerView.setAdapter(adapterBlock);
            }

        }

        return v;
    }

    private void createPdfForSlab() {
        databaseHelper.getAllDataSlab();

    }

    private void saveSheetAsPDF() {


        if (action.equals("update")) {
            for (int i = 0; i < list.size(); i++) {
                Toast.makeText(getContext(), list.get(i).getPartyName(), Toast.LENGTH_SHORT).show();
            }
        } else {
            for (int i = 0; i < sheetListMain.size(); i++) {
                Toast.makeText(getContext(), sheetListMain.get(i).getResult(), Toast.LENGTH_SHORT).show();
            }
//            PdfDocument pdfDocument = new PdfDocument();
//            Paint paint = new Paint();
        }


    }

    private void getSavedSheets() {
        Cursor cursor = databaseHelper.getAllDataSlab();
        while (cursor.moveToNext()) {
            Toast.makeText(getContext(), cursor.getInt(1) + "\n" +
                    cursor.getString(2) + "\n" +
                    cursor.getString(3) + "\n" +
                    cursor.getString(4) + "\n" +
                    cursor.getString(5), Toast.LENGTH_SHORT).show();
        }
    }


    public void addListToUpdate(List<SavedSheetModel> list) {

        spinnerLayout.setVisibility(View.VISIBLE);
        sheetRecyclerView.setVisibility(View.VISIBLE);
        copyPreviousRowButton.setVisibility(View.VISIBLE);
        moreButtons.setVisibility(View.VISIBLE);
        subTotalTextView.setVisibility(View.VISIBLE);
        addLayerLayout.setVisibility(GONE);

        if (selectionType.equals("slab")) {
            if (getActivity() != null) {
                newSheetList.clear();
                int id = 1;
                for (SavedSheetModel sheet : list) {
                    newSheetList.add(new SheetModelList(id, sheet.getLength(), sheet.getWidth(), sheet.getResult()));
                    //result = result + Double.parseDouble(sheet.getResult());
                    id++;
                }

            } else {
                Toast.makeText(getContext(), "Normal", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (getActivity() != null) {
                newSheetListForBlock.clear();
                int id = 1;
                for (SavedSheetModel sheet : list) {
                    newSheetListForBlock.add(new SheetModelForBlocks(id, sheet.getLength(), sheet.getWidth(), sheet.getHeight(), sheet.getResult()));
                    id++;
                }

            } else {
                Toast.makeText(getContext(), "Normal", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void initFields(View v) {
        progressDialog = new ProgressDialog(getContext());
        databaseHelper = new DatabaseHelper(getContext());
        sheetRecyclerView = v.findViewById(id.sheetRecyclerView);
        lengthEditText = v.findViewById(id.lengthEditText);
        widthEditText = v.findViewById(id.widthEditText);
        spinnerLayout = v.findViewById(id.spinnerLayout);
        addLayerButton = (Button) v.findViewById(id.addLayerButton);
        dialog_container = (RelativeLayout) v.findViewById(id.add_rows_dialog_container);
        addLayerLayout = (RelativeLayout) v.findViewById(id.addLayerLayout);
        backButton = (Button) v.findViewById(id.backButton);
        copyPreviousRowButton = (Button) v.findViewById(id.copyPreviousButton);
        subTotalTextView = (TextView) v.findViewById(id.subTotal);
        addMoreRowsButton = (Button) v.findViewById(id.add_more_slabs_button);
        shareSheetButton = (ImageView) v.findViewById(id.share_sheet_button);
        saveButton = (Button) v.findViewById(id.save_sheet_button);
        moreButtons = (LinearLayout) v.findViewById(id.more_buttons_layout);
        spinner1 = (Spinner) v.findViewById(id.firstChoice);
        spinner2 = (Spinner) v.findViewById(id.secondChoice);
        blockRow = (TableRow) v.findViewById(id.blockRow);
        slabRow = (TableRow) v.findViewById(id.slabRow);

    }

    private void returnUserToMainActivity() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        manager.getBackStackEntryCount();
        transaction.remove(this);
        transaction.commit();
        MainActivity.switchVisibility(true);

    }

    private void getValueAndCloseDialog() {
        dialog.dismiss();
        spinnerLayout.setVisibility(View.VISIBLE);
        sheetRecyclerView.setVisibility(View.VISIBLE);
        copyPreviousRowButton.setVisibility(View.VISIBLE);
        moreButtons.setVisibility(View.VISIBLE);
        subTotalTextView.setVisibility(View.VISIBLE);
        addLayerLayout.setVisibility(GONE);
        initializeFieldsForSheetModelList();
    }

    private void initializeFieldsForSheetModelList() {
        int numRows = Integer.parseInt(numRowsEditText.getText().toString());
        if (selectionType.equals("slab")) {
            for (int i = 0; i < numRows; i++) {
                if (action.equals("normal")) {
                    sheetListMain.add(new SheetModelList(i + 1,"","", ""));
                } else {
                    newSheetList.add(new SheetModelList(i + 1, "", "", ""));
                }

            }
        } else {
            for (int i = 0; i < numRows; i++) {
                if (action.equals("normal")) {
                    sheetListMainForBlock.add(new SheetModelForBlocks(i + 1, "", "", "", ""));
                } else {
                    newSheetListForBlock.add(new SheetModelForBlocks(i + 1, "", "", "", ""));
                }

            }
        }


    }

    private void openAddRowsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), style.Theme_AppCompat_Dialog);
        View view = LayoutInflater.from(getContext()).inflate(layout.fragment_add_rows_dialog,
                dialog_container);
        builder.setView(view);
        dialog = builder.create();

        okButton = (Button) view.findViewById(id.ok_button);
        numRowsEditText = (EditText) view.findViewById(id.input_rows);

        okButton.setOnClickListener(v -> {
            String text = numRowsEditText.getText().toString();
            if (TextUtils.isEmpty(text) || text.equals("0")) {
                numRowsEditText.setError("Invalid given number of rows, Please enter a valid number");
            } else {
                getValueAndCloseDialog();
            }
        });

        view.findViewById(id.close_button).setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }
    private double roundTotal(double value, int scale){
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

}