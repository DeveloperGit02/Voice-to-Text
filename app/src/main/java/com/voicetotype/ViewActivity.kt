package com.voicetotype

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.voicetotype.databinding.ActivityViewBinding

class ViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val data = intent.getStringExtra("datafile")
        binding.outputView.text = data.toString()

        binding.backImg.setOnClickListener {
            finish()
        }

        binding.shareImg.setOnClickListener {
            val recognizedText = data
            if (!recognizedText.isNullOrEmpty() && recognizedText != "No text available to share") {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, recognizedText)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Share Text"))
            } else {
                Toast.makeText(
                    applicationContext, "No recognized text to share", Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
}