package com.example.bookreviewapp.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity
data class History (
    @PrimaryKey @NonNull val isbn: String?,
    @ColumnInfo(name = "keyword") val keyword: String?
)