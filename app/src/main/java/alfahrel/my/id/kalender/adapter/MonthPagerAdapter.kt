package alfahrel.my.id.kalender.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import alfahrel.my.id.kalender.fragment.MonthFragment

class MonthPagerAdapter(private val activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2400

    override fun createFragment(position: Int) = MonthFragment.newInstance(position)
}