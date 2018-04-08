package com.example.coolweather

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView

class Java : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.forecast_item, null)
        val textView = view.findViewById<TextView>(R.id.info_text)
        textView.text = "sdfsdf"
    }
}
