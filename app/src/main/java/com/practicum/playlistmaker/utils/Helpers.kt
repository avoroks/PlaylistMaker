package com.practicum.playlistmaker.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import com.google.android.material.internal.ViewUtils.dpToPx

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

fun getTrackTitle(trackCount: Int) = when (trackCount.toString().last()) {
    '1' -> "трек"
    in listOf('2', '3', '4') -> "трека"
    else -> "треков"
}