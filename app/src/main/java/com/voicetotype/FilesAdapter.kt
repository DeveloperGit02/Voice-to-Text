package com.voicetotype

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.voicetotype.database.DataRecordModel
import com.voicetotype.databinding.RecentItemsBinding

class FilesAdapter(
    var allRecords: List<DataRecordModel>,
    var iconCall: (ImageView, Int, String) -> Unit,
    var id: ( String) -> Unit
) : RecyclerView.Adapter<FilesAdapter.ViewHolder>() {

    private var isExpanded = false


    inner class ViewHolder(var binding: RecentItemsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesAdapter.ViewHolder {
        val binding = RecentItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilesAdapter.ViewHolder, position: Int) {

        holder.apply {

            binding.txtFileName.text = allRecords[position].name
            binding.txtDateTime.text = allRecords[position].dateTime

            holder.binding.icDelete.setOnClickListener {
                iconCall(binding.icDelete, allRecords[position].id , allRecords[position].name )
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
        return if (isExpanded || allRecords.size <= 5) allRecords.size else 5

    }


}