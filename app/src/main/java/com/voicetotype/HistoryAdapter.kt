package com.voicetotype

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.voicetotype.database.DataRecordModel
import com.voicetotype.databinding.RecentItemsBinding

class HistoryAdapter(
    var allRecords: List<DataRecordModel>,
    var delIcon: ( Int, String) -> Unit,
    var id: ( String, String , String) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: RecentItemsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolder {
        val binding = RecentItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {

        holder.apply {

            binding.txtFileName.text = allRecords[position].name
            binding.txtDateTime.text = allRecords[position].dateTime


            val dataText = allRecords[position].data
            val words = dataText.trim().split("\\s+".toRegex())
            val preview = if (words.size <= 8) {
                words.joinToString(" ")
            } else {
                words.take(8).joinToString(" ") + "..."
            }

            binding.dataText.text = preview


            holder.binding.icDelete.setOnClickListener {
               delIcon( allRecords[position].id , allRecords[position].name)
            }


            holder.binding.view.setOnClickListener {
                id(
                    allRecords[position].data, allRecords[position].name , allRecords[position].audioPath
                )
            }
        }
    }

    override fun getItemCount(): Int {
        Log.e("size", "getItemCount: " + allRecords.size)
        return allRecords.size
    }

    fun updateRecords(newList: List<DataRecordModel>) {
        allRecords = newList
        notifyDataSetChanged()
    }
}