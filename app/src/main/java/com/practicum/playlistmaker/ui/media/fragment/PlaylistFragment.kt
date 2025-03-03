package com.practicum.playlistmaker.ui.media.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.domain.search.model.Track
import com.practicum.playlistmaker.ui.media.state.SharingPlaylistsState
import com.practicum.playlistmaker.ui.media.view_model.PlaylistViewModel
import com.practicum.playlistmaker.ui.search.fragment.rv_tracks.TrackAdapter
import com.practicum.playlistmaker.utils.delayAction
import com.practicum.playlistmaker.utils.gone
import com.practicum.playlistmaker.utils.show
import com.practicum.playlistmaker.utils.showSnackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistViewModel>() {
        parametersOf(playlistId)
    }

    private lateinit var trackAdapter: TrackAdapter

    private val args: PlaylistFragmentArgs by navArgs()
    private var playlistId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistId = args.playlistId

        trackAdapter = TrackAdapter(
            clickListener = { openPlayer(it) },
            longClickListener = {
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Хотите удалить трек?")
                    .setPositiveButton("Да") { _, _ ->
                        viewModel.deleteTrackFromPlaylist(it.trackId)
                    }
                    .setNegativeButton("Нет", null)
                    .show()

                listOf(
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE),
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                ).forEach {
                    it?.setTextColor(requireContext().getColor(R.color.main_light_color))
                }
            }
        )

        binding.rvTracks.adapter = trackAdapter

        val bottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomSheetWithAdditionalInfo).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        viewModel.getPlaylistData().observe(viewLifecycleOwner) { data ->
            with(binding) {
                playlistTitle.text = data.name
                playlistItem.playlistTitle.text = data.name

                playlistDescription.text = data.description
                duration.text = data.commonDuration

                trackCount.text = data.trackCount
                playlistItem.trackCount.text = data.trackCount

                if (data.imageUrl.isNotBlank()) {
                    cover.setImageURI(data.imageUrl.toUri())
                    playlistItem.playlistPoster.setImageURI(data.imageUrl.toUri())
                    cover.scaleType = ImageView.ScaleType.CENTER_CROP
                } else cover.scaleType = ImageView.ScaleType.FIT_CENTER

                trackAdapter.submitList(data.tracks)

                if (data.tracks.isEmpty())
                    showSnackbar(requireView(), binding, "В этом плейлисте нет треков")
            }
        }

        binding.share.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.shareInAdditionalInfo.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.editPlaylistInAdditionalInfo.setOnClickListener {
            openEditPlaylist()
        }

        binding.deletePlaylistInAdditionalInfo.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setMessage("Хотите удалить плейлист ${binding.playlistItem.playlistTitle.text}?")
                .setPositiveButton("Да") { _, _ ->
                    viewModel.removePlaylist()
                    delayAction { findNavController().popBackStack() }
                }
                .setNegativeButton("Нет", null)
                .show()

            listOf(
                dialog.getButton(AlertDialog.BUTTON_POSITIVE),
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ).forEach {
                it?.setTextColor(requireContext().getColor(R.color.main_light_color))
            }
        }

        binding.additionalInfo.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) =
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.gone()
                    }

                    else -> {
                        binding.overlay.show()
                    }
                }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        viewModel.getSharingPlaylistDetails().observe(viewLifecycleOwner) { details ->
            when (details) {
                is SharingPlaylistsState.EmptyTracksState -> {
                    showSnackbar(
                        this.requireView(),
                        binding,
                        "В этом плейлисте нет списка треков, которым можно поделиться"
                    )
                }

                is SharingPlaylistsState.ContentState -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, details.playlistData)
                    }
                    startActivity(intent)
                }
            }
        }


        binding.backFromPlaylist.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun openPlayer(track: Track) {
        val action = PlaylistFragmentDirections.actionPlaylistFragmentToPlayerFragment(track)
        this.findNavController().navigate(action)
    }

    private fun openEditPlaylist() {
        val action =
            PlaylistFragmentDirections.actionPlaylistFragmentToEditPlaylistFragment(playlistId)
        this.findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()
        viewModel.updatePlaylistData()
    }
}