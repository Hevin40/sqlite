package hego.android.touchcounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHalper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHalper";

    public DatabaseHalper(@Nullable Context context) {
        super(context, "TouchCounter.db", null, 20);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists touchCounter (id integer primary key autoincrement,date text,day text,touch text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists touchCounter");
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

}
