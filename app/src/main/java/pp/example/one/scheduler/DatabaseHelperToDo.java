package pp.example.one.scheduler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by payalkothari on 12/22/16.
 */
public class DatabaseHelperToDo extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "toDo";
    public static final String TABLE_NAME = "toDo";
    public static final int DATABASE_VERSION = 1;
    public static final String KEY_ID = "_id";
    public static final String DATE = "date";
    public static final String TASK = "task";
    public static final String STATUS = "status";
    public static final String STATUSID = "statusId";
    public static final String CREATE_TO_DO_TABLE = "create table " + TABLE_NAME + "("
            + KEY_ID + " integer primary key autoincrement," +  DATE + " text not null," + TASK + " text not null,"
            + STATUS + " text not null," + STATUSID + " integer" + ");";

    public DatabaseHelperToDo(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TO_DO_TABLE);
        db.execSQL("CREATE INDEX date_index ON toDo(date)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

