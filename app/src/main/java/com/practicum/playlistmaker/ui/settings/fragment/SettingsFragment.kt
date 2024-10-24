package com.practicum.playlistmaker.ui.settings.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.domain.sharing.model.ExtraDataForSharing
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SettingsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getIsDarkThemeSelected().observe(viewLifecycleOwner) { isDark ->
            binding.themeSwitcher.isChecked = isDark
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.swithTheme(isChecked)
        }

        binding.buttonUserGuide.setOnClickListener {
            viewModel.openOffer()
        }

        binding.buttonSupport.setOnClickListener {
            viewModel.contactSupport()
        }

        binding.buttonShare.setOnClickListener {
            viewModel.shareApp()

        }

        viewModel.getSharingDetails().observe(viewLifecycleOwner) { details ->
            when (val extra = details.extraData) {
                is ExtraDataForSharing.ExtraDataForSharingApp -> {
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, extra.appLink)
                    }
                }

                is ExtraDataForSharing.ExtraDataForContactSupport -> {
                    Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(extra.email))
                        putExtra(
                            Intent.EXTRA_SUBJECT,
                            extra.subject
                        )
                        putExtra(Intent.EXTRA_TEXT, extra.text)
                    }
                }

                is ExtraDataForSharing.ExtraDataForOpenOffer -> {
                    Intent(Intent.ACTION_VIEW, Uri.parse(extra.offerLink))
                }
            }.also {
                startActivity(it)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}