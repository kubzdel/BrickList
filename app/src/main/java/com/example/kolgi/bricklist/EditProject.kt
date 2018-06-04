package com.example.kolgi.bricklist

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.provider.ContactsContract
import android.view.View
import android.widget.AbsListView.CHOICE_MODE_SINGLE
import android.widget.AdapterView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_edit_project.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.inventory_view_layout.*

class EditProject : AppCompatActivity() {

    var invID = 0
    var title =  ""
    var selected : Int? = null
    var parts = mutableListOf<InventoryPart>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_project)
        val extras = intent.extras ?: return
        val message = extras.getInt(EXTRA_MESSAGE+'1')
        val title = extras.getString(EXTRA_MESSAGE+'2')
        invID = message
        this.title = title
        prepareList()
        displayParts()
    }

    private fun prepareList() {
        val titleView= textTitle.setText(title)
        val DBHelper = DataBaseHelper(applicationContext)
        parts = DBHelper.getInvetoryParts(invID)
        listViewParts.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            for (a in 0 until parent.childCount) {
                parent.getChildAt(a).setBackgroundColor(Color.TRANSPARENT)
            }
            if(selected!=position){
                view.setBackgroundColor(Color.YELLOW)
                selected =  position}
            else{
                selected = null
            }

        }

    }


    fun displayParts(){
     //   Collections.sort(inventories, Collections.reverseOrder())
        var partsAdapter = PartsListAdapter(this,R.layout.inventory_view_layout,parts)
        listViewParts.adapter = partsAdapter

    }

    fun increaseQuantity(v: View){
        if(selected!=null) {
            val DBHelper = DataBaseHelper(applicationContext)
            parts.get(selected!!).quantityInStore+=1
            val v = listViewParts.getChildAt(selected!!-listViewParts.firstVisiblePosition)
                    ?: return
            val someText = v.findViewById(R.id.quantityStored) as TextView
            var current = Integer.parseInt(someText.text as String?)
            someText.text =(current+1).toString()
            DBHelper.increaseQuantity(parts.get(selected!!).id,current)
        }
    }

    fun decreaseQuantity(v: View){
        if(selected!=null) {
            val DBHelper = DataBaseHelper(applicationContext)
            parts.get(selected!!).quantityInStore-=1
            val v = listViewParts.getChildAt(selected!!-listViewParts.firstVisiblePosition)
                    ?: return
            val someText = v.findViewById(R.id.quantityStored) as TextView
            var current = Integer.parseInt(someText.text as String?)
            someText.text =(current-1).toString()
            DBHelper.decreaseQuantity(parts.get(selected!!).id,current)
        }
    }
}
