package com.example.freeparking07229.Model

import java.io.Serializable
import java.sql.Time
import java.sql.Timestamp

data class UsingSpace(var parking_lot:String="",
                      var space_id:Int=0,
                      var parking_id:String="",
                      var car_number:String="",
                      var lock_time:Timestamp=Timestamp.valueOf("2024-01-01 12:00:00"),
    var time:String="",
    var cost:Double=0.0):Serializable
