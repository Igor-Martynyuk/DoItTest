package com.doit.test.ui.task.list

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.doit.test.R
import com.doit.test.ui.abstractions.MvpRecyclerAdapter
import com.doit.test.ui.task.cell.TaskCellHolder
import com.doit.test.ui.task.list.abstractions.TaskListView
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import io.reactivex.Completable
import io.reactivex.CompletableEmitter

@Suppress("ProtectedInFinal")
class TaskListFragment : Fragment(), TaskListView {
    private val presenter = TaskListPresenter(this)
    private var menuStateEmitter: CompletableEmitter? = null

    @BindView(R.id.list)
    protected lateinit var list: RecyclerView
    @BindView(R.id.fab_menu)
    protected lateinit var fabMenu: FloatingActionMenu
    @BindView(R.id.menu_item_0)
    protected lateinit var menuFirst: FloatingActionButton
    @BindView(R.id.menu_item_1)
    protected lateinit var menuSecond: FloatingActionButton
    @BindView(R.id.menu_item_2)
    protected lateinit var menuThird: FloatingActionButton

    private val adapter = object : MvpRecyclerAdapter<TaskCellHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskCellHolder {
            return TaskCellHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.task_cell,
                    parent,
                    false
                )
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        this.fabMenu.setOnMenuToggleListener { menuStateEmitter?.onComplete() }

        this.list.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        this.list.layoutManager = LinearLayoutManager(this.context)
        this.list.adapter = adapter

        object : ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                    if (viewHolder == null) return
                    getDefaultUIUtil().onSelected((viewHolder as TaskCellHolder).foreground)
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    getDefaultUIUtil().clearView((viewHolder as TaskCellHolder).foreground)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean

                ) = getDefaultUIUtil().onDraw(

                    c,
                    recyclerView,
                    (viewHolder as TaskCellHolder).foreground,
                    dX,
                    dY,
                    actionState, isCurrentlyActive

                )

                override fun onChildDrawOver(

                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder?,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean

                ) = getDefaultUIUtil().onDrawOver(

                    c,
                    recyclerView,
                    (viewHolder as TaskCellHolder).foreground,
                    dX,
                    dY,
                    actionState, isCurrentlyActive

                )

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    (viewHolder as TaskCellHolder).onSwiped()
                }
            }

        ) {}.attachToRecyclerView(this.list)

    }

    private fun setupMenu(icons: IntArray, actions: List<() -> Unit>, shouldReopen: Boolean) {
        Completable.create {
            this.menuStateEmitter = it
            this.fabMenu.close(true)
        }
            .andThen {

                this.menuFirst.setImageDrawable(ContextCompat.getDrawable(this.context!!, icons[0]))
                this.menuFirst.setOnClickListener { actions[0].invoke() }

                this.menuSecond.setImageDrawable(ContextCompat.getDrawable(this.context!!, icons[1]))
                this.menuSecond.setOnClickListener { actions[1].invoke() }

                this.menuThird.setImageDrawable(ContextCompat.getDrawable(this.context!!, icons[2]))
                this.menuThird.setOnClickListener { actions[2].invoke() }

                if (shouldReopen) this.fabMenu.open(true)

            }
            .subscribe()
    }

    override fun setUpTasksListMenu(openOnComplete: Boolean) {
        this.setupMenu(
            intArrayOf(R.drawable.ic_reset, R.drawable.ic_add, R.drawable.ic_sort),
            listOf(
                { this.presenter.onUpdateCommand() },
                { this.presenter.onCreateTaskCommand() },
                { this.presenter.onSortCommand() }
            ),
            openOnComplete
        )
    }

    override fun setUpSortingMenu(openOnComplete: Boolean) {
        this.setupMenu(
            intArrayOf(R.drawable.ic_letters, R.drawable.ic_warning, R.drawable.ic_date),
            listOf(
                { this.presenter.onSortByNameCommand() },
                { this.presenter.onSortByPriorityCommand() },
                { this.presenter.onSortByDateCommand() }
            ),
            openOnComplete
        )
    }

    override fun collapseMenu() {
        this.fabMenu.close(true)
    }

    override fun updateMenu(res: Array<Int>) {
        this.menuFirst.setImageResource(res[0])
        this.menuSecond.setImageResource(res[1])
        this.menuThird.setImageResource(res[2])
    }

    override fun update(count: Int) {
        this.adapter.count = count
    }

    override fun increment() {
        this.adapter.count++
    }

    override fun decrement() {
        this.adapter.count--
    }

}
