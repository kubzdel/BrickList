package com.example.kolgi.bricklist

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    val ADD_PROJECT_CODE = 100

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

        var inventories = mutableListOf<Inventory>()
        inventories = DBHelper.getInventories()
        var inventoryAdapter = InventoryListAdapter(this,R.layout.adapter_view_layout,inventories)
        listView.adapter = inventoryAdapter

    }

    fun addProject(v: View)
    {
        val i = Intent(this,AddProject::class.java)
        startActivityForResult(i,ADD_PROJECT_CODE)
    }

    override fun finish() {
        val data = Intent()

    }
}
