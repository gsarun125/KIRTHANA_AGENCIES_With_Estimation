package com.ka.billingsystem.proforma.activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ka.billingsystem.R;
import com.ka.billingsystem.database.DataBaseHandler;
import com.ka.billingsystem.model.CustomAdapter;
import com.ka.billingsystem.model.DataModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EstimationHistroyActivity extends AppCompatActivity {

    private CustomAdapter adapter;
    private List<DataModel> dataModels;
    private DataBaseHandler db = new DataBaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estimation_histroy);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dataModels = new ArrayList<>();
        adapter = new CustomAdapter(this, dataModels);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        getDataFromDatabase();
    }

    private void getDataFromDatabase() {
        Cursor  c1 = db.getValue("SELECT * FROM estimation");

        if (c1.moveToFirst()) {
            do {
                @SuppressLint("Range")  int estimationId = c1.getInt(c1.getColumnIndex("estimation_id"));
                @SuppressLint("Range") String escusName = c1.getString(c1.getColumnIndex("cus_name"));
                @SuppressLint("Range") String amount = c1.getString(c1.getColumnIndex("amount"));
                DataModel dataModel = new DataModel(estimationId, escusName, amount);
                dataModels.add(dataModel);
            } while (c1.moveToNext());
        }
        adapter.notifyDataSetChanged();
    }
}