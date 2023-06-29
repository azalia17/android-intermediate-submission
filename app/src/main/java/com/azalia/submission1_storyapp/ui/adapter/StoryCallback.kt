package com.azalia.submission1_storyapp.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.azalia.submission1_storyapp.response.ListStory

class StoryCallback(private val mOldStoryList: List<ListStory>, private val mNewStoryList: List<ListStory>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = mOldStoryList.size


    override fun getNewListSize(): Int = mNewStoryList.size


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = mOldStoryList[oldItemPosition].id == mNewStoryList[newItemPosition].id


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = mNewStoryList[newItemPosition] == mOldStoryList[oldItemPosition]

}