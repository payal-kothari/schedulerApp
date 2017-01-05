package listviewfromsqlitedb.example.com.scheduler;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends Activity {
    DatabaseManager adapter_ob;
    DatabaseManagerForActual manager_ob_actual;
    ListView scheduleList;
    ListView actualScheduleList;
    Button btnNewTask, btnCalendar, btnIn;
    DatabaseManager adapter;
    DatabaseHelper helper;
    private String format = "";
    private Calendar calendar;
    StringBuilder stringbuilder, stringbuilderEnd;
    static String s, e;
    int year, month, day;
    static String selectedDate;
    TextView txDate;
    static int ongoingTaskID;
    static String ongoingStartTime;
    static String ongoingTask;
    static String taskFromUser;
    EditText input;
    static int status = 1;
    private CharSequence[] Tasks;
    private String selectedText;
    static int ongoingID;
    static String ongoingDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduleList = (ListView) findViewById(R.id.list_view);
        actualScheduleList = (ListView) findViewById(R.id.list_viewActual);
        btnNewTask = (Button) findViewById(R.id.btn_newTask);
        btnIn = (Button) findViewById(R.id.btn_In);
        btnIn.setTag(1);
        manager_ob_actual = new DatabaseManagerForActual(this);
        adapter_ob = new DatabaseManager(this);
        btnCalendar = (Button) findViewById(R.id.btn_calendar);
        txDate = (TextView) findViewById(R.id.tx_date);

        Intent intent = getIntent();
        selectedDate = intent.getStringExtra("DATE");
        txDate.setText(selectedDate);

        showlist();
        showListForActual();

        actualScheduleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setMessage("Do you want to delete this entry ? ");
                adb.setIcon(android.R.drawable.ic_dialog_alert);
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        manager_ob_actual = new DatabaseManagerForActual(MainActivity.this);
                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
                        allEntries = manager_ob_actual.fetchByDateList(selectedDate);
                        Entry currentEntry = allEntries.get((int) id);
                        int rowID = currentEntry.getID();
                        manager_ob_actual.deleteOneRecord(rowID);
                        status = 1;
                        btnIn.setText("IN");
                    } });
                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                       dialog.cancel();
                    } });
                adb.show();
                return true;
            }
        });

        scheduleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,final long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setMessage("Do you want to delete this entry ? ");
                adb.setIcon(android.R.drawable.ic_dialog_alert);
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        adapter_ob = new DatabaseManager(MainActivity.this);
                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
                        allEntries = adapter_ob.fetchByDateList(selectedDate);
                        Entry currentEntry = allEntries.get((int) id);
                        int rowID = currentEntry.getID();
                        adapter_ob.deleteOneRecord(rowID);
                    } });
                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    } });
                adb.show();
                return true;
            }
        });

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        btnIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status == 1) {
                    List<String> allTasks;
                    DatabaseManager manager = new DatabaseManager(MainActivity.this);
                    allTasks = manager.fetchAllTasks();
                    allTasks.add("None");
                    //Create sequence of items
                    Tasks = allTasks.toArray(new String[allTasks.size()]);
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    dialogBuilder.setTitle("Tasks");
                    selectedText = Tasks[0].toString();
                    dialogBuilder.setSingleChoiceItems(Tasks,0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedText = Tasks[which].toString();  //Selected item in listview
                            Log.d("Selected", "on radio click: " + selectedText);
                        }
                    });

                    dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d("Selected", "now on click of OK : " + selectedText);
                            String AmPmFormat;
                            Calendar mcurrentTime = Calendar.getInstance();
                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = mcurrentTime.get(Calendar.MINUTE);
                            setTime(hour, minute, "start");
                            if(hour <12){
                                AmPmFormat = "AM";
                            }else {
                                AmPmFormat = "PM";
                            }
                            StringBuilder stringbuilder = new StringBuilder();
                            stringbuilder.append("00").append(":").append("00")
                                    .append("").append(AmPmFormat);
                            String e = stringbuilder.toString();
                            DatabaseManagerForActual manager = new DatabaseManagerForActual(MainActivity.this);
                            System.out.println("Taskkkkkk " + taskFromUser);
                            String total = "-:-";
                            manager.insertDetails(selectedDate, s, e, selectedText, total);
                            showListForActual();
                            DatabaseManagerForActual manager1 = new DatabaseManagerForActual(MainActivity.this);
                            Cursor c = manager1.fetchAllCursor();
                            c.moveToLast();
                            ongoingID = c.getInt(c.getColumnIndex("_id"));
                            Log.d("rowId ongoign ",  String.valueOf(ongoingID));
                            ongoingDate = selectedDate;
                            Log.d("date ongoign ",  ongoingDate);
                            btnIn.setText("OUT");
                            showListForActual();
                            status = 0;
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    dialogBuilder.setNeutralButton(Html.fromHtml("<b><i>" + "+" + "</i><b>"), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setTitle("Enter Task Name "); //Set Alert dialog title here

                            // Set an EditText view to get user input
                            final EditText input = new EditText(MainActivity.this);
                            alert.setView(input);

                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //You will get as string input data in this variable.
                                    // here we convert the input to a string and show in a toast.
                                    String taskFromUser = input.getEditableText().toString();
                                    if (taskFromUser.equals("")){
                                        taskFromUser = "-";
                                    }
                            String AmPmFormat;
                            Calendar mcurrentTime = Calendar.getInstance();
                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = mcurrentTime.get(Calendar.MINUTE);
                            setTime(hour, minute, "start");
                            if(hour <12){
                                AmPmFormat = "AM";
                            }else {
                                AmPmFormat = "PM";
                            }
                            StringBuilder stringbuilder = new StringBuilder();
                            stringbuilder.append("00").append(":").append("00")
                                    .append("").append(AmPmFormat);
                            String e = stringbuilder.toString();
                            DatabaseManagerForActual manager = new DatabaseManagerForActual(MainActivity.this);
                            System.out.println("Taskkkkkk " + taskFromUser);
                            String total = "-:-";
                            manager.insertDetails(selectedDate, s, e, taskFromUser, total);
                            showListForActual();
                            DatabaseManagerForActual manager1 = new DatabaseManagerForActual(MainActivity.this);
                            Cursor c = manager1.fetchAllCursor();
                            c.moveToLast();
                            ongoingID = c.getInt(c.getColumnIndex("_id"));
                            Log.d("rowId ongoign ",  String.valueOf(ongoingID));
                            ongoingDate = selectedDate;
                            Log.d("date ongoign ",  ongoingDate);
                            btnIn.setText("OUT");
                                    showListForActual();
                            status = 0;
                                } // End of onClick(DialogInterface dialog, int whichButton)
                            }); //End of alert.setPositiveButton
                            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                    dialog.cancel();
                                }
                            }); //End of alert.setNegativeButton
                            AlertDialog alertDialog = alert.create();
                            alertDialog.show();
                        }
                    });
                    AlertDialog alertDialogObject = dialogBuilder.create();
                    //Show the dialog
                    alertDialogObject.show();
                } else {
                    DatabaseManagerForActual manager = new DatabaseManagerForActual(MainActivity.this);
                    Cursor c = manager.fetchByDate(ongoingDate);
                    if (c.moveToFirst()) {
                        do {
                            int rowId = (c.getInt(c.getColumnIndex("_id")));
                            if(rowId == ongoingID){
                                ongoingStartTime = c.getString(c.getColumnIndex("startTime"));
                                ongoingTask = c.getString(c.getColumnIndex("taskName"));
                                break;
                            }
                        } while (c.moveToNext());
                    }
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    setTime(hour, minute, "start");
                    String total = calculateTotal(ongoingStartTime, s);
                    manager.updateldetail(ongoingID, ongoingDate, ongoingStartTime, s, ongoingTask, total);
                    btnIn.setText("IN");
                    showListForActual();
                    status = 1;
                }
            }
        });

        btnNewTask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new DatabaseManager(MainActivity.this);
                Cursor c = adapter.fetchByDate(selectedDate);
                int num = c.getCount();
                if(num > 0) {
                    c.moveToLast();
                    String start = c.getString(c.getColumnIndex(helper.END_TIME));
                    String tempHr = start.substring(0, 2);
                    String tempMin = start.substring(3, 5);
                    String AmPmFormat = start.substring(5, 7);
                    int tempHour = Integer.parseInt(tempHr);
                    int tempMinute = Integer.parseInt(tempMin);
                    setTimeForEnd(tempHour, tempMinute, AmPmFormat);
                    //String end = String.valueOf(tempHour) + start.substring(2,start.length());
                    String task = c.getString(c.getColumnIndex(helper.TASK_NAME));
                    String total = calculateTotal(start, e);
                    Log.d("mainActivity", total);
                    adapter.insertDetails(selectedDate, start, e, task, total);
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
                    String total = calculateTotal(s, e);
                    Log.d("mainActivity 1", total);
                    long val = adapter.insertDetails(selectedDate, s, e, task, total);
                    showlist();
                }
            }
        });
    }

    public String calculateTotal(String s, String e) {
        int diff = 0;
        String firstHalfStart = s.substring(0,2);
        String secondHalfStart = s.substring(3,5);
        String amPmStart = s.substring(5,7);
        String firstHalfEnd = e.substring(0,2);
        String secondHalfEnd = e.substring(3,5);
        String amPmEnd = e.substring(5,7);

        if(amPmStart.equals("PM") && !firstHalfStart.equals("12")){
            firstHalfStart = firstHalfStart + 12;
        }
        if(amPmStart.equals("AM") && firstHalfStart.equals("12")){
            firstHalfStart = firstHalfStart + 12;
        }
        if(amPmEnd.equals("PM") && !firstHalfEnd.equals("12")){
            firstHalfEnd = firstHalfEnd + 12;
        }
        if(amPmStart.equals("AM") && firstHalfEnd.equals("12")){
            firstHalfEnd = firstHalfEnd + 12;
        }

        int start = Integer.parseInt(firstHalfStart) * 60 + Integer.parseInt(secondHalfStart);
        int end = Integer.parseInt(firstHalfEnd) * 60 + Integer.parseInt(secondHalfEnd);

        diff = end - start;


//        Log.d("total ampmstart" , amPmStart);
//        Log.d("total ampmend" , amPmEnd);
//        if(!amPmStart.equals(amPmEnd)){
//            if(start > end){
//                int temp  = (start - end);
//                Log.d("start > end", String.valueOf(temp));
//                diff = 720 - temp;
//            }else {
//
//                int temp = end - start;
//                diff = 720 + temp;
//            }
//        }else {
//            diff = end - start;
//        }

        int hh = diff / 60;
        int mm = diff % 60;

        StringBuilder strbuilder = new StringBuilder();
        strbuilder.append(String.valueOf(hh)).append(":").append(String.valueOf(mm));

        return strbuilder.toString();
    }

    private void showListForActual() {
        manager_ob_actual = new DatabaseManagerForActual(this);
        ArrayList<Entry> allEntries = new ArrayList<Entry>();
        allEntries.clear();
        Cursor c1 = manager_ob_actual.fetchByDate(selectedDate);
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
                    allItems.setTotal(c1.getString(c1
                            .getColumnIndex("total")));
                    allEntries.add(allItems);
                } while (c1.moveToNext());
            }
        }
        c1.close();
        CustomAdapterForActual customAdapterForActual = new CustomAdapterForActual(
                MainActivity.this, allEntries);
        actualScheduleList.setAdapter(customAdapterForActual);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        StringBuilder strbuilder = new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year);

        selectedDate = strbuilder.toString();
    }

    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        showListForActual();
        showlist();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "resumed", Toast.LENGTH_SHORT).show();
        showlist();
    }

    public void showlist() {
        txDate.setText(selectedDate);
        adapter_ob = new DatabaseManager(this);
        ArrayList<Entry> allEntries = new ArrayList<Entry>();
        allEntries.clear();
        Cursor c1 = adapter_ob.fetchByDate(selectedDate);
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
                    allItems.setTotal(c1.getString(c1
                            .getColumnIndex("total")));
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

        stringbuilder = new StringBuilder();
        stringbuilder.append(hrStr).append(":").append(minstr)
                .append("").append(format);

        if(blockName.equals("start")){
            s = stringbuilder.toString();
        }else if(blockName.equals("end")){
            e = stringbuilder.toString();
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
        stringbuilderEnd.append(hrStr).append(":").append(minstr)
                .append("").append(format);
        e = stringbuilderEnd.toString();
    }
}
