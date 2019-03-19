package com.doit.test.ui.navigator

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import butterknife.BindView
import butterknife.ButterKnife
import com.doit.test.R
import com.doit.test.ui.navigator.abstractions.NavigatorView
import com.google.android.material.snackbar.Snackbar

@Suppress("ProtectedInFinal")
class NavigatorActivity : AppCompatActivity(), NavigatorView {

    private val presenter = NavigatorPresenter(this)

    @BindView(R.id.root)
    protected lateinit var rootView: ViewGroup
    @BindView(R.id.progress_view)
    protected lateinit var progress: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.navigator)

        ButterKnife.bind(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menuInflater.inflate(R.menu.menu_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item!!.itemId) {
        R.id.logout -> {
            this.presenter.onLogoutCommand()
            true
        }
        else -> false
    }

    override fun onBackPressed() {
        this.presenter.onBackCommand()
        super.onBackPressed()
    }

    override fun showProgress() {
        this.progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        this.progress.visibility = View.GONE
    }

    override fun notifyError(res: Int) {
        Snackbar.make(rootView, getString(res), Snackbar.LENGTH_LONG).show()
    }

    override fun getActualFragmentManager(): FragmentManager = this.supportFragmentManager
    override fun getParentFor(target: Class<out LifecycleOwner>) = this.rootView

}
