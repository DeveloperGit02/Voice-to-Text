package com.voicetotype

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.voicetotype.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lisener()
    }


    fun lisener() {
        binding.imgPlus.setOnClickListener(this)
        binding.icSetting.setOnClickListener(this)
        binding.icHistory.setOnClickListener(this)
        binding.uploadAudio.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.imgPlus -> {
                startActivity(Intent(this, LiveSpeech::class.java))

            }

            R.id.icSetting -> {
                startActivity(Intent(this, SettingScreen::class.java))
//                startActivity(Intent(this, ::class.java))
            }

            R.id.icHistory -> {
                startActivity(Intent(this, HistoryScreen::class.java))
            }


            R.id.uploadAudio -> {
                Toast.makeText(this@MainActivity , "Currently Not Working will try to start on next update ", Toast.LENGTH_LONG).show()
            }

        }
    }
}
