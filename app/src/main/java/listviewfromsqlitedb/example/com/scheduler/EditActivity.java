package listviewfromsqlitedb.example.com.scheduler;

/**
 * Created by payalkothari on 12/23/16.
 */

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends Activity {
    DatabaseManager regadapter;
    DatabaseHelper openHelper;
    int rowId;
    Cursor c;
    EditText startTime, endTime, taskName;
    Button btnUpdate, btnDelete;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        startTime = (EditText) findViewById(R.id.et_startTime);
        endTime = (EditText) findViewById(R.id.et_endTime);
        taskName = (EditText) findViewById(R.id.et_taskName);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnDelete = (Button) findViewById(R.id.btn_delete);

        Bundle showData = getIntent().getExtras();
        rowId = showData.getInt("keyid");
        regadapter = new DatabaseManager(this);

        c = regadapter.fetchAll(rowId);

        if (c.moveToFirst()) {
            do {
                startTime.setText(c.getString(1));
                endTime.setText(c.getString(2));
                taskName.setText(c.getString(3));
            } while (c.moveToNext());
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                regadapter.updateldetail(rowId, startTime.getText().toString(),
                        endTime.getText().toString(), taskName.getText().toString());
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                regadapter.deletOneRecord(rowId);
                finish();
            }
        });
    }
}