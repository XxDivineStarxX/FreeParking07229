package com.example.freeparking07229.Model

import java.io.Serializable
import java.sql.Time
import java.sql.Timestamp

data class EnterInfo(var parking_lot:String="",
                     var card_id:String="",
                     var car_number:String="",
                     var person:String="",
                     var phone_number:String="",
                     var enter_time:Timestamp=Timestamp.valueOf("2024-01-01 12:00:00")):Serializable
