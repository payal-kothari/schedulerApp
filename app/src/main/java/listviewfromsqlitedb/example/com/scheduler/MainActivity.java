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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class MainActivity extends Activity {
    DatabaseManager adapter_ob;
    DatabaseHelper helper_ob;
    ListView scheduleList;
    Button btnNewTask, btnRefresh;
    Cursor cursor;
    DatabaseManager adapter;
    CustomAdapter customAdapter;
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
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        adapter_ob = new DatabaseManager(this);

        showlist();

        btnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showlist();
            }
        });

        btnNewTask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new DatabaseManager(MainActivity.this);
                Cursor c = adapter.fetchAllCursor();
                int num = c.getCount();
                if(num > 0) {
                    c.moveToLast();
                    String start = c.getString(c.getColumnIndex(helper.END_TIME));
                    String tempHr = start.substring(0, 2);
                    String tempMin = start.substring(5, 7);
                    String AmPmFormat = start.substring(8, 10);
                    int tempHour = Integer.parseInt(tempHr);
                    int tempMinute = Integer.parseInt(tempMin);
                    setTimeForEnd(tempHour, tempMinute, AmPmFormat);
                    //String end = String.valueOf(tempHour) + start.substring(2,start.length());
                    String task = c.getString(c.getColumnIndex(helper.TASK_NAME));
                    adapter.insertDetails(start, e, task);
                    showlist();
                }else { // when database is empty
                    String AmPmFormat;
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    setTime(hour, minute, "start");
                    if(hour <12){
                        AmPmFormat = "AM";
                    }else {
                        hour -=12;
                        AmPmFormat = "PM";
                    }
                    setTimeForEnd(hour, minute, AmPmFormat);
                    String task = "None";
                    long val = adapter.insertDetails(s, e, task);
                    showlist();
                }
            }
        });
    }

    public void refresh(){
        showlist();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "resumed", Toast.LENGTH_SHORT).show();
        showlist();
    }

    public void showlist() {
        adapter_ob = new DatabaseManager(this);
        ArrayList<Entry> allEntries = new ArrayList<Entry>();
        allEntries.clear();
        Cursor c1 = adapter_ob.fetchAllCursor();
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    Entry allItems = new Entry();

                    allItems.setID(c1.getInt(c1
                            .getColumnIndex("_id")));
                    allItems.setStart(c1.getString(c1
                            .getColumnIndex("startTime")));
                    allItems.setEnd(c1.getString(c1
                            .getColumnIndex("endTime")));
                    allItems.setTask(c1.getString(c1
                            .getColumnIndex("taskName")));
                    allEntries.add(allItems);
                } while (c1.moveToNext());
            }
        }
        c1.close();
        CustomAdapter customAdapter = new CustomAdapter(
                MainActivity.this, allEntries);
        scheduleList.setAdapter(customAdapter);
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
