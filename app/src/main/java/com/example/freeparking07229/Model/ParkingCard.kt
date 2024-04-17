package com.example.freeparking07229.Model

import java.io.Serializable

data class ParkingCard(var parking_id:String="",
                       var name:String="",
                       var sex:String="",
                       var id_number:String="",
                       var car_number:String="",
                       var person_picture:String="",
                       var phone_number:String=""):Serializable
