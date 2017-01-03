package listviewfromsqlitedb.example.com.scheduler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by payalkothari on 12/22/16.
 */
public class DatabaseHelperForActual extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "scheduleManagerForActual";
    public static final String TABLE_NAME = "scheduleForActual";
    public static final int DATABASE_VERSION = 1;
    public static final String KEY_ID = "_id";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String TASK_NAME = "taskName";
    public static final String DATE = "date";
    public static final String CREATE_SCHEDULE_FOR_ACTUAL_TABLE = "create table " + TABLE_NAME + "("
            + KEY_ID + " integer primary key autoincrement," + DATE + " text not null," + START_TIME
            + " text not null," + END_TIME + " text not null," + TASK_NAME + " text not null"
            + ");";

    public DatabaseHelperForActual(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SCHEDULE_FOR_ACTUAL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

