package com.nikhil.here.youtube_poc

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.nikhil.here.youtube_poc.databinding.ActivityHomeBinding

class HomeActivity : ComponentActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnYtOfficial.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnYtWebView.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java))
        }

        binding.btnYtProfile.setOnClickListener {
            startActivity(Intent(this, YoutubeProfileActivity::class.java))
        }
    }



}