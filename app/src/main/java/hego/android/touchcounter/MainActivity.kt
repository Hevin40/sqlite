package hego.android.touchcounter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        initLsitener()
        checkDrawOverAppsPermission()
    }

    private fun checkDrawOverAppsPermission() {
        Log.e(TAG, "checkDrawOverAppsPermission: starts", )
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))
            startActivityForResult(intent, 0)
            Log.e(TAG, "checkDrawOverAppsPermission: getting permission", )
        }else{
            Log.e(TAG, "checkDrawOverAppsPermission: permission granted")
            startService(Intent(this,TouchCounterService::class.java))
        }
    }

    private fun init() {
        Log.e(TAG, "init: ", )
    }

    private fun initLsitener() {
        settings.setOnClickListener(this)
    }

    private fun gotoSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.settings -> gotoSettings()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Settings.canDrawOverlays(this)){
            Log.e(TAG, "onActivityResult: permission granted", )
            startService(Intent(this,TouchCounterService::class.java))
        }else{
            Log.e(TAG, "onActivityResult: permission not granted", )
            stopService(Intent(this,TouchCounterService::class.java))
        }
    }
    
    companion object{
        private const val TAG = "MainActivity"
    }
}