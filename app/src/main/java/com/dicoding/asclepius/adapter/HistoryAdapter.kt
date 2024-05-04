package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.local.entity.HistoryAnalyze
import com.dicoding.asclepius.data.repository.HistoryRepository
import com.dicoding.asclepius.databinding.ItemSavedBinding

class HistoryAdapter(
    private val historyList: List<HistoryAnalyze>,
    private val historyRepository: HistoryRepository
) : RecyclerView.Adapter<HistoryAdapter.ListViewHolder>() {

    class ListViewHolder(var binding: ItemSavedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemSavedBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val savedHistory = historyList[position]
        Glide.with(holder.itemView.context)
            .load(savedHistory.image)
            .into(holder.binding.resultImageHistory)
        holder.apply {
            binding.resultPredictionHistory.text = savedHistory.prediction
            binding.resultTextHistory.text = savedHistory.score
            binding.btnDelete.setOnClickListener {
                historyRepository.delete(savedHistory)
            }
        }
    }
}