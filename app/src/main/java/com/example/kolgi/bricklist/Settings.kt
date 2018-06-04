package com.example.kolgi.bricklist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import kotlinx.android.synthetic.main.activity_settings.*


class Settings : AppCompatActivity() {

    var URL = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val extras = intent.extras ?: return
        val message = extras.getString(EXTRA_MESSAGE)
        this.URL = message
        textViewUrl.setText(URL)
    }
}
