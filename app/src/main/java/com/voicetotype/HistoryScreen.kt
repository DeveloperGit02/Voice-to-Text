package com.voicetotype

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.voicetotype.database.AppDatabase
import com.voicetotype.database.RecordDao
import com.voicetotype.database.UserRepository
import com.voicetotype.databinding.ActivityHistoryScreenBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryScreen : AppCompatActivity() {

    lateinit var binding: ActivityHistoryScreenBinding
    private lateinit var db: AppDatabase
    private lateinit var userDao: RecordDao
    private lateinit var userRepository: UserRepository
    var fAdapter: HistoryAdapter? = null
    var recordIdValue: Int? = null
    var filename: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityHistoryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = AppDatabase.getDatabase(applicationContext)
        userDao = db.recordDao()
        userRepository = UserRepository(userDao)


        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    loadAllRecords()
                } else {
                    searchRecords(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        lifecycleScope.launch(Dispatchers.IO) {
            // Get all records from the repository
//            val allRecords = userRepository.getAllRecords()

            // Initialize the adapter with delete and item click lambdas
            fAdapter = HistoryAdapter(listOf(), delIcon = { recordId, fileNmm ->
                lifecycleScope.launch(Dispatchers.Main) {
                    filename = fileNmm
                    recordIdValue = recordId
                    showDialog()
                }
            }, id = { data, name  , audioPath->
                lifecycleScope.launch(Dispatchers.Main) {
                    startActivity(
                        Intent(
                            this@HistoryScreen, ViewActivity::class.java
                        ).putExtra("datafile", data).putExtra("nameoffile", name).putExtra("audio" , audioPath)

                    )

                }

            })


            // Back to Main thread to update UI
            withContext(Dispatchers.Main) {
                binding.rvRecentItemList.adapter = fAdapter
                loadAllRecords()
                fAdapter?.notifyDataSetChanged()
            }


        }

        binding.icHome.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }

        binding.imgHistoryPlus.setOnClickListener {
            startActivity(
                Intent(
                    this, LiveSpeech::class.java
                )
            )
        }

        binding.icSetting.setOnClickListener {
            startActivity(
                Intent(
                    this, SettingScreen::class.java
                )
            )
        }

        binding.icSearch.setOnClickListener {
            binding.searchView.visibility = View.VISIBLE
        }

        binding.txtClose.setOnClickListener {
            binding.searchBar.text?.clear()

            binding.searchView.visibility = View.GONE
        }

        binding.txtHome.setOnClickListener {
            val query = binding.searchBar.text.toString()
            if (query.isBlank()) {
                loadAllRecords()
            } else {
                searchRecords(query)
            }
        }

        binding.headerIcon.setOnClickListener{
        finish()
}

    }

    private fun loadAllRecords() {
        lifecycleScope.launch {
            val records = withContext(Dispatchers.IO) {
                userDao.getAllRecords()
            }
            fAdapter?.updateRecords(records)
        }
    }


    private fun searchRecords(query: String) {
        val trimmedQuery = query.trim()
        lifecycleScope.launch {
            val results = withContext(Dispatchers.IO) {
                userDao.searchRecords(trimmedQuery)
            }

            Log.d("SEARCH", "Query: '$trimmedQuery', Results: $results")
            fAdapter?.updateRecords(results)
        }
    }

    private fun showDialog() {


        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this file  " + filename)
            .setTitle("Delete").setPositiveButton("Yes") { dialog, which ->
                lifecycleScope.launch(Dispatchers.IO) {
                    userRepository.deleteRecordById(recordIdValue!!)
                    // After deletion, fetch updated records and update UI on the main thread
                    val updatedRecords = userRepository.getAllRecords()

                    // Post the updated records to the main thread to refresh the RecyclerView
                    withContext(Dispatchers.Main) {
                        fAdapter?.allRecords = updatedRecords
                        fAdapter?.notifyDataSetChanged()
                    }

                }
            }.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()


    }


}