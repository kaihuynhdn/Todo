package com.example.kaihuynh.todo.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.kaihuynh.todo.model.Todo;

import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {
    private final String TAG = "DBManager";
    private static final String DATABASE_NAME = "todolist_manager";
    private static final String TABLE_NAME = "todolist";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String TIME = "time";
    private static final String IMG = "image";
    private static int VERSION = 1;
    private Context context;
    private String sql = "create table " + TABLE_NAME + " (" +
            ID + " integer primary key AUTOINCREMENT, " +
            TITLE + " text, " +
            CONTENT + " text, " +
            TIME + " text, " +
            IMG + " BLOB)";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
        Log.d(TAG, "DBManager: ");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: ");
    }

    public long addTodo(Todo todo) {
        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, todo.getTitle());
        contentValues.put(CONTENT, todo.getContent());
        contentValues.put(TIME, todo.getDate());
        contentValues.put(IMG, todo.getImage());
        id = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        Log.d(TAG, "addTololist: Successfully!");
        return id;
    }

    public ArrayList<Todo> getListData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{ID, TITLE, CONTENT, TIME, IMG};
        Cursor c = db.rawQuery("select * from " + TABLE_NAME + " group by " + ID + " order by " + ID + " desc", null);
        ArrayList<Todo> arr = new ArrayList<>();
        while (c.moveToNext()) {
            arr.add(new Todo(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getBlob(4)));
        }
        c.close();
        db.close();
        return arr;
    }

    public void updateTodo(Todo todolist) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = new String[]{ID, TITLE, CONTENT, TIME};
        ContentValues values = new ContentValues();
        values.put(TITLE, todolist.getTitle());
        values.put(CONTENT, todolist.getContent());
        values.put(TIME, todolist.getDate());
        values.put(IMG, todolist.getImage());
        db.update(TABLE_NAME, values, ID + " = ?", new String[]{String.valueOf(todolist.getId())});
    }

    public void deleteTodo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID + " = " + String.valueOf(id), null);
    }

}
