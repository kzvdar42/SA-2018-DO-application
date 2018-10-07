package com.example.kzvdar42.deliveryoperatorapp.DB


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*
import java.util.concurrent.Executors


@Database(entities = [OrderEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun orderDao(): OrderDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room
                        .inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java)
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                Executors.newSingleThreadScheduledExecutor().execute { getDatabase(context).orderDao().insertAll(populateData()) }
                            }
                        })
                        .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }

        private fun populateData(): List<OrderEntity> {
            val result = LinkedList<OrderEntity>()
            result.add(OrderEntity(0, "Vlad Kuleykin", "First order description"
                    , 55.7476907, 48.7433593, 55.7867635, 49.1216088))
            result.add(OrderEntity(1, "Second order", "Second order description.\n Impossible route"
                    , 55.7476907, 48.7433593, 38.9098, -77.0295))
            result.add(OrderEntity(2, "Third order", "Third order description"
                    , 55.7867635, 49.1216088, 55.7476907, 48.7433593))
            result.add(OrderEntity(3, "Forth order", "Forth order description"
                    , 49.125392, 55.784798, 49.107202, 55.794498))
            return result
        }
    }


}