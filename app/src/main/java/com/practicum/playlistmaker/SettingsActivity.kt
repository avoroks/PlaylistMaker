package com.practicum.playlistmaker

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_EMAIL
import android.content.Intent.EXTRA_SUBJECT
import android.content.Intent.EXTRA_TEXT
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = this.findViewById<TextView>(R.id.back_from_settings)

        backButton.setOnClickListener {
            this.finish()
        }

        val darkThemeSwitch = this.findViewById<SwitchMaterial>(R.id.themeSwitcher)
        darkThemeSwitch.isChecked = (applicationContext as App).darkTheme

        darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
        }

        val userGuideButton = this.findViewById<ImageButton>(R.id.button_userGuide)

        userGuideButton.setOnClickListener {
            val webIntent =
                Intent(ACTION_VIEW, Uri.parse(getString(R.string.practicum_offer)))
            startActivity(webIntent)
        }

        val supportButton = this.findViewById<ImageButton>(R.id.button_support)

        supportButton.setOnClickListener {
            val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(EXTRA_EMAIL, arrayOf(getString(R.string.email)))
                putExtra(
                    EXTRA_SUBJECT,
                    getString(R.string.email_subject)
                )
                putExtra(EXTRA_TEXT, getString(R.string.email_text))
            }
            startActivity(mailIntent)
        }

        val shareButton = this.findViewById<ImageButton>(R.id.button_share)

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(EXTRA_TEXT, getString(R.string.course_link))
            }
            startActivity(shareIntent)
        }
    }
}