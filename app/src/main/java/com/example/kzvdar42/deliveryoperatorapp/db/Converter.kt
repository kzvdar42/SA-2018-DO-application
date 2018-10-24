package com.example.kzvdar42.deliveryoperatorapp.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



class Converter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromCoordsList(list: ArrayList<CoordsEntity>): String {
            val gson = Gson()
            return gson.toJson(list)
        }

        @TypeConverter
        @JvmStatic
        fun toCoordsList(json: String): ArrayList<CoordsEntity> {

            val listType = object : TypeToken<ArrayList<CoordsEntity>>() {}.type
            return Gson().fromJson(json, listType)
        }
    }
}