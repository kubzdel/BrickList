package com.example.kolgi.bricklist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.AdapterView.OnItemClickListener



class MainActivity : AppCompatActivity() {

    val baseURL="http://fcds.cs.put.poznan.pl/MyWeb/BL/"
    val ADD_PROJECT_CODE = 100
    val SETTINGS_CODE = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        applicationContext
        val DBHelper = DataBaseHelper(applicationContext)
        try {

            DBHelper.createDataBase()

        } catch (ioe: IOException) {

            throw Error("Unable to create database")
        }
        displayInventories()


    }


    fun displayInventories(){
        val DBHelper = DataBaseHelper(applicationContext)
        var inventories = mutableListOf<Inventory>()
        inventories = DBHelper.getInventories()
        Collections.sort(inventories,Collections.reverseOrder())
        var inventoryAdapter = InventoryListAdapter(this,R.layout.adapter_view_layout,inventories)
        listView.adapter = inventoryAdapter
        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, EditProject::class.java)
            intent.putExtra(EXTRA_MESSAGE+'1', inventories.get(position).id)
            intent.putExtra(EXTRA_MESSAGE+'2',inventories.get(position).name)
            startActivity(intent)

        }
    }

    fun addProject(v: View)
    {
        val i = Intent(this,AddProject::class.java)
        startActivityForResult(i,ADD_PROJECT_CODE)
    }

    fun settings(v: View)
    {
        val i = Intent(this,Settings::class.java)
        i.putExtra(EXTRA_MESSAGE,baseURL)
        startActivityForResult(i,SETTINGS_CODE)
    }

    override fun finish() {
        val data = Intent()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        displayInventories()
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        displayInventories()
    }
}
