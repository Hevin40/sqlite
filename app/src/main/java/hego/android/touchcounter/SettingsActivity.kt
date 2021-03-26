package hego.android.touchcounter

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        init()
        initLsitener()
    }

    private fun initLsitener() {
        touchAnalysis.setOnClickListener(this)
        touchperApps.setOnClickListener(this)

    }

    private fun init() {

    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.touchAnalysis -> gotoTouchAnalysis()
            R.id.touchperApps -> if (!isMyServiceRunning(AppDetectorService::class.java)){
                gotoAccessibilitySettings()
            }
        }
    }

    private fun gotoAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun gotoTouchAnalysis() {
        startActivity(Intent(this, TouchAnalysisActivity::class.java))
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}