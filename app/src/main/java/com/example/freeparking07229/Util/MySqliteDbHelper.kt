package com.example.freeparking07229.Util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class MySqliteDbHelper (val context: Context, name: String, version: Int) :
            SQLiteOpenHelper(context, name, null, version) {

    private val init ="drop table if exists Tempdata"

    private val createTable = "create table Tempdata (" +
            "account text," +
            "identity integer," +
            "card_id text)"
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(createTable)
        Toast.makeText(context, "Create succeeded", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}