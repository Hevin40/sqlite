package hego.android.touchcounter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
    }

    private fun init() {

    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.touchAnalysis -> gotoTouchAnalysis()
        }
    }

    private fun gotoTouchAnalysis() {
        startActivity(Intent(this, TouchAnalysisActivity::class.java))
    }
}