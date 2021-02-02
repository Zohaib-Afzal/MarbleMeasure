package com.xoftedge_dev.granitemarblemeasurementsheet;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xoftedge_dev.granitemarblemeasurementsheet.Adapters.savedSheetsAdapter;
import com.xoftedge_dev.granitemarblemeasurementsheet.Database.DatabaseHelper;
import com.xoftedge_dev.granitemarblemeasurementsheet.Model.SavedSheetModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.xoftedge_dev.granitemarblemeasurementsheet.MainActivity.dateAdded;
import static com.xoftedge_dev.granitemarblemeasurementsheet.MainActivity.selectionType;

public class getSavedSheetsFragment extends Fragment implements savedSheetClickListener {

    private RecyclerView recyclerView;
    private savedSheetsAdapter adapter;
    private DatabaseHelper databaseHelper;
    private List<SavedSheetModel> savedSheetModelList;
    private TextView noRecords;
    private Button backButton, delete_yes_button, delete_no_button;
    private ImageView delete_close_button;
    private RelativeLayout deleteSheetContainer;
    private AlertDialog dialog;
    private List<SavedSheetModel> listToUpdate = new ArrayList<>();
    private AlertDialog.Builder builder;
    private View view;

    private void switchView(boolean b) {
        if (b){
            recyclerView.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_get_saved_sheets, container, false);
        initFields(v);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        populateRecyclerView();
        backButton.setOnClickListener(v1 -> sendUserBackToMainActivity());
        return v;
    }

    public void openDeletePermissionDialog(String sheetNumber, int pos){



        builder.setView(view);
        dialog = builder.create();

        delete_yes_button = (Button)view.findViewById(R.id.yes_button);
        delete_no_button = (Button)view.findViewById(R.id.no_button);
        delete_close_button = (ImageView)view.findViewById(R.id.close_button_delete_dialog);

        if (view.getParent() != null){
            ((ViewGroup)view.getParent()).removeView(view);
        }
        delete_yes_button.setOnClickListener(v -> {
            if (selectionType.equals("slab")){
                databaseHelper.deleteRecordSlab(sheetNumber);
                savedSheetModelList.remove(pos);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }else{
                databaseHelper.deleteRecordBlock(sheetNumber);
                savedSheetModelList.remove(pos);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

        });

        delete_close_button.setOnClickListener(v -> dialog.dismiss());
        delete_no_button.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void sendUserBackToMainActivity() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        manager.getBackStackEntryCount();
        transaction.remove(this);
        transaction.commit();
        MainActivity.switchVisibility(true);
    }

    private void populateRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (selectionType.equals("slab")){
            Cursor cursor = databaseHelper.getAllDataSlab();
            if (cursor.getCount() == 0){
                noRecords.setVisibility(View.VISIBLE);
            }else{
                while (cursor.moveToNext()){
                    savedSheetModelList.add(new SavedSheetModel(cursor.getString(0), cursor.getString(8), cursor.getString(7), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(6)));
                }


                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }else{
            Cursor cursor = databaseHelper.getAllDataBlock();
            if (cursor.getCount() == 0){
                noRecords.setVisibility(View.VISIBLE);
            }else{
                while (cursor.moveToNext()){
                    savedSheetModelList.add(new SavedSheetModel(cursor.getString(0), cursor.getString(8), cursor.getString(7), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(9), cursor.getString(4), cursor.getString(6)));
                }


                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }


    }

    private void initFields(View v) {
        savedSheetModelList = new ArrayList<>();
        backButton = (Button)v.findViewById(R.id.backButton);
        recyclerView = (RecyclerView)v.findViewById(R.id.savedSheetsRecyclerView);
        noRecords = (TextView)v.findViewById(R.id.noRecorrdsTextView);
        adapter = new savedSheetsAdapter(savedSheetModelList, getContext(), this);
        deleteSheetContainer = (RelativeLayout)v.findViewById(R.id.delete_sheet_container);
        databaseHelper = new DatabaseHelper(getContext());
        builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Dialog);
        view = LayoutInflater.from(getContext()).inflate(R.layout.custom_delete_permission_dialog,
                deleteSheetContainer);
    }

    @Override
    public void onDeleteIconClickListener(int position, String sheetNumber) {
        openDeletePermissionDialog(sheetNumber, position);
        adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onUpdateIconClickListener(int position, String sheetNumber) {
        String partyName ="";
        String date = "";
        dateAdded = true;

        if (selectionType.equals("slab")){
            Cursor cursor = databaseHelper.getRecordToUpdateSlab(sheetNumber);
            while (cursor.moveToNext()){
                listToUpdate.add(new SavedSheetModel(cursor.getString(0), cursor.getString(8), cursor.getString(7), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(6)));
                partyName = cursor.getString(8);
                date = cursor.getString(7);
            }
            MainActivity.updateSheet(partyName, date, listToUpdate,sheetNumber, "update");
            returnUserToMainActivity();
        }else{
            Cursor cursor = databaseHelper.getRecordToUpdateBlock(sheetNumber);
            while (cursor.moveToNext()){
                listToUpdate.add(new SavedSheetModel(cursor.getString(0), cursor.getString(8), cursor.getString(7), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(9), cursor.getString(4), cursor.getString(6)));
                partyName = cursor.getString(8);
                date = cursor.getString(7);
            }
            MainActivity.updateSheet(partyName, date, listToUpdate,sheetNumber, "update");
            returnUserToMainActivity();
        }


    }

    @Override
    public void onViewIconClickListener(int position, String sheetNumber) {
        if (selectionType.equals("slab")){
            Toast.makeText(getContext(), sheetNumber, Toast.LENGTH_SHORT).show();
            Cursor cursor = databaseHelper.getPartyNameForSlab(sheetNumber);
            String partyName = "";
            while (cursor.moveToNext()){
                partyName = cursor.getString(8);
            }

            Intent intent = new Intent(getContext(), PdfViewerActivity.class);
            intent.putExtra("partyName", partyName);
            startActivity(intent);
        }else{
            Cursor cursor = databaseHelper.getPartyNameForBlock(sheetNumber);
            String partyName = "";
            while (cursor.moveToNext()){
                partyName = cursor.getString(8);
            }
            Intent intent = new Intent(getContext(), PdfViewerActivity.class);
            intent.putExtra("partyName", partyName);
            startActivity(intent);
        }

    }


    @Override
    public void onShareIconClickListener(String sheetNumber) {

        String partyName = "";

        if (selectionType.equals("slab")){
            Cursor cursor = databaseHelper.getPartyNameForSlab(sheetNumber);
            while (cursor.moveToNext()){
                partyName = cursor.getString(8);
            }
            String url = "/storage/emulated/0/Android/data/com.xoftedge_dev.granitemarblemeasurementsheet/files/MasterMarble/"+partyName+".pdf";
            File file = new File(url);
            Uri uri = Uri.fromFile(file);
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setPackage("com.whatsapp");

            startActivity(share);
        }else{
            Cursor cursor = databaseHelper.getPartyNameForBlock(sheetNumber);
            while (cursor.moveToNext()){
                partyName = cursor.getString(8);
            }
            String url = "/storage/emulated/0/Android/data/com.xoftedge_dev.granitemarblemeasurementsheet/files/MasterMarble/"+partyName+".pdf";
            File file = new File(url);
            Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", file);
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setPackage("com.whatsapp");
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(share);
        }


    }

    private void returnUserToMainActivity() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        manager.getBackStackEntryCount();
        transaction.remove(this);
        transaction.commit();
        MainActivity.switchVisibility(true);

    }

}