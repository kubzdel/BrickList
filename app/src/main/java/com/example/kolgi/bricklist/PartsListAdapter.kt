package com.example.kolgi.bricklist

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import android.util.DisplayMetrics
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.LinearLayout
import android.graphics.drawable.PaintDrawable
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.view.MotionEvent




class PartsListAdapter : ArrayAdapter<InventoryPart> {


    val url1 = "https://www.lego.com/service/bricks/5/2/"
    val url2 = "http://img.bricklink.com/P/"
    val url3 = "https://www.bricklink.com/PL/"
    var retView: View? = null
    var mContext: Context? = null
    var mResource: Int = 0

    constructor(context: Context?, resource: Int, objects: MutableList<InventoryPart>?) : super(context, resource, objects) {
        mContext = context
        mResource = resource

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val DBHelper = DataBaseHelper(this.context)
        var holder: InventoryPart
        var retView: View
        val inventoryPart = getItem(position)
        var name :String
        var description : String
        var category : String
        if(inventoryPart.itemID!=0) {
             name = DBHelper.getItemCode(getItem(position).itemID)
            //  var image = getItem(position).
             description = DBHelper.getColorName(getItem(position).colorID) + " " +
                    DBHelper.getPartName(getItem(position).itemID)
             category = DBHelper.getItemTypeName(getItem(position).typeID)
            val qInStore = getItem(position).quantityInStore
            val qInSet = getItem(position).quantityInSet
            val id = getItem(position).id
        }
        else{
             name = "brak klocka"
            description = "  "
            category = ""
        }

        holder = InventoryPart(getItem(position).id,getItem(position).inventoryID,getItem(position).typeID,
                getItem(position).itemID,getItem(position).quantityInSet,getItem(position).quantityInStore,
                getItem(position).colorID,getItem(position).extra)
        if (convertView == null) {
            val inflater = LayoutInflater.from(mContext)
            retView = inflater.inflate(mResource, null)

            val nameText = retView.findViewById<TextView>(R.id.textViewName)
            val imageView = retView.findViewById<ImageView>(R.id.imageView)
            val qStoredView = retView.findViewById<TextView>(R.id.quantityStored)
            val qSetView = retView.findViewById<TextView>(R.id.quantitySet)
            val descView = retView.findViewById<TextView>(R.id.textViewDescription)
            val categoryView = retView.findViewById<TextView>(R.id.textViewCategory)
            val row = retView.findViewById<LinearLayout>(R.id.row)
            qStoredView.setText(inventoryPart.quantityInStore.toString())
            qSetView.setText(inventoryPart.quantityInSet.toString())
            if(inventoryPart.quantityInStore>=inventoryPart.quantityInSet)
                row.setBackgroundColor(Color.GREEN)
            else{
                row.setBackgroundColor(Color.TRANSPARENT)
            }

            if(getItemId(position)==Singleton.selected){
                row.setBackgroundColor(Color.YELLOW)
            }
            nameText.setText(name)
            descView.setText(description)
            descView.setGravity(Gravity.CENTER)
            descView.setMovementMethod(ScrollingMovementMethod())
            descView.scrollTo(0,0)
            descView.setOnTouchListener { v, event ->
                if(descView.lineCount>3){
                val action = event.action

                when (action) {
                    MotionEvent.ACTION_DOWN ->
                        // Disallow ScrollView to intercept touch events.
                        v.parent
                                .requestDisallowInterceptTouchEvent(true)
                    MotionEvent.ACTION_UP ->
                        // Allow ScrollView to intercept touch events.
                        v.parent
                                .requestDisallowInterceptTouchEvent(false)
                }}
               // descView.scrollTo(0,0)
                return@setOnTouchListener false
            }
            categoryView.setText(category)
            if(inventoryPart.itemID!=0){
            val image = getAndSaveImage(holder.itemID,holder.colorID,name)
            val bm = BitmapFactory.decodeByteArray(image, 0, image!!.size)
            val dm = DisplayMetrics()
            imageView.setMinimumHeight(dm.heightPixels)
            imageView.setMinimumWidth(dm.widthPixels)
            imageView.setImageBitmap(bm)}
            retView.tag = holder
        } else {
            holder = convertView.tag as InventoryPart
            retView = convertView
            val nameText = retView.findViewById<TextView>(R.id.textViewName)
            val imageView = retView.findViewById<ImageView>(R.id.imageView)
            val qStoredView = retView.findViewById<TextView>(R.id.quantityStored)
            val qSetView = retView.findViewById<TextView>(R.id.quantitySet)
            val descView = retView.findViewById<TextView>(R.id.textViewDescription)
            val categoryView = retView.findViewById<TextView>(R.id.textViewCategory)
            val row = retView.findViewById<LinearLayout>(R.id.row)
         //   row.setBackgroundColor(Color.argb(221,255,255,255))
            nameText.setText(name)
            qStoredView.setText(inventoryPart.quantityInStore.toString())
            qSetView.setText(inventoryPart.quantityInSet.toString())
            if(inventoryPart.quantityInStore>=inventoryPart.quantityInSet)
                row.setBackgroundColor(Color.GREEN)
            else{
                row.setBackgroundColor(Color.TRANSPARENT)
            }

            if(getItemId(position)==Singleton.selected){
                row.setBackgroundColor(Color.YELLOW)
            }
            descView.setText(description)
            descView.setGravity(Gravity.CENTER)
//            descView.setMovementMethod(ScrollingMovementMethod())
//            descView.setOnTouchListener { v, event ->
//                descView.setGravity(Gravity.CENTER)
//                //  if (descView.lineCount>3){
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return@setOnTouchListener false;
//            }
            descView.scrollTo(0,0)
            categoryView.setText(category)
            if(inventoryPart.itemID!=0){
                val image = getAndSaveImage(inventoryPart.itemID,inventoryPart.colorID,name)
                val bm = BitmapFactory.decodeByteArray(image, 0, image!!.size)
                val dm = DisplayMetrics()
                imageView.setMinimumHeight(dm.heightPixels)
                imageView.setMinimumWidth(dm.widthPixels)
                imageView.setImageBitmap(bm)}

        }
        return retView
    }

    fun getAndSaveImage(itemID: Int, colorID: Int, partCode: String):ByteArray? {
        val DBHelper = DataBaseHelper(mContext)
        var code = DBHelper.getImageCode(itemID, colorID)
        var photo = DBHelper.getImage(code)
        val colorCode = DBHelper.getColorCode(colorID)
        if(photo==null) {
            val thread = Thread(Runnable {
                try {
                    var imageURL = URL(url1 + code)
                    var connection = imageURL.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connect()
                    if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                        imageURL = URL(url2 + colorCode + '/' + partCode + ".gif")
                        connection = imageURL.openConnection() as HttpURLConnection
                        connection.connect()
                        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                            imageURL = URL(url3 + partCode + ".jpg")
                            connection = imageURL.openConnection() as HttpURLConnection
                            connection.connect()
                            if (connection.responseCode != HttpURLConnection.HTTP_OK)
                                    else{
                                code = itemID+ colorID*10
                                DBHelper.insertCode(itemID,colorID,code)
                            }
                        }
                        else{
                            code = itemID+ colorID*10
                            DBHelper.insertCode(itemID,colorID,code)
                        }
                    }

                    val inputStream = connection.inputStream
                    val bis = BufferedInputStream(inputStream)
                    var baf = ByteArrayOutputStream();
                    var current = 0;
                    val data = ByteArray(50)
                    current = bis.read(data)
                    while (current != -1) {
                        baf.write(data, 0, current)
                        current = bis.read(data)
                    }
                    photo = baf.toByteArray()
                    DBHelper.insertImage(code,photo)
                } catch (e: Exception) {
                    Log.d("ImageManager", "Error: " + e.toString());
                }
            })
            thread.start()
            thread.join()
        }
        return photo
    }
}



