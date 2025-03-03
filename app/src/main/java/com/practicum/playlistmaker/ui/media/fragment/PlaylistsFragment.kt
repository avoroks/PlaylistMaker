package com.practicum.playlistmaker.ui.media.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.ui.media.fragment.rv_playlists.PlaylistsAdapter
import com.practicum.playlistmaker.ui.media.state.PlaylistsState
import com.practicum.playlistmaker.ui.media.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.utils.gone
import com.practicum.playlistmaker.utils.show
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistsViewModel>()

    private lateinit var playlistAdapter: PlaylistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.newPlaylist.setOnClickListener {
            openCreatePlaylist()
        }

        playlistAdapter = PlaylistsAdapter() {
            openPlaylist(it)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.recyclerView.adapter = playlistAdapter

        viewModel.getPlaylistsState().observe(viewLifecycleOwner) { state ->
            playlistAdapter.submitList(null)
            when (state) {
                is PlaylistsState.EmptyState -> {
                    binding.emptyPlaylists.root.show()
                    binding.recyclerView.gone()
                }

                is PlaylistsState.ContentState -> {
                    binding.emptyPlaylists.root.gone()
                    playlistAdapter.submitList(state.playlists)
                    binding.recyclerView.show()
                }

                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCreatePlaylist() {
        val action = MediaFragmentDirections.actionMediaFragmentToCreatePlaylistFragment()
        this.findNavController().navigate(action)
    }

    private fun openPlaylist(playlist: Playlist) {
        val action = MediaFragmentDirections.actionMediaFragmentToPlaylistFragment(playlist.id)
        this.findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()
        viewModel.updatePlaylists()
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}