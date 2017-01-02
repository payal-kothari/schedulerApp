package listviewfromsqlitedb.example.com.scheduler;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by payalkothari on 1/2/17.
 */
public class FirstActivity extends Activity{
    Button btnCalendar;
    int year, month, day;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_first_activity);
        btnCalendar = (Button) findViewById(R.id.btn_calendar);

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

        String selectedDate = strbuilder.toString();

        Toast.makeText(this, strbuilder.toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(FirstActivity.this, MainActivity.class);
        intent.putExtra("DATE", selectedDate);
        startActivity(intent);
    }
}
