package com.practicum.playlistmaker.ui.search.activity.rv_tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.search.model.Track

class TrackAdapter(private val clickListener: TrackClickListener) :
    ListAdapter<Track, TrackViewHolder>(TrackDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(currentList[position]) }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}

object TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.trackId == newItem.trackId
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }

}