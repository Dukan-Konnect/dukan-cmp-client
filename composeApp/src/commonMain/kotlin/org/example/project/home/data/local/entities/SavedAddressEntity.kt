package org.example.project.home.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_addresses")
data class SavedAddressEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "label")
    val label: String,
    @ColumnInfo(name = "house_number")
    val houseNumber: String,
    @ColumnInfo(name = "street")
    val street: String,
    @ColumnInfo(name = "city")
    val city: String,
    @ColumnInfo(name = "state")
    val state: String,
    @ColumnInfo(name = "landmark")
    val landmark: String,
    @ColumnInfo(name = "phone")
    val phone: String,
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
