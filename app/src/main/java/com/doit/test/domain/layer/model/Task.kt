package com.doit.test.domain.layer.model

import com.doit.test.R
import java.util.*

data class Task(var id: String?, var title: String?, var date: Calendar, var priority: Priority){

    enum class Priority(val nameId: Int, val equivalent: Int) {
        High(R.string.high, 3) {},
        Normal(R.string.normal, 2),
        Low(R.string.low, 1),
        NONE(R.string.none, 0)
    }
}