package pp.example.one.scheduler;

/**
 * Created by payalkothari on 12/23/16.
 */

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

public class EditActivity extends Activity {
    DatabaseManager regadapter;
    DatabaseHelper openHelper;
    int rowId;
    Cursor c;
    TextView startTime, endTime;
    EditText taskName;
    Button btnUpdate, btnDelete;
    TableRow startTimeRow, endTimeRow;
    private String format = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle showData = getIntent().getExtras();
        rowId = showData.getInt("keyid");
        regadapter = new DatabaseManager(this);

//        c = regadapter.fetch(rowId);
//
//        if (c.moveToFirst()) {
//            do {
//                startTime.setText(c.getString(1));
//                endTime.setText(c.getString(2));
//                taskName.setText(c.getString(3));
//            } while (c.moveToNext());
//        }
//
//        startTimeRow.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Calendar mcurrentTime = Calendar.getInstance();
//                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                int minute = mcurrentTime.get(Calendar.MINUTE);
//                TimePickerDialog mTimePicker;
//                mTimePicker = new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
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
//                mTimePicker = new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                        showEndTime(selectedHour, selectedMinute);
//                    }
//                }, hour, minute, false);
//                mTimePicker.setTitle("Select Time");
//                mTimePicker.show();
//            }
//        });
//
//        btnUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                regadapter.updateldetail(rowId, startTime.getText().toString(),
//                        endTime.getText().toString(), taskName.getText().toString());
//                finish();
//            }
//        });
//
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                regadapter.deleteOneRecord(rowId);
//                finish();
//            }
//        });
    }

    public String showStartTime(int hour, int min) {
        String minstr;
        String hourStr;
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

        if(hour >= 10){
            hourStr = String.valueOf(hour);
        }else {
            hourStr = "0" + String.valueOf(hour);
        }

        if(min >= 10){
            minstr = String.valueOf(min);
        }else {
            minstr = "0" + String.valueOf(min);
        }
        return (new StringBuilder().append(hourStr).append(":").append(minstr)
                .append("").append(format)).toString();
    }

    public String showEndTime(int hour, int min) {
        String minstr;
        String hourStr;

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
        if(hour >= 10){
            hourStr = String.valueOf(hour);
        }else {
            hourStr = "0" + String.valueOf(hour);
        }

        if(min >= 10){
            minstr = String.valueOf(min);
        }else {
            minstr = "0" + String.valueOf(min);
        }
        return (new StringBuilder().append(hourStr).append(":").append(minstr)
                .append("").append(format)).toString();
    }

}