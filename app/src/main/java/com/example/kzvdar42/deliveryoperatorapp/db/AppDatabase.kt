package com.example.kzvdar42.deliveryoperatorapp.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*
import java.util.concurrent.Executors


@Database(entities = [OrderEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun orderDao(): OrderDao

    companion object { //TODO: redo to this
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app.db").build()
    }

//    companion object {
//        private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            if (INSTANCE == null) {
//                INSTANCE = Room
//                        .inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java)
//                        .addCallback(object : Callback() {
//                            override fun onCreate(db: SupportSQLiteDatabase) {
//                                super.onCreate(db)
//                                Executors.newSingleThreadScheduledExecutor().execute { getDatabase(context).orderDao().insertAll(populateData()) }
//                            }
//                        })
//                        .build()
//            }
//            return INSTANCE!!
//        }

//        private fun populateData(): List<OrderEntity> {
//            val result = LinkedList<OrderEntity>()
//            result.add(OrderEntity(1, "John", "Dorian", "+79991560413", "Turk", "Turklton", "", "+78005550055"
//                    , 1.2, 0.2, 0.3, 0.5, true, 30.0, "$", "", "Approved", "{{\"latitude\" : \"55.7476907\", \"longitude\" : \"48.7433593\"} , {\"latitude\" : \"55.7867635\", \"longitude\" : \"49.1216088\"}}", "1 day", "The parcel is near the door, you can take it anytime."))
//
//            result.add(OrderEntity(2, "Turk", "Turklton", "+15555550000", "John", "Dorian", "", "+79964008291"
//                    , 3.0, 1.2, 0.7, 1.5, false, 120.0, "$", "", "Accepted", "{{\"latitude\" : \"55.7476907\", \"longitude\" : \"48.7433593\"} , {\"latitude\" : \"38.9098\", \"longitude\" : \"-77.0295\"}}", "3 days", "Second order description.\n Impossible route"))
//
//            result.add(OrderEntity(3, "David", "Hasselhov", "89174942647", "Christopher", "Turk", "Duncan", "89178234323"
//                    , 3.0, 1.2, 0.7, 1.5, true, 525.0, "$", "", "Accepted", "{{\"latitude\" : \"55.7867635\", \"longitude\" : \"49.1216088\"} , {\"latitude\" : \"55.7476907\", \"longitude\" : \"48.7433593\"}}", "3 day", emptyDescription))
//
//            result.add(OrderEntity(4, "Elliot", "Reid", "+79991561304", "John", "Dorian", "", "+79993452555"
//                    , 3.0, 1.2, 0.7, 1.5, true, 32.0, "$", "", "Accepted", "{{\"latitude\" : \"55.813524\", \"longitude\" : \"49.133618\"} , {\"latitude\" : \"55.801285\", \"longitude\" : \"48.976643\"}}", "1 day", "I'll be at the place from 5pm to 7 pm."))
//
//            result.add(OrderEntity(5, "Robert", "Kelso", "88005553535", "Perry", "Cox", "", "+79991561304"
//                    , 3.0, 1.2, 0.7, 1.5, false, 20.0, "$", "", "Accepted", "{{\"latitude\" : \"55.752847\", \"longitude\" : \"48.744952\"} , {\"latitude\" : \"55.747026\", \"longitude\" : \"48.744436\"}}", "20 minutes adventure", "Available during all day"))
//
//            result.add(OrderEntity(6, "Name", "Surname", "+1234567891", "Name", "Surname", "ThirdName", "+10987654321"
//                    , 3.0, 1.2, 0.7, 1.5, false, 30.0, "\u20BD", "", "Approved", "{{\"latitude\" : \"55.813524\", \"longitude\" : \"49.133618\"} , {\"latitude\" : \"55.801285\", \"longitude\" : \"48.976643\"}}", "5 days", "Another order notes"))
//            result.add(OrderEntity(7, "Name", "Surname", "+1234567891", "Name", "Surname", "ThirdName", "+10987654321"
//                    , 3.0, 1.2, 0.7, 1.5, true, 300.0, "\u20BD", "", "Approved", "{{\"latitude\" : \"55.813524\", \"longitude\" : \"49.133618\"} , {\"latitude\" : \"55.801285\", \"longitude\" : \"48.976643\"}}", "5 days", "Another order notes"))
//            result.add(OrderEntity(8, "Name", "Surname", "+1234567891", "Name", "Surname", "ThirdName", "+10987654321"
//                    , 3.0, 1.2, 0.7, 1.5, false, 49.5, "\u20BD", "", "Accepted", "{{\"latitude\" : \"55.813524\", \"longitude\" : \"49.133618\"} , {\"latitude\" : \"55.801285\", \"longitude\" : \"48.976643\"}}", "5 days", "Another order notes"))
//            result.add(OrderEntity(9, "Name", "Surname", "+1234567891", "Name", "Surname", "ThirdName", "+10987654321"
//                    , 3.0, 1.2, 0.7, 1.5, true, 240.0, "\u20BD", "", "Approved", "{{\"latitude\" : \"55.813524\", \"longitude\" : \"49.133618\"} , {\"latitude\" : \"55.801285\", \"longitude\" : \"48.976643\"}}", "5 days", "Another order notes"))
//            result.add(OrderEntity(10, "Name", "Surname", "+1234567891", "Name", "Surname", "ThirdName", "+10987654321"
//                    , 3.0, 1.2, 0.7, 1.5, false, 57.0, "\u20BD", "", "Accepted", "{{\"latitude\" : \"55.813524\", \"longitude\" : \"49.133618\"} , {\"latitude\" : \"55.801285\", \"longitude\" : \"48.976643\"}}", "5 days", "Another order notes"))
//            return result
//        }
//
//        private const val emptyDescription: String = "Lorem ipsum dolor sit amet, " +
//                "consectetur adipiscing elit, sed do eiusmod tempor incididunt " +
//                "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis " +
//                "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo " +
//                "consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse " +
//                "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat " +
//                "non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
//    }


}