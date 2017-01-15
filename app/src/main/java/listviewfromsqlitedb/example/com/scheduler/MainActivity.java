package listviewfromsqlitedb.example.com.scheduler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    DatabaseManager adapter_ob;
    DatabaseManagerForActual manager_ob_actual;
    ListView scheduleList;
    ListView actualScheduleList;
    Button btnNewTask, btnCalendar, btnIn, btnCopyFromPrevious, btnToDo;
    DatabaseManager adapter;
    DatabaseHelper helper;
    private String format = "";
    private Calendar calendar;
    StringBuilder stringbuilder, stringbuilderEnd;
    static String s, e;
    int year, month, day;
    public static boolean TodoTouchFlag = false;
    static String selectedDate; TextView txDate;
    DatabaseManagerToDo adapter_ob_ToDo;
    static String date;
    ListView toDoList;
    Button btnAddTask;
    EditText ed_Task;
    static int statusID=0;
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
    static int selectedDay;
    static String formatedDate;
    static boolean copyFromPrevious;
    static String selectedOldDateStr;


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
        btnCopyFromPrevious = (Button) findViewById(R.id.btn_copyFromPreviousSchedule);
        txDate = (TextView) findViewById(R.id.tx_date);
        toDoList = (ListView) findViewById(R.id.toDo_list);
        btnAddTask = (Button) findViewById(R.id.btn_toDoAdd);
        ed_Task = (EditText) findViewById(R.id.ed_task);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        selectedDate = df.format(c.getTime());
        formatedDate = setFormatedDate();
        txDate.setText(formatedDate);

        showlist();
        showListForActual();
        showlistToDo();

        toDoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("Enter task name: ");
                final EditText input = new EditText(MainActivity.this);
                adb.setView(input);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        adapter_ob_ToDo = new DatabaseManagerToDo(MainActivity.this);
                        ArrayList<EntryToDo> allEntries = new ArrayList<EntryToDo>();
                        allEntries = adapter_ob_ToDo.fetchByDateList(selectedDate);
                        EntryToDo currentEntry = allEntries.get((int) id);
                        int rowID = currentEntry.getID();
                        String dateForThisEntry = currentEntry.getDate();
                        int statusId = currentEntry.getStatusID();
                        String resultTask = input.getEditableText().toString();
                        if(!resultTask.contains("") && resultTask.trim().length() > 0 ){
                            adapter_ob_ToDo.updateldetail(rowID, dateForThisEntry, resultTask, "N", statusId);
                        }
                    } });
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    } });
                adb.setNeutralButton("Delete", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter_ob_ToDo = new DatabaseManagerToDo(MainActivity.this);
                        ArrayList<EntryToDo> allEntries = new ArrayList<EntryToDo>();
                        allEntries = adapter_ob_ToDo.fetchByDateList(selectedDate);
                        EntryToDo currentEntry = allEntries.get((int) id);
                        int rowID = currentEntry.getID();
                        adapter_ob_ToDo.deleteOneRecord(rowID);
                    }
                });
                adb.show();
                return true;
            }
        });


        toDoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter_ob_ToDo = new DatabaseManagerToDo(MainActivity.this);
                ArrayList<EntryToDo> allEntries = new ArrayList<EntryToDo>();
                allEntries = adapter_ob_ToDo.fetchByDateList(selectedDate);
                EntryToDo currentEntry = allEntries.get((int) id);
                int rowID = currentEntry.getID();
                String dateForThisEntry = currentEntry.getDate();
                String taskN = currentEntry.getTask();
                int statusId = currentEntry.getStatusID();
                if(currentEntry.getStatus().equals("N")){
                    Log.d("statusCheck", currentEntry.getStatus());
                    updateAllStatus(statusId, "Y");
                    adapter_ob_ToDo.updateldetail(rowID, dateForThisEntry, taskN, "Y", statusId);
                    showlistToDo();
                }else if (currentEntry.getStatus().equals("Y")){
                    updateAllStatus(statusId, "Y");
                    adapter_ob_ToDo.updateldetail(rowID, dateForThisEntry, taskN, "N", statusId);
                    showlistToDo();
                }
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = ed_Task.getText().toString();
                ed_Task.setText("");
                if(!taskName.equals("")){
                    statusID++;
                    adapter_ob_ToDo = new DatabaseManagerToDo(MainActivity.this);
                    adapter_ob_ToDo.insertDetails(selectedDate, taskName, "N", statusID);
                    showlistToDo();
                }else {
                    showlistToDo();
                }
            }
        });

        btnCopyFromPrevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(988);
            }
        });

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
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.my_alert_dialog, null);
                final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setView(alertLayout);
                alert.setIcon(android.R.drawable.ic_dialog_alert);
                final Button insert_above = (Button) alertLayout.findViewById(R.id.btn_insertAbove);
                final Button insert_below = (Button) alertLayout.findViewById(R.id.btn_insertBelow);
                final Button delete = (Button) alertLayout.findViewById(R.id.btn_delete);
                final AlertDialog dialog = alert.create();

                alert.setCancelable(true);
                insert_above.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
                        allEntries = adapter_ob.fetchByDateList(selectedDate);
                        Entry currentEntry = allEntries.get((int) id);
                        int currentEntryId = currentEntry.getID();
                        Cursor c1 = adapter_ob.fetchAllCursor();
                        if (c1 != null && c1.getCount() != 0) {
                            if (c1.moveToFirst()) {
                                do {
                                    int id = c1.getInt(c1.getColumnIndex("_id"));
                                    Log.d("selectedId: ",String.valueOf(currentEntryId));
                                    Log.d("now id: ", String.valueOf(id));
                                    if(id >= currentEntryId){
                                        int tempId = id;
                                        tempId++;
                                        String dateTemp = c1.getString(c1
                                                .getColumnIndex("date"));
                                        String startTemp = c1.getString(c1
                                                .getColumnIndex("startTime"));
                                        String endTemp = c1.getString(c1
                                                .getColumnIndex("endTime"));
                                        String taskTemp = c1.getString(c1
                                                .getColumnIndex("taskName"));
                                        String totalTemp = c1.getString(c1
                                                .getColumnIndex("total"));
                                        DatabaseManagerTemp dm = new DatabaseManagerTemp(MainActivity.this);
                                        Log.d("copying to temp: ", startTemp);
                                        dm.insertDetails(tempId, dateTemp, startTemp, endTemp, taskTemp, totalTemp);
                                        Log.d("deleting id: ", String.valueOf(id));
                                        adapter_ob.deleteOneRecord(id);
                                    }
                                } while (c1.moveToNext());
                            }
                        }
                        c1.close();
                        String date = currentEntry.getDate();
                        String start = currentEntry.getStartTime();
                        adapter_ob.insertDetails(date, start, start, "NEW*", "0:0");
                        copyFromOtherTable();
                        dialog.dismiss();
                    }
                });

                insert_below.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
                        allEntries = adapter_ob.fetchByDateList(selectedDate);
                        Entry currentEntry = allEntries.get((int) id);
                        int currentEntryId = currentEntry.getID();
                        Cursor c1 = adapter_ob.fetchAllCursor();
                        if (c1 != null && c1.getCount() != 0) {
                            if (c1.moveToFirst()) {
                                do {
                                    int id = c1.getInt(c1.getColumnIndex("_id"));
                                    Log.d("selectedId: ",String.valueOf(currentEntryId));
                                    Log.d("now id: ", String.valueOf(id));
                                    if(id > currentEntryId){
                                        int tempId = id;
                                        tempId++;
                                        String dateTemp = c1.getString(c1
                                                .getColumnIndex("date"));
                                        String startTemp = c1.getString(c1
                                                .getColumnIndex("startTime"));
                                        String endTemp = c1.getString(c1
                                                .getColumnIndex("endTime"));
                                        String taskTemp = c1.getString(c1
                                                .getColumnIndex("taskName"));
                                        String totalTemp = c1.getString(c1
                                                .getColumnIndex("total"));
                                        DatabaseManagerTemp dm = new DatabaseManagerTemp(MainActivity.this);
                                        Log.d("copying to temp: ", startTemp);
                                        dm.insertDetails(tempId, dateTemp, startTemp, endTemp, taskTemp, totalTemp);
                                        Log.d("deleting id: ", String.valueOf(id));
                                        adapter_ob.deleteOneRecord(id);
                                    }
                                } while (c1.moveToNext());
                            }
                        }
                        c1.close();
                        String date = currentEntry.getDate();
                        String start = currentEntry.getStartTime();
                        String end = currentEntry.getEndTime();
                        String task = currentEntry.getTask();
                        String total = currentEntry.getTotal();
                        Log.d("insertingc copy id: ", task);
                        adapter_ob.insertDetails(date, end, end, "NEW*", "0:0");
                        copyFromOtherTable();
                        dialog.dismiss();
                    }
                });

                delete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter_ob = new DatabaseManager(MainActivity.this);
                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
                        allEntries = adapter_ob.fetchByDateList(selectedDate);
                        Entry currentEntry = allEntries.get((int) id);
                        int rowID = currentEntry.getID();
                        adapter_ob.deleteOneRecord(rowID);
                        dialog.dismiss();
                    }
                });
//                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        adapter_ob = new DatabaseManager(MainActivity.this);
//                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
//                        allEntries = adapter_ob.fetchByDateList(selectedDate);
//                        Entry currentEntry = allEntries.get((int) id);
//                        int rowID = currentEntry.getID();
//                        adapter_ob.deleteOneRecord(rowID);
//                    } });
//                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    } });


//                adb.setNeutralButton("Insert below", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
//                        allEntries = adapter_ob.fetchByDateList(selectedDate);
//                        Entry currentEntry = allEntries.get((int) id);
//                        int currentEntryId = currentEntry.getID();
//                        Cursor c1 = adapter_ob.fetchAllCursor();
//                        if (c1 != null && c1.getCount() != 0) {
//                            if (c1.moveToFirst()) {
//                                do {
//                                    int id = c1.getInt(c1.getColumnIndex("_id"));
//                                    Log.d("selectedId: ",String.valueOf(currentEntryId));
//                                    Log.d("now id: ", String.valueOf(id));
//                                    if(id > currentEntryId){
//                                        int tempId = id;
//                                        tempId++;
//                                        String dateTemp = c1.getString(c1
//                                                .getColumnIndex("date"));
//                                        String startTemp = c1.getString(c1
//                                                .getColumnIndex("startTime"));
//                                        String endTemp = c1.getString(c1
//                                                .getColumnIndex("endTime"));
//                                        String taskTemp = c1.getString(c1
//                                                .getColumnIndex("taskName"));
//                                        String totalTemp = c1.getString(c1
//                                                .getColumnIndex("total"));
//                                        DatabaseManagerTemp dm = new DatabaseManagerTemp(MainActivity.this);
//                                        Log.d("copying to temp: ", startTemp);
//                                        dm.insertDetails(tempId, dateTemp, startTemp, endTemp, taskTemp, totalTemp);
//                                        Log.d("deleting id: ", String.valueOf(id));
//                                        adapter_ob.deleteOneRecord(id);
//                                    }
//                                } while (c1.moveToNext());
//                            }
//                        }
//                        c1.close();
//                        String date = currentEntry.getDate();
//                        String start = currentEntry.getStartTime();
//                        String end = currentEntry.getEndTime();
//                        String task = currentEntry.getTask();
//                        String total = currentEntry.getTotal();
//                        Log.d("insertingc copy id: ", task);
//                        adapter_ob.insertDetails(date, end, end, "NEW*", "0:0");
//                        copyFromOtherTable();
//                    } });
//                adb.setNeutralButton("Insert above", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
//                        allEntries = adapter_ob.fetchByDateList(selectedDate);
//                        Entry currentEntry = allEntries.get((int) id);
//                        int currentEntryId = currentEntry.getID();
//                        Cursor c1 = adapter_ob.fetchAllCursor();
//                        if (c1 != null && c1.getCount() != 0) {
//                            if (c1.moveToFirst()) {
//                                do {
//                                    int id = c1.getInt(c1.getColumnIndex("_id"));
//                                    Log.d("selectedId: ",String.valueOf(currentEntryId));
//                                    Log.d("now id: ", String.valueOf(id));
//                                    if(id >= currentEntryId){
//                                        int tempId = id;
//                                        tempId++;
//                                        String dateTemp = c1.getString(c1
//                                                .getColumnIndex("date"));
//                                        String startTemp = c1.getString(c1
//                                                .getColumnIndex("startTime"));
//                                        String endTemp = c1.getString(c1
//                                                .getColumnIndex("endTime"));
//                                        String taskTemp = c1.getString(c1
//                                                .getColumnIndex("taskName"));
//                                        String totalTemp = c1.getString(c1
//                                                .getColumnIndex("total"));
//                                        DatabaseManagerTemp dm = new DatabaseManagerTemp(MainActivity.this);
//                                        Log.d("copying to temp: ", startTemp);
//                                        dm.insertDetails(tempId, dateTemp, startTemp, endTemp, taskTemp, totalTemp);
//                                        Log.d("deleting id: ", String.valueOf(id));
//                                        adapter_ob.deleteOneRecord(id);
//                                    }
//                                } while (c1.moveToNext());
//                            }
//                        }
//                        c1.close();
//                        String date = currentEntry.getDate();
//                        String start = currentEntry.getStartTime();
//                        adapter_ob.insertDetails(date, start, start, "NEW*", "0:0");
//                        copyFromOtherTable();
//                    } });

                dialog.show();
                return true;
            }
        });

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
//                            Calendar mcurrentTime = Calendar.getInstance();
//                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                            int minute = mcurrentTime.get(Calendar.MINUTE);

                            String start = new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime());
                            String startWithoutSpace = start.replace(" ", "");
//
//                            setTime(hour, minute, "start");
//                            if(hour <12){
//                                AmPmFormat = "AM";
//                            }else {
//                                AmPmFormat = "PM";
//                            }
                            String AmPmFormatOfStart = startWithoutSpace.substring(startWithoutSpace.length()-2, startWithoutSpace.length());
                            StringBuilder stringbuilder = new StringBuilder();
                            stringbuilder.append("00").append(":").append("00")
                                    .append("").append(AmPmFormatOfStart);
                            String e = stringbuilder.toString();
                            DatabaseManagerForActual manager = new DatabaseManagerForActual(MainActivity.this);
                            String total = "-:-";
                            manager.insertDetails(selectedDate, startWithoutSpace, e, selectedText, total);
                            showListForActual();
                            DatabaseManagerForActual manager1 = new DatabaseManagerForActual(MainActivity.this);
                            Cursor c = manager1.fetchAllCursor();
                            c.moveToLast();
                            ongoingID = c.getInt(c.getColumnIndex("_id"));
                            Log.d("rowId ongoign ",  String.valueOf(ongoingID));
                            ongoingDate = selectedDate;
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
                                    String taskFromUser = input.getEditableText().toString();
                                    if (taskFromUser.equals("")){
                                        taskFromUser = "-";
                                    }
                            String AmPmFormat;
//                            Calendar mcurrentTime = Calendar.getInstance();
//                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                            int minute = mcurrentTime.get(Calendar.MINUTE);

                            String start = new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime());
                            String startWithoutSpace = start.replace(" ", "");

//                            setTime(hour, minute, "start");
//                            if(hour <12){
//                                AmPmFormat = "AM";
//                            }else {
//                                AmPmFormat = "PM";
//                            }
                            String AmPmFormatOfStart = startWithoutSpace.substring(startWithoutSpace.length()-2, startWithoutSpace.length());
                            StringBuilder stringbuilder = new StringBuilder();
                            stringbuilder.append("00").append(":").append("00")
                                    .append("").append(AmPmFormatOfStart);
                            String e = stringbuilder.toString();
                            DatabaseManagerForActual manager = new DatabaseManagerForActual(MainActivity.this);
                            String total = "-:-";
                            manager.insertDetails(selectedDate, startWithoutSpace, e, taskFromUser, total);
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
                    alertDialogObject.getButton(DialogInterface.BUTTON_NEUTRAL).setTextSize(40);
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
//                    Calendar mcurrentTime = Calendar.getInstance();
//                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                    int minute = mcurrentTime.get(Calendar.MINUTE);
//                    setTime(hour, minute, "start");

                    String end = new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime());
                    String endWithoutSpace = end.replace(" ", "");

                    String formatedStartTime = TimeCalculations.convertAmPmToHHMMSSTimeFormat(ongoingStartTime);
                    String formatedEndTime = TimeCalculations.convertAmPmToHHMMSSTimeFormat(endWithoutSpace);
                    String total = TimeCalculations.calculateTotal(formatedStartTime, formatedEndTime);

                    //String total = calculateTotal(ongoingStartTime, s);
                    manager.updateldetail(ongoingID, ongoingDate, ongoingStartTime, endWithoutSpace, ongoingTask, total);
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

                    String endTime = TimeCalculations.forwardTimeByGivenHour(start, 1, 0);
                    //setTimeForEnd(tempHour, tempMinute, AmPmFormat);
                    //String end = String.valueOf(tempHour) + start.substring(2,start.length());
                    String task = c.getString(c.getColumnIndex(helper.TASK_NAME));

                    String formatedStartTime = TimeCalculations.convertAmPmToHHMMSSTimeFormat(start);
                    String formatedEndTime = TimeCalculations.convertAmPmToHHMMSSTimeFormat(endTime);
                    String total = TimeCalculations.calculateTotal(formatedStartTime, formatedEndTime);

                    // String total = calculateTotal(start, e);
                    adapter.insertDetails(selectedDate, start, endTime, task, total);
                    showlist();
                }else { // when database is empty
                    String AmPmFormat;
//                    Calendar mcurrentTime = Calendar.getInstance();
//                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    String start = new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime());
                    String startWithoutSpace = start.replace(" ", "");

//                    setTime(hour, minute, "start");
//                    if(hour <12){
//                        AmPmFormat = "AM";
//                    }else {
//                        hour -=12;
//                        AmPmFormat = "PM";
//                    }
//                    setTimeForEnd(hour, minute, AmPmFormat);
                    String endTime = TimeCalculations.forwardTimeByGivenHour(startWithoutSpace, 1, 0);
                    String task = "None";

                    String formattedDateStart = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.HOUR, 1);
                    Date d = cal.getTime();
                    String formattedDateEnd = new SimpleDateFormat("HH:mm:ss").format(d);
                    String total = TimeCalculations.calculateTotal(formattedDateStart, formattedDateEnd);

                    //String total = calculateTotal(s, e);
                    Log.d("mainActivity 1", total);
                    long val = adapter.insertDetails(selectedDate, startWithoutSpace, endTime, task, total);
                    showlist();
                }
            }
        });
    }

    private String setFormatedDate() {
        Calendar cal = Calendar.getInstance();
        String date = new SimpleDateFormat("E, MMM d, yyyy").format(cal.getTime());
        return date;
    }

    private String setFormatedDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month);
        String date = new SimpleDateFormat("E, MMM d, yyyy").format(cal.getTime());
        return date;
    }

    private void copyOldToDo() throws ParseException {
        DatabaseManagerToDo manager_ob_to_do = new DatabaseManagerToDo(MainActivity.this);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date d =  dateFormat.parse(selectedDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.DATE, -1);
        String yesterdayAsString = dateFormat.format(calendar.getTime());
        Cursor c1 = manager_ob_to_do.findOldRecords(yesterdayAsString);
        Log.d("ToDoAct","now here");
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    ArrayList<String> tasksByDate = manager_ob_to_do.fetchByDateTasksList(selectedDate);
                    String date = c1.getString(c1
                            .getColumnIndex("date"));
                    String task = c1.getString(c1
                            .getColumnIndex("task"));
                    String status = c1.getString(c1
                            .getColumnIndex("status"));
                    int statusId = c1.getInt(c1
                            .getColumnIndex("statusId"));
                    if(status.equals("N") && !tasksByDate.contains(task)){
                        manager_ob_to_do.insertDetails(selectedDate, task, status, statusId );
                    }
                    Log.d("ToDoAct",date);
                    Log.d("ToDoAct",task);
                    Log.d("ToDoAct",status);
                } while (c1.moveToNext());
            }
        }
    }

    private void copyFromOtherTable() {
        DatabaseManagerTemp dm = new DatabaseManagerTemp(MainActivity.this);
        Cursor c1 = dm.fetchAllCursor();
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                        String date = c1.getString(c1
                                .getColumnIndex("date"));
                        String start = c1.getString(c1
                                .getColumnIndex("startTime"));
                        String end = c1.getString(c1
                                .getColumnIndex("endTime"));
                        String task = c1.getString(c1
                                .getColumnIndex("taskName"));
                        String total = c1.getString(c1
                                .getColumnIndex("total"));
                        adapter_ob = new DatabaseManager(MainActivity.this);
                        Log.d("copying from other: ", start);
                        adapter_ob.insertDetails(date, start, end, task, total);
                } while (c1.moveToNext());
            }
        }
        dm.deleteTable();
        c1.close();
    }

    private void formatDate(String selectedDate) {
//        String s = selectedDate.substring(selectedDate.indexOf("/") + 1);
//        s = s.substring(0, s.indexOf("/"));
//        String monthInStr = MONTHS[Integer.parseInt(s)-1];
//
//        String day = selectedDate.substring(0,selectedDate.indexOf("/"));
//        String year = selectedDate.substring(selectedDate.length()-4,selectedDate.length());
//
//        StringBuilder strb = new StringBuilder();
//        strb.append(day).append(" ").append(monthInStr).append(" ").append(year);


//
//        formatedDate = strb.toString();
//        Log.d("formated date", formatedDate);
    }


// OLD Calculate Total method
//    public String calculateTotal(String s, String e) {
//        int diff = 0;
//        String firstHalfStart = s.substring(0,2);
//        String secondHalfStart = s.substring(3,5);
//        String amPmStart = s.substring(5,7);
//        String firstHalfEnd = e.substring(0,2);
//        String secondHalfEnd = e.substring(3,5);
//        String amPmEnd = e.substring(5,7);
//
//        if(amPmEnd.equals("AM") && amPmStart.equals("AM") && firstHalfStart.equals("12")){
//            firstHalfStart = String.valueOf(0);
//        }
//
//        if(amPmStart.equals("PM") && !firstHalfStart.equals("12")){
//            int x = Integer.parseInt(firstHalfStart) + 12;
//            firstHalfStart = String.valueOf(x);
//        }
//        if(amPmStart.equals("AM") && firstHalfStart.equals("12") && !amPmEnd.equals("AM")){
//            int x = Integer.parseInt(firstHalfStart) + 12;
//            firstHalfStart = String.valueOf(x);
//        }
//        if(amPmEnd.equals("PM") && !firstHalfEnd.equals("12")){
//            int x = Integer.parseInt(firstHalfEnd) + 12;
//            firstHalfEnd = String.valueOf(x);
//        }
//        if(amPmEnd.equals("AM") && firstHalfEnd.equals("12")){
//            int x = Integer.parseInt(firstHalfEnd) + 12;
//            firstHalfEnd = String.valueOf(x);
//        }
//
//        int start = Integer.parseInt(firstHalfStart) * 60 + Integer.parseInt(secondHalfStart);
//        if(firstHalfEnd.contains("-")){
//            firstHalfEnd = String.valueOf(0);
//        }else if(secondHalfEnd.contains("-")){
//            secondHalfEnd = String.valueOf(0);
//        }
//        int end = Integer.parseInt(firstHalfEnd) * 60 + Integer.parseInt(secondHalfEnd);
//
//        diff = end - start;
//        int hh = diff / 60;
//        int mm = diff % 60;
//
//        StringBuilder strbuilder = new StringBuilder();
//        strbuilder.append(String.valueOf(hh)).append(":").append(String.valueOf(mm));
//
//        return strbuilder.toString();
//    }

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

    public void showlistToDo() {
        adapter_ob_ToDo = new DatabaseManagerToDo(this);
        ArrayList<EntryToDo> allEntries = new ArrayList<EntryToDo>();
        allEntries.clear();
        Cursor c1 = adapter_ob_ToDo.fetchByDate(selectedDate);
        Log.d("date in shwlist", selectedDate);
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    EntryToDo allItems = new EntryToDo();
                    allItems.setTask(c1.getString(c1
                            .getColumnIndex("task")));
                    allItems.setStatus(c1.getString(c1
                            .getColumnIndex("status")));
                    allEntries.add(allItems);
                } while (c1.moveToNext());
            }
        }
        c1.close();
        CustomAdapterToDo customAdapterToDo = new CustomAdapterToDo(MainActivity.this, allEntries);
        toDoList.setAdapter(customAdapterToDo);
    }

    public void updateAllStatus(int statusId, String status) {
        DatabaseManagerToDo manager_ob_to_do = new DatabaseManagerToDo(MainActivity.this);
        ArrayList<EntryToDo> allEntries = new ArrayList<EntryToDo>();
        Cursor c1 = manager_ob_to_do.fetchByStatusId(statusId);
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    int rowId = c1.getInt(c1
                            .getColumnIndex("_id"));
                    String date = c1.getString(c1
                            .getColumnIndex("date"));
                    String task = c1.getString(c1
                            .getColumnIndex("task"));
                    int statId = c1.getInt(c1
                            .getColumnIndex("statusId"));
                    manager_ob_to_do.updateldetail(rowId, date, task, status, statId );
                } while (c1.moveToNext());
            }
        }
    }

    // need to open date picker with current date
    Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, mYear, mMonth, mDay);
        }else if( id == 988){
            return new DatePickerDialog(this,
                    myDateListenerForOldSchedule, mYear, mMonth, mDay);
        }
        return null;
    }

    DatePickerDialog.OnDateSetListener myDateListenerForOldSchedule = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    copyFromPrevious = true;
                    selectedOldDateStr = formatSelectedDate(arg1, arg2, arg3);
                    Log.d("oldDate**", selectedOldDateStr);
                    setToOldSchedule(selectedOldDateStr);
                }
            };

    private String formatSelectedDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        return sdf.format(date);
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


                    selectedDate = formatSelectedDate(arg1, arg2, arg3);
                    formatedDate = setFormatedDate(arg1, arg2, arg3);
                    String todayDate = getTodayDate();
                    try {
                        if(todayDate.equals(selectedDate)){
                            copyOldToDo();
                        }
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            };

    public String getTodayDate(){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = df.format(c.getTime());

        return todayDate;
    }

//    private String makeIntsTwoDigit(int year, int month, int day) {
//        String dayStr;
//        String monthStr;
//        String yearStr;
//        if(day < 10){
//            dayStr = "0" + String.valueOf(day);
//        }else {
//            dayStr = String.valueOf(day);
//        }
//        if (month < 10){
//            monthStr = "0" + String.valueOf(month);
//        }else {
//            monthStr = String.valueOf(month);
//        }
//        yearStr = String.valueOf(year);
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(dayStr).append("/").append(monthStr).append("/").append(yearStr);
//        return sb.toString();
//    }

//    private void showDate(int year, int month, int day) {
//        String monthStr = null;
//        String dayStr = null;
//        if(month <10){
//            monthStr = "0" + String.valueOf(month);
//        }else {
//            monthStr = String.valueOf(month);
//        }
//
//        if(day <10){
//            dayStr = "0" + String.valueOf(day);
//        }else {
//            dayStr = String.valueOf(day);
//        }
//        StringBuilder strbuilder = new StringBuilder().append(dayStr).append("/")
//                .append(monthStr).append("/").append(year);
//
//        selectedDate = strbuilder.toString();
//    }

    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d("window focised1", "focus changed");
        super.onWindowFocusChanged(hasFocus);
        Log.d("window focised2", "focus changed");
        showListForActual();
        showlist();
        showlistToDo();
        Log.d("window focised3", "focus changed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showlist();
    }

    public void showlist() {
        txDate.setText(formatedDate);
        adapter_ob = new DatabaseManager(this);
        ArrayList<Entry> allEntries = new ArrayList<Entry>();
        allEntries.clear();
        Cursor c1 = adapter_ob.fetchByDate(selectedDate);
        Log.d("date in shwlist", selectedDate);
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
        CustomAdapter customAdapter = new CustomAdapter(MainActivity.this, allEntries);
        scheduleList.setAdapter(customAdapter);
    }

//    public void setTime(int hour, int minute, String blockName) {
//        String minstr;
//        String hrStr = "";
//        if(blockName.equals("end")){
//            hour++;
//        }
//        if (hour == 0) {
//            hour += 12;
//            format = "AM";
//        } else if (hour == 12) {
//            format = "PM";
//        } else if (hour > 12) {
//            hour -= 12;
//            format = "PM";
//        } else {
//            format = "AM";
//        }
//
//        if(minute >= 10){
//            minstr = String.valueOf(minute);
//        }else {
//            minstr = "0" + String.valueOf(minute);
//        }
//        if(hour < 10){
//            hrStr = "0" + String.valueOf(hour);
//        }else {
//            hrStr = String.valueOf(hour);
//        }
//
//        stringbuilder = new StringBuilder();
//        stringbuilder.append(hrStr).append(":").append(minstr)
//                .append("").append(format);
//
//        if(blockName.equals("start")){
//            s = stringbuilder.toString();
//        }else if(blockName.equals("end")){
//            e = stringbuilder.toString();
//        }
//    }

//    private void setTimeForEnd(int hour, int minute, String oldAmPm) {
//        String minstr;
//        String hrStr = "";
//        hour++;
//        if(hour == 12 && oldAmPm.equals("AM")){
//            format = "PM";
//        }else if(hour == 12 && oldAmPm.equals("PM")){
//            format = "AM";
//        }else if(hour >12 ){
//            hour -= 12;
//            format = oldAmPm;
//        }
//
//        if(minute >= 10){
//            minstr = String.valueOf(minute);
//        }else {
//            minstr = "0" + String.valueOf(minute);
//        }
//        if(hour < 10){
//            hrStr = "0" + String.valueOf(hour);
//        }else {
//            hrStr = String.valueOf(hour);
//        }
//
//        stringbuilderEnd = new StringBuilder();
//        stringbuilderEnd.append(hrStr).append(":").append(minstr)
//                .append("").append(format);
//        e = stringbuilderEnd.toString();
//    }

    public void setToOldSchedule(String selectedOldDateStr) {
        adapter_ob = new DatabaseManager(MainActivity.this);
        Cursor c1 = adapter_ob.fetchByDate(selectedOldDateStr);

        if (c1.moveToFirst()) {
            do {
                String start = c1.getString(2);
                String end = c1.getString(3);
                String task = c1.getString(4);
                String total = c1.getString(5);
                adapter_ob.insertDetails(selectedDate, start, end, task, total);
            } while (c1.moveToNext());
        }
        showlist();
    }
}
