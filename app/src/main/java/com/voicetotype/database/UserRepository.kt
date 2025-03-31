package com.voicetotype.database

class UserRepository(private val userDao: RecordDao) {

    suspend fun insert(user: DataRecordModel) {
        userDao.insertRecord(user)
    }

    suspend fun getAllRecords(): List<DataRecordModel> {
        return userDao.getAllRecords()
    }

    // Delete a record by its ID (repository function)
    suspend fun deleteRecordById(recordId: Int) {
        // Call the DAO's deleteRecord function
        userDao.deleteRecord(recordId)
    }


}