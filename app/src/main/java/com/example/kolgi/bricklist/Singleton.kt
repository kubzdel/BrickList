package com.example.kolgi.bricklist

class Singleton// Exists only to defeat instantiation.
{
        companion object {
             var selected: Long = -1
             var  invs: MutableList<Int>   = mutableListOf<Int>()}
}