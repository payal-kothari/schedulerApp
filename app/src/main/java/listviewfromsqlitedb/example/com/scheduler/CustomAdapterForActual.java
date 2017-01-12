package listviewfromsqlitedb.example.com.scheduler;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class CustomAdapterForActual extends BaseAdapter{
    DatabaseManagerForActual adapter_ob;
    Context context;
    List<Entry> list;
    LayoutInflater layoutInflater = null;
    EditActivity editActivity = new EditActivity();
    MainActivity mainActivity;
    static String selectedText;
    CharSequence[] Tasks;

    public CustomAdapterForActual(Context c, List<Entry> listE) {
        this.context = c;
        this.list = listE;
        this.mainActivity = new MainActivity();
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
            listViewHolder.tx_tot = (TextView) v.findViewById(R.id.tv_total);
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
                        adapter_ob = new DatabaseManagerForActual(context);
                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
                        allEntries = adapter_ob.fetchByDateList(MainActivity.selectedDate);
                        Entry currentEntry = allEntries.get(position);
                        int rowID = currentEntry.getID();
                        String dateForThisEntry = currentEntry.getDate();
                        String endT = currentEntry.getEndTime();
                        String taskN = currentEntry.getTask();
                        String resultS = editActivity.showStartTime(selectedHour, selectedMinute);
                        String total = mainActivity.calculateTotal(resultS, endT);
                        adapter_ob.updateldetail(rowID, dateForThisEntry, resultS, endT, taskN, total);
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
                        adapter_ob = new DatabaseManagerForActual(context);
                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
                        allEntries = adapter_ob.fetchByDateList(MainActivity.selectedDate);
                        Entry currentEntry = allEntries.get(position);
                        int rowID = currentEntry.getID();
                        String dateForThisEntry = currentEntry.getDate();
                        String startT = currentEntry.getStartTime();
                        String taskN = currentEntry.getTask();
                        String resultE = editActivity.showEndTime(selectedHour, selectedMinute);
                        String total = mainActivity.calculateTotal(startT, resultE);
                        adapter_ob.updateldetail(rowID, dateForThisEntry, startT, resultE, taskN, total);
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        listViewHolder.tx_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> allTasks;
                DatabaseManager manager = new DatabaseManager(context);
                allTasks = manager.fetchAllTasks();
                allTasks.add("None");
                //Create sequence of items
                Tasks = allTasks.toArray(new String[allTasks.size()]);
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setTitle("Tasks");
                selectedText = Tasks[0].toString();
                dialogBuilder.setSingleChoiceItems(Tasks,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedText = Tasks[which].toString();  //Selected item in listview
                        Log.d("Selected", "on redio click: " + selectedText);
                    }
                });

                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("Selected", "now on click of OK : " + selectedText);
                        adapter_ob = new DatabaseManagerForActual(context);
                        ArrayList<Entry> allEntries = new ArrayList<Entry>();
                        allEntries = adapter_ob.fetchByDateList(MainActivity.selectedDate);
                        Entry currentEntry = allEntries.get(position);
                        int rowID = currentEntry.getID();
                        System.out.println("rowIDDDD" + rowID);
                        String dateForThisEntry = currentEntry.getDate();
                        String startT = currentEntry.getStartTime();
                        String endT = currentEntry.getEndTime();
                        Toast.makeText(context,selectedText,Toast.LENGTH_LONG).show();
                        String total = mainActivity.calculateTotal(startT, endT);
                        adapter_ob.updateldetail(rowID, dateForThisEntry, startT, endT, selectedText, total);
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                dialogBuilder.setNeutralButton(Html.fromHtml("<b><i>" + "+" + "</i><b>"), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                                adapter_ob = new DatabaseManagerForActual(context);
                                ArrayList<Entry> allEntries = new ArrayList<Entry>();
                                allEntries = adapter_ob.fetchByDateList(MainActivity.selectedDate);
                                Entry currentEntry = allEntries.get(position);
                                int rowID = currentEntry.getID();
                                System.out.println("rowIDDDD" + rowID);
                                String dateForThisEntry = currentEntry.getDate();
                                String startT = currentEntry.getStartTime();
                                String endT = currentEntry.getEndTime();
                                String total = mainActivity.calculateTotal(startT, endT);
                                adapter_ob.updateldetail(rowID, dateForThisEntry, startT, endT, resultTask, total);
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

                //Create alert dialog object via builder
                AlertDialog alertDialogObject = dialogBuilder.create();
                //Show the dialog
                alertDialogObject.show();
                alertDialogObject.getButton(DialogInterface.BUTTON_NEUTRAL).setTextSize(40);
            }
        });


        listViewHolder.tx_s.setText(list.get(position).startTime);
        listViewHolder.tx_e.setText(list.get(position).endTime);
        listViewHolder.tx_t.setText(list.get(position).taskName);
        listViewHolder.tx_tot.setText(list.get(position).total);

        return v;
    }

    class ListViewHolder{
        public TextView tx_s, tx_e, tx_t, tx_tot;

        public ListViewHolder(View base) {
            tx_s = (TextView) base.findViewById(R.id.tv_startTime);
            tx_e = (TextView) base.findViewById(R.id.tv_endTime);
            tx_t = (TextView) base.findViewById(R.id.tv_task);
            tx_tot = (TextView) base.findViewById(R.id.tv_total);
        }
    }
}
