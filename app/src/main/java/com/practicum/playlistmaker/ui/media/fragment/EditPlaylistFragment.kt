package com.practicum.playlistmaker.ui.media.fragment

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.media.view_model.EditPlaylistViewModel
import com.practicum.playlistmaker.utils.delayAction
import com.practicum.playlistmaker.utils.showSnackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class EditPlaylistFragment : CreatePlaylistFragment() {
    override val viewModel by viewModel<EditPlaylistViewModel>() {
        parametersOf(playlistId)
    }
    private val args: EditPlaylistFragmentArgs by navArgs()
    private val playlistId by lazy { args.playlistId }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.getPlaylistState().observe(viewLifecycleOwner) { data ->
            with(binding) {
                pickerImage.setImageURI(data.imageUrl.toUri())
                name.setText(data.name)
                description.setText(data.description)
                createPlaylist.text = getString(R.string.save)
            }
        }

        binding.createPlaylist.setOnClickListener {
            val fileUrl =
                fileName?.let { File(requireContext().filesDir, it).toURI().toString() }.orEmpty()

            viewModel.updatePlaylist(
                name = binding.name.text.toString(),
                description = binding.description.text.toString(),
                url = fileUrl.ifBlank { null }
            )

            showSnackbar(
                this.requireView(),
                binding,
                "Плейлист ${binding.name.text.toString()} обновлен"
            )

            delayAction { findNavController().popBackStack() }
        }

        binding.backFromCreatePlaylist.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}