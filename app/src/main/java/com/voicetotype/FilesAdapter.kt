package com.voicetotype

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.voicetotype.database.DataRecordModel
import com.voicetotype.databinding.RecentItemsBinding

class FilesAdapter(var allRecords: List<DataRecordModel> , var id :(Int , String, ImageView)-> Unit  ) : RecyclerView.Adapter<FilesAdapter.ViewHolder>() {


    inner class ViewHolder(var binding: RecentItemsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesAdapter.ViewHolder {
        val binding = RecentItemsBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilesAdapter.ViewHolder, position: Int) {

        holder.apply {

            binding.txtFileName.text = allRecords[position].name
            binding.txtDateTime.text = allRecords[position].dateTime


            holder.binding.parentLayout .setOnClickListener{
                id(allRecords[position].id , allRecords[position].data , binding.icDelete)
            }


        }
    }

    override fun getItemCount(): Int {
        Log.e("size", "getItemCount: " + allRecords.size)
        return allRecords.size
    }
}