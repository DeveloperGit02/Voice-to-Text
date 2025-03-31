package com.voicetotype.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecordDao {

    @Insert
    suspend fun insertRecord(record: DataRecordModel)

    @Query("SELECT * FROM DataRecordModel ORDER BY id DESC")
    suspend fun getAllRecords(): List<DataRecordModel>

    @Query("DELETE FROM DataRecordModel WHERE id = :recordId")
    suspend fun deleteRecord(recordId: Int)

}