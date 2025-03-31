package com.voicetotype

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    var fAdapter: FilesAdapter? = null
    var recordIdValue : Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityHistoryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        db = AppDatabase.getDatabase(applicationContext)
        userDao = db.recordDao()
        userRepository = UserRepository(userDao)


//        getRecord()




        lifecycleScope.launch(Dispatchers.IO) {
            // Get all records from the repository
            val allRecords = userRepository.getAllRecords()

            // Initialize the adapter and pass the lambda for deletion
            fAdapter = FilesAdapter(allRecords) { recordId, data, image ->

                recordIdValue = recordId
                // Inside the lambda, perform the deletion in the IO thread
//                lifecycleScope.launch(Dispatchers.IO) {
//                    userRepository.deleteRecordById(recordId)


                startActivity(
                    Intent(
                        this@HistoryScreen,
                        ViewActivity::class.java
                    ).putExtra("datafile", data)
                )

                image.setOnClickListener {
                    showDialog()
                }
            }

            // Set the adapter to RecyclerView
            withContext(Dispatchers.Main) {
                binding.rvRecentItemList.adapter = fAdapter
            }
        }



        binding.icHome.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
        binding.imgPlus.setOnClickListener { startActivity(Intent(this, LiveSpeech::class.java)) }
        binding.icSetting.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SettingScreen::class.java
                )
            )
        }

    }

    private fun showDialog() {



        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setMessage("Are you sure to delete this file")
            .setTitle("Dialog")
            .setPositiveButton("Yes") { dialog, which ->
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
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()



    }


//    private fun getRecord() {
//        lifecycleScope.launch(Dispatchers.IO) {
//
//            val allRecords = userRepository.getAllRecords()
//
//            withContext(Dispatchers.Main) {
//                // For example, display the records in a Toast or use them in your UI
//                allRecords.forEach { record ->
////                    Toast.makeText(applicationContext, "Record: ${record.name}", Toast.LENGTH_SHORT)
////                        .show()
//                }
//
//
//            }
//
//            Log.e("records", "getRecord: " + allRecords)
//            // Switch back to the main thread after inserting
//            withContext(Dispatchers.Main) {
//                // Do any UI updates here (e.g., show a message that the user was added)
//
//
//            }
//        }
//    }


}