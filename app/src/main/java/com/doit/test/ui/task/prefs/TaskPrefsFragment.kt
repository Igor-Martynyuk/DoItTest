package com.doit.test.ui.task.prefs

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.preference.Preference
import androidx.preference.PreferenceDataStore
import androidx.preference.SwitchPreference

import com.doit.test.R
import com.doit.test.domain.layer.model.Task
import com.doit.test.ui.task.prefs.abstractions.TaskPrefsView
import com.google.android.material.snackbar.Snackbar
import com.takisoft.fix.support.v7.preference.DatePickerPreference
import com.takisoft.fix.support.v7.preference.EditTextPreference
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat
import com.takisoft.fix.support.v7.preference.TimePickerPreference
import java.util.*

class TaskPrefsFragment : PreferenceFragmentCompat(), TaskPrefsView {

    private val presenter = TaskPrefsPresenter(this)
    private var prefTitle: EditTextPreference? = null
    private var prefDate: DatePickerPreference? = null
    private var prefTime: TimePickerPreference? = null
    private var prefPriorityHigh: SwitchPreference? = null
    private var prefPriorityNormal: SwitchPreference? = null
    private var prefPriorityLow: SwitchPreference? = null
    private var btnApply: Button? = null

    private val dataChangedListener = Preference.OnPreferenceChangeListener { pref, value ->
        when {
            pref.key == this.prefTitle?.key -> {
                this.presenter.onTitleEntered(value as String)
                this.prefTitle?.summary = value
            }

            pref.key == this.prefDate?.key -> {
                val data = value as DatePickerPreference.DateWrapper
                val calendar = Calendar.getInstance()
                calendar.set(data.year, data.month, data.day)
                this.presenter.onDateEntered(calendar)
            }

            pref.key == this.prefTime?.key -> {
                val data = value as TimePickerPreference.TimeWrapper
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, data.hour)
                calendar.set(Calendar.MINUTE, data.minute)
                this.presenter.onTimeEntered(calendar)
            }

            pref.key == this.prefPriorityHigh?.key -> {
                this.presenter.onPriorityChecked(Task.Priority.High)
                this.prefPriorityNormal?.isChecked = false
                this.prefPriorityLow?.isChecked = false
            }

            pref.key == this.prefPriorityNormal?.key -> {
                this.presenter.onPriorityChecked(Task.Priority.Normal)
                this.prefPriorityHigh?.isChecked = false
                this.prefPriorityLow?.isChecked = false
            }

            pref.key == prefPriorityLow?.key -> {
                this.presenter.onPriorityChecked(Task.Priority.Low)
                this.prefPriorityNormal?.isChecked = false
                this.prefPriorityHigh?.isChecked = false
            }
        }
        this.reset(pref)
        true
    }

    private fun initPref(pref: Preference) {
        reset(pref)
        pref.onPreferenceChangeListener = this.dataChangedListener
        pref.preferenceDataStore = object : PreferenceDataStore() {
            var boolean: Boolean = false
            var int: Int = 0
            var long: Long = 0L
            var float: Float = 0F
            var string: String? = null
            var set: MutableSet<String>? = null

            override fun getBoolean(key: String?, defValue: Boolean) = this.boolean
            override fun putBoolean(key: String?, value: Boolean) {
                this.boolean = value
            }

            override fun getInt(key: String?, defValue: Int) = this.int
            override fun putInt(key: String?, value: Int) {
                this.int = value
            }

            override fun getLong(key: String?, defValue: Long) = this.long
            override fun putLong(key: String?, value: Long) {
                this.long = value
            }

            override fun getFloat(key: String?, defValue: Float): Float = this.float
            override fun putFloat(key: String?, value: Float) {
                this.float = value
            }

            override fun getString(key: String?, defValue: String?) = this.string
            override fun putString(key: String?, value: String?) {
                this.string = value!!
            }

            override fun getStringSet(key: String?, defValues: MutableSet<String>?) = this.set
            override fun putStringSet(key: String?, values: MutableSet<String>?) {
                this.set = values
            }
        }
    }

    private fun reset(pref: Preference) {
        val iconId = when (pref) {
            this.prefTitle -> R.drawable.ic_title
            this.prefDate -> R.drawable.ic_date
            this.prefTime -> R.drawable.ic_time
            else -> R.drawable.ic_priority
        }
        val icon = ContextCompat.getDrawable(this.context!!, iconId)?.mutate()
        DrawableCompat.setTint(icon!!, ContextCompat.getColor(this.context!!, android.R.color.darker_gray))

        pref.icon = icon
    }

    private fun notifyError(pref: Preference, res: Int) {
        val icon = ContextCompat.getDrawable(this.context!!, R.drawable.ic_warning)?.mutate()
        DrawableCompat.setTint(icon!!, ContextCompat.getColor(this.context!!, android.R.color.holo_red_light))
        pref.icon = icon

        val snack = Snackbar.make(this.view!!, res, Snackbar.LENGTH_LONG)
        snack.view.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
        snack.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, resources.getDimension(R.dimen.padding).toInt())
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

        val outVal = TypedValue()
        this.context?.theme?.resolveAttribute(android.R.attr.selectableItemBackground, outVal, true)

        this.btnApply = Button(this.context)
        this.btnApply?.layoutParams = params
        this.btnApply?.setBackgroundColor(ContextCompat.getColor(context!!, android.R.color.transparent))
        this.btnApply?.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        this.btnApply?.setBackgroundResource(outVal.resourceId)
        this.btnApply?.setOnClickListener { this.presenter.onAddTaskCommand() }

    }

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        super.addPreferencesFromResource(R.xml.prefs_task)

        this.prefTitle = this.findPreference(getString(R.string.task_title_input_key)) as EditTextPreference
        this.initPref(this.prefTitle as Preference)
        this.prefDate = this.findPreference(getString(R.string.task_date_input_key)) as DatePickerPreference?
        this.initPref(this.prefDate as Preference)
        this.prefTime = this.findPreference(getString(R.string.task_time_input_key)) as TimePickerPreference?
        this.initPref(this.prefTime as Preference)
        this.prefPriorityHigh = this.findPreference(getString(R.string.task_priority_high_key)) as SwitchPreference
        this.initPref(this.prefPriorityHigh as Preference)
        this.prefPriorityNormal = this.findPreference(getString(R.string.task_priority_normal_key)) as SwitchPreference
        this.initPref(this.prefPriorityNormal as Preference)
        this.prefPriorityLow = this.findPreference(getString(R.string.task_priority_low_key)) as SwitchPreference
        this.initPref(this.prefPriorityLow as Preference)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (btnApply?.parent == null)
            (view as LinearLayout).addView(this.btnApply)
    }

    override fun setMode(mode: TaskPrefsView.Mode) {
        this.btnApply?.text = getString(
            when (mode) {
                TaskPrefsView.Mode.ADD -> R.string.create
                TaskPrefsView.Mode.UPDATE -> R.string.update
            }
        )
    }

    override fun enableAction() {
        this.btnApply?.isEnabled = true
        this.btnApply?.setTextColor(ContextCompat.getColor(this.context!!, R.color.colorAccent))
    }

    override fun disableAction() {
        this.btnApply?.isEnabled = false
        this.btnApply?.setTextColor(ContextCompat.getColor(this.context!!, android.R.color.darker_gray))
    }

    override fun setTitle(value: String) {
        this.prefTitle?.text = value
        this.prefTitle?.summary = value
    }

    override fun setDate(date: Date, dateStr: String) {
        this.prefDate?.date = date
        this.prefDate?.summary = dateStr
    }

    override fun setTime(time: Date, dateStr: String) {
        this.prefTime?.time = time
        this.prefTime?.summary = dateStr
    }

    override fun setHighPriority() {
        this.prefPriorityHigh?.isChecked = true
    }

    override fun setNormalPriority() {
        this.prefPriorityNormal?.isChecked = true
    }

    override fun setLowPriority() {
        this.prefPriorityLow?.isChecked = true
    }

    override fun notifyInvalidTitle(res: Int) {
        this.notifyError(this.prefTitle!!, res)
    }

    override fun notifyInvalidDate(res: Int) {
        this.notifyError(this.prefDate!!, res)
    }

    override fun notifyInvalidTime(res: Int) {
        this.notifyError(this.prefTime!!, res)
    }

}
