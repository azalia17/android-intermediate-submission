package com.azalia.submission1_storyapp.ui.main

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azalia.submission1_storyapp.R
import com.azalia.submission1_storyapp.data.local.StoryEntity
import com.azalia.submission1_storyapp.databinding.ActivityMainBinding
import com.azalia.submission1_storyapp.response.StoryResponse
import com.azalia.submission1_storyapp.ui.ViewModelFactory
import com.azalia.submission1_storyapp.ui.adapter.LoadingStateAdapter
import com.azalia.submission1_storyapp.ui.adapter.StoryAdapter
import com.azalia.submission1_storyapp.ui.add.AddStoryActivity
import com.azalia.submission1_storyapp.ui.login.LoginActivity
import com.azalia.submission1_storyapp.ui.map.MapsActivity
import com.azalia.submission1_storyapp.util.Constanta.EXTRA_TOKEN
import com.azalia.submission1_storyapp.util.ViewStateCallback
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagingApi::class)
class MainActivity : AppCompatActivity(), ViewStateCallback<StoryResponse> {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var recyclerView: RecyclerView

    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val mainViewModel: MainViewModel by viewModels {
        factory
    }

    private lateinit var tkn: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        val token = intent.getStringExtra(EXTRA_TOKEN)
        Log.e(TAG, "token: $token")
        if (token != null) {
            getAllStory(token)
        }
        mainBinding.swipeRefresh.isRefreshing = true

        mainBinding.swipeRefresh.isRefreshing = false

        setAdapter()
        if (token != null) {
            if (token.isNotEmpty()){
                setSwipeRefreshLayout(token)
                getAllStory(token)
            }
            tkn = token
        }
        else {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            Toast.makeText(this, getString(R.string.please_login), Toast.LENGTH_SHORT).show()
            finish()
        }

        mainBinding.fab.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(moveIntent)
        }
    }

    private fun getAllStory(token: String) {
        mainViewModel.getStories(token).observe(this@MainActivity) {
            updateRecyclerData(it)
        }
        mainBinding.swipeRefresh.isRefreshing = false
    }

    private fun updateRecyclerData(story: PagingData<StoryEntity>) {
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        storyAdapter.submitData(lifecycle, story)
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun setSwipeRefreshLayout(token: String) {
        mainBinding.swipeRefresh.setOnRefreshListener {
            getAllStory(token)
            Toast.makeText(this, getString(R.string.list_updated), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAdapter() {
        val linearLayoutManager = LinearLayoutManager(this)
        storyAdapter = StoryAdapter()
        try {
            recyclerView = mainBinding.rvListStory
            recyclerView.apply {
                adapter = storyAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        storyAdapter.retry()
                    }
                )
                layoutManager = linearLayoutManager
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                lifecycleScope.launch {
                    mainViewModel.logout()
                }
                Toast.makeText(this, getString(R.string.logout), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                true
            }
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.menu_map -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                intent.putExtra(EXTRA_TOKEN, tkn)
                startActivity(intent)
                true
            }
            else -> false
        }
    }

    override fun onSuccess(data: StoryResponse) {
        Log.e(TAG, "onSuccess: $data")

        mainBinding.rvListStory.visibility = visible
        mainBinding.tvMessage.visibility = invisible
        mainBinding.mainProgressBar.visibility = invisible
    }

    override fun onLoading() {
        mainBinding.rvListStory.visibility = invisible
        mainBinding.tvMessage.visibility = invisible
        mainBinding.mainProgressBar.visibility = visible
        Log.e(TAG, "onLoading Main")
    }

    override fun onFailed(message: String?) {
        mainBinding.rvListStory.visibility = invisible
        mainBinding.tvMessage.text = message
        mainBinding.mainProgressBar.visibility = invisible

        Log.e(TAG, "onFailed Main: $message")
    }
}