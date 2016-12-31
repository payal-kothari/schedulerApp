package listviewfromsqlitedb.example.com.scheduler;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by payalkothari on 12/30/16.
 */
public class CustomAdapter extends BaseAdapter{
    DatabaseManager adapter_ob;
    Context context;
    List<Entry> list;
    LayoutInflater layoutInflater = null;
    EditActivity editActivity = new EditActivity();
    MainActivity mainActivity;

    public CustomAdapter(Context c, List<Entry> listE) {
        this.context = c;
        this.list = listE;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        final ListViewHolder listViewHolder;
        if(v == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.row, null);
            listViewHolder = new ListViewHolder(v);
            listViewHolder.tx_s = (TextView) v.findViewById(R.id.tv_startTime);
            listViewHolder.tx_e = (TextView) v.findViewById(R.id.tv_endTime);
            listViewHolder.tx_t = (TextView) v.findViewById(R.id.tv_task);
            v.setTag(listViewHolder);
        }
        else {
            listViewHolder = (ListViewHolder) v.getTag();
        }

        listViewHolder.tx_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "you clicked start" , Toast.LENGTH_SHORT).show();
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        adapter_ob = new DatabaseManager(context);
                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
                        allEntries.clear();
                        allEntries = adapter_ob.fetchAll();
                        Entry currentEntry = allEntries.get(position);
                        int rowID = currentEntry.getID();
                        String endT = currentEntry.getEndTime();
                        String taskN = currentEntry.getTask();
                        String resultS = editActivity.showStartTime(selectedHour, selectedMinute);
                        adapter_ob.updateldetail(rowID, resultS, endT, taskN);
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        listViewHolder.tx_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        adapter_ob = new DatabaseManager(context);
                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
                        allEntries.clear();
                        allEntries = adapter_ob.fetchAll();
                        Entry currentEntry = allEntries.get(position);
                        int rowID = currentEntry.getID();
                        String startT = currentEntry.getStartTime();
                        String taskN = currentEntry.getTask();
                        String resultE = editActivity.showEndTime(selectedHour, selectedMinute);
                        adapter_ob.updateldetail(rowID, startT, resultE, taskN);
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        listViewHolder.tx_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Enter Task Name "); //Set Alert dialog title here

                // Set an EditText view to get user input
                final EditText input = new EditText(context);
                alert.setView(input);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //You will get as string input data in this variable.
                        // here we convert the input to a string and show in a toast.
                        String resultTask = input.getEditableText().toString();
                        if (resultTask.equals("")){
                            resultTask = "-";
                        }
                        adapter_ob = new DatabaseManager(context);
                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
                        allEntries.clear();
                        allEntries = adapter_ob.fetchAll();
                        Entry currentEntry = allEntries.get(position);
                        int rowID = currentEntry.getID();
                        String startT = currentEntry.getStartTime();
                        String endT = currentEntry.getEndTime();
                        Toast.makeText(context,resultTask,Toast.LENGTH_LONG).show();
                        adapter_ob.updateldetail(rowID, startT, endT, resultTask);

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


        listViewHolder.tx_s.setText(list.get(position).startTime);
        listViewHolder.tx_e.setText(list.get(position).endTime);
        listViewHolder.tx_t.setText(list.get(position).taskName);

        return v;
    }

    class ListViewHolder{
        public TextView tx_s, tx_e, tx_t;

        public ListViewHolder(View base) {
            tx_s = (TextView) base.findViewById(R.id.tv_startTime);
            tx_e = (TextView) base.findViewById(R.id.tv_endTime);
            tx_t = (TextView) base.findViewById(R.id.tv_task);
        }

    }

    public void showDialogBox(){

    }
}
