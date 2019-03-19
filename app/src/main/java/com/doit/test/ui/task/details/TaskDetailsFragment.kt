package com.doit.test.ui.task.details

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

import com.doit.test.R
import com.doit.test.ui.task.details.abstractions.TaskDetailsView

class TaskDetailsFragment : Fragment(), TaskDetailsView {
    private var presenter: TaskDetailsPresenter = TaskDetailsPresenter(this)

    @BindView(R.id.tv_day)
    lateinit var remainDaysTv: TextView
    @BindView(R.id.tv_month)
    lateinit var remainHoursTv: TextView
    @BindView(R.id.tv_time)
    lateinit var remainMinutesTv: TextView
    @BindView(R.id.tv_title)
    lateinit var titleTv: TextView
    @BindView(R.id.tv_priority)
    lateinit var priorityTv: TextView
    @BindView(R.id.tv_notification)
    lateinit var notificationTv: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.task_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
    }

    @OnClick(R.id.btn_edit)
    fun onEditBtnClicked() = this.presenter.onEnableNotificationCommand()

    override fun setDay(value: String) {
        this.remainDaysTv.text = value
    }

    override fun setMonth(value: String) {
        this.remainHoursTv.text = value
    }

    override fun setTime(value: String) {
        this.remainMinutesTv.text = value
    }

    override fun setTitle(value: String) {
        this.titleTv.text = value
    }

    @SuppressLint("SetTextI18n")
    override fun setPriority(res: Int) {
        this.priorityTv.text = getString(R.string.priority) + ": " + getString(res)
    }

    override fun setNotificationEnabled(res: Int) {
        this.notificationTv.text = getString(res)
    }


}
