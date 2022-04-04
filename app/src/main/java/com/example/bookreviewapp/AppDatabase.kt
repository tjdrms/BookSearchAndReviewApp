package com.example.bookreviewapp

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bookreviewapp.dao.HistoryDao
import com.example.bookreviewapp.model.History

@Database(entities = [History::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}