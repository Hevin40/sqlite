package hego.android.touchcounter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_touch_analysis.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class TouchAnalysisActivity : AppCompatActivity() {

    lateinit var databaseHalper: DatabaseHalper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touch_analysis)

        init()
        setData()
    }

    private fun init() {
        databaseHalper = DatabaseHalper(this)

        val broadcastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null && intent.action.equals("touch")){
                    setData()
                }else{
                    Log.e(TAG, "onReceive: intent is null")
                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("touch"))
    }

    private fun setData() {
        setTodayData()
        setYesterdayData()
        setTotalTouch()
        setMaxTouch()
        setAvgTouch()
        setTotalDays()
    }

    private fun setTotalDays() {
        totalDays.text = "Days : "+getTotalDaysCount().toString()
    }

    fun getTotalDaysCount(): Int {
        val sqLiteDatabase = databaseHalper.readableDatabase
        val countQuery = "SELECT  * FROM touchCounter"
        val cursor: Cursor = sqLiteDatabase.rawQuery(countQuery, null)
        val count: Int = cursor.getCount()
        return count
    }

    private fun setAvgTouch() {
        val sqLiteDatabase = databaseHalper.readableDatabase
        val cursor = sqLiteDatabase.rawQuery("SELECT AVG(touch) as Avg FROM touchCounter", null)
        if (cursor.moveToFirst()) {
            val avg = cursor.getInt(cursor.getColumnIndex("Avg"))
            avgTouch.text = "AVG : "+avg.toString()
        }
    }

    private fun setMaxTouch() {
        val sqLiteDatabase = databaseHalper.readableDatabase
        val cursor = sqLiteDatabase.rawQuery("SELECT MAX(touch) as Max FROM touchCounter", null)
        if (cursor.moveToFirst()) {
            val max = cursor.getInt(cursor.getColumnIndex("Max"))
            mostCounted.text = "Most Counted : "+max.toString()
        }
    }

    private fun setTotalTouch() {
        val sqLiteDatabase = databaseHalper.readableDatabase
        val cursor = sqLiteDatabase.rawQuery("SELECT SUM(touch) as Total FROM touchCounter", null)
        if (cursor.moveToFirst()) {
            val total = cursor.getInt(cursor.getColumnIndex("Total"))
            totalTouch.text = "Total : "+total.toString()
        }
    }

    private fun setYesterdayData() {
        val sqLiteDatabase = databaseHalper.readableDatabase
        val rawQuery = sqLiteDatabase.rawQuery("select * from touchCounter", null, null)
        while (rawQuery.moveToNext()){
            val date = rawQuery.getString(1)
            if (date.equals(getYesterdayDate())){
                val touch = rawQuery.getString(3)
                yesterdayTouch.text = "Yesterday : "+touch
            }else{
                Log.e(TAG, "setData: date not equals current date")
            }
        }
    }

    private fun setTodayData() {
        val sqLiteDatabase = databaseHalper.readableDatabase
        val rawQuery = sqLiteDatabase.rawQuery("select * from touchCounter", null, null)
        while (rawQuery.moveToNext()){
            val date = rawQuery.getString(1)
            if (date.equals(getCurrentDate())){
                val touch = rawQuery.getString(3)
                todayTouch.text = "today : "+touch
            }else{
                Log.e(TAG, "setData: date not equals current date")
            }
        }
    }

    private fun getCurrentDate() : String{
        val c: Date = Calendar.getInstance().getTime()
        val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)
        return formattedDate
    }

    private fun getYesterdayDate() : String{
        val dateFormat: DateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return dateFormat.format(cal.time)
    }

    companion object{
        private const val TAG = "TouchAnalysisActivity"
    }

}