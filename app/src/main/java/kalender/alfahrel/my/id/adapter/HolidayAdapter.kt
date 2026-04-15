package kalender.alfahrel.my.id.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R
import kalender.alfahrel.my.id.model.HolidayInfo
import kalender.alfahrel.my.id.model.HolidayType

class HolidayAdapter(private val holidays: List<HolidayInfo>) :
    RecyclerView.Adapter<HolidayAdapter.HolidayVH>() {

    inner class HolidayVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(kalender.alfahrel.my.id.R.id.tvHolidayDate)
        val tvName: TextView = view.findViewById(kalender.alfahrel.my.id.R.id.tvHolidayName)
        val tvDesc: TextView = view.findViewById(kalender.alfahrel.my.id.R.id.tvHolidayDesc)
        val chip: TextView   = view.findViewById(kalender.alfahrel.my.id.R.id.tvHolidayChip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HolidayVH(LayoutInflater.from(parent.context)
            .inflate(kalender.alfahrel.my.id.R.layout.item_holiday, parent, false))

    override fun getItemCount() = holidays.size

    override fun onBindViewHolder(h: HolidayVH, position: Int) {
        val item = holidays[position]
        val ctx  = h.itemView.context

        val monthNames = listOf("","Jan","Feb","Mar","Apr","Mei","Jun",
            "Jul","Agu","Sep","Okt","Nov","Des")
        h.tvDate.text = "${item.day} ${monthNames[item.month]}"
        h.tvName.text = item.name
        h.tvDesc.text = item.description

        when (item.type) {
            HolidayType.NATIONAL -> {
                h.chip.text = "Nasional"
                h.chip.setTextColor(ctx.resolveAttrColor(R.attr.colorOnError))
            }
            HolidayType.RELIGIOUS -> {
                h.chip.text = "Keagamaan"
                h.chip.setTextColor(ctx.resolveAttrColor(R.attr.colorOnError))
            }
            HolidayType.JOINT_LEAVE -> {
                h.chip.text = "Cuti Bersama"
                h.chip.setTextColor(ctx.resolveAttrColor(R.attr.colorOnError))
            }
        }
    }
}

private fun Context.resolveAttrColor(attr: Int): Int {
    val tv = TypedValue()
    theme.resolveAttribute(attr, tv, true)
    return tv.data
}