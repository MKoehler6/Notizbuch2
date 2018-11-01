package com.example.micha.notizbuch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class TagebuchDB extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "dates";
    public static final String _ID = "_id";
    public static final String COLNAME_DATE = "date";
    public static final String COLNAME_STAERKE = "staerke";
    public static final String COLNAME_NOTIZ = "notiz";
    public  static final int DATABASE_VERSION = 1;
    public static  final String DATABASE_NAME = "Tagebuch.db";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLNAME_STAERKE + " TINYINT(1)," +
            COLNAME_DATE + " DATETIME DEFAULT CURRENT_DATE," +
            COLNAME_NOTIZ + " TEXT" + ")";

    private static final String TABLE_TODO_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public TagebuchDB (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = TABLE_TODO_DROP;
        db.execSQL(dropTable);
        onCreate(db);
    }

    public ArrayList<String> readDate(){
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            ArrayList<String> dates = new ArrayList<>();
            Cursor cursor = db.query(TABLE_NAME, new String[] {COLNAME_DATE, COLNAME_STAERKE},
                    null, null, null, null, null);

            try {
                while (cursor.moveToNext()) {
                    String date = cursor.getString(0);
                    dates.add(0, date);// wird an Position 0 eingefügt
                }
                Log.d("MEINLOGreadDate", dates.toString());
                return dates;

            } finally {
                cursor.close();
            }

        } finally {
            db.close();
        }
    }

    public ArrayList<String> readID(){
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            ArrayList<String> ids = new ArrayList<>();
            Cursor cursor = db.query(TABLE_NAME, new String[] {_ID, COLNAME_DATE, COLNAME_STAERKE},
                    null, null, null, null, null);

            try {
                while (cursor.moveToNext()) {
                    Integer id = cursor.getInt(0);
                    String idString = id.toString();
                    ids.add(0, idString);// wird an Position 0 eingefügt
                }
                Log.d("MEINLOGreadDate", ids.toString());
                return ids;

            } finally {
                cursor.close();
            }

        } finally {
            db.close();
        }
    }

    public ArrayList<String> readNotiz(){
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            ArrayList<String> notizen = new ArrayList<>();
            Cursor cursor = db.query(TABLE_NAME, new String[] {COLNAME_DATE, COLNAME_NOTIZ},
                    null, null, null, null, null);

            try {
                while (cursor.moveToNext()) {
                    String date = cursor.getString(1);
                    notizen.add(0, date); // wird an Position 0 eingefügt
                }
                //Log.d("MEINLOGreadNotiz", notizen.toString());
                return notizen;

            } finally {
                cursor.close();
            }

        } finally {
            db.close();
        }
    }
    public void insert(int staerke){
        long rowId = -1;
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLNAME_STAERKE, staerke);
            rowId = database.insert(TABLE_NAME, null, values);
            Log.d("MEINLOG", "insert");
        } finally {
            database.close();
        }
    }
    public void insertText(String id, String text){
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLNAME_NOTIZ, text);
            String whereClause = _ID + " = ?";
            String[] whereArgs = { id };
            database.update(TABLE_NAME, values, whereClause, whereArgs);
            Log.d("MEINLOG", "insertText");
        } finally {
            database.close();
        }
    }

    public int delete(String id) {

        SQLiteDatabase database = this.getWritableDatabase();

        try {
            String whereClause = _ID + " = ?";
            String[] whereArgs = { id };
            return database.delete(TABLE_NAME, whereClause, whereArgs);
        } finally {
            database.close();
        }
    }
}