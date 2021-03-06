package com.foryouandme.data.repository.location.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HomeLocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocation(location: HomeLocationDatabaseEntity)

    @Query("SELECT * FROM home_location")
    suspend fun getLocations(): List<HomeLocationDatabaseEntity>

}