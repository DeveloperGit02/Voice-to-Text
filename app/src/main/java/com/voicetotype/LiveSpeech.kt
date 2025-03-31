package com.voicetotype

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.RESULTS_RECOGNITION
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.voicetotype.database.AppDatabase
import com.voicetotype.database.DataRecordModel
import com.voicetotype.database.RecordDao
import com.voicetotype.database.UserRepository
import com.voicetotype.databinding.ActivityLiveSpeechBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LiveSpeech : AppCompatActivity() {

    lateinit var binding: ActivityLiveSpeechBinding
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private var isListening = false
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var db: AppDatabase
    private lateinit var userDao: RecordDao
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveSpeechBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesManager = SharedPreferencesManager(applicationContext)


        db = AppDatabase.getDatabase(applicationContext)
        userDao = db.recordDao()
        userRepository = UserRepository(userDao)


        // Request microphone permission using ActivityResultContracts
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                initializeSpeechRecognizer()
            } else {
                Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show()
            }
        }.launch(android.Manifest.permission.RECORD_AUDIO)

        // Clear any previously saved text
        clearSavedTextInSharedPreferences()

        // Handle Start/Stop Button
        binding.imgPlus.setOnClickListener {
            if (isListening) {
                stopListening()
                binding.txtTaptoSpech.text = "Tap to start Live Speech"

                binding.imgPlus.setImageResource(R.drawable.livespeech_mic)
                binding.icLiveRecorder.visibility = View.GONE
                addData()
            } else {
                startListening()
                binding.txtTaptoSpech.text = "Tap to Stop"
                binding.imgPlus.setImageResource(R.drawable.livespeech_mic_red)
                binding.icLiveRecorder.visibility = View.GONE
            }
        }

        binding.shareImg.setOnClickListener {
            val recognizedText = sharedPreferencesManager.getRecognizedText()
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

        binding.icSetting.setOnClickListener {
            startActivity(Intent(this, SettingScreen::class.java))
        }

        binding.icHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.backImg.setOnClickListener {
            finish()
        }
    }

    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Start speaking...")
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                if (isListening) startListening()
            }

            override fun onError(error: Int) {
                if (isListening) startListening()
            }


            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    val recognizedText = matches[0] // First match

//                    // Retrieve the previous text from SharedPreferences
                    val existingText = sharedPreferencesManager.getRecognizedText()


                    // Append the new recognized text to the existing text
                    val updatedText = if (existingText != "No data found") {
                        "$existingText  $recognizedText"
                    } else {
                        recognizedText // If there's no previous text, just use the new text
                    }

                    // Update the UI with the updated text
                    sharedPreferencesManager.saveRecognizedText(updatedText)

                    binding.textView1.text = updatedText


                }
                if (isListening) startListening()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                if (isListening) startListening()
//                val matches = partialResults?.getStringArrayList(RESULTS_RECOGNITION)
//                matches?.let { binding.textView1.text = it[0] }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }


    private fun generateValidFileName(text: String): String {
        return text.replace("[^a-zA-Z0-9]".toRegex(), "_")
    }


    fun generateRandomString(length: Int): String {
        val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { characters.random() }.joinToString("")
    }

    private fun addData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val existingText = sharedPreferencesManager.getRecognizedText()

            val randomString = generateRandomString(6) // Generates a random string of length 10
            println(randomString) // Example output: "aB3xYzP9Qw"
            var textName = "Speech_" + randomString


            val firstTwoWords = existingText?.split(" ")?.take(2)?.joinToString(" ")

        // Generate the file name with the first two words
            val fileName = "Speech_" + firstTwoWords


//            val fileName = "Speech" + generateValidFileName(existingText.toString())


            val currentDateTime =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                    Date()
                )


            val user = DataRecordModel(0, fileName, currentDateTime, "dummy", existingText.toString())
            userRepository.insert(user)

            Log.e("record", "addData: " + user)
            // Switch back to the main thread after inserting
            withContext(Dispatchers.Main) {
                startActivity(Intent(this@LiveSpeech , HistoryScreen::class.java))
            }
        }
    }


    private fun clearSavedTextInSharedPreferences() {
        sharedPreferencesManager.clearAll()
    }

    private fun startListening() {
        isListening = true
        speechRecognizer.startListening(speechIntent)
    }

    private fun stopListening() {
        isListening = false
        speechRecognizer.stopListening()
        speechRecognizer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }


}
