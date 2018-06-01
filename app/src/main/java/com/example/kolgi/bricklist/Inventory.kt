package com.example.kolgi.bricklist

class Inventory {
    var id: Int = 0
    var name: String = ""
    var active: Int = 1
    var lastAccess: Long = 0
    constructor(id: Int,name: String,active:Int,lastAccess:Long)
    {
        this.id=id
        this.name=name
        this.active=active
        this.lastAccess=lastAccess
    }

    constructor(name: String,active:Int,lastAccess:Long)
    {
        this.name=name
        this.active=active
        this.lastAccess=lastAccess
    }
}