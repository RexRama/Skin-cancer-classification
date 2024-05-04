package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.databinding.ItemNewsBinding

class NewsAdapter(private var newsItem: List<ArticlesItem>) :
    RecyclerView.Adapter<NewsAdapter.ListViewHolder>() {

    class ListViewHolder(var binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    fun updateData(newData: List<ArticlesItem>) {
        newsItem = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return newsItem.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val newsItem = newsItem[position]
        Glide.with(holder.itemView.context)
            .load(newsItem.urlToImage)
            .into(holder.binding.ivNews)
        holder.apply {
            binding.tvNewsTitle.text = newsItem.title
            binding.tvNewsDescription.text = newsItem.description
        }
    }
}