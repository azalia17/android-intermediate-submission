package com.azalia.submission1_storyapp.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.azalia.submission1_storyapp.data.local.StoryEntity
import com.azalia.submission1_storyapp.databinding.ItemStoryListBinding
import com.azalia.submission1_storyapp.ui.detail.DetailActivity
import com.azalia.submission1_storyapp.util.Constanta.EXTRA_STORY
import com.bumptech.glide.Glide

class StoryAdapter: PagingDataAdapter<StoryEntity, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.StoryViewHolder {
        val itemStoryListBinding = ItemStoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(itemStoryListBinding)
    }


    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(holder.itemView.context, story)
        }
    }

    class StoryViewHolder(private val binding: ItemStoryListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: StoryEntity) {
            binding.apply {
                tvDetailDescription.text = story.description
                tvStoryName.text = story.name
            }

            Glide.with(itemView.context)
                .load(story.photoUrl)
                .fitCenter()
                .into(binding.ivPhotoDetail)

            itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.tvStoryName, "name"),
                        Pair(binding.ivPhotoDetail, "photo"),
                        Pair(binding.tvDetailDescription, "description")
                    )

                Intent(context, DetailActivity::class.java).also { intent ->
                    intent.putExtra(EXTRA_STORY, story)
                    context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryEntity,
                newItem: StoryEntity
            ): Boolean = oldItem == newItem
        }
    }
}