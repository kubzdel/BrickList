package com.example.kolgi.bricklist

class InventoryPart:Comparable<InventoryPart> {
    var id: Int = 0
    var inventoryID: Int = 0
    var typeID: Int = 1
    var itemID: Int = 0
    var quantityInStore: Int = 0
    var quantityInSet: Int = 0
    var colorID: Int = 0
    var extra: Int = 0

    constructor(id: Int, inventoryID: Int, typeID: Int, itemID: Int,  quantityInSet: Int,quantityInStore: Int, colorID: Int, extra: Int) {
        this.id = id
        this.inventoryID = inventoryID
        this.typeID = typeID
        this.itemID = itemID
        this.quantityInSet = quantityInSet
        this.quantityInStore = quantityInStore
        this.colorID = colorID
        this.extra = extra

    }

    constructor(inventoryID: Int, typeID: Int, itemID: Int,  quantityInSet: Int,quantityInStore: Int, colorID: Int, extra: Int) {
        this.inventoryID = inventoryID
        this.typeID = typeID
        this.itemID = itemID
        this.quantityInSet = quantityInSet
        this.quantityInStore = quantityInStore
        this.colorID = colorID
        this.extra = extra
    }

    override fun compareTo(other: InventoryPart): Int {
        return (this.quantityInStore/this.quantityInSet).compareTo(other.quantityInStore/other.quantityInSet)
    }

}
