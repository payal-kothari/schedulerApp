package listviewfromsqlitedb.example.com.scheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;


/**
 * Created by payalkothari on 12/22/16.
 */
public class DatabaseManager{
    SQLiteDatabase database_ob;
    DatabaseHelper databaseHelper_ob;
    Context context;

    public DatabaseManager(Context c){
        context = c;
    }

    public DatabaseManager opnToRead() {
        databaseHelper_ob = new DatabaseHelper(context, databaseHelper_ob.DATABASE_NAME, null, databaseHelper_ob.DATABASE_VERSION);
        database_ob = databaseHelper_ob.getReadableDatabase();
        return this;
    }

    public DatabaseManager opnToWrite() {
        databaseHelper_ob = new DatabaseHelper(context, databaseHelper_ob.DATABASE_NAME, null, databaseHelper_ob.DATABASE_VERSION);
        database_ob = databaseHelper_ob.getWritableDatabase();
        return this;
    }

    public void Close() {
        database_ob.close();
    }

    public long insertDetails(String date, String startTime, String endTime, String task, String total) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(databaseHelper_ob.DATE, date);
        contentValues.put(databaseHelper_ob.START_TIME, startTime);
        contentValues.put(databaseHelper_ob.END_TIME, endTime);
        contentValues.put(databaseHelper_ob.TASK_NAME, task);
        contentValues.put(databaseHelper_ob.TOTAL, total);
        opnToWrite();
        long val = database_ob.insert(databaseHelper_ob.TABLE_NAME, null, contentValues);
        Close();
        return val;
    }

    public ArrayList<Entry> fetchAll() {
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.DATE, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME, databaseHelper_ob.TOTAL };
        opnToWrite();
        Cursor c = database_ob.query(databaseHelper_ob.TABLE_NAME, cols, null, null, null, null, null);
        ArrayList<Entry> entryList = new ArrayList<Entry>();
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Entry entry = new Entry();
                entry.setID(c.getInt(0));
                entry.setDate(c.getString(1));
                entry.setStart(c.getString(2));
                entry.setEnd(c.getString(3));
                entry.setTask(c.getString(4));
                entry.setTotal(c.getString(5));
                // Adding contact to list
                entryList.add(entry);
            } while (c.moveToNext());
        }
        return entryList;
    }

    public ArrayList<String> fetchAllTasks(){
        ArrayList<String> tasksList = new ArrayList<>();
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.DATE, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME, databaseHelper_ob.TOTAL };
        opnToWrite();
        Cursor c = database_ob.query(databaseHelper_ob.TABLE_NAME, cols, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                if(!tasksList.contains(c.getString(4)) && !c.getString(4).equals("None")){
                    tasksList.add(c.getString(4));
                }
            } while (c.moveToNext());
        }
        return tasksList;
    }

    public ArrayList<Entry> fetchByDateList(String date) {
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.DATE, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME, databaseHelper_ob.TOTAL };
        opnToWrite();
        int nameId = 1;
        Cursor c = database_ob.query(databaseHelper_ob.TABLE_NAME,
                null, databaseHelper_ob.DATE + "=?", new String[] { date }, null, null, null, null);
        ArrayList<Entry> entryList = new ArrayList<Entry>();
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Entry entry = new Entry();
                entry.setID(c.getInt(0));
                entry.setDate(c.getString(1));
                entry.setStart(c.getString(2));
                entry.setEnd(c.getString(3));
                entry.setTask(c.getString(4));
                entry.setTotal(c.getString(5));
                // Adding contact to list
                entryList.add(entry);
            } while (c.moveToNext());
        }
        return entryList;
    }

    public Cursor fetchByDate(String date) {
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.DATE, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME, databaseHelper_ob.TOTAL };
        opnToWrite();
        Cursor c = database_ob.query(databaseHelper_ob.TABLE_NAME,
                null, databaseHelper_ob.DATE + "=?", new String[] { date }, null, null, null, null);
        return c;
    }

    public Cursor fetchAllCursor() {
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.DATE, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME, databaseHelper_ob.TOTAL };
        opnToWrite();
        Cursor c = database_ob.query(databaseHelper_ob.TABLE_NAME, cols, null, null, null, null, null);
        return c;
    }

    public Cursor fetch(int nameId) {
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.DATE, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME, databaseHelper_ob.TOTAL };
        opnToWrite();
        Cursor c = database_ob.query(databaseHelper_ob.TABLE_NAME, cols, databaseHelper_ob.KEY_ID + "=" + nameId, null, null, null, null);
        return c;
    }

    public long updateldetail(int rowId, String date, String startTime, String endTime, String task, String total) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(databaseHelper_ob.DATE, date);
        contentValues.put(databaseHelper_ob.START_TIME, startTime);
        contentValues.put(databaseHelper_ob.END_TIME, endTime);
        contentValues.put(databaseHelper_ob.TASK_NAME, task);
        contentValues.put(databaseHelper_ob.TOTAL, total);
        opnToWrite();
        long val = database_ob.update(databaseHelper_ob.TABLE_NAME, contentValues, databaseHelper_ob.KEY_ID + "=" + rowId, null);
        Close();
        return val;
    }

    public int deleteOneRecord(int rowId) {
        opnToWrite();
        int val = database_ob.delete(databaseHelper_ob.TABLE_NAME, databaseHelper_ob.KEY_ID + "=" + rowId, null);
        Close();
        return val;
    }
}
