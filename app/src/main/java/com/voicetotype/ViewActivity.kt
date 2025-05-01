package com.voicetotype

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.voicetotype.databinding.ActivityViewBinding
import java.util.Locale

class ViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityViewBinding
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.US  // or Locale("hi", "IN") for Hindi
            }
        }



        val data = intent.getStringExtra("datafile")
        binding.outputView.text = data.toString()

        val fName = intent.getStringExtra("nameoffile")
        Log.e("name", "onCreate: " + fName)

        binding.speechname.text = fName

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


        binding.playButton.setOnClickListener {
            speakText(data.toString()) {
                binding.playButton.visibility = View.VISIBLE
                binding.pauseButton.visibility = View.GONE
            }
            binding.pauseButton.visibility = View.VISIBLE
            binding.playButton.visibility = View.GONE
        }

        binding.pauseButton.setOnClickListener {
            textToSpeech.stop()
            binding.pauseButton.visibility = View.GONE
            binding.playButton.visibility = View.VISIBLE
        }


//        binding.playButton.setOnClickListener {
//            speakText(data.toString())
//            binding.pauseButton.visibility = View.VISIBLE
//            binding.playButton.visibility = View.GONE
//        }
//
//        binding.pauseButton.setOnClickListener {
//            binding.pauseButton.visibility = View.GONE
//            binding.playButton.visibility = View.VISIBLE
//            textToSpeech.stop()
//        }

    }


    private fun speakText(text: String, onComplete: () -> Unit) {
        val utteranceId = "TTS_${System.currentTimeMillis()}"

        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // Optional: hide play button while speaking
            }

            override fun onDone(utteranceId: String?) {
                runOnUiThread {
                    onComplete()
                }
            }

            override fun onError(utteranceId: String?) {
                // Optional: handle error
            }
        })

        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }


//    private fun speakText(text: String) {
//        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
//    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

}