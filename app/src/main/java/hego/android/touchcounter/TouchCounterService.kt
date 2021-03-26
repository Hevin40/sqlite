package hego.android.touchcounter

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*


class TouchCounterService : Service(){

    private var appName: String? = null
    var windowType = 0
    private var windowManager: WindowManager? = null
    private var linearLayout: LinearLayout? = null
    lateinit var databaseHalper: DatabaseHalper

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        databaseHalper = DatabaseHalper(this)

        val broadcastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null && intent.action.equals("appName")){
                    appName = intent.getStringExtra("appName")
                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("appName"))


        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val linearLayout = LinearLayout(this)
        linearLayout.setBackgroundColor(0)
        val layoutParams = LinearLayout.LayoutParams(1, -1)
        linearLayout.setLayoutParams(layoutParams)

        val floatingText = TextView(this)
        floatingText.setBackgroundColor(Color.WHITE)
        floatingText.setText("Okay")
        floatingText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        floatingText.setTextColor(Color.WHITE)
        floatingText.setBackgroundColor(0)
        floatingText.setLayoutParams(layoutParams)


        if (Build.VERSION.SDK_INT >= 26) {
            this.windowType = 2038
        } else if (Build.VERSION.SDK_INT >= 19) {
            this.windowType = 2003
        }

        val p = WindowManager.LayoutParams(
                1,
                1,
                windowType,
                FLAG_NOT_FOCUSABLE or
                        FLAG_NOT_TOUCH_MODAL or
                        FLAG_WATCH_OUTSIDE_TOUCH,
                -2
        )

        val p2 = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                windowType,
                FLAG_NOT_FOCUSABLE or
                        FLAG_NOT_TOUCH_MODAL or
                        FLAG_FULLSCREEN or
                        FLAG_WATCH_OUTSIDE_TOUCH,
                -2
        )

        p2.gravity = Gravity.TOP or Gravity.CENTER
        p.x = 0
        p.y = 0

        windowManager!!.addView(linearLayout, p)
        windowManager!!.addView(floatingText, p2)

        linearLayout.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                val c = Calendar.getInstance().time
                val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                val date: String = df.format(c)

                var sqLiteDatabase = databaseHalper.readableDatabase
                val rawQuery = sqLiteDatabase.rawQuery("select * from touchCounter where day = '" + day + "'", null, null)

                if (rawQuery.moveToNext()) {
                    var lastValue = rawQuery.getString(3).toInt()
                    Log.e(TAG, "onTouch: available last value : "+lastValue )
                    var increment = lastValue + 1
                    databaseHalper.insertUpdateData(Data(date, day.toString(), increment.toString()))
                    Log.e(TAG, "onTouch: available last value plus : "+increment )
                } else {
                    databaseHalper.insertUpdateData(Data(date, day.toString(), "1"))
                    Log.e(TAG, "onTouch: not available", )
                }

                return false
            }
        })

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Touch Counter Service")
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                    NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object{
        const val CHANNEL_ID = "ForegroundServiceChannel"
        private const val TAG = "TouchCounterService"
    }
}