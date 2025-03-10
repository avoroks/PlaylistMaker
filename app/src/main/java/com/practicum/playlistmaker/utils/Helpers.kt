package com.practicum.playlistmaker.utils

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.view.View
import androidx.viewbinding.ViewBinding
import com.google.android.material.color.MaterialColors
import com.google.android.material.internal.ViewUtils.dpToPx
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.R

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun Context.dpToPx(dp: Int) = dpToPx(this, dp).toInt()

fun getFileNameFromUri(context: Context, uri: Uri): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    return cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) it.getString(nameIndex) else null
        } else null
    } ?: "image_${System.currentTimeMillis()}.png"
}

fun getTrackTitle(trackCount: Int) = if (trackCount in 10..20) "треков"
else when (trackCount.toString().last()) {
    '1' -> "трек"
    in listOf('2', '3', '4') -> "трека"
    else -> "треков"
}


fun getMinuteTitle(minute: Long) = if (minute in 10..20) "минут"
else when (minute.toString().last()) {
    '1' -> "минута"
    in listOf('2', '3', '4') -> "минуты"
    else -> "минут"
}

fun getSecondTitle(second: Long) = if (second in 10..20) "секунд"
else when (second.toString().last()) {
    '1' -> "секунда"
    in listOf('2', '3', '4') -> "секунды"
    else -> "секунд"
}

fun showSnackbar(view: View, binding: ViewBinding, text: String) = Snackbar.make(
    binding.root,
    text,
    Snackbar.LENGTH_SHORT
)
    .setBackgroundTint(
        MaterialColors.getColor(
            view,
            R.attr.playlistInfoColor
        )
    )
    .setTextColor(
        MaterialColors.getColor(
            view,
            R.attr.playlistCoverColor
        )
    )
    .show()

fun delayAction(action: () -> Unit) = Handler(Looper.getMainLooper()).postDelayed({ action() }, 300)