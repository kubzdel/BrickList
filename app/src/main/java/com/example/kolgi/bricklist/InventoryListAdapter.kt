package com.example.kolgi.bricklist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class InventoryListAdapter : ArrayAdapter<Inventory> {


    var retView: View? = null
    var mContext : Context? = null
    var mResource : Int = 0


    constructor(context: Context?, resource: Int, objects: MutableList<Inventory>?) : super(context, resource, objects){
        mContext = context
        mResource=resource

    }

    fun toSimpleString(date: Date) : String {
        val format = SimpleDateFormat("yyyy.MM.dd' at' HH:mm:ss")
        return format.format(date)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: Inventory
        var retView: View
        val name = getItem(position).name
        var access = getItem(position).lastAccess
        val active = getItem(position).active
        val id = getItem(position).id

        holder = Inventory(id, name, active, access)
        if (convertView == null) {
            val inflater = LayoutInflater.from(mContext)
            retView = inflater.inflate(mResource, null)

            val nameText = retView.findViewById<TextView>(R.id.textView1)
            val accessText = retView.findViewById<TextView>(R.id.textView2)
            nameText.setText(name)
            accessText.setText(toSimpleString(Date(access)))
            retView.tag = holder
        } else {
            holder = convertView.tag as Inventory
            retView = convertView
            val nameText = retView.findViewById<TextView>(R.id.textView1)
            val accessText = retView.findViewById<TextView>(R.id.textView2)
            nameText.setText(name)

            accessText.setText(toSimpleString(Date(access)))
        }
        return retView
    }
}
