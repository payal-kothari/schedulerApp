package listviewfromsqlitedb.example.com.scheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Array;
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


    public long insertDetails(String startTime, String endTime, String task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(databaseHelper_ob.START_TIME, startTime);
        contentValues.put(databaseHelper_ob.END_TIME, endTime);
        contentValues.put(databaseHelper_ob.TASK_NAME, task);
        opnToWrite();
        long val = database_ob.insert(databaseHelper_ob.TABLE_NAME, null, contentValues);
        Close();
        return val;
    }

    public ArrayList<Entry> fetchAll() {
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME };
        opnToWrite();
        //Cursor c = database_ob.query(databaseHelper_ob.TABLE_NAME, cols, null, null, null, null,"startTime ASC");
        Cursor c = database_ob.query(databaseHelper_ob.TABLE_NAME, cols, null, null, null, null, null);
        ArrayList<Entry> entryList = new ArrayList<Entry>();
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Entry entry = new Entry();
                entry.setID(c.getInt(0));
                entry.setStart(c.getString(1));
                entry.setEnd(c.getString(2));
                entry.setTask(c.getString(3));
                // Adding contact to list
                entryList.add(entry);
            } while (c.moveToNext());
        }
        return entryList;
    }

    public Cursor fetchAllCursor() {
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME };
        opnToWrite();
        //Cursor c = database_ob.query(databaseHelper_ob.TABLE_NAME, cols, null, null, null, null,"startTime ASC");
        Cursor c = database_ob.query(databaseHelper_ob.TABLE_NAME, cols, null, null, null, null, null);
        return c;
    }

    public Cursor fetch(int nameId) {
        String[] cols = { databaseHelper_ob.KEY_ID, databaseHelper_ob.START_TIME, databaseHelper_ob.END_TIME, databaseHelper_ob.TASK_NAME };
        opnToWrite();
        Cursor c = database_ob.query(databaseHelper_ob.TABLE_NAME, cols, databaseHelper_ob.KEY_ID + "=" + nameId, null, null, null, null);
        return c;
    }

    public long updateldetail(int rowId, String startTime, String endTime, String task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(databaseHelper_ob.START_TIME, startTime);
        contentValues.put(databaseHelper_ob.END_TIME, endTime);
        contentValues.put(databaseHelper_ob.TASK_NAME, task);
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
