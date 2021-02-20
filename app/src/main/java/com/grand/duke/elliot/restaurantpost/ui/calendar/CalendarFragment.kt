package com.grand.duke.elliot.restaurantpost.ui.calendar

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.base.BaseFragment
import com.grand.duke.elliot.restaurantpost.databinding.CalendarDayBinding
import com.grand.duke.elliot.restaurantpost.databinding.CalendarMonthHeaderBinding
import com.grand.duke.elliot.restaurantpost.databinding.FragmentCalendarBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

class CalendarFragment: BaseFragment<CalendarViewModel, FragmentCalendarBinding>() {
    override val layoutRes: Int
        get() = R.layout.fragment_calendar

    override fun viewModel(): Class<CalendarViewModel> = CalendarViewModel::class.java

    private val daysOfWeek = daysOfWeekFromLocale()

    private lateinit var uiController: UiController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        uiController = UiController(viewDataBinding)
        uiController.init()

        return view
    }

    private inner class UiController(private val binding: FragmentCalendarBinding) {
        fun init() {
            initCalendarView()
            setupCalendarView()
        }

        private fun initCalendarView() {
            binding.calendarView.dayBinder = object: DayBinder<DayViewContainer> {
                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    container.binding.textView.text = day.date.dayOfMonth.toString()
                }

                override fun create(view: View): DayViewContainer = DayViewContainer(view)
            }

            binding.calendarView.monthHeaderBinder = object: MonthHeaderFooterBinder<MonthHeaderViewContainer> {
                override fun create(view: View) = MonthHeaderViewContainer(view)
                override fun bind(container: MonthHeaderViewContainer, month: CalendarMonth) {
                    val calendarDaysOfWeekLayout = container.binding.calendarDaysOfWeekLayout
                    val textColor = ContextCompat.getColor(requireContext(), R.color.color_text_medium_emphasis)
                    if (calendarDaysOfWeekLayout.tag == null) {
                        calendarDaysOfWeekLayout.tag = month.yearMonth

                        (calendarDaysOfWeekLayout as ViewGroup).children.map { it as TextView }.forEachIndexed { index, textView ->
                            textView.text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                    .toUpperCase(Locale.ENGLISH)
                            textView.setTextColor(textColor)
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
                        }
                    }
                }
            }
        }

        private fun setupCalendarView() {
            val now = YearMonth.now()
            binding.calendarView.setup(
                    now.minusMonths(12),
                    now.plusMonths(12),
                    daysOfWeek.first()
            )
            binding.calendarView.scrollToMonth(now)
        }
    }
}

class DayViewContainer(view: View): ViewContainer(view) {
    val binding = CalendarDayBinding.bind(view)
}

class MonthHeaderViewContainer(view: View) : ViewContainer(view) {
    val binding = CalendarMonthHeaderBinding.bind(view)
}