package com.voicetotype

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.voicetotype.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)

        setContentView(binding.root)


        Handler().postDelayed({
            // After the delay, open MainActivity
            startActivity(Intent(this, MainActivity::class.java))

            // Close the SplashScreen activity so the user can't come back to it
            finish()
        }, 3000) // 3000 milliseconds = 3 seconds

    }
}