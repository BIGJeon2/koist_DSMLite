package com.bigjeon.johoblite.pageadapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bigjeon.johoblite.userpage.BasicInfoFragment
import com.bigjeon.johoblite.userpage.PasswordFragment
import com.bigjeon.johoblite.userpage.ThemeFragment

class UserActivityPageAdapter (fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
	
	private val numTab = 3
	private val fragments = mutableListOf(BasicInfoFragment(), PasswordFragment(), ThemeFragment())
	
	override fun getItemCount(): Int {
		return numTab
	}
	
	override fun createFragment(position: Int): Fragment = fragments[position]
}
