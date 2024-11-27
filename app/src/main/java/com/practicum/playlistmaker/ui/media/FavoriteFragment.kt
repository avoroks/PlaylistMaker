package com.practicum.playlistmaker.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.databinding.FragmentFavoriteBinding
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.media.state.FavoriteTracksState
import com.practicum.playlistmaker.ui.media.view_model.FavoriteViewModel
import com.practicum.playlistmaker.ui.search.fragment.rv_tracks.TrackAdapter
import com.practicum.playlistmaker.utils.gone
import com.practicum.playlistmaker.utils.show
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FavoriteViewModel>()

    private lateinit var trackAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter {
            openPlayer(it)
        }

        binding.rvTracks.adapter = trackAdapter

        viewModel.getFavoriteTracksState().observe(viewLifecycleOwner) { state ->
            trackAdapter.submitList(null)
            when (state) {
                is FavoriteTracksState.EmptyState -> {
                    binding.emptyFavoriteList.root.show()
                    binding.rvTracks.gone()
                }

                is FavoriteTracksState.ContentState -> {
                    binding.emptyFavoriteList.root.gone()
                    trackAdapter.submitList(state.tracks)
                    binding.rvTracks.show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateFavoriteTracks()
    }

    private fun openPlayer(track: Track) {
        val action = MediaFragmentDirections.actionMediaFragmentToPlayerActivity(track)
        this.findNavController().navigate(action)
    }

    companion object {
        fun newInstance() = FavoriteFragment()
    }
}