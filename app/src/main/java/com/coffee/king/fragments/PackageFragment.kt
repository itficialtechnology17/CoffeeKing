package com.coffee.king.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.coffee.king.R
import com.coffee.king.adapter.CategoryAdapter
import com.coffee.king.clickcallback.NestedItemClickCallback
import com.coffee.king.model.ModelEventByDate
import com.coffee.king.model.ModelPackage
import kotlinx.android.synthetic.main.fragment_package.view.*

class PackageFragment(var modelEventByDate: ModelEventByDate) : Fragment(),
    NestedItemClickCallback {


    lateinit var viewFragment:View
    lateinit var categoryAdapter:CategoryAdapter
    var arrOfPackage=ArrayList<ModelPackage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_package, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFragment=view
        initDefine()
    }

    private fun initDefine() {
        setPackageAdapter()
    }

    private fun setPackageAdapter() {
        arrOfPackage=modelEventByDate.arrOfPackage
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewFragment.rvPackageList.layoutManager = layoutManager
        categoryAdapter = CategoryAdapter(context!!, arrOfPackage, this, 0)
        viewFragment.rvPackageList.adapter = categoryAdapter
    }

    override fun onNestedItemClick(view: View, parentPosition: Int, childPosition: Int) {
        if (view.id == R.id.ivArrow || view.id == R.id.rlCategory) {
            arrOfPackage[parentPosition].isSelected = !arrOfPackage[parentPosition].isSelected
        }
        categoryAdapter.notifyDataSetChanged()
    }
}
