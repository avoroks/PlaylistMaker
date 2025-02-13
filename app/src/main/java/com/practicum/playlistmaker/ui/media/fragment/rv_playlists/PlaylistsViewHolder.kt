package com.practicum.playlistmaker.ui.media.fragment.rv_playlists

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.utils.getTrackTitle

class PlaylistsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val playlistPoster: ImageView = itemView.findViewById(R.id.playlistPoster)
    private val playlistName: TextView = itemView.findViewById(R.id.playlistTitle)
    private val trackCount: TextView = itemView.findViewById(R.id.trackCount)

    fun bind(item: Playlist) {
        if (item.imageUrl.isNotBlank())
            playlistPoster.setImageURI(item.imageUrl.toUri())
        else playlistPoster.setImageResource(R.drawable.track_placeholder)
        playlistName.text = item.name
        trackCount.text = "${item.trackCount} ${getTrackTitle(item.trackCount)}"
    }
}