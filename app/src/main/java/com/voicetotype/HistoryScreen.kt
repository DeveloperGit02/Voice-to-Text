package com.voicetotype

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
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


        lifecycleScope.launch(Dispatchers.IO) {
            // Get all records from the repository
            val allRecords = userRepository.getAllRecords()

            // Initialize the adapter with delete and item click lambdas
            fAdapter = HistoryAdapter(allRecords, delIcon = {  recordId , fileNmm ->
                lifecycleScope.launch(Dispatchers.Main) {
                    filename = fileNmm
                    recordIdValue = recordId
                    showDialog()
                }
            }, id = { data ->
                lifecycleScope.launch(Dispatchers.Main) {


                    startActivity(
                        Intent(
                            this@HistoryScreen, ViewActivity::class.java
                        ).putExtra("datafile", data)
                    )
                }
            })


            // Back to Main thread to update UI
            withContext(Dispatchers.Main) {
                binding.rvRecentItemList.adapter = fAdapter
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

    }


    private fun showDialog() {


        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this file" + filename)
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