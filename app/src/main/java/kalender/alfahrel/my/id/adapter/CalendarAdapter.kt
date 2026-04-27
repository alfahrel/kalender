package kalender.alfahrel.my.id.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R
import kalender.alfahrel.my.id.model.CalendarDay
import kalender.alfahrel.my.id.model.HolidayType

class CalendarAdapter(private val days: List<CalendarDay>) :
    RecyclerView.Adapter<CalendarAdapter.DayVH>() {

    inner class DayVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(kalender.alfahrel.my.id.R.id.tvDay)
        val dot: View       = view.findViewById(kalender.alfahrel.my.id.R.id.dotHoliday)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DayVH(LayoutInflater.from(parent.context)
            .inflate(kalender.alfahrel.my.id.R.layout.item_calendar_day, parent, false))

    override fun getItemCount() = days.size

    override fun onBindViewHolder(h: DayVH, position: Int) {
        val item = days[position]
        val ctx  = h.itemView.context

        if (item.day == 0) {
            h.tvDay.text       = ""
            h.tvDay.background = null
            h.tvDay.alpha      = 1f
            h.dot.visibility   = View.INVISIBLE
            return
        }

        h.tvDay.text = item.day.toString()

        if (item.isTrailing) {
            h.tvDay.background = null
            h.tvDay.alpha      = 0.35f
            h.tvDay.setTextColor(
                when {
                    item.isHoliday || item.isSunday ->
                        ctx.resolveAttrColor(R.attr.colorErrorContainer)
                    else ->
                        ctx.resolveAttrColor(R.attr.colorOnSurface)
                }
            )
            h.dot.visibility = View.INVISIBLE
            return
        }

        h.tvDay.alpha = 1f

        when {
            item.isToday -> {
                h.tvDay.setBackgroundResource(kalender.alfahrel.my.id.R.drawable.bg_today)
                h.tvDay.setTextColor(ctx.resolveAttrColor(R.attr.colorOnPrimary))
            }
            item.holidayType == HolidayType.JOINT_LEAVE -> {
                h.tvDay.setBackgroundResource(kalender.alfahrel.my.id.R.drawable.bg_joint)
                h.tvDay.setTextColor(ctx.resolveAttrColor(R.attr.colorOnErrorContainer))
            }
            item.isHoliday -> {
                h.tvDay.setBackgroundResource(kalender.alfahrel.my.id.R.drawable.bg_holiday)
                h.tvDay.setTextColor(ctx.resolveAttrColor(R.attr.colorOnError))
            }
            item.isSunday || item.isSaturday -> {
                h.tvDay.setBackgroundResource(kalender.alfahrel.my.id.R.drawable.bg_sunday)
                h.tvDay.setTextColor(ctx.resolveAttrColor(R.attr.colorOnError))
            }
            else -> {
                h.tvDay.background = null
                h.tvDay.setTextColor(ctx.resolveAttrColor(R.attr.colorOnSurface))
            }
        }

        h.dot.visibility = if (item.isHoliday) View.VISIBLE else View.INVISIBLE
    }
}

private fun Context.resolveAttrColor(attr: Int): Int {
    val tv = TypedValue()
    theme.resolveAttribute(attr, tv, true)
    return tv.data
}