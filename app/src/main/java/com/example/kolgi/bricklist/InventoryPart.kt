package com.example.kolgi.bricklist

class InventoryPart {
    var id: Int = 0
    var inventoryID: Int? = null
    var typeID: Int = 1
    var itemID: Int = 0
    var quantityInStore: Int = 0
    var quantityInSet: Int = 0
    var colorID: Int = 0
    var extra: String? = null

    constructor(id: Int, inventoryID: Int?, typeID: Int, itemID: Int,  quantityInSet: Int, colorID: Int, extra: String) {
        this.id = id
        this.inventoryID = inventoryID
        this.typeID = typeID
        this.itemID = itemID
        this.quantityInSet = quantityInSet
        this.colorID = colorID
        this.extra = extra
    }

    constructor(inventoryID: Int?, typeID: Int, itemID: Int,  quantityInSet: Int, colorID: Int, extra: String) {
        this.inventoryID = inventoryID
        this.typeID = typeID
        this.itemID = itemID
        this.quantityInSet = quantityInSet
        this.colorID = colorID
        this.extra = extra
    }

}
