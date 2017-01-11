package listviewfromsqlitedb.example.com.scheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;


/**
 * Created by payalkothari on 12/22/16.
 */
public class DatabaseManagerToDo {
    SQLiteDatabase database_ob;
    DatabaseHelperToDo DatabaseHelperToDo_ob;
    Context context;

    public DatabaseManagerToDo(Context c){
        context = c;
    }

    public DatabaseManagerToDo opnToRead() {
        DatabaseHelperToDo_ob = new DatabaseHelperToDo(context, DatabaseHelperToDo_ob.DATABASE_NAME, null, DatabaseHelperToDo_ob.DATABASE_VERSION);
        database_ob = DatabaseHelperToDo_ob.getReadableDatabase();
        return this;
    }

    public DatabaseManagerToDo opnToWrite() {
        DatabaseHelperToDo_ob = new DatabaseHelperToDo(context, DatabaseHelperToDo_ob.DATABASE_NAME, null, DatabaseHelperToDo_ob.DATABASE_VERSION);
        database_ob = DatabaseHelperToDo_ob.getWritableDatabase();
        return this;
    }

    public void Close() {
        database_ob.close();
    }

    public long insertDetails(String date, String task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelperToDo_ob.DATE, date);
        contentValues.put(DatabaseHelperToDo_ob.TASK, task);
        opnToWrite();
        long val = database_ob.insert(DatabaseHelperToDo_ob.TABLE_NAME, null, contentValues);
        Close();
        return val;
    }

    public ArrayList<Entry> fetchAll() {
        String[] cols = { DatabaseHelperToDo_ob.KEY_ID, DatabaseHelperToDo_ob.DATE, DatabaseHelperToDo_ob.TASK };
        opnToWrite();
        //Cursor c = database_ob.query(DatabaseHelperToDo_ob.TABLE_NAME, cols, null, null, null, null,"startTime ASC");
        Cursor c = database_ob.query(DatabaseHelperToDo_ob.TABLE_NAME, cols, null, null, null, null, null);
        ArrayList<Entry> entryList = new ArrayList<Entry>();
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Entry entry = new Entry();
                entry.setID(c.getInt(0));
                entry.setDate(c.getString(1));
                System.out.println("********" + c.getString(1));
                entry.setTask(c.getString(2));
                // Adding contact to list
                entryList.add(entry);
            } while (c.moveToNext());
        }
        return entryList;
    }

    public ArrayList<String> fetchAllTasks(){
        ArrayList<String> tasksList = new ArrayList<>();
        String[] cols = { DatabaseHelperToDo_ob.KEY_ID, DatabaseHelperToDo_ob.DATE, DatabaseHelperToDo_ob.TASK };
        opnToWrite();
        //Cursor c = database_ob.query(DatabaseHelperToDo_ob.TABLE_NAME, cols, null, null, null, null,"startTime ASC");
        Cursor c = database_ob.query(DatabaseHelperToDo_ob.TABLE_NAME, cols, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                    tasksList.add(c.getString(2));
            } while (c.moveToNext());
        }
        return tasksList;
    }

    public ArrayList<EntryToDo> fetchByDateList(String date) {
        String[] cols = { DatabaseHelperToDo_ob.KEY_ID, DatabaseHelperToDo_ob.DATE, DatabaseHelperToDo_ob.TASK };
        opnToWrite();
        Cursor c = database_ob.query(DatabaseHelperToDo_ob.TABLE_NAME,
                null, DatabaseHelperToDo_ob.DATE + "=?", new String[] { date }, null, null, null, null);
        ArrayList<EntryToDo> entryList = new ArrayList<EntryToDo>();
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                EntryToDo entry = new EntryToDo();
                entry.setID(c.getInt(0));
                entry.setDate(c.getString(1));
                System.out.println("********" + c.getString(1));
                entry.setTask(c.getString(2));
                // Adding contact to list
                entryList.add(entry);
            } while (c.moveToNext());
        }
        return entryList;
    }


    public Cursor fetchByDate(String date) {
        String[] cols = { DatabaseHelperToDo_ob.KEY_ID, DatabaseHelperToDo_ob.DATE, DatabaseHelperToDo_ob.TASK };
        opnToWrite();
        int nameId = 1;
        Cursor c = database_ob.query(DatabaseHelperToDo_ob.TABLE_NAME,
                null, DatabaseHelperToDo_ob.DATE + "=?", new String[] { date }, null, null, null, null);
        return c;
    }

    public Cursor fetchAllCursor() {
        String[] cols = { DatabaseHelperToDo_ob.KEY_ID, DatabaseHelperToDo_ob.DATE, DatabaseHelperToDo_ob.TASK };
        opnToWrite();
        //Cursor c = database_ob.query(DatabaseHelperToDo_ob.TABLE_NAME, cols, null, null, null, null,"startTime ASC");
        Cursor c = database_ob.query(DatabaseHelperToDo_ob.TABLE_NAME, cols, null, null, null, null, null);
        return c;
    }

    public Cursor fetch(int nameId) {
        String[] cols = { DatabaseHelperToDo_ob.KEY_ID, DatabaseHelperToDo_ob.DATE, DatabaseHelperToDo_ob.TASK };
        opnToWrite();
        Cursor c = database_ob.query(DatabaseHelperToDo_ob.TABLE_NAME, cols, DatabaseHelperToDo_ob.KEY_ID + "=" + nameId, null, null, null, null);
        return c;
    }

    public long updateldetail(int rowId, String date, String task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelperToDo_ob.DATE, date);
        contentValues.put(DatabaseHelperToDo_ob.TASK, task);
        opnToWrite();
        long val = database_ob.update(DatabaseHelperToDo_ob.TABLE_NAME, contentValues, DatabaseHelperToDo_ob.KEY_ID + "=" + rowId, null);
        Close();
        return val;
    }

    public int deleteOneRecord(int rowId) {
        opnToWrite();
        int val = database_ob.delete(DatabaseHelperToDo_ob.TABLE_NAME, DatabaseHelperToDo_ob.KEY_ID + "=" + rowId, null);
        Close();
        return val;
    }
}
