package com.example.myitschool_proj;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DBInteractor extends SQLiteOpenHelper {
    private static DBInteractor mInstance = null;
    private static final String TABLE_NAME = "NOTIFICATIONS";
    private static final String COLUMN_ID_NAME = "id";
    private static final String COLUMN_TITLE_NAME = "title";
    private static final String COLUMN_DESCRIPTION_NAME = "description";
    private static final String COLUMN_TIME_NAME = "time";
    private static final String COLUMN_NOTIF_TIME_NAME = "notif_time";

    public DBInteractor(@Nullable Context context) {
        super(context, "notifications.db", null, 1);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
    public static DBInteractor getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new DBInteractor(ctx.getApplicationContext());
        }
        return mInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_table_statement = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + COLUMN_ID_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TITLE_NAME + " TEXT, " + COLUMN_DESCRIPTION_NAME + " TEXT, " + COLUMN_TIME_NAME + " DATETIME, " + COLUMN_NOTIF_TIME_NAME + " DATETIME)";
        sqLiteDatabase.execSQL(create_table_statement);

    }
    public long addOne(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Log.e("DT", notification.getTime().toString());
        cv.put(COLUMN_TIME_NAME, notification.getTime().toString());
        cv.put(COLUMN_NOTIF_TIME_NAME, notification.getNotif_time().toString());
        cv.put(COLUMN_DESCRIPTION_NAME, notification.getDescription());
        cv.put(COLUMN_TITLE_NAME, notification.getTitle());
        long code = db.insert(TABLE_NAME, null, cv);
        db.close();
        return code;
    }
    public void deletePassed() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE DATETIME(" + COLUMN_TIME_NAME + ")<= DATETIME('now', 'localtime')";
        Log.e("DEL", "DATETIME(" + COLUMN_TIME_NAME + ")<=Datetime('now')");
        db.execSQL(query);
        String queryString = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TIME_NAME + ">datetime('2000-1-1')";
        Cursor cur = db.rawQuery(queryString, null);
        Log.e("SZ", String.valueOf(cur.getCount()));
        db.close();
    }
    public boolean deleteOne(int id1) {
        SQLiteDatabase db = this.getWritableDatabase();
        long code = db.delete(TABLE_NAME, COLUMN_ID_NAME + "="+id1, null);
        return code != -1;
    }
    public List<Notification> getAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_NAME;
        Cursor cur = db.rawQuery(queryString, null);
        List<Notification> res = new ArrayList<>();
        while (cur.moveToNext()) {
            int id = cur.getInt(0);
            String title = cur.getString(1);
            String description = cur.getString(2);
            Log.e("DB", Timestamp.valueOf(cur.getString(3)).toString());
            Timestamp time = Timestamp.valueOf(cur.getString(3));
            Timestamp notif_time = Timestamp.valueOf(cur.getString(4));
            Notification cur_not = new Notification(title, time, notif_time,description, id);
            res.add(cur_not);
        }
        cur.close();
        db.close();
        return res;
    }
    public Notification getOne(int id1) {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID_NAME + "=" + id1;
        Cursor cur = db.rawQuery(queryString, null);
        if (cur.moveToFirst()) {
            int id = cur.getInt(0);
            String title = cur.getString(1);
            String description = cur.getString(2);
            Timestamp time = Timestamp.valueOf(cur.getString(3));
            Timestamp notif_time = Timestamp.valueOf(cur.getString(4));
            cur.close();
            db.close();
            return new Notification(title, time, notif_time, description, id );
        }
        cur.close();
        db.close();
        throw new Resources.NotFoundException();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
