package com.example.freeparking07229.Model

import java.io.Serializable

data class ParkingLot(var longitude:Double=0.0,
                      var latitude:Double=0.0,
                      var location:String="",
                      var parking_name:String="",
                      var description:String="",
                      var space_number:Int=0,
                      var parking_picture:String="",
                      var space_available:Int=0,
                      var admin:String=""):Serializable
