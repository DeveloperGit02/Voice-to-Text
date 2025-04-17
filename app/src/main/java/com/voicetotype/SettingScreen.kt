package com.voicetotype

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.voicetotype.databinding.ActivitySettingScreenBinding

class SettingScreen : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivitySettingScreenBinding
    var faqData =
        "1. What does this app do?\n" + "Our app converts speech into written text in real-time or from pre-recorded audio files. It’s ideal for students, professionals, and anyone needing accurate transcriptions quickly.\n" + "\n" + "2. What types of audio files are supported?\n" + "You can upload most common audio formats using the built-in file picker. We recommend using .m4a, .mp3, or .wav for best results.\n" + "\n" + "3. Is internet connection required?\n" + "Live transcription uses Apple’s built-in speech framework and can work offline for supported languages. However, uploading audio may require internet access, especially for larger files.\n" + "\n" + "4. Can I share the transcribed text?\n" + "Yes! After transcription, you can copy the text or share it via other apps using the share menu.\n" + "\n" + "5. Can I transcribe audio shared from other apps?\n" + "Absolutely. If an audio file is shared from another app, ours can automatically open, play, and transcribe the file."

    var pp =
        " Privacy Policy\n" + "\n" + "Your privacy is important to us. Here's how we handle your data:\n" + "\n" + "- Speech Recognition: Your voice data is used only for transcription. We do not store or share your audio or text unless you explicitly choose to export it.\n" + "            \n" + "- Uploaded Files: Audio files you upload are processed locally on your device. No data is sent to external servers.\n" + "            \n" + "- Shared Audio: Files shared from other apps are only accessed temporarily for transcription and are not stored or transmitted anywhere.\n" + "            \n" + "- Analytics: We may collect anonymous usage statistics to improve app performance. This data is never linked to you personally.\n" + "\n" + "By using the app, you agree to this privacy policy."

    var about =
        " Speech to Text: Fast, Accurate, and Easy\n" + "\n" + "This app is designed to help you convert spoken words into editable, shareable text — instantly. Whether you're attending a lecture, capturing a meeting, or uploading a podcast episode, this app ensures seamless transcription every time.\n" + "\n" + "\uD83D\uDD11 Key Features:\n" + "• Real-time speech-to-text conversion\n" + "• Upload and transcribe existing audio files\n" + "• Automatic handling of audio shared from other apps\n" + "• Share or copy your transcribed text easily\n" + "\n" + "Built with love using Apple’s Speech & Audio frameworks.\n" + "Perfect for students, journalists, content creators, and anyone who needs fast transcription on the go."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lisner()

        binding.back.setOnClickListener {
            finish()
        }
        binding.icHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))

            finish()
        }

        binding.imgPlus.setOnClickListener {
            startActivity(Intent(this, LiveSpeech::class.java))
            finish()

        }


    }

    fun lisner() {
        binding.txtFaq.setOnClickListener(this)
        binding.txtContactUs.setOnClickListener(this)
        binding.txtPP.setOnClickListener(this)
        binding.AboutApp.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {


            R.id.txtFaq -> {
                startActivity(Intent(this, AppContentScreen::class.java).putExtra("data", faqData).putExtra("heading", "FAQs"))

            }

            R.id.txtContactUs -> {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cogniter.com/contactus.aspx"))
                startActivity(i)

            }

            R.id.txtPP -> {
                startActivity(Intent(this, AppContentScreen::class.java).putExtra("data", pp).putExtra("heading", "Privacy Policy"))

            }

            R.id.AboutApp -> {
                startActivity(Intent(this, AppContentScreen::class.java).putExtra("data", about).putExtra("heading", "About App"))

            }

        }

    }
}