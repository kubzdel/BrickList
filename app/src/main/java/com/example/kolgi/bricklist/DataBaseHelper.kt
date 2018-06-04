package com.example.kolgi.bricklist

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException



class DataBaseHelper
/**
 * Constructor
 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
 * @param context
 */
(private val myContext: Context?) : SQLiteOpenHelper(myContext, DB_NAME, null, 1) {

    private var myDataBase: SQLiteDatabase? = null

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    @Throws(IOException::class)
    fun createDataBase() {

        val dbExist = checkDataBase()
        var db_read : SQLiteDatabase?=null

        if (dbExist) {
            //do nothing - database already exis
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            db_read = this.readableDatabase
            db_read.close()

            try {

                copyDataBase()

            } catch (e: IOException) {

                throw Error("Error copying database")

            }

        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private fun checkDataBase(): Boolean {

        var checkDB: SQLiteDatabase? = null

        try {
            val myPath = DB_PATH + DB_NAME
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)

        } catch (e: SQLiteException) {

            //database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close()

        }

        return if (checkDB != null) true else false
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    @Throws(IOException::class)
    private fun copyDataBase() {

        //Open your local db as the input stream
        val myInput = myContext!!.getAssets().open(DB_NAME)

        // Path to the just created empty db
        val outFileName = DB_PATH + DB_NAME

        //Open the empty db as the output stream
        val myOutput = FileOutputStream(outFileName)

        //transfer bytes from the inputfile to the outputfile
        val buffer = ByteArray(1024)
        var length: Int
        length = myInput.read(buffer)
        while (length  > 0) {
            myOutput.write(buffer, 0, length)
            length = myInput.read(buffer)
        }

        //Close the streams
        myOutput.flush()
        myOutput.close()
        myInput.close()

    }

    @Throws(SQLException::class)
    fun openDataBase() {

        //Open the database
        val myPath = DB_PATH + DB_NAME
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)

    }

    @Synchronized
    override fun close() {

        if (myDataBase != null)
            myDataBase!!.close()

        super.close()

    }

    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    fun addInventory(inventory: Inventory)
    {
        val values = ContentValues()
        values.put(COLUMN_INVENTORYNAME,inventory.name)
        values.put(COLUMN_INVENTORYACTIVE,inventory.active)
        values.put(COLUMN_INVENTORYACCESS,inventory.lastAccess)
        val db = this.writableDatabase
        db.insert(TABLE_INVENTORIES,null,values)
        db.close()
    }

    fun getLastEntryID(): Int{
        val query = "SELECT _id FROM Inventories WHERE  _id = (SELECT MAX(_id)  FROM Inventories)"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        var id = 0
        if(cursor.moveToFirst()){
            id = Integer.parseInt(cursor.getString(0))
            cursor.close()
        }
        return id
    }

    fun getItemTypeID(code: String): Int{
        val query = "SELECT _id FROM ItemTypes WHERE  Code='$code'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        var id = 0
        if(cursor.moveToFirst()){
            id = Integer.parseInt(cursor.getString(0))
            cursor.close()
        }
        return id
    }

    fun getItemID(code: String): Int{
        val query = "SELECT _id FROM Parts WHERE  Code='$code'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        var id = 0
        if(cursor.moveToFirst()){
            id = Integer.parseInt(cursor.getString(0))
            cursor.close()
        }
        return id
    }

    fun getItemCode(id: Int): String{
        val query = "SELECT Code FROM Parts WHERE  _id=$id"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        var code = ""
        if(cursor.moveToFirst()){
            code = cursor.getString(0)
            cursor.close()
        }
        return code
    }


    fun getColorID(code: String): Int{
        val query = "SELECT _id FROM Colors WHERE  Code=$code"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        var id = 0
        if(cursor.moveToFirst()){
            id = cursor.getInt(0)
            cursor.close()
        }
        return id
    }

    fun getColorCode(id:Int):Int{
        val query = "SELECT Code FROM Colors WHERE  _id=$id"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        var code = 0
        if(cursor.moveToFirst()){
            code = cursor.getInt(0)
            cursor.close()
        }
        return code
    }

    fun getColorName(id:Int):String{
        val query = "SELECT Name FROM Colors WHERE  _id=$id"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        var name = ""
        if(cursor.moveToFirst()){
            name = cursor.getString(0)
            cursor.close()
        }
        return name
    }

    fun getItemTypeName(id:Int):String{
        val query = "SELECT Name FROM ItemTypes WHERE  _id=$id"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        var name = ""
        if(cursor.moveToFirst()){
            name = cursor.getString(0)
            cursor.close()
        }
        return name
    }

    fun getCategoryName(id:Int):String{
        val query = "SELECT Name FROM Categories WHERE  _id=$id"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        var name = ""
        if(cursor.moveToFirst()){
            name = cursor.getString(0)
            cursor.close()
        }
        return name
    }




    fun insertInventoryPart(part: InventoryPart){
        val values = ContentValues()
        values.put(COLUMN_PARTINVID,part.inventoryID)
        values.put(COLUMN_PARTITEMID,part.itemID)
        values.put(COLUMN_PARTTYPEID,part.typeID)
        values.put(COLUMN_PARTCOLORID,part.colorID)
        values.put(COLUMN_EXTRAS,part.extra)
        values.put(COLUMN_QUANTITYSET,part.quantityInSet)
        values.put(COLUMN_QUANTITYSTORED,part.quantityInStore)
        val db = this.writableDatabase
        db.insert(TABLE_INVENTORYPARTS,null,values)
        db.close()
    }

    fun insertImage(code:Int,img:ByteArray?){
        val values = ContentValues()
        values.put("Image",img)
        val db = this.writableDatabase
        val strFilter = "Code=$code"
        db.update("Codes",values,strFilter,null)
        db.close()
    }

    fun getImage(code:Int): ByteArray?{
        var img : ByteArray? = null
        val query = "SELECT Image FROM Codes WHERE  Code=$code"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            img = cursor.getBlob(0)
            cursor.close()
        }
        if(img==null)return null
        return img
    }

    fun getInventories() : MutableList<Inventory>{
        var inventories = mutableListOf<Inventory>()
        val query = "SELECT * FROM Inventories WHERE Active=1"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        while(cursor.moveToNext()){
            val name  = cursor.getString(1)
            val access = cursor.getLong(3)
            val id = cursor.getInt(0)
            val active = cursor.getInt(2)
            val inv  = Inventory(id,name,active,access)
            inventories.add(inv)
        }
        cursor.close()
        return inventories
    }

    fun increaseQuantity(id:Int,currentQuantity:Int){
        val values = ContentValues()
        values.put("QuantityInStore",currentQuantity+1)
        val db = this.writableDatabase
        val strFilter = "_id=$id"
        db.update("InventoriesParts",values,strFilter,null)
        db.close()
    }

    fun decreaseQuantity(id:Int,currentQuantity:Int){
        val values = ContentValues()
        values.put("QuantityInStore",currentQuantity-1)
        val db = this.writableDatabase
        val strFilter = "_id=$id"
        db.update("InventoriesParts",values,strFilter,null)
        db.close()
    }

    fun getInvetoryParts(invID : Int) : MutableList<InventoryPart>{
        var parts = mutableListOf<InventoryPart>()
        val query = "SELECT * FROM InventoriesParts WHERE InventoryID=$invID"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        while(cursor.moveToNext()){
            val id = cursor.getInt(0)
            val inventoryID = cursor.getInt(1)
            val typeID = cursor.getInt(2)
            val itemID = cursor.getInt(3)
            val qInSet = cursor.getInt(4)
            val qInStore = cursor.getInt(5)
            val colorID = cursor.getInt(6)
            val extras = cursor.getInt(7)
            val part = InventoryPart(id,inventoryID,typeID,itemID,qInSet,qInStore,colorID,extras)
            parts.add(part)
        }
        cursor.close()
        return parts
    }

    fun getImageCode(itemID:Int,colorID:Int):Int{
        val query = "SELECT Code FROM Codes WHERE ItemID=$itemID AND ColorID=$colorID"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        var code = 0
        if(cursor.moveToFirst()){
            code =cursor.getInt(0)
            cursor.close()
        }
        return code
    }

    fun getPartName(partID:Int):String{
        val query = "SELECT Name FROM Parts WHERE _id=$partID"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        var name = ""
        if(cursor.moveToFirst()){
            name =cursor.getString(0)
            cursor.close()
        }
        return name
    }


    companion object {

        //The Android's default system path of your application database.
        private val DB_PATH = "/data/data/com.example.kolgi.bricklist/databases/"

        private val DB_NAME = "BrickList.db"
        val COLUMN_ID = "_id"
        val COLUMN_INVENTORYNAME = "Name"
        val COLUMN_INVENTORYACTIVE ="Active"
        val COLUMN_INVENTORYACCESS = "LastAccessed"
        val TABLE_INVENTORIES = "Inventories"
        val TABLE_INVENTORYPARTS = "InventoriesParts"

        val COLUMN_PARTINVID = "InventoryID"
        val COLUMN_PARTTYPEID = "TypeID"
        val COLUMN_PARTITEMID = "ItemID"
        val COLUMN_PARTCOLORID = "ColorID"
        val COLUMN_QUANTITYSET = "QuantityInSet"
        val COLUMN_QUANTITYSTORED = "QuantityInStore"
        val COLUMN_EXTRAS = "Extra"
    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

}