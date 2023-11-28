package com.example.wander_app
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


const val DATABASE_VERSION = 1;
@Database(entities = [ItineraryItem::class], version = DATABASE_VERSION, exportSchema = false)
@TypeConverters()
abstract class Database : RoomDatabase() {
    abstract fun itineraryItemDao(): ItineraryItemDao
}
@Dao
interface ItineraryItemDao {
    @Insert
    fun insert(itineraryItem: ItineraryItem)
    @Query("SELECT * FROM ItineraryItem")
    fun getAll(): List<ItineraryItem>

    @Query("DELETE FROM ItineraryItem")
    fun deleteAll()
}