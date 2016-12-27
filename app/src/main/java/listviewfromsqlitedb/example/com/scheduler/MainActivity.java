package listviewfromsqlitedb.example.com.scheduler;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.StringTokenizer;

public class MainActivity extends Activity {
    DatabaseManager adapter_ob;
    DatabaseHelper helper_ob;
    ListView scheduleList;
    Button btnNewTask;
    Cursor cursor;
    DatabaseManager adapter;
    DatabaseHelper helper;
    public TextView startTime, endTime, taskName;
    Button btnSubmit, btnReset;
    private static final int  REQUEST_CODE_START_TIME = 1;
    private static final int  REQUEST_CODE_END_TIME = 2;
    private String format = "";
    private Calendar calendar;
    TableRow startTimeRow, endTimeRow;
    int currentHour;
    int concatedHrAndMinInt;
    int startTimeInt, endTimeInt;
    StringBuilder stringbuilderStart, stringbuilderEnd;
    static String s, e;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduleList = (ListView) findViewById(R.id.list_view);
        btnNewTask = (Button) findViewById(R.id.btn_newTask);
        adapter_ob = new DatabaseManager(this);

        String[] from = { helper_ob.START_TIME, helper_ob.END_TIME, helper_ob.TASK_NAME };
        int[] to = { R.id.tv_startTime, R.id.tv_endTime, R.id.tv_task };
        cursor = adapter_ob.fetchAll();
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

//        btnNewTask.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                Intent registerIntent = new Intent(MainActivity.this,
//                        AddTaskActivity.class);
//
//                startActivity(registerIntent);
//            }
//        });

        btnNewTask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new DatabaseManager(MainActivity.this);
                Cursor c = adapter.fetchAll();
                int num = c.getCount();
                if(num > 0) {
                    c.moveToLast();
                    String start = c.getString(c.getColumnIndex(helper.END_TIME));
                    String tempHr = start.substring(0, 2);
                    String tempMin = start.substring(5, 7);
                    String AmPmFormat = start.substring(8, 10);
                    Toast.makeText(MainActivity.this, tempMin, Toast.LENGTH_LONG).show();
                    int tempHour = Integer.parseInt(tempHr);
                    int tempMinute = Integer.parseInt(tempMin);
                    setTimeForEnd(tempHour, tempMinute, AmPmFormat);
                    //String end = String.valueOf(tempHour) + start.substring(2,start.length());
                    String task = c.getString(c.getColumnIndex(helper.TASK_NAME));
                    adapter.insertDetails(start, e, task);
                    refresh();
                    //finish();
                }else { // when database is empty
                    String AmPmFormat;
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    setTime(hour, minute, "start");
                    Toast.makeText(MainActivity.this, String.valueOf(hour), Toast.LENGTH_LONG).show();
                    if(hour <12){
                        AmPmFormat = "AM";
                    }else {
                        hour -=12;
                        AmPmFormat = "PM";
                    }
                    setTimeForEnd(hour, minute, AmPmFormat);
                    String task = "None";
                    long val = adapter.insertDetails(s, e, task);
                    refresh();
                    //finish();
                }
            }
        });
    }

    private void refresh() {
        adapter_ob = new DatabaseManager(MainActivity.this);
        scheduleList = (ListView) findViewById(R.id.list_view);
        String[] from = { helper_ob.START_TIME, helper_ob.END_TIME, helper_ob.TASK_NAME };
        int[] to = { R.id.tv_startTime, R.id.tv_endTime, R.id.tv_task };
        cursor = adapter_ob.fetchAll();
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);
        scheduleList.setAdapter(cursorAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        cursor.requery();
    }

    public void setTime(int hour, int minute, String blockName) {
        String minstr;
        String hrStr = "";
        if(blockName.equals("end")){
            hour++;
        }
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        if(minute >= 10){
            minstr = String.valueOf(minute);
        }else {
            minstr = "0" + String.valueOf(minute);
        }
        if(hour < 10){
            hrStr = "0" + String.valueOf(hour);
        }else {
            hrStr = String.valueOf(hour);
        }

        if(blockName.equals("start")){
            stringbuilderStart = new StringBuilder();
            stringbuilderStart.append(hrStr).append(" : ").append(minstr)
                    .append(" ").append(format);
            s = stringbuilderStart.toString();

        }else if(blockName.equals("end")){
            stringbuilderEnd = new StringBuilder();
            stringbuilderEnd.append(hrStr).append(" : ").append(minstr)
                    .append(" ").append(format);
            e = stringbuilderEnd.toString();
        }
    }

    private void setTimeForEnd(int hour, int minute, String oldAmPm) {
        String minstr;
        String hrStr = "";
        hour++;
        if(hour == 12 && oldAmPm.equals("AM")){
            format = "PM";
        }else if(hour == 12 && oldAmPm.equals("PM")){
            format = "AM";
        }else if(hour >12 ){
            hour -= 12;
            format = oldAmPm;
        }

        if(minute >= 10){
            minstr = String.valueOf(minute);
        }else {
            minstr = "0" + String.valueOf(minute);
        }
        if(hour < 10){
            hrStr = "0" + String.valueOf(hour);
        }else {
            hrStr = String.valueOf(hour);
        }

        stringbuilderEnd = new StringBuilder();
        stringbuilderEnd.append(hrStr).append(" : ").append(minstr)
                .append(" ").append(format);
        e = stringbuilderEnd.toString();
    }
}
