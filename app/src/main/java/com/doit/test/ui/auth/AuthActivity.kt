package com.doit.test.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import butterknife.*

import com.doit.test.R
import com.doit.test.ui.auth.abstractions.AuthView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

@Suppress("ProtectedInFinal")
class AuthActivity : AppCompatActivity(), AuthView {
    private val presenter = AuthPresenter(this)

    @BindView(R.id.root)
    protected lateinit var rootView: View
    @BindView(R.id.input_email)
    protected lateinit var emailInput: EditText
    @BindView(R.id.input_layout_email)
    protected lateinit var emailLayout: TextInputLayout
    @BindView(R.id.input_pass)
    protected lateinit var passInput: EditText
    @BindView(R.id.input_layout_pass)
    protected lateinit var passLayout: TextInputLayout
    @BindView(R.id.btn_accept)
    protected lateinit var btnAccept: Button
    @BindView(R.id.progress)
    protected lateinit var progress: ProgressBar

    @OnCheckedChanged(R.id.input_mode)
    protected fun onModeChanged(value: Boolean) {
        this.presenter.isInSignUpMode = value
    }

    @OnTextChanged(R.id.input_email)
    protected fun onEmailChanged(value: CharSequence) {
        this.presenter.email = value.toString()
    }

    @OnFocusChange(R.id.input_email)
    protected fun onEmailFocusChanged() {
        if (!this.emailInput.hasFocus()) this.presenter.onEmailEntered()
    }

    @OnTextChanged(R.id.input_pass)
    protected fun onPassChanged(value: CharSequence) {
        this.presenter.password = value.toString()
    }

    @OnFocusChange(R.id.input_pass)
    protected fun onPassFocusChanged() {
        if (!this.passInput.hasFocus()) this.presenter.onPassEntered()
    }

    @OnClick(R.id.btn_accept)
    protected fun onAcceptClicked() {
        this.presenter.onAccepted()
    }

    override fun setEmail(value: String) = this.emailInput.setText(value)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.auth)

        ButterKnife.bind(this)
        KeyboardVisibilityEvent.setEventListener(this) { if (!it) this.rootView.clearFocus() }
    }

    override fun notifySignUpMode() {
        this.btnAccept.setText(R.string.upper_case_register)
    }

    override fun notifySignInMode() {
        this.btnAccept.setText(R.string.upper_case_login)
    }

    override fun notifyInvalidEmail(res: Int) {
        this.emailLayout.error = getString(res)
    }

    override fun disposeInvalidEmailNotification() {
        this.emailLayout.error = null
    }

    override fun notifyInvalidPass(res: Int) {
        this.passLayout.error = getString(res)
    }

    override fun disposeInvalidPassNotification() {
        this.passLayout.error = null
    }

    override fun notifyLoginEnabled() {
        this.btnAccept.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        this.btnAccept.isEnabled = true
    }

    override fun notifyLoginDisabled() {
        this.btnAccept.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        this.btnAccept.isEnabled = false
    }

    override fun notifyInProgress() {
        this.progress.visibility = View.VISIBLE
        this.emailInput.isEnabled = false
        this.passInput.isEnabled = false
    }

    override fun notifyFree() {
        this.progress.visibility = View.GONE
        this.emailInput.isEnabled = true
        this.passInput.isEnabled = true
    }

    override fun notifyLoginFailed(res: Int) {
        this.progress.visibility = View.GONE

        val snack = Snackbar.make(this.rootView, getString(res), Snackbar.LENGTH_LONG)
        snack.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        snack.show()
    }

}
