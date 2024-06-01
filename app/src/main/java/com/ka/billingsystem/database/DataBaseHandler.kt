package com.ka.billingsystem.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import java.util.Calendar
import java.util.Random

val DATABASENAME = "BILLING_SYSTEM"
val TABLENAME1 = "Stock"
val COL_PRODUCT_NAME = "Product_Name"
val COL_PRODUCT_ID = "Product_Id"
val COl_QUANTITY = "quantity"
val COL_COST = "cost"

val TABLENAME2 = "Transation"
val COL_BILL_NO = "Bill_No"
val COL_FILE_PATH = "file_Path"
val COL_SALES_USER = "sales_user"
val COL_PRODUCT_ID2 = "Product_Id"
val COl_QUANTITY2 = "quantity"
val COL_RATE = "rate"
val COL_AMOUNT = "amount"
val COL_TIMESTAMP = "time"
val CoL_TOTAL_AMOUNT = "tamount"
val Col_Image_printer = "printer_img"

val TABLENAME3 = "customer"
val COL_CUSID = "cus_id"
val COL_CUSNAME = "cus_name"
val COl_CUSPHONE = "cus_Phone"

val TABLENAME4 = "user"
val COL_LOGIN="firstLogin"
val COL_ID = "id"
val COL_USER_NAME = "user_name"
val COL_USER_id = "user_id"
val COL_PASS = "password"
val COL_TIMESTAMP_CREATE = "created_date"
val COL_TIMESTAMP_MODIFIE = "modified_date"
val COL_SIGN = "signature"
val COL_IGST = "igst"
val COL_CGST = "cgst"
val COL_SGST = "sgst"
val COL_TIMESTAMP_LOGOUT = "Last_Logout"

val TABLENAME5 = "Deleted"


val TABLENAME6 = "estimation"
val COL_EstimationId = "estimation_id"
val COL_ESCUSNAME = "cus_name"

val PROFORMA_TABLENAME1 = "proforma_Stock"
val PROFORMA_COL_PRODUCT_NAME = "proforma_Product_Name"
val PROFORMA_COL_PRODUCT_ID = "proforma_Product_Id"
val PROFORMA_COl_QUANTITY = "proforma_quantity"
val PROFORMA_COL_COST = "proforma_cost"

val PROFORMA_TABLENAME2 = "proforma_Transation"
val PROFORMA_COL_BILL_NO = "proforma_Bill_No"
val PROFORMA_COL_FILE_PATH = "proforma_file_Path"
val PROFORMA_COL_SALES_USER = "proforma_sales_user"
val PROFORMA_COL_PRODUCT_ID2 = "proforma_Product_Id"
val PROFORMA_COl_QUANTITY2 = "proforma_quantity"
val PROFORMA_COL_RATE = "proforma_rate"
val PROFORMA_COL_AMOUNT = "proforma_amount"
val PROFORMA_COL_TIMESTAMP = "proforma_time"
val PROFORMA_CoL_TOTAL_AMOUNT = "proforma_tamount"
val PROFORMA_Col_Image_printer = "proforma_printer_img"

val PROFORMA_TABLENAME3 = "proforma_customer"
val PROFORMA_COL_CUSID = "proforma_cus_id"
val PROFORMA_COL_CUSNAME = "proforma_cus_name"
val PROFORMA_COl_CUSPHONE = "proforma_cus_Phone"

val PROFORMA_TABLENAME4 = "proforma_user"
val PROFORMA_COL_ID = "proforma_id"
val PROFORMA_COL_USER_NAME = "proforma_user_name"
val PROFORMA_COL_USER_id = "proforma_user_id"
val PROFORMA_COL_PASS = "proforma_password"
val PROFORMA_COL_TIMESTAMP_CREATE = "proforma_created_date"
val PROFORMA_COL_TIMESTAMP_MODIFIE = "proforma_modified_date"
val PROFORMA_COL_SIGN = "proforma_signature"
val PROFORMA_COL_IGST = "proforma_igst"
val PROFORMA_COL_CGST = "proforma_cgst"
val PROFORMA_COL_SGST = "proforma_sgst"
val PROFORMA_COL_TIMESTAMP_LOGOUT = "proforma_Last_Logout"

val PROFORMA_TABLENAME5 = "proforma_Deleted"


val PROFORMA_TABLENAME6 = "proforma_estimation"
val PROFORMA_COL_EstimationId = "proforma_estimation_id"
val PROFORMA_COL_ESCUSNAME = "proforma_cus_name"

private const val DATABASE_NAME = "BILLING_SYSTEM.db"
private const val DATABASE_VERSION = 1


class DataBaseHandler(var context: Context) : SQLiteOpenHelper(
    context, DATABASENAME, null,
    3
) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE " + TABLENAME1 + " (" + COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + COL_PRODUCT_NAME + " VARCHAR(1000)," + COl_QUANTITY + " INTEGER," + COL_COST + " INTEGER)"
       db?.execSQL(createTable)

        val proformaCreateTable =
            "CREATE TABLE " + PROFORMA_TABLENAME1 + " (" + PROFORMA_COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + PROFORMA_COL_PRODUCT_NAME + " VARCHAR(1000)," + PROFORMA_COl_QUANTITY + " INTEGER," + PROFORMA_COL_COST + " INTEGER)"
        db?.execSQL(proformaCreateTable)

        val createTable2 =
            "CREATE TABLE " + TABLENAME2 + " (" + COL_CUSID + " INTEGER ," + COL_BILL_NO + " INTEGER ," + COL_PRODUCT_NAME + " VARCHAR(1000)," + COl_QUANTITY2 + " INTEGER," + COL_RATE + " INTEGER," + COL_AMOUNT + " INTEGER," + CoL_TOTAL_AMOUNT + " INTEGER," + COL_TIMESTAMP + " LONG," + COL_SALES_USER + " VARCHAR(1000)," + COL_FILE_PATH + " VARCHAR(1000)," + COL_SIGN + " VARCHAR(1000)," + Col_Image_printer + " VARCHAR(1000)," + COL_IGST + " VARCHAR(1000)," + COL_SGST + " VARCHAR(1000)," + COL_CGST + " VARCHAR(1000),FOREIGN KEY(cus_id) REFERENCES customer(cus_id))"

        db?.execSQL(createTable2)

        val proformaCreateTable2 =
            "CREATE TABLE " + PROFORMA_TABLENAME2 + " (" + PROFORMA_COL_CUSID + " INTEGER ," + PROFORMA_COL_BILL_NO + " INTEGER ," + PROFORMA_COL_PRODUCT_NAME + " VARCHAR(1000)," + PROFORMA_COl_QUANTITY2 + " INTEGER," + PROFORMA_COL_RATE + " INTEGER," + PROFORMA_COL_AMOUNT + " INTEGER," + PROFORMA_CoL_TOTAL_AMOUNT + " INTEGER," + PROFORMA_COL_TIMESTAMP + " LONG," + PROFORMA_COL_SALES_USER + " VARCHAR(1000)," + PROFORMA_COL_FILE_PATH + " VARCHAR(1000)," + PROFORMA_COL_SIGN + " VARCHAR(1000)," + PROFORMA_Col_Image_printer + " VARCHAR(1000)," + PROFORMA_COL_IGST + " VARCHAR(1000)," + PROFORMA_COL_SGST + " VARCHAR(1000)," + PROFORMA_COL_CGST + " VARCHAR(1000),FOREIGN KEY(proforma_cus_id) REFERENCES proforma_customer(proforma_cus_id))"

        db?.execSQL(proformaCreateTable2)

        val createTable3 =
            "CREATE TABLE " + TABLENAME3 + " (" + COL_CUSID + " INTEGER UNIQUE," + COL_CUSNAME + " VARCHAR(1000)," + COl_CUSPHONE + " INTEGER)"

        db?.execSQL(createTable3)

        val proformaCreateTable3 =
            "CREATE TABLE " + PROFORMA_TABLENAME3 + " (" + PROFORMA_COL_CUSID + " INTEGER UNIQUE," + PROFORMA_COL_CUSNAME + " VARCHAR(1000)," + PROFORMA_COl_CUSPHONE + " INTEGER)"

        db?.execSQL(proformaCreateTable3)

        val createTable4 =
            "CREATE TABLE " + TABLENAME4 + " ( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + COL_USER_id + " VARCHAR(1000) UNIQUE," + COL_USER_NAME + " VARCHAR(1000) ," + COL_PASS + " VARCHAR(1000)," + COL_TIMESTAMP_CREATE + " LONG," + COL_TIMESTAMP_MODIFIE + " LONG," + COL_TIMESTAMP_LOGOUT + " LONG," + COL_SIGN + " VARCHAR(1000)," + COL_IGST + " VARCHAR(1000)," + COL_SGST + " VARCHAR(1000)," + COL_CGST + " VARCHAR(1000)," + COL_LOGIN + " BOOLEAN)"
        db?.execSQL(createTable4)

        val proformaCreateTable4 =
            "CREATE TABLE " + PROFORMA_TABLENAME4 + " ( " + PROFORMA_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + PROFORMA_COL_USER_id + " VARCHAR(1000) UNIQUE," + PROFORMA_COL_USER_NAME + " VARCHAR(1000) ," + PROFORMA_COL_PASS + " VARCHAR(1000)," + PROFORMA_COL_TIMESTAMP_CREATE + " LONG," + PROFORMA_COL_TIMESTAMP_MODIFIE + " LONG," + PROFORMA_COL_TIMESTAMP_LOGOUT + " LONG," + PROFORMA_COL_SIGN + " VARCHAR(1000)," + PROFORMA_COL_IGST + " VARCHAR(1000)," + PROFORMA_COL_SGST + " VARCHAR(1000)," + PROFORMA_COL_CGST + " VARCHAR(1000))"
        db?.execSQL(proformaCreateTable4)

        val createTable5 =
            "CREATE TABLE " + TABLENAME5 + " (" + COL_CUSID + " INTEGER ," + COL_BILL_NO + " INTEGER," + COL_PRODUCT_NAME + " VARCHAR(1000)," + COl_QUANTITY2 + " INTEGER," + COL_RATE + " INTEGER," + COL_AMOUNT + " INTEGER," + CoL_TOTAL_AMOUNT + " INTEGER," + COL_TIMESTAMP + " LONG," + COL_SALES_USER + " VARCHAR(1000)," + COL_FILE_PATH + " VARCHAR(1000)," + COL_SIGN + " VARCHAR(1000)," + Col_Image_printer + " VARCHAR(1000)," + COL_IGST + " Long," + COL_SGST + " VARCHAR(1000)," + COL_CGST + " VARCHAR(1000),FOREIGN KEY(cus_id) REFERENCES customer(cus_id))"
        db?.execSQL(createTable5)

        val proformaCreateTable5 =
            "CREATE TABLE " + PROFORMA_TABLENAME5 + " (" + PROFORMA_COL_CUSID + " INTEGER ," + PROFORMA_COL_BILL_NO + " INTEGER," + PROFORMA_COL_PRODUCT_NAME + " VARCHAR(1000)," + PROFORMA_COl_QUANTITY2 + " INTEGER," + PROFORMA_COL_RATE + " INTEGER," + PROFORMA_COL_AMOUNT + " INTEGER," + PROFORMA_CoL_TOTAL_AMOUNT + " INTEGER," + PROFORMA_COL_TIMESTAMP + " LONG," + PROFORMA_COL_SALES_USER + " VARCHAR(1000)," + PROFORMA_COL_FILE_PATH + " VARCHAR(1000)," + PROFORMA_COL_SIGN + " VARCHAR(1000)," + PROFORMA_Col_Image_printer + " VARCHAR(1000)," + PROFORMA_COL_IGST + " Long," + PROFORMA_COL_SGST + " VARCHAR(1000)," + PROFORMA_COL_CGST + " VARCHAR(1000),FOREIGN KEY(proforma_cus_id) REFERENCES proforma_customer(proforma_cus_id))"
        db?.execSQL(proformaCreateTable5)

        val createTable6 =
            "CREATE TABLE " + TABLENAME6 + " (" + COL_EstimationId + " INTEGER PRIMARY KEY AUTOINCREMENT  ,"  +  COL_ESCUSNAME + " VARCHAR(1000)," + COL_AMOUNT + " INTEGER )"
        db?.execSQL(createTable6)

        val proformaCreateTable6 =
            "CREATE TABLE " + PROFORMA_TABLENAME6 + " (" + PROFORMA_COL_EstimationId + " INTEGER PRIMARY KEY AUTOINCREMENT  ,"  +  PROFORMA_COL_ESCUSNAME + " VARCHAR(1000)," + PROFORMA_COL_AMOUNT + " INTEGER )"
        db?.execSQL(proformaCreateTable6)


        this.userData(db, "Admin", "admin", "admin", null);
        //  this.UserData(db, "ARUN","a", "a",null);

        if (db != null) {
            // this.generateRandomData(db)
        };
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= 28) {
            if (db != null) {
             db.disableWriteAheadLogging()
            };
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }

    fun getValue1(query: String?, params: Array<String?>?): Cursor? {
        val db = readableDatabase
        return db.rawQuery(query, params)
    }


    fun getValue(query: String): Cursor? {
        val db = this.writableDatabase

        return db.rawQuery(query, null);

    }


    fun ADD_Sgin(Sgin: String) {

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COL_SIGN, Sgin)
        db.update(TABLENAME4, values, "$COL_ID=?", arrayOf<String>("1"))
    }

    fun UpdateLogin() {

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COL_LOGIN,false)
        db.update(TABLENAME4, values, "$COL_ID=?", arrayOf<String>("1"))
    }


    fun Add_GST(Gst: String) {

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COL_IGST, Gst)
        db.update(TABLENAME4, values, "$COL_ID=?", arrayOf<String>("1"))
    }

    fun ADD_CGST_SGST(CGST: String, SGST: String) {

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COL_CGST, CGST)
        values.put(COL_SGST, SGST)
        db.update(TABLENAME4, values, "$COL_ID=?", arrayOf<String>("1"))
    }


    fun insertdataToTrancation(
        Cus_Id: Int,
        Bill_id: Int,
        Product_Name: String,
        quantity: String,
        rate: String,
        amount: Float,
        tamount: Float,
        time: Long,
        UserName: String,
    ) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_CUSID, Cus_Id)
        contentValues.put(COL_BILL_NO, Bill_id)
        contentValues.put(COL_PRODUCT_NAME, Product_Name)
        contentValues.put(COl_QUANTITY2, quantity)
        contentValues.put(COL_RATE, rate)
        contentValues.put(COL_AMOUNT, amount)
        contentValues.put(CoL_TOTAL_AMOUNT, tamount)
        contentValues.put(COL_TIMESTAMP, time)

        contentValues.put(COL_SALES_USER, UserName)


        database.insert(TABLENAME2, null, contentValues)

    }

    fun proformaInsertdataToTrancation(
        Cus_Id: Int,
        Bill_id: Int,
        Product_Name: String,
        quantity: String,
        rate: String,
        amount: Float,
        tamount: Float,
        time: Long,
        UserName: String,
    ) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(PROFORMA_COL_CUSID, Cus_Id)
        contentValues.put(PROFORMA_COL_BILL_NO, Bill_id)
        contentValues.put(PROFORMA_COL_PRODUCT_NAME, Product_Name)
        contentValues.put(PROFORMA_COl_QUANTITY2, quantity)
        contentValues.put(PROFORMA_COL_RATE, rate)
        contentValues.put(PROFORMA_COL_AMOUNT, amount)
        contentValues.put(PROFORMA_CoL_TOTAL_AMOUNT, tamount)
        contentValues.put(PROFORMA_COL_TIMESTAMP, time)

        contentValues.put(PROFORMA_COL_SALES_USER, UserName)


        database.insert(PROFORMA_TABLENAME2, null, contentValues)

    }
    fun insertdataToCustomer(Cus_Id: Int, Cus_name: String, Phoneno: String) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_CUSID, Cus_Id)
        contentValues.put(COL_CUSNAME, Cus_name)
        contentValues.put(COl_CUSPHONE, Phoneno)


        database.insert(TABLENAME3, null, contentValues)
    }

    fun profomainsertdataToCustomer(Cus_Id: Int, Cus_name: String, Phoneno: String) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(PROFORMA_COL_CUSID, Cus_Id)
        contentValues.put(PROFORMA_COL_CUSNAME, Cus_name)
        contentValues.put(PROFORMA_COl_CUSPHONE, Phoneno)


        database.insert(PROFORMA_TABLENAME3, null, contentValues)
    }
    fun insertDataToEstimation(escusName: String, amount: Int) {
        val contentValues = ContentValues()
        contentValues.put(COL_ESCUSNAME, escusName)
        contentValues.put(COL_AMOUNT, amount)

        val db = this.writableDatabase
        db.insert(TABLENAME6, null, contentValues)
        db.close()
    }



    fun insertdataToUser(
        username: String,
        userid: String,
        pass: String,
        cDate: Long,
        mDate: Long,
    ) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_USER_NAME, username)
        contentValues.put(COL_USER_id, userid)
        contentValues.put(COL_PASS, pass)
        contentValues.put(COL_TIMESTAMP_CREATE, cDate)
        contentValues.put(COL_TIMESTAMP_MODIFIE, mDate)

        database.insert(TABLENAME4, null, contentValues)
    }

    fun userData(
        db: SQLiteDatabase?,
        username: String?,
        userid: String?,
        pass: String?,
        sign: String?,
    ) {
        val values = ContentValues()
        values.put(COL_USER_NAME, username)
        values.put(COL_USER_id, userid)
        values.put(COL_PASS, pass)
        values.put(COL_SIGN, sign)
        values.put(COL_TIMESTAMP_LOGOUT, 0)
        values.put(COL_IGST, 18)
        values.put(COL_CGST, 9)
        values.put(COL_SGST, 9)
        values.put(COL_LOGIN,true)
        db?.insert(TABLENAME4, null, values)

    }

    fun filePath(Bill_No: Int, Path: String, Sgin: String, IGST: String, CGST: String, SGST: String) {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_SIGN, Sgin)
        values.put(COL_FILE_PATH, Path)
        values.put(COL_IGST, IGST)
        values.put(COL_CGST, CGST)
        values.put(COL_SGST, SGST)
        db.update(TABLENAME2, values, "$COL_BILL_NO=?", arrayOf(Bill_No.toString()))
        db.beginTransaction()
        try {
            val rowsAffected = db.update(TABLENAME2, values, "$COL_BILL_NO=?", arrayOf(Bill_No.toString()))
            if (rowsAffected == 0) {
                Log.e("DatabaseUpdate", "Update failed, no rows affected.")
            } else {
                Log.i("DatabaseUpdate", "Successfully updated $rowsAffected rows.")
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("DatabaseUpdate", "Error updating record: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }

    fun proformafilePath(Bill_No: Int, Path: String, Sgin: String, IGST: Long, CGST: Long, SGST: Long) {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(PROFORMA_COL_SIGN, Sgin)
        values.put(PROFORMA_COL_FILE_PATH, Path)
        values.put(PROFORMA_COL_IGST, IGST)
        values.put(PROFORMA_COL_CGST, CGST)
        values.put(PROFORMA_COL_SGST, SGST)
        val rowsAffected = db.update(PROFORMA_TABLENAME2, values, "proforma_Bill_No=?", arrayOf(Bill_No.toString()))

        // Log the result
        if (rowsAffected > 0) {
            println("added")
        } else {
            println("not added")
        }
    }
    fun proformafilePath(Bill_No: Int, Path: String) {

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(PROFORMA_COL_FILE_PATH, Path)

        val rowsAffected = db.update(PROFORMA_TABLENAME2, values, "proforma_Bill_No=?", arrayOf(Bill_No.toString()))

        // Log the result

    }
    fun printerImage(Bill_No: Int, image: String) {

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(Col_Image_printer, image)
        db.update(TABLENAME2, values, "Bill_No=?", arrayOf<String>(Bill_No.toString()))
    }
    fun proformaprinterImage(Bill_No: Int, image: String) {

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(PROFORMA_Col_Image_printer, image)
        db.update(PROFORMA_TABLENAME2, values, "proforma_Bill_No=?", arrayOf<String>(Bill_No.toString()))
    }

    fun printerImageDEl(Bill_No: Int, image: String) {

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(Col_Image_printer, image)
        db.update(TABLENAME5, values, "Bill_No=?", arrayOf<String>(Bill_No.toString()))
    }


    private fun generateRandomData(db: SQLiteDatabase) {
        // Generate random data for Stock table
        for (i in 1..1000) {
            val productValues = ContentValues()
            productValues.put(COL_PRODUCT_NAME, "Product $i")
            productValues.put(COl_QUANTITY, Random().nextInt(100))
            productValues.put(COL_COST, Random().nextInt(100))
            db.insert(TABLENAME1, null, productValues)
        }

        val generatedDates = HashSet<Long>()
        for (i in 1..1000) {
            val transactionValues = ContentValues()
            transactionValues.put(COL_CUSID, i)
            transactionValues.put(COL_BILL_NO, i)
            transactionValues.put(COL_PRODUCT_NAME, "Product $i")
            transactionValues.put(COl_QUANTITY2, Random().nextInt(100))
            transactionValues.put(COL_RATE, Random().nextInt(100))
            transactionValues.put(COL_AMOUNT, Random().nextInt(100))
            transactionValues.put(CoL_TOTAL_AMOUNT, Random().nextInt(1000))

            val calendar = Calendar.getInstance()
            val year = 2018 + Random().nextInt(6) // Generate a random year between 2018 and 2023
            val month = Random().nextInt(12) // Generate a random month
            val day =
                1 + Random().nextInt(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) // Generate a random day
            calendar.set(year, month, day)

            var randomTimestamp = calendar.timeInMillis
            if (randomTimestamp > System.currentTimeMillis()) {
                randomTimestamp = System.currentTimeMillis()
            }

            var attempts = 0
            while (generatedDates.contains(randomTimestamp) && attempts < 10) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                randomTimestamp = calendar.timeInMillis
                attempts++
            }
            generatedDates.add(randomTimestamp)

            transactionValues.put(COL_TIMESTAMP, randomTimestamp)
            transactionValues.put(COL_SALES_USER, "SalesUser $i")
            transactionValues.put(COL_FILE_PATH, "/storage/emulated/0/DATA/Invoice$i.pdf")
            db.insert(TABLENAME2, null, transactionValues)
        }


        // Generate random data for Customer table
        for (i in 1..1000) {
            val customerValues = ContentValues()
            customerValues.put(COL_CUSID, i)
            customerValues.put(COL_CUSNAME, "Customer $i")
            customerValues.put(COl_CUSPHONE, "123456789$i")
            db.insert(TABLENAME3, null, customerValues)
        }

        // Generate random data for User table
        for (i in 1..10) {
            val userValues = ContentValues()
            userValues.put(COL_USER_NAME, "User Name$i")
            userValues.put(COL_USER_id, "User$i")
            userValues.put(COL_PASS, "password$i")
            userValues.put(COL_TIMESTAMP_CREATE, System.currentTimeMillis())
            userValues.put(COL_TIMESTAMP_MODIFIE, System.currentTimeMillis())
            db.insert(TABLENAME4, null, userValues)
        }
    }

    fun forgotPasswordChange(Userid: String, Password: String) {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_PASS, Password)
        db.update(TABLENAME4, values, "user_id=?", arrayOf<String>(Userid))
    }

    @SuppressLint("Range")
    fun moveDataFromTable2ToTable5(billNo: String) {
        val db = this.writableDatabase
        val moveDataQuery =
            "INSERT INTO " + TABLENAME5 + " SELECT * FROM " + TABLENAME2 + " WHERE " + COL_BILL_NO + " = " + billNo

        db.execSQL(moveDataQuery)

        val deleteDataQuery =
            "UPDATE " + TABLENAME2 + " SET " + COL_CUSID + " = NULL, " + COL_PRODUCT_NAME + " = NULL, " + COl_QUANTITY2 + " = NULL, " + COL_RATE + " = NULL, " + COL_AMOUNT + " = NULL, " + CoL_TOTAL_AMOUNT + " = NULL, " + COL_TIMESTAMP + " = NULL, " + COL_SALES_USER + " = NULL, " + COL_SIGN + " = NULL, " + Col_Image_printer + " = NULL, " + COL_FILE_PATH + " = NULL, " + COL_IGST + " = NULL WHERE " + COL_BILL_NO + " = " + billNo

        db.execSQL(deleteDataQuery)
    }
    @SuppressLint("Range")
    fun proformamoveDataFromTable2ToTable5(billNo: String) {
        val db = this.writableDatabase
        val moveDataQuery =
            "INSERT INTO " + PROFORMA_TABLENAME5 + " SELECT * FROM " + PROFORMA_TABLENAME2 + " WHERE " + PROFORMA_COL_BILL_NO + " = " + billNo

        db.execSQL(moveDataQuery)

        val deleteDataQuery =
            "UPDATE " + PROFORMA_TABLENAME2 + " SET " + PROFORMA_COL_CUSID + " = NULL, " + PROFORMA_COL_PRODUCT_NAME + " = NULL, " + PROFORMA_COl_QUANTITY2 + " = NULL, " + PROFORMA_COL_RATE + " = NULL, " + PROFORMA_COL_AMOUNT + " = NULL, " + PROFORMA_CoL_TOTAL_AMOUNT + " = NULL, " + PROFORMA_COL_TIMESTAMP + " = NULL, " + PROFORMA_COL_SALES_USER + " = NULL, " + PROFORMA_COL_SIGN + " = NULL, " + PROFORMA_Col_Image_printer + " = NULL, " + PROFORMA_COL_FILE_PATH + " = NULL, " + PROFORMA_COL_IGST + " = NULL WHERE " + PROFORMA_COL_BILL_NO + " = " + billNo

        db.execSQL(deleteDataQuery)
    }

    fun undoMoveDataFromTable2ToTable5(billNo: String) {
        val db = this.writableDatabase
        val deleteDataQuery1 =
            "DELETE FROM " + TABLENAME2 + " WHERE " + COL_BILL_NO + " = " + billNo
        db.execSQL(deleteDataQuery1)

        // Move data back to TABLENAME2
        val undoMoveDataQuery =
            "INSERT INTO " + TABLENAME2 + " SELECT * FROM " + TABLENAME5 + " WHERE " + COL_BILL_NO + " = " + billNo
        db.execSQL(undoMoveDataQuery)

        // Delete data from TABLENAME5
        val deleteDataQuery = "DELETE FROM " + TABLENAME5 + " WHERE " + COL_BILL_NO + " = " + billNo
        db.execSQL(deleteDataQuery)
    }
    fun proformaundoMoveDataFromTable2ToTable5(billNo: String) {
        val db = this.writableDatabase
        val deleteDataQuery1 =
            "DELETE FROM " + PROFORMA_TABLENAME2 + " WHERE " + PROFORMA_COL_BILL_NO + " = " + billNo
        db.execSQL(deleteDataQuery1)

        // Move data back to TABLENAME2
        val undoMoveDataQuery =
            "INSERT INTO " + PROFORMA_TABLENAME2 + " SELECT * FROM " + PROFORMA_TABLENAME5 + " WHERE " + PROFORMA_COL_BILL_NO + " = " + billNo
        db.execSQL(undoMoveDataQuery)

        // Delete data from TABLENAME5
        val deleteDataQuery = "DELETE FROM " + PROFORMA_TABLENAME5 + " WHERE " + PROFORMA_COL_BILL_NO + " = " + billNo
        db.execSQL(deleteDataQuery)
    }

    fun permanentDelete(billNo: String) {
        val db = this.writableDatabase
        val tableName = "Deleted"  // Replace with your actual table name
        val whereClause = "$COL_BILL_NO = ?"
        val whereArgs = arrayOf(billNo)
        db?.delete(tableName, whereClause, whereArgs)
    }
    fun proforma_permanentDelete(billNo: String) {
        val db = this.writableDatabase
        val tableName = "proforma_Deleted"  // Replace with your actual table name
        val whereClause = "$PROFORMA_COL_BILL_NO = ?"
        val whereArgs = arrayOf(billNo)
        db?.delete(tableName, whereClause, whereArgs)
    }

    fun permanentDeleteTrancation(billNo: String) {
        val db = this.writableDatabase
         // Replace with your actual table name
        val whereClause = "$COL_BILL_NO = ?"
        val whereArgs = arrayOf(billNo)
        db?.delete(TABLENAME2, whereClause, whereArgs)
    }
    fun profomapermanentDeleteTrancation(billNo: String) {
        val db = this.writableDatabase
        // Replace with your actual table name
        val whereClause = "$PROFORMA_COL_BILL_NO = ?"
        val whereArgs = arrayOf(billNo)
        db?.delete(PROFORMA_TABLENAME2, whereClause, whereArgs)
    }

    fun permanentCusDetails(billNo: String) {
        val db = this.writableDatabase
        // Replace with your actual table name
        val whereClause = "$COL_CUSID = ?"
        val whereArgs = arrayOf(billNo)
        db?.delete(TABLENAME3, whereClause, whereArgs)
    }
    fun profomapermanentCusDetails(billNo: String) {
        val db = this.writableDatabase
        // Replace with your actual table name
        val whereClause = "$PROFORMA_COL_CUSID = ?"
        val whereArgs = arrayOf(billNo)
        db?.delete(PROFORMA_TABLENAME3, whereClause, whereArgs)
    }


    fun lastLogout(userid: String?, time: Long) {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_TIMESTAMP_LOGOUT, time)
        db.update(TABLENAME4, values, "user_id=?", arrayOf<String>(userid.toString()))
    }


}