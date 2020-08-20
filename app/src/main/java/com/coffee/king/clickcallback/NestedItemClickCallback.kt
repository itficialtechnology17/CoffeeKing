package com.coffee.king.clickcallback

import android.view.View

interface NestedItemClickCallback {

    fun onNestedItemClick(view: View, parentPosition: Int,childPosition: Int)

}