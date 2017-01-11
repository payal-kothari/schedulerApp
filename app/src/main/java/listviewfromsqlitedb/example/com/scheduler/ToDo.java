package listviewfromsqlitedb.example.com.scheduler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by payalkothari on 1/11/17.
 */
public class ToDo extends Activity{

    TextView txDate;
    DatabaseManagerToDo adapter_ob_ToDo;
    static String date;
    ListView toDoList;
    Button btnAddTask;
    EditText ed_Task;
    static int statusID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        txDate = (TextView) findViewById(R.id.tx_date);
        toDoList = (ListView) findViewById(R.id.toDo_list);
        btnAddTask = (Button) findViewById(R.id.btn_addTask);
        ed_Task = (EditText) findViewById(R.id.ed_task);

        Intent intent = getIntent();
        date = intent.getStringExtra("DATE");
        txDate.setText(date);

        showlist();

        toDoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(ToDo.this);
                adb.setTitle("Enter task name: ");
                final EditText input = new EditText(ToDo.this);
                adb.setView(input);
                adb.setIcon(android.R.drawable.ic_dialog_alert);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        adapter_ob_ToDo = new DatabaseManagerToDo(ToDo.this);
                        ArrayList<EntryToDo> allEntries = new ArrayList<EntryToDo>();
                        allEntries = adapter_ob_ToDo.fetchByDateList(date);
                        EntryToDo currentEntry = allEntries.get((int) id);
                        int rowID = currentEntry.getID();
                        String dateForThisEntry = currentEntry.getDate();
                        int statusId = currentEntry.getStatusID();
                        String resultTask = input.getEditableText().toString();
                        adapter_ob_ToDo.updateldetail(rowID, dateForThisEntry, resultTask, "N", statusId);
                    } });
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    } });
                adb.show();
                return true;
            }
        });


        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = ed_Task.getText().toString();
                ed_Task.setText("");
                if(!taskName.equals("")){
                    statusID++;
                    adapter_ob_ToDo = new DatabaseManagerToDo(ToDo.this);
                    adapter_ob_ToDo.insertDetails(date, taskName, "N", statusID);
                    showlist();
                }
            }
        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showlist();
    }

    private void showlist() {
        adapter_ob_ToDo = new DatabaseManagerToDo(this);
        ArrayList<EntryToDo> allEntries = new ArrayList<EntryToDo>();
        allEntries.clear();
        Cursor c1 = adapter_ob_ToDo.fetchByDate(date);
        Log.d("date in shwlist", date);
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
        CustomAdapterToDo customAdapterToDo = new CustomAdapterToDo(ToDo.this, allEntries);
        toDoList.setAdapter(customAdapterToDo);
    }
}
