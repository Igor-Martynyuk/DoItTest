package com.doit.test.ui.task.cell

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.doit.test.R
import com.doit.test.ui.abstractions.MvpRecyclerHolder
import com.doit.test.ui.task.cell.abstractions.TaskCellView

@Suppress("ProtectedInFinal")
class TaskCellHolder(view: View) : MvpRecyclerHolder(view),
    TaskCellView {

    private val presenter = TaskCellPresenter(this)

    @BindView(R.id.tv_title)
    protected lateinit var tvName: TextView
    @BindView(R.id.tv_due_value)
    protected lateinit var tvDue: TextView
    @BindView(R.id.tv_priority)
    protected lateinit var tvPriority: TextView
    @BindView(R.id.img_priority)
    protected lateinit var imgPriority: ImageView

    @BindView(R.id.foreground)
    lateinit var foreground: View

    init {
        ButterKnife.bind(this, view)
    }

    fun onSwiped(){
        this.presenter.onRemoveCommand()
    }

    @OnClick(R.id.foreground)
    fun onForgroundClick(){
        this.presenter.onSelectCommand()
    }

    override fun onBind(index: Int) {
        this.presenter.index = index
    }

    override fun updateTitle(value: String) {
        this.tvName.text = value
    }

    override fun updateDue(value: String) {
        this.tvDue.text = value
    }

    override fun updatePriority(resText: Int, resColor: Int) {
        this.tvPriority.text = super.context.getString(resText)
        this.imgPriority.drawable.mutate().setTint(ContextCompat.getColor(super.context, resColor))
    }

}