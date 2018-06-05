package com.example.kolgi.bricklist

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.AdapterView.OnItemClickListener
import android.widget.CheckBox
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_add_project.*
import kotlinx.android.synthetic.main.adapter_view_layout.*
import android.R.id.button1




class MainActivity : AppCompatActivity() {

    var baseURL="http://fcds.cs.put.poznan.pl/MyWeb/BL/"
    val ADD_PROJECT_CODE = 100
    val SETTINGS_CODE = 200
    var DISPLAY_ACTIVE_ONLY = false
    var inventories = mutableListOf<Inventory>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Lista projektów"
        setContentView(R.layout.activity_main)
        applicationContext
        val DBHelper = DataBaseHelper(applicationContext)
        try {

            DBHelper.createDataBase()

        } catch (ioe: IOException) {

            throw Error("Unable to create database")
        }
        Singleton.invs = DBHelper.getActiveIDs()
        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, EditProject::class.java)
            intent.putExtra(EXTRA_MESSAGE+'1', inventories.get(position).id)
            intent.putExtra(EXTRA_MESSAGE+'2',inventories.get(position).name)
            startActivity(intent)

        }
        displayInventories()


    }


    fun displayInventories(){
        val DBHelper = DataBaseHelper(applicationContext)
        inventories = if(DISPLAY_ACTIVE_ONLY) {
            DBHelper.getActiveInventories()
        }
        else DBHelper.getInventories()
        Collections.sort(inventories,Collections.reverseOrder())
        var inventoryAdapter = InventoryListAdapter(this,R.layout.adapter_view_layout,inventories,DISPLAY_ACTIVE_ONLY)
        listView.adapter = inventoryAdapter
    }

    fun addProject(v: View)
    {
        val i = Intent(this,AddProject::class.java)
        i.putExtra("URL",this.baseURL)
        startActivityForResult(i,ADD_PROJECT_CODE)
    }

    fun settings(v: View)
    {
        val i = Intent(this,Settings::class.java)
        i.putExtra(EXTRA_MESSAGE+1,baseURL)
        i.putExtra(EXTRA_MESSAGE+2,DISPLAY_ACTIVE_ONLY)

        startActivityForResult(i,SETTINGS_CODE)
    }

    override fun finish() {
        val data = Intent()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode== Activity.RESULT_OK){
            if(data!=null){
                this.DISPLAY_ACTIVE_ONLY = data.extras.getBoolean("active_only")
                this.baseURL = data.extras.getString("URL")
            }
        }
        displayInventories()
        super.onActivityResult(requestCode, resultCode, data)
    }
//    override fun onActivityReenter(resultCode: Int, data: Intent?) {
//        if(resultCode== Activity.RESULT_OK){
//            if(data!=null){
//                this.DISPLAY_ACTIVE_ONLY = data.extras.getBoolean("active_only")
//                this.baseURL = data.extras.getString("URL")
//            }
//        }
//        displayInventories()
//        super.onActivityReenter(resultCode, data)
//
//    }

    override fun onResume() {
        val DBHelper = DataBaseHelper(applicationContext)
        Singleton.invs = DBHelper.getActiveIDs()
        displayInventories()
        super.onResume()
    }
}
