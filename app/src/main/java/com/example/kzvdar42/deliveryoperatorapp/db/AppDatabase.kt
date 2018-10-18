package com.example.kzvdar42.deliveryoperatorapp.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*
import java.util.concurrent.Executors


@Database(entities = [OrderEntity::class], version = 2, exportSchema = false)
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
            result.add(OrderEntity(1,true, "Vlad Kuleykin", "The parcel is near the door, you can take it anytime.", "+79173452517"
                    ,1.2,0.2,0.3,0.5,"1 day", 55.7476907, 48.7433593, 55.7867635, 49.1216088))

            result.add(OrderEntity(2,false, "Turk Turklton", "Second order description.\n Impossible route", "+15555550000"
                    ,3.0,1.2,0.7,1.5,"infinite", 55.7476907, 48.7433593, 38.9098, -77.0295))

            result.add(OrderEntity(3,false, "Christopher Duncan Turk", "$emptyDescription$emptyDescription$emptyDescription", "+79173452517"
                    ,3.0,1.2,0.7,1.5,"3 day", 55.7867635, 49.1216088, 55.7476907, 48.7433593))

            result.add(OrderEntity(4,false, "Boris The Blade", "I'll be at the place from 5pm to 7 pm.", "+79991561304"
                    ,3.0,1.2,0.7,1.5,"1 day", 55.813524, 49.133618, 55.801285, 48.976643))

            result.add(OrderEntity(5,false, "Name Surname", "Available during all day", "+79991561304"
                    ,3.0,1.2,0.7,1.5,"20 minutes adventure",55.752847, 48.744952,55.747026, 48.744436))

            result.add(OrderEntity(6,true, "Name Surname", "Another order description", "+79991561304"
                    ,3.0,1.2,0.7,1.5,"1 day", 55.813524, 49.133618, 55.801285, 48.976643))
            result.add(OrderEntity(7,true, "Name Surname", "Another order description", "+79991561304"
                    ,3.0,1.2,0.7,1.5,"1 day", 55.813524, 49.133618, 55.801285, 48.976643))
            result.add(OrderEntity(8,true, "Name Surname", "Another order description", "+79991561304"
                    ,3.0,1.2,0.7,1.5,"1 day", 55.813524, 49.133618, 55.801285, 48.976643))
            result.add(OrderEntity(9,false, "Name Surname", "Another order description", "+79991561304"
                    ,3.0,1.2,0.7,1.5,"1 day", 55.813524, 49.133618, 55.801285, 48.976643))
            result.add(OrderEntity(10,true, "Name Surname", "Another order description", "+79991561304"
                    ,3.0,1.2,0.7,1.5,"1 day", 55.813524, 49.133618, 55.801285, 48.976643))
            result.add(OrderEntity(11,false, "Name Surname", "Another order description", "+79991561304"
                    ,3.0,1.2,0.7,1.5,"1 day", 55.813524, 49.133618, 55.801285, 48.976643))
            return result
        }

        private const val emptyDescription: String = "Lorem ipsum dolor sit amet, " +
                "consectetur adipiscing elit, sed do eiusmod tempor incididunt " +
                "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis " +
                "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo " +
                "consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse " +
                "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat " +
                "non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    }


}