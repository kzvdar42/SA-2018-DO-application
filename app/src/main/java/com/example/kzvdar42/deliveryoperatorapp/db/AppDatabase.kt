package com.example.kzvdar42.deliveryoperatorapp.db


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
            result.add(OrderEntity(1, "Vlad Kuleykin", "The parcel is near the door, you can take it anytime.\n Phone num: +79173452517"
                    , 55.7476907, 48.7433593, 55.7867635, 49.1216088))
            result.add(OrderEntity(2, "Turk Turklton", "Second order description.\n Impossible route"
                    , 55.7476907, 48.7433593, 38.9098, -77.0295))
            result.add(OrderEntity(3, "Christopher Duncan Turk", "$emptyDescription$emptyDescription$emptyDescription"
                    , 55.7867635, 49.1216088, 55.7476907, 48.7433593))
            result.add(OrderEntity(4, "Boris The Blade", "I'll be at the place from 5pm to 7 pm.\n Phone num: +79991561304"
                    , 55.813524, 49.133618, 55.801285, 48.976643))
            result.add(OrderEntity(5, "Name Surname", "Another order description"
                    , 55.813524, 49.133618, 55.801285, 48.976643))
            result.add(OrderEntity(6, "Name Surname", "Another order description"
                    , 55.813524, 49.133618, 55.801285, 48.976643))
            result.add(OrderEntity(7, "Name Surname", "Another order description"
                    , 55.813524, 49.133618, 55.801285, 48.976643))
            result.add(OrderEntity(8, "Name Surname", "Another order description"
                    , 55.813524, 49.133618, 55.801285, 48.976643))
            result.add(OrderEntity(9, "Name Surname", "Another order description"
                    , 55.813524, 49.133618, 55.801285, 48.976643))
            result.add(OrderEntity(10, "Name Surname", "Another order description"
                    , 55.813524, 49.133618, 55.801285, 48.976643))
            result.add(OrderEntity(11, "Name Surname", "Another order description"
                    , 55.813524, 49.133618, 55.801285, 48.976643))
            return result
        }

        private val emptyDescription: String = "Lorem ipsum dolor sit amet, " +
                "consectetur adipiscing elit, sed do eiusmod tempor incididunt " +
                "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis " +
                "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo " +
                "consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse " +
                "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat " +
                "non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    }


}