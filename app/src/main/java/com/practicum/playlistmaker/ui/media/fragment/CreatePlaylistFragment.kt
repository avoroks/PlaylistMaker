package com.practicum.playlistmaker.ui.media.fragment

import android.app.AlertDialog.BUTTON_NEGATIVE
import android.app.AlertDialog.BUTTON_POSITIVE
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.ui.media.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.utils.getFileNameFromUri
import com.practicum.playlistmaker.utils.showSnackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

open class CreatePlaylistFragment : Fragment() {
    private var _binding: FragmentCreatePlaylistBinding? = null
    val binding get() = _binding!!

    open val viewModel by viewModel<PlaylistsViewModel>()

    var fileName: String? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                binding.pickerImage.setImageURI(it)
                saveImageToPrivateStorage(it)
                fileName = getFileNameFromUri(requireContext(), it)
            } ?: run { Log.d("PhotoPicker", "No media selected") }
        }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            processBackPressed()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pickerImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ImageOnly))
        }

        binding.name.addTextChangedListener { s ->
            binding.textInputLayoutForName.error =
                if (s.isNullOrEmpty()) getString(R.string.required_field) else null
            binding.createPlaylist.isEnabled = !s.isNullOrEmpty()
        }

        binding.createPlaylist.setOnClickListener {
            val fileUrl =
                fileName?.let { File(requireContext().filesDir, it).toURI().toString() }.orEmpty()

            viewModel.createPlaylist(
                name = binding.name.text.toString(),
                description = binding.description.text.toString(),
                url = fileUrl
            )

            showSnackbar(this.requireView(), binding, "Плейлист ${binding.name.text.toString()} создан")

            findNavController().popBackStack()
        }

        binding.backFromCreatePlaylist.setOnClickListener {
            processBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback.remove()
    }

    private fun processBackPressed() {
        if (!fileName.isNullOrEmpty() || !binding.name.text.isNullOrEmpty() || !binding.description.text.isNullOrEmpty()) {
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.complete_playlist_creation))
                .setMessage(getString(R.string.all_unsafe_data_will_lost))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.complete)) { _, _ -> findNavController().popBackStack() }
                .show()

            listOf(
                dialog.getButton(BUTTON_POSITIVE),
                dialog.getButton(BUTTON_NEGATIVE)
            ).forEach {
                it?.setTextColor(requireContext().getColor(R.color.main_light_color))
            }
        } else {
            findNavController().popBackStack()
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        try {
            val filename = getFileNameFromUri(requireContext(), uri)
            val file = File(context?.filesDir, filename)

            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        } catch (e: IOException) {
            Log.e("FileSave", "Ошибка записи файла: ${e.message}")
        }
    }
}
