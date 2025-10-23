package com.wagonarchive.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WagonDao {
    @Query("SELECT * FROM wagons ORDER BY date DESC")
    fun getAllWagons(): Flow<List<WagonEntity>>

    @Query("SELECT * FROM wagons WHERE serialNumber LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' OR status LIKE '%' || :query || '%'")
    fun searchWagons(query: String): Flow<List<WagonEntity>>

    @Query("SELECT * FROM wagons WHERE id = :id")
    suspend fun getWagonById(id: Int): WagonEntity?

    @Query("SELECT * FROM wagons WHERE serialNumber = :serialNumber")
    suspend fun getWagonBySerialNumber(serialNumber: String): WagonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWagon(wagon: WagonEntity)

    @Update
    suspend fun updateWagon(wagon: WagonEntity)

    @Delete
    suspend fun deleteWagon(wagon: WagonEntity)

    @Query("DELETE FROM wagons")
    suspend fun deleteAll()
}
