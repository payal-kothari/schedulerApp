package listviewfromsqlitedb.example.com.scheduler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by payalkothari on 12/30/16.
 */
public class CustomAdapter extends BaseAdapter{
    Context context;
    List<Entry> list;
    LayoutInflater layoutInflater = null;

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
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        ListViewHolder listViewHolder;
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
                Toast.makeText(context, "you clicked start" , Toast.LENGTH_SHORT).show();
            }
        });

        listViewHolder.tx_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "you clicked end" , Toast.LENGTH_SHORT).show();
            }
        });

        listViewHolder.tx_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "you clicked task" , Toast.LENGTH_SHORT).show();
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
}
