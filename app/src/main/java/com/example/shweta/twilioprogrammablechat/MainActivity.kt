package com.example.shweta.twilioprogrammablechat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.shweta.twilioprogrammablechat.common.BaseActivityMVVM
import com.example.shweta.twilioprogrammablechat.databinding.ActivityMainBinding

class MainActivity : BaseActivityMVVM<ActivityMainBinding>() {
    override fun initialization() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
