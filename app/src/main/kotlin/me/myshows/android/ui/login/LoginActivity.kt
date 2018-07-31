package me.myshows.android.ui.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_login.*
import me.myshows.android.MyShowsApplication
import me.myshows.android.R
import me.myshows.android.ui.activities.MainActivity

class LoginActivity : MviActivity<LoginView, LoginPresenter>(), LoginView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

        setupNewAccountTextView(new_account)
        setEnabled(login_layout, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            login.setAutofillHints(View.AUTOFILL_HINT_USERNAME)
            password.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
        }
    }

    override fun loginIntent(): Observable<LoginView.Credentials> = sign_in_button.clicks()
            .map { LoginView.Credentials(login.text.toString(), password.text.toString()) }

    override fun render(state: LoginViewState) {
        when (state) {
            is AutoLoginResult -> {
                if (state.success) {
                    openMainScreen()
                } else {
                    animate()
                }
            }
            NeedManualLogin -> {
                logo.translationY = 0f
                login_layout.alpha = 1f
                setEnabled(login_layout, true)
            }
            is LoginResult -> {
                if (state.success) {
                    openMainScreen()
                } else {
                    Toast.makeText(this, R.string.incorrect_login_or_password, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun createPresenter(): LoginPresenter =
            MyShowsApplication.getComponent(this).newLoginPresenter()

    private fun setupNewAccountTextView(view: TextView) {
        val register = getString(R.string.register)
        val newAccount = getString(R.string.new_account, register)

        val start = newAccount.indexOf(register)
        val end = start + register.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(REGISTER_URL))
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(this@LoginActivity, R.color.red_80_opacity)
                ds.isUnderlineText = true
            }
        }

        val ss = SpannableString(newAccount)
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        view.setText(ss, TextView.BufferType.SPANNABLE)
        view.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun openMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setEnabled(parent: ViewGroup, enabled: Boolean) {
        parent.isEnabled = enabled
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            when (child) {
                is ViewGroup -> setEnabled(child, enabled)
                else -> child.isEnabled = enabled
            }
        }
    }

    private fun animate() {
        val logoAnimator = ObjectAnimator.ofFloat(logo, View.TRANSLATION_Y, 0f)
        val loginLayoutAnimator = ObjectAnimator.ofFloat(login_layout, View.ALPHA, 1f)

        AnimatorSet().apply {
            playTogether(logoAnimator, loginLayoutAnimator)
            duration = ANIMATION_DURATION
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) = setEnabled(login_layout, true)
            })
        }.start()
    }

    companion object {
        private const val REGISTER_URL = "http://myshows.me/"
        private const val ANIMATION_DURATION: Long = 500
    }
}
