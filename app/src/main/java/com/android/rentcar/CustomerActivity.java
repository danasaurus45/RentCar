package com.android.rentcar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.rentcar.R;
import com.android.rentcar.helper.DataHelper;

import static android.R.layout.*;

public class CustomerActivity extends AppCompatActivity {

    String[] daftar;
    int[] id;
    ListView ListView1;
    Menu menu;
    protected Cursor cursor;
    DataHelper dbcenter;
    public static CustomerActivity m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        Button btTambah = findViewById(R.id.tambahPenyewa);

        btTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CustomerActivity.this, RentActivity.class);
                startActivity(i);
            }
        });

        m = this;
        dbcenter = new DataHelper(this);

        RefreshList();
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbPenyewa);
        toolbar.setTitle("Daftar Penyewa");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void RefreshList() {
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM penyewa", null);
        daftar = new String[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            daftar[i] = cursor.getString(0);
        }

        ListView1 = findViewById(R.id.listView1);
        ListView1.setAdapter(new ArrayAdapter(this, simple_list_item_1, daftar));
        ListView1.setSelected(true);
        ListView1.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                final String selection = daftar[arg2];
                final CharSequence[] dialogitem = {"Lihat Data", "Hapus Data"};
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerActivity.this);
                builder.setTitle("Pilihan");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0: {
                                Intent i = new Intent(CustomerActivity.this, DetailCustomerActivity.class);
                                i.putExtra("nama", selection);
                                startActivity(i);
                                break;
                            }
                            case 1: {
                                SQLiteDatabase db = dbcenter.getWritableDatabase();
                                db.execSQL("DELETE FROM penyewa where nama = '" + selection + "'");
                                db.execSQL("DELETE FROM sewa where nama = '" + selection + "'");
                                RefreshList();
                                break;
                            }
                        }
                    }
                });
                builder.create().show();
            }
        });
        ((ArrayAdapter) ListView1.getAdapter()).notifyDataSetInvalidated();

    }
}