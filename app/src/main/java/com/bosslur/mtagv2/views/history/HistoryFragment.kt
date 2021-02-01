package com.bosslur.mtagv2.views.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.bosslur.mtagv2.R
import com.bosslur.mtagv2.databinding.FragmentHistoryBinding
import com.bosslur.mtagv2.date.MyDate.fromTimeStampToDate
import com.bosslur.mtagv2.date.MyDate.toCalendar
import com.bosslur.mtagv2.date.MyDate.toDate
import com.bosslur.mtagv2.date.MyDate.toDay
import com.bosslur.mtagv2.date.MyDate.toMonth
import com.bosslur.mtagv2.date.MyDate.toTime
import com.bosslur.mtagv2.dialog.MyDialog
import com.bosslur.mtagv2.hawkstorage.HawkStorage
import com.bosslur.mtagv2.model.History
import com.bosslur.mtagv2.model.HistoryResponse
import com.bosslur.mtagv2.networking.ApiServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class HistoryFragment : Fragment() {

    private companion object {
        private val TAG: String = HistoryFragment::class.java.simpleName
    }

    private var binding: FragmentHistoryBinding? = null
    private var dataHistories: List<History?>? = null
    private val events = mutableListOf<EventDay>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        requestDataHistory()

        setupCalendar()

        onClick()
    }

    private fun setupCalendar() {
        binding?.calendarViewHistory?.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                requestDataHistory()
            }

        })

        binding?.calendarViewHistory?.setOnForwardPageChangeListener(object : OnCalendarPageChangeListener{
            override fun onChange() {
                requestDataHistory()
            }

        })
    }

    private fun requestDataHistory() {
        val calendar = binding?.calendarViewHistory?.currentPageDate
        val lastDay = calendar?.getActualMaximum(Calendar.DAY_OF_MONTH)
        val month = calendar?.get(Calendar.MONTH)?.plus(1)
        val year = calendar?.get(Calendar.YEAR)

        val fromDate = "$year-$month-01"
        val toDate = "$year-$month-$lastDay"
        getDataHistory(fromDate, toDate)
    }

    private fun getDataHistory(fromDate: String, toDate: String) {
        val token = HawkStorage.instance(context).getToken()
        binding?.pbHistory?.visibility = View.VISIBLE
        ApiServices.getMtagServices()
                .getHistoryAttendance("Bearer $token", fromDate, toDate)
                .enqueue(object : Callback<HistoryResponse>{
                    override fun onResponse(
                            call: Call<HistoryResponse>,
                            response: Response<HistoryResponse>
                    ) {
                        binding?.pbHistory?.visibility = View.GONE
                        if (response.isSuccessful){
                            dataHistories = response.body()?.histories
                            if (dataHistories != null){
                                for (dataHistory in dataHistories!!){
                                    val status = dataHistory?.status
                                    val checkInTime: String
                                    val checkOutTime: String
                                    val calendarHistoryCheckIn: Calendar?
                                    val calendarHistoryCheckOut: Calendar?
                                    val currentDate = Calendar.getInstance()

                                    if (status == 1){
                                        checkInTime = dataHistory.detail?.get(0)?.createdAt.toString()
                                        checkOutTime = dataHistory.detail?.get(1)?.createdAt.toString()

                                        calendarHistoryCheckOut = checkOutTime.fromTimeStampToDate()?.toCalendar()

                                        if (calendarHistoryCheckOut != null){
                                            events.add(EventDay(calendarHistoryCheckOut, R.drawable.ic_baseline_check_circle_primary_24))
                                        }

                                        if (currentDate.get(Calendar.DAY_OF_MONTH) == calendarHistoryCheckOut?.get(Calendar.DAY_OF_MONTH)){
                                            binding?.tvCurrentDate?.text = checkInTime.fromTimeStampToDate()?.toDay()
                                            binding?.tvCurrentMonth?.text = checkInTime.fromTimeStampToDate()?.toMonth()
                                            binding?.tvTimeCheckIn?.text = checkInTime.fromTimeStampToDate()?.toTime()
                                            binding?.tvTimeCheckOut?.text = checkOutTime.fromTimeStampToDate()?.toTime()
                                        }
                                    }else{
                                        checkInTime = dataHistory?.detail?.get(0)?.createdAt.toString()
                                        calendarHistoryCheckIn = checkInTime.fromTimeStampToDate()?.toCalendar()

                                        if (calendarHistoryCheckIn != null){
                                            events.add(EventDay(calendarHistoryCheckIn, R.drawable.ic_baseline_check_circle_yellow_light_24))
                                        }

                                        if (currentDate.get(Calendar.DAY_OF_MONTH) == calendarHistoryCheckIn?.get(Calendar.DAY_OF_MONTH)){
                                            binding?.tvCurrentDate?.text = checkInTime.fromTimeStampToDate()?.toDay()
                                            binding?.tvCurrentMonth?.text = checkInTime.fromTimeStampToDate()?.toMonth()
                                            binding?.tvTimeCheckIn?.text = checkInTime.fromTimeStampToDate()?.toTime()
                                        }
                                    }
                                }
                            }

                            binding?.calendarViewHistory?.setEvents(events)
                        }else{
                            MyDialog.dynamicDialog(context, getString(R.string.alert), getString(R.string.something_wrong))
                        }
                    }

                    override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                        binding?.pbHistory?.visibility = View.GONE
                        MyDialog.dynamicDialog(context, getString(R.string.alert), "${t.message}")
                        Log.e(TAG, "Error: ${t.message}")
                    }

                })
    }

    private fun onClick() {
        binding?.calendarViewHistory?.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                binding?.tvCurrentDate?.text = clickedDayCalendar.toDate().toDay()
                binding?.tvCurrentMonth?.text = clickedDayCalendar.toDate().toMonth()

                if (dataHistories != null) {
                    for (dataHistory in dataHistories!!) {
                        val checkInTime: String
                        val checkOutTime: String
                        val updateDate = dataHistory?.updatedAt
                        val calendarUpdated = updateDate?.fromTimeStampToDate()?.toCalendar()
                        if (clickedDayCalendar.get(Calendar.DAY_OF_MONTH) == calendarUpdated?.get(Calendar.DAY_OF_MONTH)) {
                            if (dataHistory.status == 1) {
                                checkInTime = dataHistory.detail?.get(0)?.createdAt.toString()
                                checkOutTime = dataHistory.detail?.get(1)?.createdAt.toString()

                                binding?.tvTimeCheckIn?.text = checkInTime.fromTimeStampToDate()?.toTime()
                                binding?.tvTimeCheckOut?.text = checkOutTime.fromTimeStampToDate()?.toTime()
                                break
                            } else {
                                checkInTime = dataHistory.detail?.get(0)?.createdAt.toString()
                                binding?.tvTimeCheckIn?.text = checkInTime.fromTimeStampToDate()?.toTime()
                                break
                            }
                        } else {
                            binding?.tvTimeCheckIn?.text = getString(R.string.default_text)
                            binding?.tvTimeCheckOut?.text = getString(R.string.default_text)
                        }
                    }
                }
            }


        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}




