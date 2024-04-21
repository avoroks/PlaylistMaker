package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.data.Track

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackPoster: ImageView = itemView.findViewById(R.id.track_poster)
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)

    fun bind(item: Track) {
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.ic_music)
            .fitCenter()
            .transform(RoundedCorners(2))
            .into(trackPoster)

        trackName.text = item.trackName
        artistName.text = item.artistName
        trackTime.text = item.trackTime
    }
}