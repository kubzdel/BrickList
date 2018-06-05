package com.example.kolgi.bricklist

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit_project.*
import kotlinx.android.synthetic.main.inventory_view_layout.view.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class EditProject : AppCompatActivity() {

    val XML_PATH = "/data/data/com.example.kolgi.bricklist/"
    var invID = 0
    var title =  ""
    var selected : Int? = null
    var partsAdapter :PartsListAdapter? = null
    var parts = mutableListOf<InventoryPart>()
    var DBHelper :DataBaseHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_project)
        DBHelper =  DataBaseHelper(applicationContext)
        val extras = intent.extras ?: return
        val message = extras.getInt(EXTRA_MESSAGE+'1')
        val title = extras.getString(EXTRA_MESSAGE+'2')
        invID = message
        DBHelper!!.updateLastAccess(invID,System.currentTimeMillis())
        this.title = title
        prepareList()
        displayParts()
    }

    private fun prepareList() {
        textTitle.setText(title)
        parts = DBHelper!!.getInvetoryParts(invID)
        listViewParts.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if(selected!=position){
                selected =  position
                Singleton.selected = position.toLong()
                partsAdapter!!.notifyDataSetChanged()
            }
            else{
                selected = null
                Singleton.selected = -1
                partsAdapter!!.notifyDataSetChanged()
            }
        }
    }

    fun displayParts(){
        Collections.sort(parts)
        partsAdapter = PartsListAdapter(this,R.layout.inventory_view_layout,parts)
        listViewParts.adapter = partsAdapter

    }

    fun increaseQuantity(v: View){
        if(selected!=null) {
            parts.get(selected!!).quantityInStore+=1
            val v = listViewParts.getChildAt(selected!!-listViewParts.firstVisiblePosition)
                    ?: return
            val qSet = v.findViewById(R.id.quantityStored) as TextView
            var current = Integer.parseInt(qSet.text as String?)
            qSet.text =(current+1).toString()
            DBHelper!!.increaseQuantity(parts.get(selected!!).id,current)
        }
    }

    fun decreaseQuantity(v: View){
        if(selected!=null) {
            parts.get(selected!!).quantityInStore-=1
            val v = listViewParts.getChildAt(selected!!-listViewParts.firstVisiblePosition)
                    ?: return
            val qSet = v.findViewById(R.id.quantityStored) as TextView
            var current = Integer.parseInt(qSet.text as String?)
            qSet.text =(current-1).toString()
            DBHelper!!.decreaseQuantity(parts.get(selected!!).id,current)
        }
    }

    override fun finish() {
        Singleton.selected = -1
        DBHelper!!.updateLastAccess(invID,System.currentTimeMillis())
        super.finish()
    }
    fun exportInsufficientParts(v: View) {
        val invName = DBHelper!!.getInventoryName(this.invID)
        try{
            val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc: Document = docBuilder.newDocument()
            val rootElement: Element = doc.createElement("INVENTORY")
            for (part in parts) {
                if ((part.quantityInStore < part.quantityInSet) && part.itemID!=0) {
                    val item: Element = doc.createElement("ITEM")
                    val itemType: Element = doc.createElement("ITEMTYPE")
                    itemType.appendChild(doc.createTextNode(DBHelper!!.getItemTypeCode(part.typeID)))
                    item.appendChild(itemType)

                    val itemID: Element = doc.createElement("ITEMID")
                    itemID.appendChild(doc.createTextNode(DBHelper!!.getItemCode(part.itemID)))
                    item.appendChild(itemID)

                    val color: Element = doc.createElement("COLOR")
                    color.appendChild(doc.createTextNode(DBHelper!!.getColorCode(part.colorID).toString()))
                    item.appendChild(color)

                    val qtyFilled: Element = doc.createElement("QTYFILLED")
                    qtyFilled.appendChild(doc.createTextNode((part.quantityInSet - part.quantityInStore).toString()))
                    item.appendChild(qtyFilled)
                    rootElement.appendChild(item)
                }
            }
            doc.appendChild(rootElement)
            val transformer: Transformer = TransformerFactory.newInstance().newTransformer()

            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

            val path = this.XML_PATH
            val outDir = File(path, "MissingParts")
            outDir.mkdir()
            val file = File(outDir, "$invName.xml")

            transformer.transform(DOMSource(doc), StreamResult(file))
        val toast = Toast.makeText(applicationContext, "Pomyślnie wyeksportowano plik xml o nazwie $invName", Toast.LENGTH_SHORT)
        toast.show()}
        catch(e:Exception){
            val toast = Toast.makeText(applicationContext,e.message, Toast.LENGTH_SHORT)
            toast.show()
        }
        }

}
