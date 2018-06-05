package com.example.kolgi.bricklist

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_project.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import android.R.string.cancel
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputType
import android.webkit.URLUtil
import android.widget.EditText




class AddProject : AppCompatActivity() {

    var DBHelper :DataBaseHelper? = null
    private var DOWNLOAD_PATH = "/data/data/com.example.kolgi.bricklist/downloadedXML/"
    var baseURL="http://fcds.cs.put.poznan.pl/MyWeb/BL/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_project)
        val extras = intent.extras ?: return
        val url = extras.getString("URL")
        this.baseURL = url
        DBHelper = DataBaseHelper(applicationContext)
    }

    fun downloadXML() : Boolean
    {
        var success = true;
        val thread = Thread(Runnable {
            val xmlFolder = File(DOWNLOAD_PATH)
            if(!xmlFolder.exists()){
                xmlFolder.mkdir()
            }
            val filePath = DOWNLOAD_PATH+inventoryCode.text.toString()+".xml"
            var file =File(filePath)
           // if(!file.exists()) {
                var input: InputStream? = null
                var output: OutputStream? = null
                val stringURL: String = baseURL + inventoryCode.text.toString() + ".xml"
                if (URLUtil.isValidUrl(stringURL)) {
                    val url = URL(stringURL)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connectTimeout = 500
                    connection.connect()
                    println(baseURL + inventoryCode.text + ".xml")
                    println(url.toExternalForm())
                    val fileLength = connection.getContentLength()
                    // download the file
                    if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                        success = false
                        return@Runnable
                    }
                    input = connection.getInputStream()
                    output = FileOutputStream(filePath)
                    val data = ByteArray(4096)
                    var total: Long = 0
                    var count: Int
                    count = input.read(data)
                    while (count != -1) {
                        // allow canceling with back button
                        total += count.toLong()
                        // publishing the progress....
                        // only if total length is known
                        output.write(data, 0, count)
                        count = input.read(data)
                    }
                    output.close()
                    input.close()
                    connection.disconnect()
                }
                else{
                    success = false
                    return@Runnable
                }
           // }

        })
        thread.start()
        thread.join()
        return success

    }


    fun readXML(fileName:String)
    {
        val fXmlFile = File(fileName)
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(fXmlFile)
        doc.getDocumentElement().normalize()
        System.out.println("Root element :" + doc.getDocumentElement().getNodeName())

        val nList = doc.getElementsByTagName("ITEM")

        println("----------------------------")

        for (temp in 0 until nList.length) {

            val nNode = nList.item(temp)

            System.out.println("\nCurrent Element :" + nNode.nodeName)

            if (nNode.nodeType == Node.ELEMENT_NODE) {
                val eElement = nNode as Element
                if(eElement.getElementsByTagName("ALTERNATE").item(0).textContent=="N"){
                val DBHepler = DataBaseHelper(applicationContext)
                val inventoryID = DBHepler.getLastEntryID()
                val itemTypeID = DBHepler.getItemTypeID(eElement.getElementsByTagName("ITEMTYPE").item(0).textContent)
                val quantityNeeded = eElement.getElementsByTagName("QTY").item(0).textContent.toInt()
                val itemID = DBHepler.getItemID(eElement.getElementsByTagName("ITEMID").item(0).textContent)
                val colorID = DBHepler.getColorID(eElement.getElementsByTagName("COLOR").item(0).textContent)
                val extrasLetter = eElement.getElementsByTagName("EXTRA").item(0).textContent.toString()
                    var extras = 0
                    if(extrasLetter!="N") extras = 1
                val part = InventoryPart(inventoryID,itemTypeID,itemID,quantityNeeded,0,colorID,extras)
                DBHepler.insertInventoryPart(part)
                    DBHepler.close()
                }
            }
        }

    }

    fun addInventory(v:View)
    {
        try {
            var toast : Toast
            if(downloadXML()){
            }
            else{
                toast =  Toast.makeText(applicationContext,"Nie znaleziono projektu o podanej nazwie!",Toast.LENGTH_SHORT)
                toast.show()
                return
            }
            showNameInput()
        } catch (ioe: IOException) {

            throw Error("Unable to add item")

        }
    }

    fun showNameInput(){
        var name : String = "DefaultName"
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Wprowadź swoją nazwę projektu")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> name = input.text.toString()
            var inventory = Inventory(name,1,System.currentTimeMillis())
            DBHelper!!.addInventory(inventory)
            val filePath = DOWNLOAD_PATH+inventoryCode.text.toString()+".xml"
            readXML(filePath)
            DBHelper!!.close()
            val toast = Toast.makeText(applicationContext, "Dodano projekt o nazwie $name",Toast.LENGTH_SHORT)
            toast.show()
        })
        builder.show()
    }
}
