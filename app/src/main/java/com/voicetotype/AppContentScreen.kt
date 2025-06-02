package com.voicetotype

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.voicetotype.databinding.ActivityAppContentScreenBinding

class AppContentScreen : AppCompatActivity() {

    private lateinit var binding: ActivityAppContentScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppContentScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val haedingData = intent.getStringExtra("heading")
        binding.heading.text = haedingData

        val data = intent.getStringExtra("data")
        binding.txtContentData.text =
            HtmlCompat.fromHtml(data.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)



        binding.back.setOnClickListener {
            finish()
        }

    }


}