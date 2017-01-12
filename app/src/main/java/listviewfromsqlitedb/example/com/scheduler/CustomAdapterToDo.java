package listviewfromsqlitedb.example.com.scheduler;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    Context context;
    List<EntryToDo> list;
    LayoutInflater layoutInflater = null;
    DatabaseManagerToDo adapterToDo_ob;
    List<View> viewList;

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

        EntryToDo myTask = list.get(position);



        listViewHolder.tx_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView task = (TextView) v.findViewById(R.id.tx_task);
                adapterToDo_ob = new DatabaseManagerToDo(context);
                ArrayList<EntryToDo> allEntries = new ArrayList<EntryToDo>();
                allEntries = adapterToDo_ob.fetchByDateList(MainActivity.selectedDate);
                EntryToDo currentEntry = allEntries.get(position);
                int rowID = currentEntry.getID();
                String dateForThisEntry = currentEntry.getDate();
                String taskN = currentEntry.getTask();
                int statusId = currentEntry.getStatusID();
                if(currentEntry.getStatus().equals("Y")){
                    Log.d("statusCheck", currentEntry.getStatus());
                    task.setPaintFlags(task.getPaintFlags() | (~Paint.STRIKE_THRU_TEXT_FLAG));
                    updateAll(statusId, "N");
                    listViewHolder.tx_task.setText(list.get(position).task);
                    adapterToDo_ob.updateldetail(rowID, dateForThisEntry, taskN, "N", statusId);
                }else if(currentEntry.getStatus().equals("N")){
                    Log.d("statusCheck", currentEntry.getStatus());
                    task.setPaintFlags(task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    updateAll(statusId, "Y");
                    adapterToDo_ob.updateldetail(rowID, dateForThisEntry, taskN, "Y", statusId);
                }
            }
        });

        listViewHolder.tx_task.setText(list.get(position).task);
//        Log.d("here",list.get(position).status);
        if (list.get(position).status.equals("Y"))
        {
            listViewHolder.tx_task.setPaintFlags(listViewHolder.tx_task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else if (list.get(position).status.equals("N"))
        {
            listViewHolder.tx_task.setPaintFlags( listViewHolder.tx_task.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        return v;
    }

    private void updateAll(int statusId, String status) {
        adapterToDo_ob = new DatabaseManagerToDo(context);
        ArrayList<EntryToDo> allEntries = new ArrayList<EntryToDo>();
        Cursor c1 = adapterToDo_ob.fetchByStatusId(statusId);
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
                    adapterToDo_ob.updateldetail(rowId, date, task, status, statId );
                } while (c1.moveToNext());
            }
        }
    }


    class ListViewHolder{
        public TextView tx_task;

        public ListViewHolder(View base) {
            tx_task = (TextView) base.findViewById(R.id.tx_task);
        }
    }

}




