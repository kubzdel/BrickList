package com.example.kolgi.bricklist

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import kotlinx.android.synthetic.main.activity_add_project.*
import kotlinx.android.synthetic.main.activity_settings.*


class Settings : AppCompatActivity() {

    var URL = ""
    var DISPLAY_ACTIVE_ONLY = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val extras = intent.extras ?: return
        val message = extras.getString(EXTRA_MESSAGE+1)
        val displayActive = extras.getBoolean(EXTRA_MESSAGE+2)
        this.URL = message
        this.DISPLAY_ACTIVE_ONLY = displayActive
        textViewUrl.setText(URL)
        active_only_box.setOnCheckedChangeListener { buttonView, isChecked ->
            DISPLAY_ACTIVE_ONLY = isChecked
        }
        active_only_box.isChecked = DISPLAY_ACTIVE_ONLY
    }

    override fun finish() {
        this.URL = textViewUrl.text.toString()
        val data = Intent()
        data.putExtra("URL",this.URL)
        data.putExtra("active_only",this.DISPLAY_ACTIVE_ONLY)
        setResult(Activity.RESULT_OK,data)
        super.finish()
    }
}
