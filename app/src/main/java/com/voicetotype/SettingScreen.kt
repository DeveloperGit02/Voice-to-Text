package com.voicetotype

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.voicetotype.databinding.ActivitySettingScreenBinding

class SettingScreen : AppCompatActivity() {
    lateinit var binding: ActivitySettingScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        
        binding.back.setOnClickListener {
            finish()
        }
        binding.icHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.imgPlus.setOnClickListener {
            startActivity(Intent(this, LiveSpeech::class.java))
        }


    }
}