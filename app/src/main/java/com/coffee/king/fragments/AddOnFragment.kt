package com.coffee.king.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.coffee.king.R
import com.coffee.king.adapter.CouplePackageAdapter
import com.coffee.king.adapter.UpgradeEventAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelCouplePackage
import com.coffee.king.model.ModelEventByDate
import com.coffee.king.model.ModelUpgradeEvent
import kotlinx.android.synthetic.main.fragment_add_on.view.*

class AddOnFragment(var modelEventByDate: ModelEventByDate) : Fragment(), ItemClickCallback {


    lateinit var viewFragment:View
    var arrOfModelUpgradeEvent=ArrayList<ModelUpgradeEvent>()
    var arrOfCouplePackage=ArrayList<ModelCouplePackage>()
    lateinit var upgradeEventAdapter: UpgradeEventAdapter
    lateinit var couplePackageAdapter: CouplePackageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_on, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFragment=view
        initDefine()
    }

    private fun initDefine() {
        if (modelEventByDate.bookingType == "1") {
            viewFragment.rvUpgradeEvent.visibility = View.VISIBLE
            viewFragment.rvCouplePackage.visibility = View.GONE
            arrOfModelUpgradeEvent = modelEventByDate.arrOfUpgradeEvent
            setUpgradeEventAdapter()
        } else {
            viewFragment.rvUpgradeEvent.visibility = View.GONE
            viewFragment.rvCouplePackage.visibility = View.VISIBLE
            arrOfCouplePackage = modelEventByDate.arrOfCouplePackage
            setCouplePackageAdapter()
        }
    }

    private fun setUpgradeEventAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewFragment.rvUpgradeEvent.layoutManager = layoutManager
        upgradeEventAdapter = UpgradeEventAdapter(context!!, arrOfModelUpgradeEvent, this, 0)
        viewFragment.rvUpgradeEvent.adapter = upgradeEventAdapter
    }

    private fun setCouplePackageAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewFragment.rvCouplePackage.layoutManager = layoutManager
        couplePackageAdapter = CouplePackageAdapter(context!!, arrOfCouplePackage, this, 0)
        viewFragment.rvCouplePackage.adapter = couplePackageAdapter
    }

    override fun onItemClick(view: View, position: Int) {

    }
}
