package com.voicetotype


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.voicetotype.databinding.ActivityUploadAudioBinding


class UploadAudio : AppCompatActivity() {
    lateinit var binding: ActivityUploadAudioBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }
}