package com.voicetotype

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.voicetotype.database.AppDatabase
import com.voicetotype.database.RecordDao
import com.voicetotype.database.UserRepository
import com.voicetotype.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityMainBinding
    var fAdapter: FilesAdapter? = null
    private lateinit var db: AppDatabase
    private lateinit var userDao: RecordDao
    private lateinit var userRepository: UserRepository
    var recordIdValue: Int? = null
    var filename: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        db = AppDatabase.getDatabase(applicationContext)
        userDao = db.recordDao()
        userRepository = UserRepository(userDao)

        lifecycleScope.launch(Dispatchers.IO) {
            // Get all records from the repository
            val allRecords = userRepository.getAllRecords()

            if (allRecords.isEmpty()) {
                binding.recentFilesView.visibility = View.VISIBLE
            } else {
                binding.recentFilesView.visibility = View.GONE
            }

            // Initialize the adapter with delete and item click lambdas
            fAdapter = FilesAdapter(allRecords, iconCall = { view, recordId, fileNm ->
                lifecycleScope.launch(Dispatchers.Main) {
                    filename = fileNm
                    recordIdValue = recordId
                    showDialog()
                }
            }, id = { data ->
                lifecycleScope.launch(Dispatchers.Main) {
                    startActivity(
                        Intent(
                            this@MainActivity, ViewActivity::class.java
                        ).putExtra("datafile", data)
                    )
                }
            })


            // Back to Main thread to update UI
            withContext(Dispatchers.Main) {
                binding.rvHomeList.adapter = fAdapter
                fAdapter?.notifyDataSetChanged()
            }
        }


        lisener()

    }

    fun lisener() {
        binding.imgmainPlus.setOnClickListener(this)
        binding.icSetting.setOnClickListener(this)
        binding.icHistory.setOnClickListener(this)
        binding.uploadAudio.setOnClickListener(this)
        binding.txtViewAll.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.imgmainPlus -> {
                startActivity(Intent(this, LiveSpeech::class.java))
            }

            R.id.icSetting -> {
                startActivity(Intent(this, SettingScreen::class.java))
//                startActivity(Intent(this, ::class.java))

            }

            R.id.icHistory -> {
                startActivity(Intent(this, HistoryScreen::class.java))

            }

            R.id.txtViewAll -> {
                startActivity(Intent(this, HistoryScreen::class.java))

            }

            R.id.uploadAudio -> {
                Toast.makeText(
                    this@MainActivity,
                    "Currently Not Working will try to start on next update ",
                    Toast.LENGTH_LONG
                ).show()
            }

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

                        if(updatedRecords.isEmpty()){
                            binding.recentFilesView.visibility = View.VISIBLE
                        }else{
                            binding.recentFilesView.visibility = View.GONE
                        }
                    }

                }
            }.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()


    }


}


