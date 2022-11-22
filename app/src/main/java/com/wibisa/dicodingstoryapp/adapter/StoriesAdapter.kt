package com.wibisa.dicodingstoryapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wibisa.dicodingstoryapp.core.data.remote.response.Story
import com.wibisa.dicodingstoryapp.core.util.loadImage
import com.wibisa.dicodingstoryapp.databinding.ItemStoryBinding

class StoriesAdapter(private val clickListener: StoryListener) :
    PagingDataAdapter<Story, StoriesAdapter.StoriesAdapterViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesAdapterViewHolder {
        val itemStoryBinding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoriesAdapterViewHolder(itemStoryBinding)
    }

    override fun onBindViewHolder(holder: StoriesAdapterViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) holder.bind(story, clickListener)
    }

    class StoriesAdapterViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story, clickListener: StoryListener) {

            binding.imgPhoto.loadImage(story.photoUrl)
            binding.tvName.text = story.name
            binding.tvDescription.text = story.description

            itemView.setOnClickListener { clickListener.onClick(story) }
        }
    }

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem == newItem
        }
    }
}

class StoryListener(val clickListener: (story: Story) -> Unit) {
    fun onClick(story: Story) = clickListener(story)
}