package me.myshows.android.ui.activities

import android.os.Bundle
import kotlinx.android.synthetic.main.search_activity.*
import me.myshows.android.R

class SearchActivity : HomeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)

        setupActionBar(toolbar)
    }
}