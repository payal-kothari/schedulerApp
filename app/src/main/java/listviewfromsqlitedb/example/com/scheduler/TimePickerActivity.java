package listviewfromsqlitedb.example.com.scheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

/**
 * Created by payalkothari on 12/23/16.
 */
public class TimePickerActivity extends Activity{
    Button btnSave;
    private TimePicker timePicker1;
    protected static final String HOUR = "Hour";
    protected static final String MIN = "Min";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timepicker);
        btnSave = (Button) findViewById(R.id.btn_save);
        timePicker1 = (TimePicker) findViewById(R.id.timePicker1);

        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int hour = timePicker1.getCurrentHour();
                int min = timePicker1.getCurrentMinute();
                Intent timeIntent = new Intent(TimePickerActivity.this, AddTaskActivity.class);
                timeIntent.putExtra(HOUR, hour);
                timeIntent.putExtra(MIN, min);
                setResult(Activity.RESULT_OK, timeIntent);
                finish();
            }
        });
    }
}
