package com.voicetotype

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.voicetotype.database.DataRecordModel
import com.voicetotype.databinding.RecentItemsBinding

class HistoryAdapter(
    var allRecords: List<DataRecordModel>,
    var delIcon: ( Int, String) -> Unit,
    var id: ( String) -> Unit
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


            holder.binding.icDelete.setOnClickListener {
               delIcon( allRecords[position].id , allRecords[position].name)
            }


            holder.binding.view.setOnClickListener {
                id(
                    allRecords[position].data,
                )
            }
        }
    }

    override fun getItemCount(): Int {
        Log.e("size", "getItemCount: " + allRecords.size)
        return allRecords.size
    }


}