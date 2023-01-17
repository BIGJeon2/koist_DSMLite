package com.bigjeon.johoblite.pageadapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bigjeon.johoblite.subtitle.AnalysisSubTitleFragment
import com.bigjeon.johoblite.subtitle.HomeSubTitleFragment
import com.bigjeon.johoblite.subtitle.ReportSubTitleFragment
import com.bigjeon.johoblite.subtitle.UploadSubTitleFragment

class MainViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle){
	
	private val numTab = 4
	
	override fun getItemCount(): Int {
		return numTab
	}
	
	override fun createFragment(position: Int): Fragment {
		when(position){
			0 -> return HomeSubTitleFragment()
			1 -> return UploadSubTitleFragment()
			2 -> return AnalysisSubTitleFragment()
			3 -> return ReportSubTitleFragment()
		}
		return HomeSubTitleFragment()
	}
	
	
}