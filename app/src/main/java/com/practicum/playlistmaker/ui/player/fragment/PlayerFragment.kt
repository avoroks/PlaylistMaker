package com.practicum.playlistmaker.ui.player.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.media.state.PlaylistsState
import com.practicum.playlistmaker.ui.player.rv_playlists.PlaylistsInBottomSheetAdapter
import com.practicum.playlistmaker.ui.player.state.AddTrackToPlaylistState.TrackAdded
import com.practicum.playlistmaker.ui.player.state.AddTrackToPlaylistState.TrackAlreadyExistsInPlaylist
import com.practicum.playlistmaker.ui.player.state.PlayerState
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.utils.dpToPx
import com.practicum.playlistmaker.utils.gone
import com.practicum.playlistmaker.utils.show
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!


    private val args: PlayerFragmentArgs by navArgs()

    private val viewModel by viewModel<PlayerViewModel>() {
        parametersOf(track)
    }

    private lateinit var playlistAdapter: PlaylistsInBottomSheetAdapter

    private lateinit var track: Track

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track = args.track

        binding.track.text = track.trackName
        binding.author.text = track.artistName
        binding.trackTimeMills.text = track.humanReadableTrackTime
        binding.collectionName.text = track.collectionName
        binding.releaseDate.text = track.releaseYear
        binding.primaryGenreName.text = track.primaryGenreName
        binding.country.text = track.country

        Glide.with(this)
            .load(track.coverUrl)
            .placeholder(R.drawable.track_placeholder)
            .error(R.drawable.track_placeholder)
            .centerCrop()
            .transform(RoundedCorners(requireContext().dpToPx(8)))
            .into(binding.cover)

        binding.playButton.setOnClickListener {
            viewModel.playBackControl()
        }

        viewModel.getPlayerState().observe(viewLifecycleOwner) { playerState ->
            when (playerState) {
                is PlayerState.Prepared -> {
                    binding.playButton.setImageResource(R.drawable.ic_play)
                    binding.trackTime.text = getString(R.string.default_zero_time)
                }

                PlayerState.Paused -> {
                    binding.playButton.setImageResource(R.drawable.ic_play)
                }

                is PlayerState.Playing -> {
                    binding.playButton.setImageResource(R.drawable.ic_pause)
                    binding.trackTime.text = playerState.trackTime
                }

                PlayerState.TrackEnded -> {
                    binding.trackTime.text = getString(R.string.default_zero_time)
                    binding.playButton.setImageResource(R.drawable.ic_play)
                }
            }
        }

        binding.addToFav.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        viewModel.getIsTrackAddedToFavoriteState().observe(viewLifecycleOwner) {
            changeFavIcon(it)
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = STATE_HIDDEN
        }

        binding.addButton.setOnClickListener {
            loadPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) =
                when (newState) {
                    STATE_HIDDEN -> {
                        binding.overlay.gone()
                    }

                    else -> {
                        binding.overlay.show()
                    }
                }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        viewModel.getAddTrackToPlaylistState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is TrackAdded -> {
                    Toast.makeText(
                        requireContext(),
                        "Добавлено в плейлист ${state.playlistName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is TrackAlreadyExistsInPlaylist -> {
                    Toast.makeText(
                        requireContext(),
                        "Трек уже добавлен в плейлист ${state.playlistName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.newPlaylist.setOnClickListener {
            val action = PlayerFragmentDirections.actionPlayerFragmentToCreatePlaylistFragment()
            this.findNavController().navigate(action)
        }

        binding.backFromPlayer.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopPlayer()
    }

    private fun loadPlaylists() {
        playlistAdapter = PlaylistsInBottomSheetAdapter() {
            viewModel.addTrackToPlaylist(track, it)
        }

        binding.rvPlaylists.adapter = playlistAdapter

        viewModel.getPlaylistsState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsState.ContentState -> {
                    playlistAdapter.submitList(state.playlists)
                }

                else -> {
                    playlistAdapter.submitList(emptyList())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updatePlaylists()
    }

    private fun changeFavIcon(isFav: Boolean) {
        val icon = when (isFav) {
            true -> R.drawable.ic_added_to_favorite
            else -> R.drawable.ic_add_to_favorite
        }
        binding.addToFav.setImageResource(icon)
    }
}