package com.practicum.playlistmaker.ui.settings.activity

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_EMAIL
import android.content.Intent.EXTRA_SUBJECT
import android.content.Intent.EXTRA_TEXT
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.domain.sharing.model.ExtraDataForSharing
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            SettingsViewModel.factory()
        )[SettingsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backFromSettings.setOnClickListener {
            this.finish()
        }

        viewModel.getIsDarkThemeSelected().observe(this) { isDark ->
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

        viewModel.getSharingDetails().observe(this) { details ->
            when (val extra = details.extraData) {
                is ExtraDataForSharing.ExtraDataForSharingApp -> {
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(EXTRA_TEXT, extra.appLink)
                    }
                }

                is ExtraDataForSharing.ExtraDataForContactSupport -> {
                    Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(EXTRA_EMAIL, arrayOf(extra.email))
                        putExtra(
                            EXTRA_SUBJECT,
                            extra.subject
                        )
                        putExtra(EXTRA_TEXT, extra.text)
                    }
                }

                is ExtraDataForSharing.ExtraDataForOpenOffer -> {
                    Intent(ACTION_VIEW, Uri.parse(extra.offerLink))
                }
            }.also {
                startActivity(it)
            }
        }
    }
}