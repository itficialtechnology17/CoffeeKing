package com.coffee.king.clickcallback

import android.view.View

interface ItemClickCallback {
    fun onItemClick(view: View, position: Int)
}