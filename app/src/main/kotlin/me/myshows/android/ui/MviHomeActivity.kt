package me.myshows.android.ui

import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView

abstract class MviHomeActivity<V : MvpView, P : MviPresenter<V, VS>, VS> : MviActivity<V, P>() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun setupActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            onPostSetupActionBar(actionBar)
        }
    }

    protected fun onPostSetupActionBar(actionBar: ActionBar) {}
}
