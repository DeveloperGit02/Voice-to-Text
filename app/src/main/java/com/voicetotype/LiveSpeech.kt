package com.voicetotype

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.RESULTS_RECOGNITION
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.voicetotype.database.AppDatabase
import com.voicetotype.database.DataRecordModel
import com.voicetotype.database.RecordDao
import com.voicetotype.database.UserRepository
import com.voicetotype.databinding.ActivityLiveSpeechBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveSpeechBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesManager = SharedPreferencesManager(applicationContext)

        db = AppDatabase.getDatabase(applicationContext)
        userDao = db.recordDao()
        userRepository = UserRepository(userDao)
        initializeSpeechRecognizer()

        // Clear any previously saved text
        sharedPreferencesManager.clearRecognizedText()

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    if (isListening) {

                        if (binding.textView1.text.isEmpty()) {
                            stopListening()
                            binding.txtTaptoSpech.text = "Tap to start Live Speech"
                            binding.imgRecord.setImageResource(R.drawable.livespeech_mic)
                            Toast.makeText(
                                this@LiveSpeech,
                                "Cannot save file because Text is Empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            stopListening()
                            binding.txtTaptoSpech.text = "Tap to start Live Speech"
                            binding.imgRecord.setImageResource(R.drawable.livespeech_mic)
                            binding.icLiveRecorder.visibility = View.GONE
                            addData()
                        }


                    } else {
                        startListening()
                        binding.txtTaptoSpech.text = "Tap to Stop and Save File"
                        binding.imgRecord.setImageResource(R.drawable.livespeech_mic_red)
                        binding.icLiveRecorder.visibility = View.GONE
                    }
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this, android.Manifest.permission.RECORD_AUDIO
                        )
                    ) {
                        // User checked "Don't ask again"
                        showPermissionSettingsDialog()
                    } else {
                        Toast.makeText(
                            this,
                            "Audio permission is required to use speech features.",
                            Toast.LENGTH_LONG
                        ).show()
                    }


                }
            }

        binding.imgRecord.setOnClickListener {
            requestPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
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
            finish()

        }

        binding.icHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()

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
                val matches = partialResults?.getStringArrayList(RESULTS_RECOGNITION)
                matches?.let { binding.textView1.text = it[0] }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun addData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val existingText = sharedPreferencesManager.getRecognizedText()

//            val currentDateTime = SimpleDateFormat("dd MMMM 'at' h:mm a", Locale.getDefault()).format(Date())
            val currentDateTime =
                SimpleDateFormat("dd MMMM yyyy 'at' h:mm a", Locale.getDefault()).format(Date())


            val listIndex = sharedPreferencesManager.getLastSpeechIndex()

            val newindex = listIndex + 1

            sharedPreferencesManager.saveLastSpeechIndex(newindex)

            val user = DataRecordModel(
                0, "Speech_" + newindex, currentDateTime, "dummy", existingText.toString()
            )
            userRepository.insert(user)

            Log.e("record", "addData: " + user)
            // Switch back to the main thread after inserting
            withContext(Dispatchers.Main) {
                startActivity(Intent(this@LiveSpeech, HistoryScreen::class.java))
            }
        }
    }

    private fun startListening() {
        isListening = true
        coroutineScope.launch {
            delay(500) // Delay for 500 milliseconds
            speechRecognizer.startListening(speechIntent)
        }
    }

    private fun stopListening() {
        isListening = false
        speechRecognizer.stopListening()
        speechRecognizer.cancel()
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    private fun showPermissionSettingsDialog() {
        AlertDialog.Builder(this).setTitle("Permission Required")
            .setMessage("Please enable microphone access in Settings to use speech recognition.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }.setNegativeButton("Cancel", null).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.e("123456789", "onDestroy: " + "onDestroy")
        stopListening()
        speechRecognizer.destroy()

    }


}

