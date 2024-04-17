package com.example.freeparking07229.Model

import java.io.Serializable
import java.sql.Time
import java.sql.Timestamp

data class UserParkingRecord(var person:String="",
                             var parking_lot:String="",
                             var space_id:Int=0,
                             var enter_time:Timestamp= Timestamp.valueOf("2024-01-01 12:00:00"),
                             var leave_time:Timestamp=Timestamp.valueOf("2024-01-01 12:00:00"),
                             var time:String="",
                             var cost:Double=0.0,
                             var lock_time:Timestamp=Timestamp.valueOf("2024-01-01 12:00:00"),
                             var unlock_time:Timestamp=Timestamp.valueOf("2024-01-01 12:00:00")):Serializable
