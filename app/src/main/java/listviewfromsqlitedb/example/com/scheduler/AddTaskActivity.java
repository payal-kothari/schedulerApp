package listviewfromsqlitedb.example.com.scheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by payalkothari on 12/22/16.
 */

public class AddTaskActivity extends Activity {
    DatabaseManager adapter;
    DatabaseHelper helper;
    public TextView startTime, endTime, taskName;
    Button btnSubmit, btnReset;
    private static final int  REQUEST_CODE_START_TIME = 1;
    private static final int  REQUEST_CODE_END_TIME = 2;
    private String format = "";
    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);
        startTime = (TextView) findViewById(R.id.tx_startTime);
        endTime = (TextView) findViewById(R.id.tx_endTime);
        taskName = (EditText) findViewById(R.id.et_taskName);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        adapter = new DatabaseManager(this);

        startTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTaskActivity.this, TimePickerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_START_TIME);
            }
        });

        endTime.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTaskActivity.this, TimePickerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_END_TIME);
            }
        });

        btnSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String start = startTime.getText().toString();
                String end = endTime.getText().toString();
                String task = taskName.getText().toString();
                long val = adapter.insertDetails(start, end, task);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_START_TIME) {
            if(resultCode == Activity.RESULT_OK) {
                int result_hour = data.getIntExtra(TimePickerActivity.HOUR, 0);
                int result_min = data.getIntExtra(TimePickerActivity.HOUR, 0);
                showStartTime(result_hour, result_min);
            } else {
                calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);
                showStartTime(hour, min);
            }
        }
        else if (requestCode == REQUEST_CODE_END_TIME){
            if(resultCode == Activity.RESULT_OK) {
                int result_hour = data.getIntExtra(TimePickerActivity.HOUR, 0);
                int result_min = data.getIntExtra(TimePickerActivity.HOUR, 0);
                showEndTime(result_hour, result_min);
            } else {
                calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);
                showEndTime(hour, min);
            }
        }
    }

    public void showStartTime(int hour, int min) {
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

        startTime.setText(new StringBuilder().append(hour).append(" : ").append(min)
                .append(" ").append(format));
    }

    public void showEndTime(int hour, int min) {
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

        endTime.setText(new StringBuilder().append(hour).append(" : ").append(min)
                .append(" ").append(format));
    }

}