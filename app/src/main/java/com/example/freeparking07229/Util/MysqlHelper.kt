package com.example.freeparking07229.Util

import android.util.Log
import com.example.freeparking07229.Model.ParkingCard
import com.example.freeparking07229.Model.ParkingLot
import com.example.freeparking07229.Model.ParkingSpace
import com.example.freeparking07229.Model.UsingSpace
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

    suspend fun LoginIdentity(username: String, password: String): Int = withContext(Dispatchers.IO) {
        val connection = establishConnection()
        val sql = "SELECT * FROM user WHERE account=? AND password=?" //查询语句
        var result = 0

        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, username)
            preparedStatement.setString(2, password)
            val resultSet: ResultSet = preparedStatement.executeQuery()
            if(resultSet.next())
                result=resultSet.getInt("identity")

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

    suspend fun getCardIDInfoByAccount(account:String):String = withContext(Dispatchers.IO){
        val connection=establishConnection()
        val sql ="SELECT * FROM parkingcard WHERE account = ? "
        var item=ParkingCard()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, account)
            val rs:ResultSet=preparedStatement.executeQuery()

            if(rs.next()){
                item.parking_id=rs.getString("parking_id")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            connection.close()
        }
        item.parking_id
    }

    suspend fun getNameInfoByAccount(account:String):String = withContext(Dispatchers.IO){
        val connection=establishConnection()
        val sql ="SELECT * FROM parkingcard WHERE account = ? "
        var item=ParkingCard()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, account)
            val rs:ResultSet=preparedStatement.executeQuery()

            if(rs.next()){
                item.name=rs.getString("name")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            connection.close()
        }
        item.name
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
                item.admin=rs.getString("admin")
                Log.d("getParkLotInfoByName","成功获取到Item"+item.toString())
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            connection.close()
        }
        item
    }

    suspend fun getParkLotInfoByAdmin(admin:String):ParkingLot = withContext(Dispatchers.IO){
        val connection=establishConnection()
        val sql ="SELECT * FROM parkinglot WHERE admin = ? "
        var item=ParkingLot()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, admin)
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
                item.admin=rs.getString("admin")
                Log.d("getParkLotInfoByName","成功获取到Item"+item.toString())
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            connection.close()
        }
        item
    }
    suspend fun getParkSpaceInfoByName(parkingLotName:String):ArrayList<ParkingSpace> = withContext(Dispatchers.IO){
        val connection=establishConnection()
        val sql ="SELECT * FROM parkingspace WHERE parking_lot = ? "
        val list =ArrayList<ParkingSpace>()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, parkingLotName)
            val rs:ResultSet=preparedStatement.executeQuery()

            while(rs.next()){
                var item=ParkingSpace()
                item.parking_lot = rs.getString("parking_lot")
                item.space_id=rs.getInt("space_id")
                item.state=rs.getInt("state")
                list.add(item)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            connection.close()
        }
        list
    }
    suspend fun getUsingSpaceInfoByLotAndSpace(parkingLotName:String , spaceId:Int):UsingSpace = withContext(Dispatchers.IO){
        val connection=establishConnection()
        val sql ="SELECT * FROM usingspace WHERE parking_lot = ? AND space_id = ?"
        var item = UsingSpace()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, parkingLotName)
            preparedStatement.setInt(2, spaceId)
            val rs:ResultSet=preparedStatement.executeQuery()
            if(rs.next()){
                item.parking_lot = rs.getString("parking_lot")
                item.space_id=rs.getInt("space_id")
                item.parking_id=rs.getString("parking_id")
                item.car_number=rs.getString("car_number")
                item.is_reserved=rs.getInt("is_reserved")
                item.lock_time=rs.getTimestamp("lock_time")
                item.time=rs.getString("time")
                item.cost=rs.getBigDecimal("cost").toDouble()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            connection.close()
        }
        item
    }
    suspend fun getParkCardInfoByAccount(account:String):ParkingCard = withContext(Dispatchers.IO){
        val connection=establishConnection()
        val sql ="SELECT * FROM parkingcard WHERE account = ? "
        var item =ParkingCard()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, account)
            val rs:ResultSet=preparedStatement.executeQuery()

            if(rs.next()){
                item.account= rs.getString("account")
                item.parking_id = rs.getString("parking_id")
                item.name = rs.getString("name")
                item.sex = rs.getString("sex")
                item.id_number = rs.getString("id_number")
                item.car_number = rs.getString("car_number")
                item.person_picture = rs.getString("person_picture")
                item.phone_number = rs.getString("phone_number")
            }
            Log.d("Mysqlhelper","成功获取用户卡"+item.toString())
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            connection.close()
        }
        item
    }

    suspend fun InsertUsingSpace(usingSpace: UsingSpace){
        withContext(Dispatchers.IO){
            val connection=establishConnection()
            val sql ="INSERT INTO usingspace (parking_lot,space_id,parking_id,car_number,is_reserved,lock_time,time,cost)" +
                    " VALUES(?,?,?,?,?,?,?,?)"
            try {
                val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
                preparedStatement.setString(1, usingSpace.parking_lot)
                preparedStatement.setInt(2, usingSpace.space_id)
                preparedStatement.setString(3, usingSpace.parking_id)
                preparedStatement.setString(4, usingSpace.car_number)
                preparedStatement.setInt(5, usingSpace.is_reserved)
                if(usingSpace.is_reserved==1){
                    preparedStatement.setString(6, null)
                    preparedStatement.setString(7, null)
                    preparedStatement.setBigDecimal(8, null)
                }else{
                    preparedStatement.setTimestamp(6, usingSpace.lock_time)
                    preparedStatement.setString(7, usingSpace.time)
                    preparedStatement.setBigDecimal(8, usingSpace.cost.toBigDecimal())
                }

                preparedStatement.executeUpdate()
                Log.d("Mysqlhelper","成功插入信息"+usingSpace.toString())
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                connection.close()
            }
        }
    }

    suspend fun UpdateParkingSpace(parkingSpace: ParkingSpace){
        withContext(Dispatchers.IO){
            val connection=establishConnection()
            val sql ="UPDATE parkingspace SET state = 1 WHERE parking_lot = ? AND space_id = ?"
            try {
                val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
                preparedStatement.setString(1, parkingSpace.parking_lot)
                preparedStatement.setInt(2, parkingSpace.space_id)

                preparedStatement.executeUpdate()
                Log.d("Mysqlhelper","成功更改信息")
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                connection.close()
            }
        }
    }

}