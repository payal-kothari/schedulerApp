package listviewfromsqlitedb.example.com.scheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * Created by payalkothari on 12/22/16.
 */
public class DatabaseManagerForActual{
    SQLiteDatabase database_ob_for_actual;
    DatabaseHelperForActual databaseHelper_ob;
    Context context;

    public DatabaseManagerForActual(Context c){
        context = c;
    }

    public DatabaseManagerForActual opnToRead() {
        databaseHelper_ob = new DatabaseHelperForActual(context, databaseHelper_ob.DATABASE_NAME, null, databaseHelper_ob.DATABASE_VERSION);
        database_ob_for_actual = databaseHelper_ob.getReadableDatabase();
        return this;
    }

    public DatabaseManagerForActual opnToWrite() {
        databaseHelper_ob = new DatabaseHelperForActual(context, databaseHelper_ob.DATABASE_NAME, null, databaseHelper_ob.DATABASE_VERSION);
        database_ob_for_actual = databaseHelper_ob.getWritableDatabase();
        return this;
    }

    public void Close() {
        database_ob_for_actual.close();
    }


    public long insertDetails(String date, String startTime, String endTime, String task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(databaseHelper_ob.DATE, date);
        contentValues.put(databaseHelper_ob.START_TIME, startTime);
        contentValues.put(databaseHelper_ob.END_TIME, endTime);
        contentValues.put(databaseHelper_ob.TASK_NAME, task);
        opnToWrite();
        long val = database_ob_for_actual.insert(databaseHelper_ob.TABLE_NAME, null, contentValues);
        Close();
        return val;
    }

    public ArrayList<Entry> fetchAll() {
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.DATE, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME };
        opnToWrite();
        //Cursor c = database_ob.query(databaseHelper_ob.TABLE_NAME, cols, null, null, null, null,"startTime ASC");
        Cursor c = database_ob_for_actual.query(databaseHelper_ob.TABLE_NAME, cols, null, null, null, null, null);
        ArrayList<Entry> entryList = new ArrayList<Entry>();
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Entry entry = new Entry();
                entry.setID(c.getInt(0));
                entry.setDate(c.getString(1));
                System.out.println("********" + c.getString(1));
                entry.setStart(c.getString(2));
                entry.setEnd(c.getString(3));
                entry.setTask(c.getString(4));
                // Adding contact to list
                entryList.add(entry);
            } while (c.moveToNext());
        }
        return entryList;
    }

    public ArrayList<Entry> fetchByDateList(String date) {
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.DATE, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME };
        opnToWrite();
        int nameId = 1;
        Cursor c = database_ob_for_actual.query(databaseHelper_ob.TABLE_NAME,
                null, databaseHelper_ob.DATE + "=?", new String[] { date }, null, null, null, null);
        ArrayList<Entry> entryList = new ArrayList<Entry>();
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Entry entry = new Entry();
                entry.setID(c.getInt(0));
                entry.setDate(c.getString(1));
                System.out.println("********" + c.getString(1));
                entry.setStart(c.getString(2));
                entry.setEnd(c.getString(3));
                entry.setTask(c.getString(4));
                // Adding contact to list
                entryList.add(entry);
            } while (c.moveToNext());
        }
        return entryList;
    }


    public Cursor fetchByDate(String date) {
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.DATE, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME };
        opnToWrite();
        int nameId = 1;
        Cursor c = database_ob_for_actual.query(databaseHelper_ob.TABLE_NAME,
                null, databaseHelper_ob.DATE + "=?", new String[] { date }, null, null, null, null);
        return c;
    }

    public Cursor fetchAllCursor() {
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.DATE, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME };
        opnToWrite();
        //Cursor c = database_ob.query(databaseHelper_ob.TABLE_NAME, cols, null, null, null, null,"startTime ASC");
        Cursor c = database_ob_for_actual.query(databaseHelper_ob.TABLE_NAME, cols, null, null, null, null, null);
        return c;
    }

    public Cursor fetch(int nameId) {
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.DATE, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME };
        opnToWrite();
        Cursor c = database_ob_for_actual.query(databaseHelper_ob.TABLE_NAME, cols, databaseHelper_ob.KEY_ID + "=" + nameId, null, null, null, null);
        return c;
    }

    public long updateldetail(int rowId, String date, String startTime, String endTime, String task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(databaseHelper_ob.DATE, date);
        contentValues.put(databaseHelper_ob.START_TIME, startTime);
        contentValues.put(databaseHelper_ob.END_TIME, endTime);
        contentValues.put(databaseHelper_ob.TASK_NAME, task);
        opnToWrite();
        long val = database_ob_for_actual.update(databaseHelper_ob.TABLE_NAME, contentValues, databaseHelper_ob.KEY_ID + "=" + rowId, null);
        Close();
        return val;
    }

    public int deleteOneRecord(int rowId) {
        opnToWrite();
        int val = database_ob_for_actual.delete(databaseHelper_ob.TABLE_NAME, databaseHelper_ob.KEY_ID + "=" + rowId, null);
        Close();
        return val;
    }
}
