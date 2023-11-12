package com.itis.friendsplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.itis.friendsplanner.databinding.FragmentViewPagerBinding

open class ViewPagerFragment : Fragment(R.layout.fragment_view_pager) {


    private val viewBinding : FragmentViewPagerBinding by viewBinding(FragmentViewPagerBinding::bind)

    private var vpAdapter: ViewPagerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }


    private fun initAdapter() {
        val adapter = ViewPagerAdapter(parentFragmentManager, lifecycle)

        with (viewBinding) {
            vpWeek.adapter = adapter

            TabLayoutMediator(tbWeek, vpWeek) { tab, position ->
                tab.text = daysOfWeek[position]
            }.attach()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        vpAdapter = null
    }


    companion object {
        fun newInstance() = ViewPagerFragment()

        private val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    }
}
