package com.example.rsparking.util

import androidx.room.TypeConverter

const val SEPARATOR= "_,_"
class Converter {


    @TypeConverter
    fun arrayToString(list: ArrayList<Float>?): String? {
        return if (list == null) null else {
            var string=""
            for (item in list) {
                var index= 0
                string += item.toString()
                index++
                if (index < (list.size - 1)) {
                    string += SEPARATOR
                }
            }
            return string
        }
    }

    @TypeConverter
    fun stringToArray(string: String?): ArrayList<Float>? {
        return if (string == null || string.isEmpty()) null else {
            val splitString= string.split(SEPARATOR)
            val arrayList= ArrayList<Float>()
            for (item in splitString) {
                arrayList.add(item.toFloat())
            }
            return arrayList
        }
    }
}