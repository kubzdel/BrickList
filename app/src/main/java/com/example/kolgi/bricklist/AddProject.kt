package com.example.kolgi.bricklist

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_project.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import java.lang.reflect.Array.getLength





class AddProject : AppCompatActivity() {

    val PATHNAME = "/downloadedXML/"
    private val DOWNLOAD_PATH = "/data/data/com.example.kolgi.bricklist/downloadedXML/"
    val baseURL="http://fcds.cs.put.poznan.pl/MyWeb/BL/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_project)
        val extras = intent.extras ?: return
        baseURL


    }

    fun downloadXML()
    {
        val thread = Thread(Runnable {
            val filePath = DOWNLOAD_PATH+inventoryCode.text.toString()+".xml"
            var file =File(filePath)
            if(!file.exists()) {
            var input: InputStream? = null
            var output: OutputStream? = null
            val url = URL(baseURL + inventoryCode.text.toString()+".xml")
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()
            println(baseURL + inventoryCode.text+".xml")
            println(url.toExternalForm())
            val fileLength = connection.getContentLength()
            // download the file

            input = connection.getInputStream()
            output = FileOutputStream(filePath)
            val data = ByteArray(4096)
            var total: Long = 0
            var count: Int
            count = input.read(data)
            while (count  != -1) {
                // allow canceling with back button
                total += count.toLong()
                // publishing the progress....
                // only if total length is known
                output.write(data, 0, count)
                count = input.read(data)
            }
            output.close()
            input.close()
            connection.disconnect()}
            readXML(filePath)
        })
        thread.start()



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
                val extras = eElement.getElementsByTagName("EXTRA").item(0).textContent
                val part = InventoryPart(inventoryID,itemTypeID,itemID,quantityNeeded,colorID,extras)
                DBHepler.insertInventoryPart(part)
                }
            }
        }

    }

    fun addInventory(v:View)
    {
        val context:Context
        context = this
        val DBHelper = DataBaseHelper(applicationContext)
        try {
            var inventory = Inventory(inventoryCode.text.toString(),1,System.currentTimeMillis())
            DBHelper.addInventory(inventory)
            downloadXML()
            val toast = Toast.makeText(applicationContext,"Dodano projekt o nazwie "+inventoryCode.text.toString(),Toast.LENGTH_SHORT)
            toast.show()

        } catch (ioe: IOException) {

            throw Error("Unable to add item")

        }
    }
}
