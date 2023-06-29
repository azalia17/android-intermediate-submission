package com.azalia.submission1_storyapp.ui.detail

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.azalia.submission1_storyapp.data.local.StoryEntity
import com.azalia.submission1_storyapp.databinding.ActivityDetailBinding
import com.azalia.submission1_storyapp.getAddressName
import com.azalia.submission1_storyapp.response.ListStory
import com.azalia.submission1_storyapp.util.Constanta.EXTRA_STORY
import com.azalia.submission1_storyapp.util.ViewStateCallback
import com.bumptech.glide.Glide


class DetailActivity : AppCompatActivity(), ViewStateCallback<ListStory> {

    private lateinit var detailBinding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        val story = intent.getParcelableExtra<StoryEntity>(EXTRA_STORY)
        Log.e(TAG, "story: $story")
        setData(story)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
        }
    }

    private fun setData(story: StoryEntity?) {
        if (story != null) {
            detailBinding.tvDetailName.text = story.name
            detailBinding.tvDetailDesc.text = story.description
            val getAddress = story.lat?.let { story.lon?.let { it1 ->
                getAddressName(this, it,
                    it1
                )
            } }
            detailBinding.tvDetailLocation.text = "$getAddress"
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .into(detailBinding.ivDetail)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onSuccess(data: ListStory) {
        Log.e(TAG, "onSuccess Detail: $data")

        detailBinding.apply {
            tvDesc.text = data.description
            tvDetailName.text = data.name
            Glide.with(this@DetailActivity)
                .load(data.photoUrl)
                .into(detailBinding.ivDetail)
        }
    }

    override fun onLoading() {
        Log.e(TAG, "onLoading Detail")
    }

    override fun onFailed(message: String?) {
        Log.e(TAG, "onFailed Detail: $message")
    }
}