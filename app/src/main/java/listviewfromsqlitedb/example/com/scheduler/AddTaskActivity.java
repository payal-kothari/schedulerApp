package listviewfromsqlitedb.example.com.scheduler;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by payalkothari on 12/22/16.
 */

public class AddTaskActivity extends Activity {
    DatabaseManager adapter;
    DatabaseHelper helper;
    public TextView startTime, endTime;
    private String format = "";
    int concatedHrAndMinInt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new DatabaseManager(this);

        Cursor c = adapter.fetchAllCursor();
        int num = c.getCount();
        if(num > 0) {
            c.moveToLast();
            String start = c.getString(c.getColumnIndex(helper.START_TIME));
            String end = c.getString(c.getColumnIndex(helper.END_TIME));
            startTime.setText(start);
            endTime.setText(end);
        }

//        startTimeRow.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Calendar mcurrentTime = Calendar.getInstance();
//                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                int minute = mcurrentTime.get(Calendar.MINUTE);
//                TimePickerDialog mTimePicker;
//                mTimePicker = new TimePickerDialog(AddTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                        startTimeInt = concatIntegers(selectedHour,selectedMinute);
//                        showStartTime(selectedHour, selectedMinute);
//                    }
//                }, hour, minute, false);
//                mTimePicker.setTitle("Select Time");
//                mTimePicker.show();
//            }
//        });
//
//        endTimeRow.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Calendar mcurrentTime = Calendar.getInstance();
//                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                int minute = mcurrentTime.get(Calendar.MINUTE);
//                TimePickerDialog mTimePicker;
//                mTimePicker = new TimePickerDialog(AddTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                        endTimeInt = concatIntegers(selectedHour,selectedMinute);
//                        showEndTime(selectedHour, selectedMinute);
//                    }
//                }, hour, minute, false);
//                mTimePicker.setTitle("Select Time");
//                mTimePicker.show();
//            }
//        });

//        btnSubmit.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                String start = startTime.getText().toString();
//                String end = endTime.getText().toString();
//                String task = taskName.getText().toString();
//                long val = adapter.insertDetails(start, end, task);
//                finish();
//            }
//        });
    }

    public int concatIntegers(int hr, int min){
        String concatedHrAndMinStr = String.valueOf(hr) + String.valueOf(min);
        concatedHrAndMinInt = Integer.parseInt(concatedHrAndMinStr);
        return concatedHrAndMinInt;
    }

    public void showStartTime(int hour, int min) {
        String minstr;
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

        if(min >= 10){
            minstr = String.valueOf(min);
        }else {
            minstr = "0" + String.valueOf(min);
        }
        startTime.setText(new StringBuilder().append(hour).append(":").append(minstr)
                .append("").append(format));
    }

    public void showEndTime(int hour, int min) {
        String minstr;
        String hrStr = "";
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
        if(min >= 10){
            minstr = String.valueOf(min);
        }else {
            minstr = "0" + String.valueOf(min);
        }
        if(hour < 10){
            hrStr = "0" + String.valueOf(hour);
        }else {
            hrStr = String.valueOf(hour);
        }

        endTime.setText(new StringBuilder().append(hrStr).append(":").append(minstr)
                .append("").append(format));
    }

}