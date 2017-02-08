package pp.example.one.scheduler;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static final String app_id = "pp.example.one.scheduler1";
    DatabaseManager adapter_ob;
    DatabaseManagerForActual manager_ob_actual;
    ListView scheduleList;
    ListView actualScheduleList;
    Button btnNewTask, btnCalendar, btnIn, btnCopyFromPrevious;
    DatabaseManager adapter;
    static String selectedDate;
    TextView txDate;
    DatabaseManagerToDo adapter_ob_ToDo;
    static String date;
    ListView toDoList;
    Button btnAddTask;
    EditText ed_Task;
    static int statusID=0;
    String ongoingStartTime;
    String ongoingTask;
    private CharSequence[] Tasks;
    private String selectedText;
    int ongoingID;
    String ongoingDate;
    static String formatedDate;
    static boolean copyFromPrevious;
    static String selectedOldDateStr;
    static  int toDoListIndex, toDoListTop, plannedListIndex, plannedListTop, actualListIndex, actualListTop;
    static final String SHARED_PREF_ALARM_TONES = "ALARM_TONES";
    static final String SHARED_PREF_CENTRAL_ALARM_TONES = "CENTRAL_ALARM_TONES";
    static final String SHARED_PREF_IN_OUT_DATA = "IN_OUT_DATA" + app_id;
    static final String SHARED_PREF_MISC_DATA = "MISC_DATA";
    static final String NewTaskBtnVisibility = "NewTaskBtnStatus";
    GestureDetectorCompat mDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(MainActivity.this, "on create", Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
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
        ed_Task = (EditText) findViewById(R.id.ed_taskToDo);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        selectedDate = df.format(c.getTime());
        formatedDate = setFormatedDate();
        txDate.setText(formatedDate);

        boolean InOutExist =  isSharedPrefExist(SHARED_PREF_IN_OUT_DATA);
        if(!InOutExist){
            Log.d("in out status: ", "file doesn't exist");
            btnIn.setText("IN");
            saveInOutDataInSharedPref("InOutStatus", "showing_IN", MainActivity.this);
        }else {
            String showing_status = getInOutDataFromSharedPref("InOutStatus", MainActivity.this);
            Log.d("in out status: ", showing_status);
            if(showing_status.equals("showing_IN")){
                btnIn.setText("IN");
            }else if(showing_status.equals("showing_OUT")){
                btnIn.setText("OUT");
            }
        }

        toDoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adapter_ob_ToDo = new DatabaseManagerToDo(MainActivity.this);
                ArrayList<EntryToDo> allEntries = new ArrayList<EntryToDo>();
                allEntries = adapter_ob_ToDo.fetchByDateList(selectedDate);
                final EntryToDo currentEntry = allEntries.get((int) id);
                final int rowID = currentEntry.getID();
                String taskBeforeEditing = currentEntry.getTask();
                adb.setTitle("Enter task name: ");
                final EditText input = new EditText(MainActivity.this);
                input.setText(taskBeforeEditing);
                int posOfCursor = taskBeforeEditing.length();
                input.setSelection(posOfCursor);
                adb.setView(input);
                toDoListIndex = toDoList.getFirstVisiblePosition();
                View v = toDoList.getChildAt(0);
                toDoListTop = (v == null) ? 0 : v.getTop();

                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String dateForThisEntry = currentEntry.getDate();
                        int statusId = currentEntry.getStatusID();
                        String resultTask = input.getText().toString();
                        if(resultTask.trim().length() > 0 ){
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
                toDoListIndex = toDoList.getFirstVisiblePosition();
                View v = toDoList.getChildAt(0);
                toDoListTop = (v == null) ? 0 : v.getTop();
                if(currentEntry.getStatus().equals("N")){
                    Log.d("statusCheck", currentEntry.getStatus());
                    updateAllStatus(statusId, "Y");
                    adapter_ob_ToDo.updateldetail(rowID, dateForThisEntry, taskN, "Y", statusId);
                    try {
                        showlistToDo();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else if (currentEntry.getStatus().equals("Y")){
                    updateAllStatus(statusId, "Y");
                    adapter_ob_ToDo.updateldetail(rowID, dateForThisEntry, taskN, "N", statusId);
                    try {
                        showlistToDo();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = ed_Task.getText().toString();
                if (ed_Task.length() > 0) {
                    ed_Task.getText().clear();
                }
                if(!taskName.equals("")){
                    statusID++;
                    adapter_ob_ToDo = new DatabaseManagerToDo(MainActivity.this);
                    adapter_ob_ToDo.insertDetails(selectedDate, taskName, "N", statusID);
                    try {
                        showlistToDo();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        showlistToDo();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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
                actualListIndex = scheduleList.getFirstVisiblePosition();
                View v = scheduleList.getChildAt(0);
                actualListTop = (v == null) ? 0 : v.getTop();
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        manager_ob_actual = new DatabaseManagerForActual(MainActivity.this);
                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
                        allEntries = manager_ob_actual.fetchByDateList(selectedDate);
                        Entry currentEntry = allEntries.get((int) id);
                        int rowID = currentEntry.getID();
                        manager_ob_actual.deleteOneRecord(rowID);
                        //status = 1;
                        btnIn.setText("IN");
                        saveInOutDataInSharedPref("InOutStatus", "showing_IN", MainActivity.this);
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
                final View alertLayout = inflater.inflate(R.layout.my_alert_dialog, null);
                final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setView(alertLayout);
                final Button insert_above = (Button) alertLayout.findViewById(R.id.btn_insertAbove);
                final Button delete = (Button) alertLayout.findViewById(R.id.btn_delete);
                final Switch alarm_switch = (Switch) alertLayout.findViewById(R.id.alarm_switch);
                final RadioGroup alarmTone_radioGroup = (RadioGroup) alertLayout.findViewById(R.id.alarmTone_radioGroup);
                final AlertDialog dialog = alert.create();
                final RadioButton beep_radio = (RadioButton) alertLayout.findViewById(R.id.radio_beep);
                final RadioButton vibrate_radio = (RadioButton) alertLayout.findViewById(R.id.radio_vibrate);
                final RadioButton ring_radio = (RadioButton) alertLayout.findViewById(R.id.radio_ring);

                dialog.setTitle("Choose your option: ");
                alert.setCancelable(true);

                ArrayList<Entry> allEntries;
                allEntries = adapter_ob.fetchByDateList(selectedDate);
                plannedListIndex = scheduleList.getFirstVisiblePosition();
                View v = scheduleList.getChildAt(0);
                plannedListTop = (v == null) ? 0 : v.getTop();
                final Entry currentEntry = allEntries.get((int) id);
                final int rowID = currentEntry.getID();
                SharedPreferences pref = MainActivity.this.getSharedPreferences(SHARED_PREF_ALARM_TONES, Context.MODE_PRIVATE);
                if(pref.contains(String.valueOf(rowID))) {
                    alarm_switch.setChecked(true);
                    String toneFromSharedPref = getAlarmToneFromSharedPref(rowID, MainActivity.this);
                    if(toneFromSharedPref.equals("beep")){
                        alarmTone_radioGroup.clearCheck();
                        beep_radio.setChecked(true);
                    }else if(toneFromSharedPref.equals("vibrate")){
                        alarmTone_radioGroup.clearCheck();
                        vibrate_radio.setChecked(true);
                    }else if(toneFromSharedPref.equals("ring")){
                        alarmTone_radioGroup.clearCheck();
                        ring_radio.setChecked(true);
                    }
                    alarmTone_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            int rowID = currentEntry.getID();
                            String dateToSet = currentEntry.getDate();
                            String timeToSet = currentEntry.getStartTime();
                            String task = currentEntry.getTask();
                            int id = alarmTone_radioGroup.getCheckedRadioButtonId();
                            RadioButton selectedRadioBtn = (RadioButton) alertLayout.findViewById(id);
                            String selectedRadioText = selectedRadioBtn.getText().toString();
                            updateAlarm(rowID, dateToSet, timeToSet, task, selectedRadioText, MainActivity.this);
                        }
                    });
                }
                alarm_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int rowID = currentEntry.getID();
                        String dateToSet = currentEntry.getDate();
                        String timeToSet = currentEntry.getStartTime();
                        String task = currentEntry.getTask();
                        if(isChecked){
                            int id = alarmTone_radioGroup.getCheckedRadioButtonId();
                            RadioButton selectedRadioBtn = (RadioButton) alertLayout.findViewById(id);
                            String selectedRadioText = selectedRadioBtn.getText().toString();
                            setAlarm(rowID, dateToSet, timeToSet, task, selectedRadioText, MainActivity.this);
                        }else {
                            cancelAlarm(rowID, MainActivity.this);
                        }
                        dialog.dismiss();
                    }
                });

                insert_above.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        adapter_ob.insertDetails(date, start, start, "NEW*", "0:0", "N");
                        copyFromOtherTable();
                        dialog.dismiss();
                    }
                });

                delete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int rowID = currentEntry.getID();
                        adapter_ob.deleteOneRecord(rowID);
                        dialog.dismiss();
                    }
                });
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
                String buttonShowing = getInOutDataFromSharedPref("InOutStatus", MainActivity.this);
                if(buttonShowing.equals("showing_IN")) {  // status == 1
                    List<String> allTasks;
                    DatabaseManager manager = new DatabaseManager(MainActivity.this);
                    allTasks = manager.fetchAllTasks();
                    allTasks.add("None");
                    //Create sequence of items
                    Tasks = allTasks.toArray(new String[allTasks.size()]);
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    dialogBuilder.setTitle("Check In");
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

                            String start = new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime());
                            String startWithoutSpace = start.replace(" ", "");
                            String AmPmFormatOfStart = startWithoutSpace.substring(startWithoutSpace.length()-2, startWithoutSpace.length());
                            StringBuilder stringbuilder = new StringBuilder();
                            //stringbuilder.append("00").append(":").append("00").append("").append(AmPmFormatOfStart);
                            stringbuilder.append("--------------");
                            String e = stringbuilder.toString();
                            DatabaseManagerForActual manager = new DatabaseManagerForActual(MainActivity.this);
                            String total = "--------";
                            manager.insertDetails(selectedDate, startWithoutSpace, e, selectedText, total);
                            showListForActual();
                            DatabaseManagerForActual manager1 = new DatabaseManagerForActual(MainActivity.this);
                            Cursor c = manager1.fetchAllCursor();
                            c.moveToLast();
                            ongoingID = c.getInt(c.getColumnIndex("_id"));
                            Log.d("rowId ongoign ",  String.valueOf(ongoingID));
                            ongoingDate = selectedDate;
                            btnIn.setText("OUT");
                            saveInOutDataInSharedPref("ongoingID", String.valueOf(ongoingID), MainActivity.this);
                            saveInOutDataInSharedPref("ongoingDate", ongoingDate, MainActivity.this);
                            saveInOutDataInSharedPref("InOutStatus", "showing_OUT", MainActivity.this);
                            showListForActual();
                            //status = 0;
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    dialogBuilder.setNeutralButton("+", new DialogInterface.OnClickListener() {

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

                            String start = new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime());
                            String startWithoutSpace = start.replace(" ", "");

                            String AmPmFormatOfStart = startWithoutSpace.substring(startWithoutSpace.length()-2, startWithoutSpace.length());
                            StringBuilder stringbuilder = new StringBuilder();
                            //stringbuilder.append("00").append(":").append("00").append("").append(AmPmFormatOfStart);
                            stringbuilder.append("--------------");
                            String e = stringbuilder.toString();
                            DatabaseManagerForActual manager = new DatabaseManagerForActual(MainActivity.this);
                            String total = "--------";
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
                            saveInOutDataInSharedPref("ongoingID", String.valueOf(ongoingID), MainActivity.this);
                            saveInOutDataInSharedPref("ongoingDate", ongoingDate, MainActivity.this);
                            saveInOutDataInSharedPref("InOutStatus", "showing_OUT", MainActivity.this);
                            showListForActual();
                            //status = 0;
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
                    ongoingDate = getInOutDataFromSharedPref("ongoingDate", MainActivity.this);
                    String ongoingIDstr  = getInOutDataFromSharedPref("ongoingID", MainActivity.this);
                    ongoingID = Integer.parseInt(ongoingIDstr);
                    Cursor c = manager.fetchByDate(ongoingDate);
                    if (c.moveToFirst()) {
                        do {
                            int rowId = (c.getInt(c.getColumnIndex("_id")));
                            if(rowId == ongoingID){
                                ongoingStartTime = c.getString(c.getColumnIndex("startTime"));
                                ongoingTask = c.getString(c.getColumnIndex("taskName"));
                                saveInOutDataInSharedPref("ongoingTask", ongoingTask, MainActivity.this);
                                saveInOutDataInSharedPref("ongoingStartTime", ongoingStartTime, MainActivity.this);
                                break;
                            }
                        } while (c.moveToNext());
                    }

                    String end = new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime());
                    String endWithoutSpace = end.replace(" ", "");

                    String formatedStartTime = TimeCalculations.convertAmPmToHHmmssTimeFormat(ongoingStartTime);
                    String formatedEndTime = TimeCalculations.convertAmPmToHHmmssTimeFormat(endWithoutSpace);
                    String total = TimeCalculations.calculateTotal(formatedStartTime, formatedEndTime);

                    manager.updateldetail(ongoingID, ongoingDate, ongoingStartTime, endWithoutSpace, ongoingTask, total);
                    btnIn.setText("IN");
                    saveInOutDataInSharedPref("InOutStatus", "showing_IN", MainActivity.this);
                    showListForActual();
                    btnIn.performClick();
                    //status = 1;
                }
            }
        });

        btnNewTask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new DatabaseManager(MainActivity.this);

                new AsyncTask<String, Void, Cursor>(){
                    @Override
                    protected Cursor doInBackground(String... params) {
                        Cursor c = adapter.fetchByDate(selectedDate);
                        return c;
                    }

                    @Override
                    protected void onPostExecute(Cursor c) {
                        super.onPostExecute(c);

                        if(c.moveToLast()) {
                            //c.moveToLast();
                            String start = c.getString(3); // get end time and store as start for next task

                            String endTime = TimeCalculations.forwardTimeByGivenHour(start, 1, 0);
                            Log.d("Endtime from timeCalc", endTime);
                            String task = c.getString(4); // get task name

                            String formatedStartTime = TimeCalculations.convertAmPmToHHmmssTimeFormat(start);
                            String formatedEndTime = TimeCalculations.convertAmPmToHHmmssTimeFormat(endTime);
                            Log.d("Endtime", formatedStartTime);
                            Log.d("Endtime", formatedEndTime);
                            String total = TimeCalculations.calculateTotal(formatedStartTime, formatedEndTime);

                            adapter.insertDetails(selectedDate, start, endTime, task, total, "N");
                            c.close();
                            showlist();
                        }else { // when database is empty
                            DateFormat time = new SimpleDateFormat("hh:mm a");
                            String start = time.format(Calendar.getInstance().getTime());
                            String startWithoutSpace = start.replace(" ", "");

                            String endTime = TimeCalculations.forwardTimeByGivenHour(startWithoutSpace, 1, 0);
                            String task = "None";

//                            String formattedDateStart = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
//                            Calendar cal = Calendar.getInstance();
//                            cal.add(Calendar.HOUR, 1);
//                            Date d = cal.getTime();
//                            String formattedDateEnd = new SimpleDateFormat("HH:mm:ss").format(d);
                            String formatedStartTime = TimeCalculations.convertAmPmToHHmmssTimeFormat(startWithoutSpace);
                            String formatedEndTime = TimeCalculations.convertAmPmToHHmmssTimeFormat(endTime);

                            Log.d("Endtime", formatedStartTime);
                            Log.d("Endtime", formatedEndTime);
                            String total = TimeCalculations.calculateTotal(formatedStartTime, formatedEndTime);

                            Log.d("mainActivity 1", total);
                            adapter.insertDetails(selectedDate, startWithoutSpace, endTime, task, total, "N");
                            c.close();
                            showlist();
                        }
                    }
                }.execute(selectedDate);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
        adapter_ob = new DatabaseManager(this);

        switch (item.getItemId()){

            case R.id.make_fixed:
                break;

            case R.id.alarm:
                LayoutInflater inflater = getLayoutInflater();
                final View alertLayout = inflater.inflate(R.layout.central_alarm_selector_dialog, null);
                final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setView(alertLayout);
                final Switch alarm_switch = (Switch) alertLayout.findViewById(R.id.central_alarm_switch);
                final RadioGroup alarmTone_radioGroup = (RadioGroup) alertLayout.findViewById(R.id.central_alarmTone_radioGroup);
                final AlertDialog dialog = alert.create();
                final RadioButton beep_radio = (RadioButton) alertLayout.findViewById(R.id.central_radio_beep);
                final RadioButton vibrate_radio = (RadioButton) alertLayout.findViewById(R.id.central_radio_vibrate);
                final RadioButton ring_radio = (RadioButton) alertLayout.findViewById(R.id.central_radio_ring);

                dialog.setTitle("Choose your option: ");
                alert.setCancelable(true);
                final ArrayList<Entry> allEntries;
                allEntries = adapter_ob.fetchByDateList(selectedDate);
                SharedPreferences centralAlarmSharedPref  = getSharedPreferences(SHARED_PREF_CENTRAL_ALARM_TONES, Context.MODE_PRIVATE);
                if(centralAlarmSharedPref.contains("Central Tone")){
                    alarm_switch.setChecked(true);

                    String centralAlarmTone = centralAlarmSharedPref.getString("Central Tone", null);
                    if(centralAlarmTone.equals("beep")){
                        beep_radio.setChecked(true);
                    }else if (centralAlarmTone.equals("vibrate")){
                        vibrate_radio.setChecked(true);
                    }else  if (centralAlarmTone.equals("ring")){
                        ring_radio.setChecked(true);
                    }

                    alarmTone_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            int id = alarmTone_radioGroup.getCheckedRadioButtonId();
                            RadioButton selectedRadioBtn = (RadioButton) alertLayout.findViewById(id);
                            String selectedRadioText = selectedRadioBtn.getText().toString();

                            for(int i = 0 ; i<allEntries.size(); i++) {
                                final Entry currentEntry = allEntries.get(i);
                                int rowID = currentEntry.getID();
                                String dateToSet = currentEntry.getDate();
                                String timeToSet = currentEntry.getStartTime();
                                String task = currentEntry.getTask();
                                updateAlarm(rowID, dateToSet, timeToSet, task, selectedRadioText, MainActivity.this);
                            }
                        }
                    });
                }

                alarm_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if(isChecked){
                                int id = alarmTone_radioGroup.getCheckedRadioButtonId();
                                RadioButton selectedRadioBtn = (RadioButton) alertLayout.findViewById(id);
                                String selectedRadioText = selectedRadioBtn.getText().toString();
                                saveCentralAlarmToneInSharedPref(MainActivity.this, selectedRadioText);

                                for(int i = 0 ; i<allEntries.size(); i++) {
                                    final Entry currentEntry = allEntries.get(i);
                                    int rowID = currentEntry.getID();
                                    String dateToSet = currentEntry.getDate();
                                    String timeToSet = currentEntry.getStartTime();
                                    String task = currentEntry.getTask();

                                    Log.d("Setting alarm at: ", timeToSet);
                                    setAlarm(rowID, dateToSet, timeToSet, task, selectedRadioText, MainActivity.this);
                                }
                            }else {

                                for(int i = 0 ; i<allEntries.size(); i++) {
                                    final Entry currentEntry = allEntries.get(i);
                                    int rowID = currentEntry.getID();
                                    cancelAlarm(rowID, MainActivity.this);
                                    removeCentralAlarmToneInSharedPref(MainActivity.this);
                                }
                            }
                            dialog.dismiss();
                    }
                });
                dialog.show();

                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void removeCentralAlarmToneInSharedPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_CENTRAL_ALARM_TONES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if(pref.contains("Central Tone")){
            editor.remove("Central Tone");
        }
        editor.commit();
    }

    public void saveCentralAlarmToneInSharedPref(Context context, String centralTone) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF_CENTRAL_ALARM_TONES, 0).edit();
        editor.putString("Central Tone", centralTone);
        editor.commit();
    }

    public boolean isSharedPrefExist(String fileName) {
        String filename = "/data/data/" + app_id + "/shared_prefs/" + fileName + ".xml";
        Log.d("filename: ", filename);
        File f = new File(filename);
        if (f.exists()){
            Log.d("TAG", "SharedPreferences inOUT : exist");
            return true;
        }
        else{
            Log.d("TAG", "Setup default preferences");
            return  false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent ev) {
        // Do your calcluations
        return super.dispatchTouchEvent(ev);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";
        static final int SWIPE_MIN_DISTANCE = 120;
        static final int SWIPE_MAX_OFF_PATH = 250;
        static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {

            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH
                        || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
                    return false;
                }
                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) {
                    Toast.makeText(MainActivity.this, "bottomToTop" ,
                            Toast.LENGTH_SHORT).show();
                } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE) {
                    Toast.makeText(MainActivity.this,
                            "topToBottom  " , Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
                    return false;
                }
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
                    Toast.makeText(MainActivity.this,
                            "swipe RightToLeft " , Toast.LENGTH_SHORT).show();
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {


                    Toast.makeText(MainActivity.this,
                            "swipe LeftToright  " , Toast.LENGTH_SHORT).show();


                }
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public String getAlarmToneFromSharedPref(int rowID, Context context) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_ALARM_TONES, Context.MODE_PRIVATE);
        String tone = pref.getString(String.valueOf(rowID), null);
        return tone;
    }

    public void cancelAlarm(int rowID, Context context) {
        removeAlarmToneForRowFromSharedPref(rowID, context);
        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                rowID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void removeAlarmToneForRowFromSharedPref(int rowID, Context context) {
        String rowIDStr = String.valueOf(rowID);
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_ALARM_TONES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if(pref.contains(rowIDStr)){
            editor.remove(rowIDStr);
        }
        editor.commit();
    }

    public void setAlarm(int rowID, String dateToSet, String timeToSet, String task, String tone, Context context) {
        int hour = 0;
        String spaceAddedTimeToset = timeToSet.substring(0, 5) + " " + timeToSet.substring(5, timeToSet.length());
        Log.d("time to  set: ", timeToSet);
        if(spaceAddedTimeToset.substring(spaceAddedTimeToset.indexOf(" ")+1, spaceAddedTimeToset.length()).equals("PM")){
            String hr = spaceAddedTimeToset.substring(0, 2);
            if(!hr.equals("12")){
                hour = Integer.parseInt(hr) + 12;
            }else {
                hour = Integer.parseInt(hr);
            }
        }else {
            String hr = spaceAddedTimeToset.substring(0, 2);
            if(!hr.equals("12")){
                hour = Integer.parseInt(hr);
            }else {
                hour = 0;
            }
        }
        String minute = spaceAddedTimeToset.substring(3, 5);
        int min = Integer.parseInt(minute);
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = format.parse(dateToSet);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        cal.setTime(date);
        Log.d("hour after to  set: ", String.valueOf(hour));
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);

        if(cal.getTimeInMillis() > System.currentTimeMillis()){
            Notification notification = getNotification( task + " task has started", tone, context);
            Intent notificationIntent = new Intent(context, AlarmReceiver.class);
            notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, rowID);
            notificationIntent.putExtra(AlarmReceiver.NOTIFICATION, notification);
            notificationIntent.putExtra("Tone", tone);
//        alarmsMap.put(rowID, tone);
            saveAlarmToneForRowInSharedPref(rowID, tone, context);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    rowID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Log.d("hour set: ", String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
            Log.d("hour set: ", String.valueOf(cal.get(Calendar.MINUTE)));
            Log.d("hour set: ", String.valueOf(cal.get(Calendar.SECOND)));
            Log.d("hour set: ", String.valueOf(cal.getTimeInMillis()));
            Log.d("date set: ", String.valueOf(date.toString()));

            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }
    }

    public void saveAlarmToneForRowInSharedPref(int rowID, String tone, Context context) {
        String rowIDStr = String.valueOf(rowID);
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF_ALARM_TONES, 0).edit();
        editor.putString(rowIDStr, tone);
        editor.commit();
    }

    public void saveInOutDataInSharedPref(String stringName, String status, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF_IN_OUT_DATA, 0).edit();
        editor.putString(stringName, status);
       // editor.putString("InOutStatus", status);
        editor.commit();
    }

    private String getInOutDataFromSharedPref(String stringName, Context context) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_IN_OUT_DATA, Context.MODE_PRIVATE);
        String buttonShowing = pref.getString(stringName, null);
        return buttonShowing;
    }

    public void saveMiscDataInSharedPref(String stringName, String value, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF_MISC_DATA, 0).edit();
        editor.putString(stringName, value);
        // editor.putString("InOutStatus", status);
        editor.commit();
    }

    private String getMiscDataFromSharedPref(String stringName, Context context) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_MISC_DATA, Context.MODE_PRIVATE);
        String value = pref.getString(stringName, null);
        return value;
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
                        adapter_ob.insertDetails(date, start, end, task, total, "N");
                } while (c1.moveToNext());
            }
        }
        dm.deleteTable();
        c1.close();
    }

    private Notification getNotification(String content, String tone, Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        if(tone.equals("vibrate")){
            builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        }
        builder.setContentTitle("Scheduler");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.icon);
        return builder.build();
    }

    public void updateAlarm(int rowId, String date, String timeToSet, String task, String changedTone, Context context){
        String tone = changedTone;
//        String previosTone = alarmsMap.get(rowId);
        String previosTone = getAlarmToneFromSharedPref(rowId, context);

        if(previosTone != null){
            if(tone == null){
                tone = previosTone;
            }
            cancelAlarm(rowId, context);
            setAlarm(rowId, date, timeToSet, task, tone, context);
        }
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
                    txDate.setText(formatedDate);
                    String todayDate = getTodayDate();
                    try {
                        if(todayDate.equals(selectedDate)){
                            copyOldToDo();
                            btnIn.setEnabled(true);
                        }else {
                            btnIn.setEnabled(false);
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

    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d("window focised1", "focus changed");
        super.onWindowFocusChanged(hasFocus);
        Log.d("window focised2", "focus changed");
        showListForActual();
        showlist();
        try {
            showlistToDo();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("window focised3", "focus changed");
    }

    @Override
    protected void onResume() {
        Toast.makeText(MainActivity.this, "in resume", Toast.LENGTH_LONG).show();
        super.onResume();
        showlist();
        showListForActual();
        try {
            showlistToDo();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private class showPlannedList extends AsyncTask<String, Object, ArrayList<Entry>> {
        ArrayList<Entry> allEntries = new ArrayList<Entry>();

        @Override
        protected ArrayList<Entry> doInBackground(String... params) {
            DatabaseManager adapter_ob = new DatabaseManager(MainActivity.this);
            Cursor c1 =  adapter_ob.fetchByDate(params[0]);
            Log.d("date in shwlist", selectedDate);
            if (c1 != null) {
                if (c1.moveToFirst()) {
                    do {
                        Entry allItems = new Entry();
                        allItems.setID(c1.getInt(0));
                        allItems.setStart(c1.getString(2));
                        allItems.setEnd(c1.getString(3));
                        allItems.setTask(c1.getString(4));
                        allItems.setTotal(c1.getString(5));
                        allEntries.add(allItems);
                        Log.d("work in loadign", "too much");
                    } while (c1.moveToNext());
                }
            }
            c1.close();
            return allEntries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> allEntries) {
            super.onPostExecute(allEntries);

            CustomAdapter customAdapter = new CustomAdapter(MainActivity.this, allEntries);
            scheduleList.setAdapter(customAdapter);
            scheduleList.setSelectionFromTop(plannedListIndex, plannedListTop);
        }
    }

    public void showlist() {
        //txDate.setText(formatedDate);
        adapter_ob  = new DatabaseManager(MainActivity.this);
        Log.d("selectedDate is ",selectedDate);
        Cursor c1 =  adapter_ob.fetchByDate(selectedDate);
        if(c1.moveToLast()){
            String lastTime = c1.getString(3);
            Log.d("lastTime is ",lastTime);
            if(lastTime.equals("11:59PM")){
                btnNewTask.setEnabled(false);
            }else {
                Log.d("lastTime is ","");
                btnNewTask.setEnabled(true);
            }
        }else {
            Log.d("lastTime is ","");
            btnNewTask.setEnabled(true);
        }
        c1.close();
        new showPlannedList().execute(selectedDate);
    }

    public void showlistToDo() throws ParseException {
        copyOldToDo();
        new showToDoList().execute(selectedDate);
    }

    private class showToDoList extends AsyncTask<String, Object, ArrayList<EntryToDo>> {
        ArrayList<EntryToDo> allEntries = new ArrayList<EntryToDo>();

        @Override
        protected ArrayList<EntryToDo> doInBackground(String... params) {
            DatabaseManagerToDo adapter_ob_ToDo = new DatabaseManagerToDo(MainActivity.this);
            Cursor c1 = adapter_ob_ToDo.fetchByDate(params[0]);
            if (c1 != null) {
                if (c1.moveToFirst()) {
                    do {
                        EntryToDo allItems = new EntryToDo();
                        allItems.setTask(c1.getString(2));
                        allItems.setStatus(c1.getString(3));
                        allEntries.add(allItems);
                    } while (c1.moveToNext());
                }
            }
            c1.close();
            return allEntries;
        }

        @Override
        protected void onPostExecute(ArrayList<EntryToDo> allEntries) {
            super.onPostExecute(allEntries);
            CustomAdapterToDo customAdapterToDo = new CustomAdapterToDo(MainActivity.this, allEntries);
            toDoList.setAdapter(customAdapterToDo);
        }
    }

    public void showListForActual() {
        new showActualList().execute(selectedDate);
    }

    private class showActualList extends AsyncTask<String, Object, ArrayList<Entry>> {
        ArrayList<Entry> allEntries = new ArrayList<Entry>();

        @Override
        protected ArrayList<Entry> doInBackground(String... params) {
            DatabaseManagerForActual manager_ob_actual = new DatabaseManagerForActual(MainActivity.this);
            Cursor c1 =  manager_ob_actual.fetchByDate(params[0]);
            if (c1 != null) {
                if (c1.moveToFirst()) {
                    do {
                        Entry allItems = new Entry();
                        allItems.setID(c1.getInt(0));
                        allItems.setStart(c1.getString(2));
                        allItems.setEnd(c1.getString(3));
                        allItems.setTask(c1.getString(4));
                        allItems.setTotal(c1.getString(5));
                        allEntries.add(allItems);
                    } while (c1.moveToNext());
                }
            }
            c1.close();
            return allEntries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> allEntries) {
            super.onPostExecute(allEntries);

            CustomAdapterForActual customAdapterForActual = new CustomAdapterForActual(
                    MainActivity.this, allEntries);
            actualScheduleList.setAdapter(customAdapterForActual);
            actualScheduleList.setSelectionFromTop(actualListIndex, actualListTop);
        }
    }

    public void setToOldSchedule(String selectedOldDateStr) {
        adapter_ob = new DatabaseManager(MainActivity.this);
        Cursor c1 = adapter_ob.fetchByDate(selectedOldDateStr);

        if (c1.moveToFirst()) {
            do {
                String start = c1.getString(2);
                String end = c1.getString(3);
                String task = c1.getString(4);
                String total = c1.getString(5);
                adapter_ob.insertDetails(selectedDate, start, end, task, total, "N");
            } while (c1.moveToNext());
        }
        c1.close();
        showlist();
    }
}
