package com.example.freeparking07229.Util

import android.util.Log
import com.example.freeparking07229.Model.ParkingLot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class MysqlHelper {
    private val dbUrl="jdbc:mysql://rm-cn-x0r3p3sos000q6ko.rwlb.rds.aliyuncs.com:3306/free_parking"
    private val username = "wang2462372905"
    private val password = "wanglinhaoWLH0"

    suspend fun establishConnection():Connection = withContext(Dispatchers.IO){
        Class.forName("com.mysql.jdbc.Driver")
        Log.d("DBHelper","连接成功")
        DriverManager.getConnection(dbUrl,username,password)

    }

    suspend fun insertUser(account:String,password:String,email:String,identity:Int){
        withContext(Dispatchers.IO){
            val connection=establishConnection()
            val sql ="INSERT INTO user (account, password, email, identity) VALUES (?, ?, ?, ?)"

            try{
                val preparedStatement:PreparedStatement = connection.prepareStatement(sql)
                preparedStatement.setString(1,account)
                preparedStatement.setString(2,password)
                preparedStatement.setString(3,email)
                preparedStatement.setInt(4,identity)
                preparedStatement.executeUpdate()
            }finally {
                connection.close()
            }
        }
    }

    suspend fun checkLogin(username: String, password: String): Boolean = withContext(Dispatchers.IO) {
            val connection = establishConnection()
            val sql = "SELECT * FROM user WHERE account=? AND password=?" //查询语句
            var result = false

            try {
                val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
                preparedStatement.setString(1, username)
                preparedStatement.setString(2, password)
                val resultSet: ResultSet = preparedStatement.executeQuery()
                result = resultSet.next()
            } finally {
                connection.close()
            }
        result
    }

    suspend fun getParkingList():ArrayList<ParkingLot> = withContext(Dispatchers.IO){
            val connection=establishConnection()
            val sql ="SELECT * FROM parkinglot"
            var list=ArrayList<ParkingLot>()
            try {
                val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
                val rs:ResultSet=preparedStatement.executeQuery()

                while(rs.next()){
                    var item:ParkingLot= ParkingLot()

                    item.longitude=rs.getDouble("longitude")
                    item.latitude=rs.getDouble("latitude")
                    item.location=rs.getString("location")
                    item.parking_name=rs.getString("parking_name")
                    item.description=rs.getString("description")
                    item.space_number=rs.getInt("space_number")
                    item.parking_picture=rs.getString("parking_picture")
                    item.space_available=rs.getInt("space_available")

                    list.add(item)
                    Log.d("MysqlHelper","add一次"+item.toString())
                }
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                connection.close()
            }
        list
        }

    suspend fun getParkLotInfoByName(parkingLotName:String):ParkingLot = withContext(Dispatchers.IO){
        val connection=establishConnection()
        val sql ="SELECT * FROM parkinglot WHERE parking_name = ? "
        var item=ParkingLot()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, parkingLotName)
            val rs:ResultSet=preparedStatement.executeQuery()

            if(rs.next()){
                item.longitude=rs.getDouble("longitude")
                item.latitude=rs.getDouble("latitude")
                item.location=rs.getString("location")
                item.parking_name=rs.getString("parking_name")
                item.description=rs.getString("description")
                item.space_number=rs.getInt("space_number")
                item.parking_picture=rs.getString("parking_picture")
                item.space_available=rs.getInt("space_available")
                Log.d("getParkLotInfoByName","成功获取到Item"+item.toString())
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            connection.close()
        }
        item
    }
}