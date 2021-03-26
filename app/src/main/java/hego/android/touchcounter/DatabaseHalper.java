package hego.android.touchcounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatabaseHalper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHalper";

    public DatabaseHalper(@Nullable Context context) {
        super(context, "TouchCounter.db", null, 20);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists touchCounter (id integer primary key autoincrement,date text,day text,touch text)");
        db.execSQL("create table if not exists appsTouchCounter (id integer primary key autoincrement,date text,day text,app text,touch text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists touchCounter");
        db.execSQL("drop table if exists appsTouchCounter");
        onCreate(db);
    }

    public void insertUpdateData(Data data){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",data.getDate());
        contentValues.put("day",data.getDay());
        contentValues.put("touch",data.getTouch());

        Cursor cursor = sqLiteDatabase.rawQuery("select * from touchCounter where day = '" + data.getDay() + "' ", null, null);

        if (cursor.moveToNext()){
            Log.e(TAG, "insertUpdateData: data update day : "+data.getDay() );
            int touchCounter = sqLiteDatabase.update("touchCounter", contentValues, "day = " + data.getDay() + " ", null);
            Log.e(TAG, "insertUpdateData: data updated : "+touchCounter );
            Log.e(TAG, "insertUpdateData: data update touch : "+data.getTouch() );

        }else {
            long touchCounter = sqLiteDatabase.insert("touchCounter", null, contentValues);
            Log.e(TAG, "insertUpdateData: data insert : "+touchCounter );
        }
    }

    public void insertUpdateDataApps(DataApps dataApps){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",dataApps.getDate());
        contentValues.put("day",dataApps.getDay());
        contentValues.put("app",dataApps.getApp());
        contentValues.put("touch",dataApps.getTouch());

        Cursor cursor = sqLiteDatabase.rawQuery("select * from appsTouchCounter where app = '" + dataApps.getApp() + "' and day = "+dataApps.getDay()+" ", null, null);

        if (cursor.moveToNext()){
            int touchCounter = sqLiteDatabase.update("appsTouchCounter", contentValues, "app = '" + dataApps.getApp() + "' and day = "+dataApps.getDay()+" ", null);
            Log.e(TAG, "insertUpdateAppData: data updated : "+touchCounter);
        }else {
            long touchCounter = sqLiteDatabase.insert("appsTouchCounter", null, contentValues);
            Log.e(TAG, "insertUpdateAppData: data inserted : "+touchCounter );
        }
    }

    private String getCurrentDate(){
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate =  df.format(date);
        return formattedDate;
    }

}
