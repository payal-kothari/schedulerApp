package listviewfromsqlitedb.example.com.scheduler;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
public class CustomAdapterToDo extends BaseAdapter{
    DatabaseManager adapter_ob;
    Context context;
    List<EntryToDo> list;
    LayoutInflater layoutInflater = null;
    DatabaseManagerToDo adapterToDo_ob;

    public CustomAdapterToDo(Context c, List<EntryToDo> listE) {
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
            v = layoutInflater.inflate(R.layout.row_to_do, null);
            listViewHolder = new ListViewHolder(v);
            listViewHolder.tx_task = (TextView) v.findViewById(R.id.tx_task);
            v.setTag(listViewHolder);
        }
        else {
            listViewHolder = (ListViewHolder) v.getTag();
        }

        listViewHolder.tx_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterToDo_ob = new DatabaseManagerToDo(context);
                ArrayList<EntryToDo> allEntries = new ArrayList<EntryToDo>();
                allEntries = adapterToDo_ob.fetchByDateList(MainActivity.selectedDate);
                EntryToDo currentEntry = allEntries.get(position);
                int rowID = currentEntry.getID();
                String dateForThisEntry = currentEntry.getDate();
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setTitle("Tasks");
                final EditText input = new EditText(context);
                dialogBuilder.setView(input);

                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        adapterToDo_ob = new DatabaseManagerToDo(context);
                        ArrayList<EntryToDo> allEntries = new ArrayList<EntryToDo>();
                        allEntries = adapterToDo_ob.fetchByDateList(MainActivity.selectedDate);
                        EntryToDo currentEntry = allEntries.get(position);
                        int rowID = currentEntry.getID();
                        String dateForThisEntry = currentEntry.getDate();
                        String resultTask = input.getEditableText().toString();
                        adapterToDo_ob.updateldetail(rowID, dateForThisEntry, resultTask);
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialogObject = dialogBuilder.create();
                //Show the dialog
                alertDialogObject.show();
            }
        });

        listViewHolder.tx_task.setText(list.get(position).task);

        return v;
    }


    class ListViewHolder{
        public TextView tx_task;

        public ListViewHolder(View base) {
            tx_task = (TextView) base.findViewById(R.id.tx_task);
        }
    }

}




