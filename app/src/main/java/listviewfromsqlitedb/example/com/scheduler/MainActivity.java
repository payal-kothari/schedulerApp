package listviewfromsqlitedb.example.com.scheduler;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;


public class MainActivity extends Activity {
    DatabaseManager adapter_ob;
    DatabaseHelper helper_ob;
    SQLiteDatabase db_ob;
    ListView scheduleList;
    Button btnNewTask;
    Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduleList = (ListView) findViewById(R.id.list_view);
        btnNewTask = (Button) findViewById(R.id.btn_newTask);
        adapter_ob = new DatabaseManager(this);

        String[] from = { helper_ob.START_TIME, helper_ob.END_TIME, helper_ob.TASK_NAME };
        int[] to = { R.id.tv_startTime, R.id.tv_endTime, R.id.tv_task };
        cursor = adapter_ob.fetch();
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);
        scheduleList.setAdapter(cursorAdapter);
        scheduleList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                Bundle passdata = new Bundle();
                Cursor listCursor = (Cursor) arg0.getItemAtPosition(arg2);
                int nameId = listCursor.getInt(listCursor
                        .getColumnIndex(helper_ob.KEY_ID));
                passdata.putInt("keyid", nameId);
                Intent passIntent = new Intent(MainActivity.this,
                        EditActivity.class);
                passIntent.putExtras(passdata);
                startActivity(passIntent);
            }
        });

        btnNewTask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent registerIntent = new Intent(MainActivity.this,
                        AddTaskActivity.class);
                startActivity(registerIntent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        cursor.requery();

    }
}
