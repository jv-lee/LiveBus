package com.bus.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bus.annotation.InjectBus

@InjectBus
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}