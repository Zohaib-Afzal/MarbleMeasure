package com.xoftedge_dev.granitemarblemeasurementsheet.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xoftedge_dev.granitemarblemeasurementsheet.Database.DatabaseHelper;
import com.xoftedge_dev.granitemarblemeasurementsheet.MainActivity;
import com.xoftedge_dev.granitemarblemeasurementsheet.Model.SavedSheetModel;
import com.xoftedge_dev.granitemarblemeasurementsheet.R;
import com.xoftedge_dev.granitemarblemeasurementsheet.getSavedSheetsFragment;
import com.xoftedge_dev.granitemarblemeasurementsheet.savedSheetClickListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class savedSheetsAdapter extends RecyclerView.Adapter<savedSheetsAdapter.ViewHolder> {

    private List<SavedSheetModel> savedSheestList;
    private List<SavedSheetModel> listToUpdate = new ArrayList<>();
    private savedSheetClickListener clickListener;
    private Context context;
    
    private DatabaseHelper databaseHelper;

    public savedSheetsAdapter(List<SavedSheetModel> savedSheestList, Context context, savedSheetClickListener clickListener) {
        this.savedSheestList = savedSheestList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_sheets, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (savedSheestList.size() > 0){
            SavedSheetModel list = savedSheestList.get(position);
            holder.partyName.setText(list.getPartyName());
            holder.savedDate.setText(" "+list.getDate());
            holder.sheetNumber.setText(list.getSheetNumber());
            
            String sheetNumber = holder.sheetNumber.getText().toString();
            
            holder.deleteSheet.setOnClickListener(v -> {
                clickListener.onDeleteIconClickListener(position, sheetNumber);
                notifyItemRemoved(position);
                notifyDataSetChanged();
            });

            holder.updateSheet.setOnClickListener(v -> {
                String partyName = "";
                String date = "";
                clickListener.onUpdateIconClickListener(position, sheetNumber);


            });

            holder.viewSheet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onViewIconClickListener(position, sheetNumber);
                }
            });

            holder.shareSheet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onShareIconClickListener(sheetNumber);
                }
            });
            
        }
    }

    @Override
    public int getItemCount() {
        return savedSheestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView partyName, savedDate, sheetNumber;
        ImageView deleteSheet, updateSheet, viewSheet, shareSheet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            partyName = (TextView)itemView.findViewById(R.id.sheetPartyNameTextView);
            savedDate = (TextView)itemView.findViewById(R.id.sheetSavedDateTextView);
            deleteSheet = (ImageView)itemView.findViewById(R.id.deleteSheet);
            sheetNumber = (TextView)itemView.findViewById(R.id.sheetNumber);
            updateSheet = (ImageView)itemView.findViewById(R.id.editSheet);
            viewSheet = (ImageView)itemView.findViewById(R.id.viewSheet);
            shareSheet = (ImageView)itemView.findViewById(R.id.shareSheet);
            
            databaseHelper = new DatabaseHelper(context);
        }
    }
}
