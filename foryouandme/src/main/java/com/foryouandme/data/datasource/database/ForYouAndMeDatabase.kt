package com.foryouandme.data.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.foryouandme.data.repository.device.database.DeviceInfoDao
import com.foryouandme.data.repository.device.database.DeviceInfoDatabaseEntity

@Database(
    entities = [DeviceInfoDatabaseEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ForYouAndMeDatabase : RoomDatabase() {

    abstract fun deviceInfoDao(): DeviceInfoDao

}