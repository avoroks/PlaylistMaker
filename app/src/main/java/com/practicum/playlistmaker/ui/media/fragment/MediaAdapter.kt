package com.practicum.playlistmaker.ui.media.fragment

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediaAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int) =
        when (position) {
            0 -> FavoriteFragment.newInstance()
            1 -> PlaylistsFragment.newInstance()
            else -> FavoriteFragment.newInstance()
        }
}
